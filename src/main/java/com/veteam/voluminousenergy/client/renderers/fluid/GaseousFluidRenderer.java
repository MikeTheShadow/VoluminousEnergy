package com.veteam.voluminousenergy.client.renderers.fluid;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.FluidRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.fluid.CustomFluidRenderer;

/**
 * A vertical mirror of vanilla's {@link FluidRenderer#tesselate}, for fluids that pool
 * upward against a ceiling instead of downward on a floor. The cap and the free surface swap
 * ends of the block: heights go through {@link #flipY} and quad winding is reversed.
 */
public class GaseousFluidRenderer implements CustomFluidRenderer {

    private static float flipY(float height) {
        return 1.0F - height;
    }

    @Override
    public boolean renderFluid(FluidRenderer fluidRenderer, FluidState fluidState, BlockAndTintGetter level, BlockPos pos, FluidRenderer.Output output, BlockState blockState) {
        FluidModel model = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluidState);
        VertexConsumer buffer = output.getBuilder(model.layer());
        TextureAtlasSprite stillSprite = model.stillMaterial().sprite();
        TextureAtlasSprite flowingSprite = model.flowingMaterial().sprite();
        TextureAtlasSprite overlaySprite = model.overlayMaterial() != null ? model.overlayMaterial().sprite() : null;
        int tint = model.fluidTintSource() != null ? model.fluidTintSource().colorInWorld(fluidState, blockState, level, pos) : -1;

        BlockState aboveState = level.getBlockState(pos.above());
        BlockState belowState = level.getBlockState(pos.below());
        BlockState northState = level.getBlockState(pos.north());
        FluidState northFluid = northState.getFluidState();
        BlockState southState = level.getBlockState(pos.south());
        FluidState southFluid = southState.getFluidState();
        BlockState westState = level.getBlockState(pos.west());
        FluidState westFluid = westState.getFluidState();
        BlockState eastState = level.getBlockState(pos.east());
        FluidState eastFluid = eastState.getFluidState();

        boolean renderSurfaceFace = !isNeighborStateHidingOverlay(fluidState, belowState, Direction.UP);
        boolean renderCapFace = FluidRenderer.shouldRenderFace(fluidState, blockState, Direction.UP, aboveState);
        boolean renderNorth = FluidRenderer.shouldRenderFace(fluidState, blockState, Direction.NORTH, northState);
        boolean renderSouth = FluidRenderer.shouldRenderFace(fluidState, blockState, Direction.SOUTH, southState);
        boolean renderWest = FluidRenderer.shouldRenderFace(fluidState, blockState, Direction.WEST, westState);
        boolean renderEast = FluidRenderer.shouldRenderFace(fluidState, blockState, Direction.EAST, eastState);

        if (!(renderSurfaceFace || renderCapFace || renderEast || renderWest || renderNorth || renderSouth)) {
            renderCapFace = true;
        }

        CardinalLighting cardinalLighting = level.cardinalLighting();

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
            heightNE = calculateAverageHeight(level, fluid, ownHeight, northHeight, eastHeight, pos.north().east());
            heightNW = calculateAverageHeight(level, fluid, ownHeight, northHeight, westHeight, pos.north().west());
            heightSE = calculateAverageHeight(level, fluid, ownHeight, southHeight, eastHeight, pos.south().east());
            heightSW = calculateAverageHeight(level, fluid, ownHeight, southHeight, westHeight, pos.south().west());
        }

        float localX = (float) (pos.getX() & 15);
        float localY = (float) (pos.getY() & 15);
        float localZ = (float) (pos.getZ() & 15);
        float capEdge = renderCapFace ? 0.001F : 0.0F;

        if (renderSurfaceFace && !isFaceOccludedByNeighbor(Direction.DOWN, Math.min(Math.min(heightNW, heightSW), Math.min(heightSE, heightNE)), belowState)) {
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
                u0 = stillSprite.getU0();
                v0 = stillSprite.getV0();
                u1 = u0;
                v1 = stillSprite.getV1();
                u2 = stillSprite.getU1();
                v2 = v1;
                u3 = u2;
                v3 = v0;
            } else {
                float angle = (float) Mth.atan2(flow.z, flow.x) - (float) (Math.PI / 2);
                float sin = Mth.sin(angle) * 0.25F;
                float cos = Mth.cos(angle) * 0.25F;
                u0 = flowingSprite.getU(0.5F - cos - sin);
                v0 = flowingSprite.getV(0.5F - cos + sin);
                u1 = flowingSprite.getU(0.5F - cos + sin);
                v1 = flowingSprite.getV(0.5F + cos + sin);
                u2 = flowingSprite.getU(0.5F + cos + sin);
                v2 = flowingSprite.getV(0.5F + cos - sin);
                u3 = flowingSprite.getU(0.5F + cos - sin);
                v3 = flowingSprite.getV(0.5F - cos - sin);
            }

            int light = getLightCoords(level, pos.below());
            int color = ARGB.scaleRGB(tint, cardinalLighting.down());

            vertex(buffer, localX + 0.0F, localY + flipY(heightNW), localZ + 0.0F, color, u0, v0, light);
            vertex(buffer, localX + 1.0F, localY + flipY(heightNE), localZ + 0.0F, color, u3, v3, light);
            vertex(buffer, localX + 1.0F, localY + flipY(heightSE), localZ + 1.0F, color, u2, v2, light);
            vertex(buffer, localX + 0.0F, localY + flipY(heightSW), localZ + 1.0F, color, u1, v1, light);
            if (fluidState.shouldRenderBackwardUpFace(level, pos.below())) {
                vertex(buffer, localX + 0.0F, localY + flipY(heightNW), localZ + 0.0F, color, u0, v0, light);
                vertex(buffer, localX + 0.0F, localY + flipY(heightSW), localZ + 1.0F, color, u1, v1, light);
                vertex(buffer, localX + 1.0F, localY + flipY(heightSE), localZ + 1.0F, color, u2, v2, light);
                vertex(buffer, localX + 1.0F, localY + flipY(heightNE), localZ + 0.0F, color, u3, v3, light);
            }
        }

        if (renderCapFace) {
            float u0 = stillSprite.getU0();
            float u1 = stillSprite.getU1();
            float v0 = stillSprite.getV0();
            float v1 = stillSprite.getV1();
            int light = getLightCoords(level, pos);
            int color = ARGB.scaleRGB(tint, cardinalLighting.up());
            vertex(buffer, localX + 1.0F, localY + flipY(capEdge), localZ + 1.0F, color, u1, v1, light);
            vertex(buffer, localX + 1.0F, localY + flipY(capEdge), localZ, color, u1, v0, light);
            vertex(buffer, localX, localY + flipY(capEdge), localZ, color, u0, v0, light);
            vertex(buffer, localX, localY + flipY(capEdge), localZ + 1.0F, color, u0, v1, light);
        }

        int sideLight = getLightCoords(level, pos);

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            float surfaceNear;
            float surfaceFar;
            float xNear;
            float xFar;
            float zNear;
            float zFar;
            boolean renderSide;
            BlockState neighborState;
            switch (direction) {
                case NORTH -> {
                    surfaceNear = heightNW;
                    surfaceFar = heightNE;
                    xNear = localX;
                    xFar = localX + 1.0F;
                    zNear = localZ + 0.001F;
                    zFar = localZ + 0.001F;
                    renderSide = renderNorth;
                    neighborState = northState;
                }
                case SOUTH -> {
                    surfaceNear = heightSE;
                    surfaceFar = heightSW;
                    xNear = localX + 1.0F;
                    xFar = localX;
                    zNear = localZ + 1.0F - 0.001F;
                    zFar = localZ + 1.0F - 0.001F;
                    renderSide = renderSouth;
                    neighborState = southState;
                }
                case WEST -> {
                    surfaceNear = heightSW;
                    surfaceFar = heightNW;
                    xNear = localX + 0.001F;
                    xFar = localX + 0.001F;
                    zNear = localZ + 1.0F;
                    zFar = localZ;
                    renderSide = renderWest;
                    neighborState = westState;
                }
                default -> {
                    surfaceNear = heightNE;
                    surfaceFar = heightSE;
                    xNear = localX + 1.0F - 0.001F;
                    xFar = localX + 1.0F - 0.001F;
                    zNear = localZ;
                    zFar = localZ + 1.0F;
                    renderSide = renderEast;
                    neighborState = eastState;
                }
            }

            if (renderSide && !isFaceOccludedByNeighbor(direction, Math.max(surfaceNear, surfaceFar), neighborState)) {
                BlockPos neighborPos = pos.relative(direction);
                boolean isOverlay = overlaySprite != null && neighborState.shouldDisplayFluidOverlay(level, neighborPos, fluidState);
                TextureAtlasSprite sideSprite = isOverlay ? overlaySprite : flowingSprite;

                float u0 = sideSprite.getU(0.0F);
                float u1 = sideSprite.getU(0.5F);
                float vNear = sideSprite.getV((1.0F - surfaceNear) * 0.5F);
                float vFar = sideSprite.getV((1.0F - surfaceFar) * 0.5F);
                float vCap = sideSprite.getV(0.5F);
                float axisShade = direction.getAxis() == Direction.Axis.Z ? cardinalLighting.north() : cardinalLighting.west();
                int color = ARGB.scaleRGB(tint, cardinalLighting.up() * axisShade);

                vertex(buffer, xNear, localY + 1.0F, zNear, color, u0, vCap, sideLight);
                vertex(buffer, xFar, localY + 1.0F, zFar, color, u1, vCap, sideLight);
                vertex(buffer, xFar, localY + flipY(surfaceFar), zFar, color, u1, vFar, sideLight);
                vertex(buffer, xNear, localY + flipY(surfaceNear), zNear, color, u0, vNear, sideLight);
                if (!isOverlay) {
                    vertex(buffer, xNear, localY + flipY(surfaceNear), zNear, color, u0, vNear, sideLight);
                    vertex(buffer, xFar, localY + flipY(surfaceFar), zFar, color, u1, vFar, sideLight);
                    vertex(buffer, xFar, localY + 1.0F, zFar, color, u1, vCap, sideLight);
                    vertex(buffer, xNear, localY + 1.0F, zNear, color, u0, vCap, sideLight);
                }
            }
        }
        return true;
    }

    private static float calculateAverageHeight(BlockAndTintGetter level, Fluid fluid, float ownHeight, float northSouthHeight, float eastWestHeight, BlockPos diagonalPos) {
        float diagonalHeight = getHeight(level, fluid, diagonalPos);
        if (ownHeight >= 1.0F || northSouthHeight >= 1.0F || eastWestHeight >= 1.0F || diagonalHeight >= 1.0F) {
            return 1.0F;
        }

        float[] weighted = new float[2];
        if (eastWestHeight > 0.0F || northSouthHeight > 0.0F || diagonalHeight > 0.0F) {
            addWeightedHeight(weighted, diagonalHeight);
        }

        addWeightedHeight(weighted, ownHeight);
        addWeightedHeight(weighted, eastWestHeight);
        addWeightedHeight(weighted, northSouthHeight);
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
        if (!fluid.isSame(fluidState.getType())) {
            return blockState.isSolid() ? -1.0F : 0.0F;
        }
        BlockState belowState = level.getBlockState(pos.below());
        return fluid.isSame(belowState.getFluidState().getType()) ? 1.0F : fluidState.getOwnHeight();
    }

    private static boolean isNeighborStateHidingOverlay(FluidState selfState, BlockState otherState, Direction neighborFace) {
        return otherState.shouldHideAdjacentFluidFace(neighborFace, selfState);
    }

    private static boolean isFaceOccludedByNeighbor(Direction face, float height, BlockState neighborState) {
        VoxelShape occluder = neighborState.getFaceOcclusionShape(face.getOpposite());
        if (occluder == Shapes.empty()) {
            return false;
        }
        if (occluder == Shapes.block()) {
            return face != Direction.DOWN || height == 1.0F;
        }
        VoxelShape shape = Shapes.box(0.0, flipY(height), 0.0, 1.0, 1.0, 1.0);
        return Shapes.blockOccludes(shape, occluder, face);
    }

    private static int getLightCoords(BlockAndTintGetter level, BlockPos pos) {
        return LightCoordsUtil.max(LightCoordsUtil.getLightCoords(level, pos), LightCoordsUtil.getLightCoords(level, pos.above()));
    }

    private static void vertex(VertexConsumer buffer, float x, float y, float z, int color, float u, float v, int light) {
        buffer.addVertex(x, y, z, color, u, v, OverlayTexture.NO_OVERLAY, light, 0.0F, 1.0F, 0.0F);
    }
}
