package com.veteam.voluminousenergy.client.renderers.fluid;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.textures.FluidSpriteCache;

/**
 * A vertical mirror of vanilla's {@link LiquidBlockRenderer#tesselate}, for fluids that pool
 * upward against a ceiling instead of downward on a floor. The cap and the free surface swap
 * ends of the block: heights go through {@link #flipY} and quad winding is reversed.
 */
@OnlyIn(Dist.CLIENT)
public final class GaseousFluidRenderer {

    private GaseousFluidRenderer() {
    }

    private static float flipY(float height) {
        return 1.0F - height;
    }

    public static void tesselate(BlockAndTintGetter level, BlockPos pos, VertexConsumer buffer, BlockState blockState, FluidState fluidState) {
        // Every gas block renders ceiling flush, including rising tips with no ceiling yet. Handing
        // those to vanilla's floor flush renderer instead puts two incompatible anchorings in one
        // fluid body, and the boundary between them cannot close.
        BlockState aboveState = level.getBlockState(pos.relative(Direction.UP));

        TextureAtlasSprite[] sprites = FluidSpriteCache.getFluidSprites(level, pos, fluidState);
        int tint = IClientFluidTypeExtensions.of(fluidState).getTintColor(fluidState, level, pos);
        float alpha = (float) (tint >> 24 & 255) / 255.0F;
        float red = (float) (tint >> 16 & 0xFF) / 255.0F;
        float green = (float) (tint >> 8 & 0xFF) / 255.0F;
        float blue = (float) (tint & 0xFF) / 255.0F;

        BlockState belowState = level.getBlockState(pos.relative(Direction.DOWN));
        BlockState northState = level.getBlockState(pos.relative(Direction.NORTH));
        FluidState northFluid = northState.getFluidState();
        BlockState southState = level.getBlockState(pos.relative(Direction.SOUTH));
        FluidState southFluid = southState.getFluidState();
        BlockState westState = level.getBlockState(pos.relative(Direction.WEST));
        FluidState westFluid = westState.getFluidState();
        BlockState eastState = level.getBlockState(pos.relative(Direction.EAST));
        FluidState eastFluid = eastState.getFluidState();

        // Free surface hangs off the bottom, cap sits at the top against the ceiling.
        boolean renderSurfaceFace = !isNeighborStateHidingOverlay(fluidState, belowState, Direction.UP);
        // No occlusion test on the cap: vanilla's assumes a cube shaped neighbor, which jagged cave
        // ceilings are not, and it wrongly hides the cap often enough to leave visible gaps.
        boolean renderCapFace = LiquidBlockRenderer.shouldRenderFace(level, pos, fluidState, blockState, Direction.UP, aboveState);
        boolean renderNorth = LiquidBlockRenderer.shouldRenderFace(level, pos, fluidState, blockState, Direction.NORTH, northState);
        boolean renderSouth = LiquidBlockRenderer.shouldRenderFace(level, pos, fluidState, blockState, Direction.SOUTH, southState);
        boolean renderWest = LiquidBlockRenderer.shouldRenderFace(level, pos, fluidState, blockState, Direction.WEST, westState);
        boolean renderEast = LiquidBlockRenderer.shouldRenderFace(level, pos, fluidState, blockState, Direction.EAST, eastState);

        if (!(renderSurfaceFace || renderCapFace || renderEast || renderWest || renderNorth || renderSouth)) {
            // Each face test is right in isolation, but at an inner corner they can combine to
            // leave a block with real fluid in it drawing nothing. Always draw something.
            renderCapFace = true;
        }

        float shadeDown = level.getShade(Direction.DOWN, true);
        float shadeUp = level.getShade(Direction.UP, true);
        float shadeZ = level.getShade(Direction.NORTH, true);
        float shadeX = level.getShade(Direction.WEST, true);

        Fluid fluid = fluidState.getType();
        float ownHeight = getHeight(level, fluid, pos, blockState, fluidState);
        float heightNE;
        float heightNW;
        float heightSE;
        float heightSW;
        if (ownHeight >= 1.0F) {
            heightNE = 1.0F;
            heightNW = 1.0F;
            heightSE = 1.0F;
            heightSW = 1.0F;
        } else {
            float northHeight = getHeight(level, fluid, pos.north(), northState, northFluid);
            float southHeight = getHeight(level, fluid, pos.south(), southState, southFluid);
            float eastHeight = getHeight(level, fluid, pos.east(), eastState, eastFluid);
            float westHeight = getHeight(level, fluid, pos.west(), westState, westFluid);
            heightNE = calculateAverageHeight(level, fluid, ownHeight, northHeight, eastHeight, pos.relative(Direction.NORTH).relative(Direction.EAST));
            heightNW = calculateAverageHeight(level, fluid, ownHeight, northHeight, westHeight, pos.relative(Direction.NORTH).relative(Direction.WEST));
            heightSE = calculateAverageHeight(level, fluid, ownHeight, southHeight, eastHeight, pos.relative(Direction.SOUTH).relative(Direction.EAST));
            heightSW = calculateAverageHeight(level, fluid, ownHeight, southHeight, westHeight, pos.relative(Direction.SOUTH).relative(Direction.WEST));
        }

        float localX = (float) (pos.getX() & 15);
        float localY = (float) (pos.getY() & 15);
        float localZ = (float) (pos.getZ() & 15);
        float capEdge = renderCapFace ? 0.001F : 0.0F;
        // Walls run to the true boundary rather than sharing capEdge. Vanilla can share it because
        // its cap flag collapses the epsilon to zero on solid ground; ours never collapses, so
        // sharing it opens a hairline slot around every ceiling hugging block.
        float wallEdge = 0.0F;

        if (renderSurfaceFace && !isFaceOccludedByNeighbor(level, pos, Direction.DOWN, Math.min(Math.min(heightNW, heightSW), Math.min(heightSE, heightNE)), belowState)) {
            // No 0.001 inset on the free surface. Vanilla's only runs for blocks that draw a
            // surface, so mirrored it lifts a terminus block's floor off the boundary its enclosed
            // neighbors still reach, tracing a hairline around that block's footprint. The z-fight
            // it guards is already culled by the occlusion test on this branch.
            Vec3 flow = fluidState.getFlow(level, pos);
            float u0;
            float u1;
            float u2;
            float u3;
            float v0;
            float v1;
            float v2;
            float v3;
            if (flow.x == 0.0 && flow.z == 0.0) {
                TextureAtlasSprite stillSprite = sprites[0];
                u0 = stillSprite.getU(0.0F);
                v0 = stillSprite.getV(0.0F);
                u1 = u0;
                v1 = stillSprite.getV(1.0F);
                u2 = stillSprite.getU(1.0F);
                v2 = v1;
                u3 = u2;
                v3 = v0;
            } else {
                TextureAtlasSprite flowSprite = sprites[1];
                float angle = (float) Mth.atan2(flow.z, flow.x) - (float) (Math.PI / 2);
                float sin = Mth.sin(angle) * 0.25F;
                float cos = Mth.cos(angle) * 0.25F;
                u0 = flowSprite.getU(0.5F + (-cos - sin));
                v0 = flowSprite.getV(0.5F + -cos + sin);
                u1 = flowSprite.getU(0.5F + -cos + sin);
                v1 = flowSprite.getV(0.5F + cos + sin);
                u2 = flowSprite.getU(0.5F + cos + sin);
                v2 = flowSprite.getV(0.5F + (cos - sin));
                u3 = flowSprite.getU(0.5F + (cos - sin));
                v3 = flowSprite.getV(0.5F + (-cos - sin));
            }

            float avgU = (u0 + u1 + u2 + u3) / 4.0F;
            float avgV = (v0 + v1 + v2 + v3) / 4.0F;
            float shrink = sprites[0].uvShrinkRatio();
            u0 = Mth.lerp(shrink, u0, avgU);
            u1 = Mth.lerp(shrink, u1, avgU);
            u2 = Mth.lerp(shrink, u2, avgU);
            u3 = Mth.lerp(shrink, u3, avgU);
            v0 = Mth.lerp(shrink, v0, avgV);
            v1 = Mth.lerp(shrink, v1, avgV);
            v2 = Mth.lerp(shrink, v2, avgV);
            v3 = Mth.lerp(shrink, v3, avgV);

            int light = getLightColor(level, pos.below());
            float r = shadeDown * red;
            float g = shadeDown * green;
            float b = shadeDown * blue;

            // Reversed winding relative to vanilla's top face: this quad now faces down.
            vertex(buffer, localX + 0.0F, localY + flipY(heightNW), localZ + 0.0F, r, g, b, alpha, u0, v0, light);
            vertex(buffer, localX + 1.0F, localY + flipY(heightNE), localZ + 0.0F, r, g, b, alpha, u3, v3, light);
            vertex(buffer, localX + 1.0F, localY + flipY(heightSE), localZ + 1.0F, r, g, b, alpha, u2, v2, light);
            vertex(buffer, localX + 0.0F, localY + flipY(heightSW), localZ + 1.0F, r, g, b, alpha, u1, v1, light);
            if (fluidState.shouldRenderBackwardUpFace(level, pos.below())) {
                vertex(buffer, localX + 0.0F, localY + flipY(heightNW), localZ + 0.0F, r, g, b, alpha, u0, v0, light);
                vertex(buffer, localX + 0.0F, localY + flipY(heightSW), localZ + 1.0F, r, g, b, alpha, u1, v1, light);
                vertex(buffer, localX + 1.0F, localY + flipY(heightSE), localZ + 1.0F, r, g, b, alpha, u2, v2, light);
                vertex(buffer, localX + 1.0F, localY + flipY(heightNE), localZ + 0.0F, r, g, b, alpha, u3, v3, light);
            }
        }

        if (renderCapFace) {
            float u0 = sprites[0].getU0();
            float u1 = sprites[0].getU1();
            float v0 = sprites[0].getV0();
            float v1 = sprites[0].getV1();
            int light = getLightColor(level, pos);
            float r = shadeUp * red;
            float g = shadeUp * green;
            float b = shadeUp * blue;
            // Reversed winding relative to vanilla's bottom cap: this quad now faces up.
            vertex(buffer, localX + 1.0F, localY + flipY(capEdge), localZ + 1.0F, r, g, b, alpha, u1, v1, light);
            vertex(buffer, localX + 1.0F, localY + flipY(capEdge), localZ, r, g, b, alpha, u1, v0, light);
            vertex(buffer, localX, localY + flipY(capEdge), localZ, r, g, b, alpha, u0, v0, light);
            vertex(buffer, localX, localY + flipY(capEdge), localZ + 1.0F, r, g, b, alpha, u0, v1, light);
        }

        int sideLight = getLightColor(level, pos);

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            float surfaceNear;
            float surfaceFar;
            float xNear;
            float xFar;
            float zNear;
            float zFar;
            boolean renderSide;
            // The wall's inner edge reuses the same heightNW/NE/SE/SW the free-surface quad was
            // built from, so the two always meet at exactly the same point.
            switch (direction) {
                case NORTH:
                    surfaceNear = heightNW;
                    surfaceFar = heightNE;
                    xNear = localX;
                    xFar = localX + 1.0F;
                    zNear = localZ + 0.001F;
                    zFar = localZ + 0.001F;
                    renderSide = renderNorth;
                    break;
                case SOUTH:
                    surfaceNear = heightSE;
                    surfaceFar = heightSW;
                    xNear = localX + 1.0F;
                    xFar = localX;
                    zNear = localZ + 1.0F - 0.001F;
                    zFar = localZ + 1.0F - 0.001F;
                    renderSide = renderSouth;
                    break;
                case WEST:
                    surfaceNear = heightSW;
                    surfaceFar = heightNW;
                    xNear = localX + 0.001F;
                    xFar = localX + 0.001F;
                    zNear = localZ + 1.0F;
                    zFar = localZ;
                    renderSide = renderWest;
                    break;
                default:
                    surfaceNear = heightNE;
                    surfaceFar = heightSE;
                    xNear = localX + 1.0F - 0.001F;
                    xFar = localX + 1.0F - 0.001F;
                    zNear = localZ;
                    zFar = localZ + 1.0F;
                    renderSide = renderEast;
            }

            if (renderSide && !isFaceOccludedByNeighbor(level, pos, direction, Math.max(surfaceNear, surfaceFar), level.getBlockState(pos.relative(direction)))) {
                BlockPos neighborPos = pos.relative(direction);
                TextureAtlasSprite flowSprite = sprites[1];
                if (sprites[2] != null) {
                    if (level.getBlockState(neighborPos).shouldDisplayFluidOverlay(level, neighborPos, fluidState)) {
                        flowSprite = sprites[2];
                    }
                }

                float u0 = flowSprite.getU(0.0F);
                float u1 = flowSprite.getU(0.5F);
                float vNear = flowSprite.getV((1.0F - surfaceNear) * 0.5F);
                float vFar = flowSprite.getV((1.0F - surfaceFar) * 0.5F);
                float vCap = flowSprite.getV(0.5F);
                float axisShade = direction.getAxis() == Direction.Axis.Z ? shadeZ : shadeX;
                float r = shadeUp * axisShade * red;
                float g = shadeUp * axisShade * green;
                float b = shadeUp * axisShade * blue;

                // Reversed winding relative to vanilla's side quads: the surface edge moved from top to bottom.
                vertex(buffer, xNear, localY + flipY(wallEdge), zNear, r, g, b, alpha, u0, vCap, sideLight);
                vertex(buffer, xFar, localY + flipY(wallEdge), zFar, r, g, b, alpha, u1, vCap, sideLight);
                vertex(buffer, xFar, localY + flipY(surfaceFar), zFar, r, g, b, alpha, u1, vFar, sideLight);
                vertex(buffer, xNear, localY + flipY(surfaceNear), zNear, r, g, b, alpha, u0, vNear, sideLight);
                if (flowSprite != sprites[2]) {
                    vertex(buffer, xNear, localY + flipY(surfaceNear), zNear, r, g, b, alpha, u0, vNear, sideLight);
                    vertex(buffer, xFar, localY + flipY(surfaceFar), zFar, r, g, b, alpha, u1, vFar, sideLight);
                    vertex(buffer, xFar, localY + flipY(wallEdge), zFar, r, g, b, alpha, u1, vCap, sideLight);
                    vertex(buffer, xNear, localY + flipY(wallEdge), zNear, r, g, b, alpha, u0, vCap, sideLight);
                }
            }
        }
    }

    private static float calculateAverageHeight(BlockAndTintGetter level, Fluid fluid, float currentHeight, float height1, float height2, BlockPos diagonalPos) {
        // Vanilla only pins this corner to 1.0 off the current block or a cardinal neighbor, which
        // misses two full pillars meeting diagonally: the diagonal block pins, this one blends
        // lower, and the shared corner tears. Consult the diagonal height here too.
        float diagonalHeight = getHeight(level, fluid, diagonalPos);
        if (currentHeight >= 1.0F || height1 >= 1.0F || height2 >= 1.0F || diagonalHeight >= 1.0F) {
            return 1.0F;
        }

        float[] weighted = new float[2];
        if (height2 > 0.0F || height1 > 0.0F || diagonalHeight > 0.0F) {
            addWeightedHeight(weighted, diagonalHeight);
        }

        addWeightedHeight(weighted, currentHeight);
        addWeightedHeight(weighted, height2);
        addWeightedHeight(weighted, height1);
        return weighted[0] / weighted[1];
    }

    private static void addWeightedHeight(float[] output, float height) {
        if (height >= 0.8F) {
            output[0] += height * 10.0F;
            output[1] += 10.0F;
        } else if (height >= 0.0F) {
            output[0] += height;
            output[1]++;
        }
    }

    private static float getHeight(BlockAndTintGetter level, Fluid fluid, BlockPos pos) {
        BlockState blockState = level.getBlockState(pos);
        return getHeight(level, fluid, pos, blockState, blockState.getFluidState());
    }

    private static float getHeight(BlockAndTintGetter level, Fluid fluid, BlockPos pos, BlockState blockState, FluidState fluidState) {
        if (fluid.isSame(fluidState.getType())) {
            // The one piece of vanilla's height math that is orientation dependent, so it mirrors
            // with the vertices. Water fills top down, so water above means brimful; gas fills
            // ceiling down, so gas below means brimful. Probing above instead inverts the taper.
            BlockState belowState = level.getBlockState(pos.below());
            return fluid.isSame(belowState.getFluidState().getType()) ? 1.0F : fluidState.getOwnHeight();
        } else {
            return !blockState.isSolid() ? 0.0F : -1.0F;
        }
    }

    private static boolean isNeighborStateHidingOverlay(FluidState selfState, BlockState otherState, Direction neighborFace) {
        return otherState.shouldHideAdjacentFluidFace(neighborFace, selfState);
    }

    private static boolean isFaceOccludedByState(BlockGetter level, Direction face, float height, BlockPos pos, BlockState state) {
        if (state.canOcclude()) {
            VoxelShape shape = Shapes.box(0.0, 0.0, 0.0, 1.0, (double) height, 1.0);
            VoxelShape adjacentShape = state.getOcclusionShape(level, pos);
            return Shapes.blockOccudes(shape, adjacentShape, face);
        } else {
            return false;
        }
    }

    private static boolean isFaceOccludedByNeighbor(BlockGetter level, BlockPos pos, Direction side, float height, BlockState blockState) {
        return isFaceOccludedByState(level, side, height, pos.relative(side), blockState);
    }

    private static int getLightColor(BlockAndTintGetter level, BlockPos pos) {
        int i = LevelRenderer.getLightColor(level, pos);
        int j = LevelRenderer.getLightColor(level, pos.above());
        int k = i & 0xFF;
        int l = j & 0xFF;
        int i1 = i >> 16 & 0xFF;
        int j1 = j >> 16 & 0xFF;
        return (k > l ? k : l) | (i1 > j1 ? i1 : j1) << 16;
    }

    private static void vertex(VertexConsumer buffer, float x, float y, float z, float red, float green, float blue, float alpha, float u, float v, int light) {
        buffer.addVertex(x, y, z)
                .setColor(red, green, blue, alpha)
                .setUv(u, v)
                .setLight(light)
                .setNormal(0.0F, 1.0F, 0.0F);
    }
}
