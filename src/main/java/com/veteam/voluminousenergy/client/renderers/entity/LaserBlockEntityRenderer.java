package com.veteam.voluminousenergy.client.renderers.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.veteam.voluminousenergy.VoluminousEnergy;
import com.veteam.voluminousenergy.blocks.tiles.DimensionalLaserTile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class LaserBlockEntityRenderer implements BlockEntityRenderer<DimensionalLaserTile> {

    public static final ResourceLocation BEAM_RESOURCE_LOCATION =  new ResourceLocation(VoluminousEnergy.MODID, "textures/entity/beacon_beam.png");

    public LaserBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {
    }

    // If you want to modify things pull it from the DimensionalLaserTile
    @Override
    public void render(DimensionalLaserTile dimensionalLaserTile, float f1, @NotNull PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int i1, int i2) {
        long gameTime = dimensionalLaserTile.getLevel().getGameTime();
        List<BeaconBlockEntity.BeaconBeamSection> list = new ArrayList<>();
        // If this float is not 1,1,1 expect a black screen. You can modify these if you want special colors
        list.add(new BeaconBlockEntity.BeaconBeamSection(new float[] {1,1,1}));
        int totalHeight = 0;

        for(int listPosition = 0; listPosition < list.size(); ++listPosition) {
            BeaconBlockEntity.BeaconBeamSection section = list.get(listPosition);
            renderBeaconBeam(poseStack, multiBufferSource, f1, gameTime, totalHeight, listPosition == list.size() - 1 ? 1024 : section.getHeight(), section.getColor());
            totalHeight += section.getHeight();
        }

    }

    public static void renderBeaconBeam(PoseStack p_112177_, MultiBufferSource p_112178_, float f1, long p_112180_, int p_112181_, int p_112182_, float[] p_112183_) {
        renderBeaconBeam(p_112177_, p_112178_, BEAM_RESOURCE_LOCATION, f1, 1.0F, p_112180_, p_112181_, p_112182_, p_112183_, 0.2F, 0.25F);
    }

    public static void renderBeaconBeam(PoseStack poseStack, MultiBufferSource multiBufferSource, ResourceLocation resourceLocation, float p_112188_, float p_112189_, long p_112190_, int p_112191_, int p_112192_, float[] p_112193_, float p_112194_, float p_112195_) {
        int i = p_112191_ + p_112192_;
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        float f = (float)Math.floorMod(p_112190_, 40) + p_112188_;
        float f1 = p_112192_ < 0 ? f : -f;
        float f2 = Mth.frac(f1 * 0.2F - (float)Mth.floor(f1 * 0.1F));
        float f3 = p_112193_[0];
        float f4 = p_112193_[1];
        float f5 = p_112193_[2];
        poseStack.pushPose();
        poseStack.mulPose(Vector3f.YP.rotationDegrees(f * 2.25F - 45.0F));
        float f9 = -p_112194_;
        float f12 = -p_112194_;
        float f15 = -1.0F + f2;
        float f16 = (float)p_112192_ * p_112189_ * (0.5F / p_112194_) + f15;
        renderPart(poseStack, multiBufferSource.getBuffer(RenderType.beaconBeam(resourceLocation, false)), f3, f4, f5, 1.0F, p_112191_, i, 0.0F, p_112194_, p_112194_, 0.0F, f9, 0.0F, 0.0F, f12, 0.0F, 1.0F, f16, f15);
        poseStack.popPose();
        float f6 = -p_112195_;
        float f7 = -p_112195_;
        float f8 = -p_112195_;
        f9 = -p_112195_;
        f15 = -1.0F + f2;
        f16 = (float)p_112192_ * p_112189_ + f15;
        renderPart(poseStack, multiBufferSource.getBuffer(RenderType.beaconBeam(resourceLocation, true)), f3, f4, f5, 0.125F, p_112191_, i, f6, f7, p_112195_, f8, f9, p_112195_, p_112195_, p_112195_, 0.0F, 1.0F, f16, f15);
        poseStack.popPose();
    }

    private static void renderPart(PoseStack p_112156_, VertexConsumer p_112157_, float p_112158_, float p_112159_, float p_112160_, float p_112161_, int p_112162_, int p_112163_, float p_112164_, float p_112165_, float p_112166_, float p_112167_, float p_112168_, float p_112169_, float p_112170_, float p_112171_, float p_112172_, float p_112173_, float p_112174_, float p_112175_) {
        PoseStack.Pose pose = p_112156_.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        renderQuad(matrix4f, matrix3f, p_112157_, p_112158_, p_112159_, p_112160_, p_112161_, p_112162_, p_112163_, p_112164_, p_112165_, p_112166_, p_112167_, p_112172_, p_112173_, p_112174_, p_112175_);
        renderQuad(matrix4f, matrix3f, p_112157_, p_112158_, p_112159_, p_112160_, p_112161_, p_112162_, p_112163_, p_112170_, p_112171_, p_112168_, p_112169_, p_112172_, p_112173_, p_112174_, p_112175_);
        renderQuad(matrix4f, matrix3f, p_112157_, p_112158_, p_112159_, p_112160_, p_112161_, p_112162_, p_112163_, p_112166_, p_112167_, p_112170_, p_112171_, p_112172_, p_112173_, p_112174_, p_112175_);
        renderQuad(matrix4f, matrix3f, p_112157_, p_112158_, p_112159_, p_112160_, p_112161_, p_112162_, p_112163_, p_112168_, p_112169_, p_112164_, p_112165_, p_112172_, p_112173_, p_112174_, p_112175_);
    }

    private static void renderQuad(Matrix4f p_112120_, Matrix3f p_112121_, VertexConsumer p_112122_, float p_112123_, float p_112124_, float p_112125_, float p_112126_, int p_112127_, int p_112128_, float p_112129_, float p_112130_, float p_112131_, float p_112132_, float p_112133_, float p_112134_, float p_112135_, float p_112136_) {
        addVertex(p_112120_, p_112121_, p_112122_, p_112123_, p_112124_, p_112125_, p_112126_, p_112128_, p_112129_, p_112130_, p_112134_, p_112135_);
        addVertex(p_112120_, p_112121_, p_112122_, p_112123_, p_112124_, p_112125_, p_112126_, p_112127_, p_112129_, p_112130_, p_112134_, p_112136_);
        addVertex(p_112120_, p_112121_, p_112122_, p_112123_, p_112124_, p_112125_, p_112126_, p_112127_, p_112131_, p_112132_, p_112133_, p_112136_);
        addVertex(p_112120_, p_112121_, p_112122_, p_112123_, p_112124_, p_112125_, p_112126_, p_112128_, p_112131_, p_112132_, p_112133_, p_112135_);
    }

    private static void addVertex(Matrix4f p_112107_, Matrix3f p_112108_, VertexConsumer p_112109_, float p_112110_, float p_112111_, float p_112112_, float p_112113_, int p_112114_, float p_112115_, float p_112116_, float p_112117_, float p_112118_) {
        p_112109_.vertex(p_112107_, p_112115_, (float)p_112114_, p_112116_).color(p_112110_, p_112111_, p_112112_, p_112113_).uv(p_112117_, p_112118_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(p_112108_, 0.0F, 1.0F, 0.0F).endVertex();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull DimensionalLaserTile dimensionalLaserTile) {
        return true;
    }

    @Override
    public boolean shouldRender(DimensionalLaserTile p_173531_, Vec3 p_173532_) {
        return Vec3.atCenterOf(p_173531_.getBlockPos()).multiply(1.0D, 0.0D, 1.0D).closerThan(p_173532_.multiply(1.0D, 0.0D, 1.0D), this.getViewDistance());
    }

    @Override
    public int getViewDistance() {
        return 256;
    }



}
