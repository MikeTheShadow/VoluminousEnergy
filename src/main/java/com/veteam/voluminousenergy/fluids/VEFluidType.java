package com.veteam.voluminousenergy.fluids;

import com.veteam.voluminousenergy.util.VEAttachments;
import com.veteam.voluminousenergy.util.extensions.VEFluidClientExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * A fluid type whose entity and item movement comes from its {@link VEFluidPhysics} preset and its
 * density, replacing the water movement NeoForge applies to modded fluids by default.
 */
public class VEFluidType extends FluidType {
    private static final double BODY_DENSITY = 1010.0D;
    private static final double FLUID_GRAVITY_SCALE = 0.375D;
    private static final double MAX_BUOYANT_ACCELERATION = 0.1D;
    private static final double WATER_ACCELERATION = 0.02D;
    private static final float SPRINTING_RETENTION = 0.9F;
    private static final float DEPTH_STRIDER_RETENTION = 0.54600006F;
    private static final float DOLPHINS_GRACE_RETENTION = 0.96F;
    private static final double CLIMBING_SPEED = 0.2D;
    private static final double STEP_UP_HEIGHT = 0.6D;
    private static final double STEP_UP_SPEED = 0.3D;
    private static final float AIR_DRAG = 0.91F;
    private static final double AIR_VERTICAL_DRAG = 0.98D;
    // NeoForge's jumpInFluid impulse for non-water fluids, and vanilla's per-tick zeroing of tiny motion
    private static final double FLUID_JUMP_IMPULSE = 0.04D;
    private static final double JUMP_IMPULSE_TOLERANCE = 1.0E-4D;
    private static final double MOTION_ZEROING_THRESHOLD = 0.003D;
    private static final double GAS_LIFT = 0.01D;
    private static final double GAS_MAX_RISE_SPEED = 0.06D;
    private static final double GAS_FLOW_PUSH = 0.003D;
    private static final double LEVITATION_SPEED = 0.05D;
    private static final double LEVITATION_APPROACH = 0.2D;

    private final ResourceLocation STILL_TEXTURE;
    private final ResourceLocation FLOWING_TEXTURE;
    private ResourceLocation overlayTexture;
    private int colourTint;
    private final VEFluidPhysics physics;
    private final int overlayColour;

    /**
     * Default constructor.
     *
     * @param properties     the general properties of the fluid type
     * @param physics        the movement and visibility preset of the fluid
     * @param overlayColour  the RGB colour of the fog and screen tint with the camera inside the fluid
     * @param stillTexture   is the still texture for the fluid
     * @param flowingTexture is the flowing texture for the fluid
     */
    public VEFluidType(Properties properties, VEFluidPhysics physics, int overlayColour, ResourceLocation stillTexture, ResourceLocation flowingTexture) {
        super(properties);
        this.physics = physics;
        this.overlayColour = overlayColour;
        this.STILL_TEXTURE = stillTexture;
        this.FLOWING_TEXTURE = flowingTexture;
    }

    /**
     * Default constructor.
     *
     * @param properties     the general properties of the fluid type
     * @param physics        the movement and visibility preset of the fluid
     * @param overlayColour  the RGB colour of the fog and screen tint with the camera inside the fluid
     * @param stillTexture   is the still texture for the fluid
     * @param flowingTexture is the flowing texture for the fluid
     * @param overlayTexture is the overlay texture when an entity is in the fluid (I think)
     */
    public VEFluidType(Properties properties, VEFluidPhysics physics, int overlayColour, ResourceLocation stillTexture, ResourceLocation flowingTexture, ResourceLocation overlayTexture) {
        this(properties, physics, overlayColour, stillTexture, flowingTexture);
        this.overlayTexture = overlayTexture;
    }

    public void setColourTint(int colourTint) {
        this.colourTint = colourTint;
    }

    public VEFluidPhysics getPhysics() {
        return physics;
    }

    public int getOverlayColour() {
        return overlayColour;
    }

    public VEFluidClientExtension getFluidClientExtension() {
        return new VEFluidClientExtension(STILL_TEXTURE, FLOWING_TEXTURE, overlayTexture, colourTint, physics, overlayColour);
    }

    @Override
    public boolean move(FluidState state, LivingEntity entity, Vec3 movementVector, double gravity) {
        if (physics.isGas()) {
            moveInGas(state, entity, movementVector, gravity);
        } else {
            moveInLiquid(entity, movementVector, gravity);
        }
        return true;
    }

    @Override
    public void setItemMovement(ItemEntity entity) {
        Vec3 motion = entity.getDeltaMovement();
        if (physics.isGas()) {
            entity.setDeltaMovement(motion.add(0.0D, -entity.getGravity(), 0.0D));
            return;
        }
        double horizontal = physics.horizontalRetention();
        double vertical = motion.y * physics.verticalRetention() + buoyantAcceleration(entity, entity.getGravity());
        entity.setDeltaMovement(motion.x * horizontal, vertical, motion.z * horizontal);
    }

    @Override
    public boolean canDrownIn(LivingEntity entity) {
        return super.canDrownIn(entity) && !entity.canBreatheUnderwater();
    }

    private void moveInLiquid(LivingEntity entity, Vec3 movementVector, double gravity) {
        double startY = entity.getY();
        float horizontalRetention = physics.horizontalRetention();
        float acceleration = (float) WATER_ACCELERATION * physics.acceleration();
        if (physics.usesSwimAids()) {
            if (entity.isSprinting()) {
                horizontalRetention = SPRINTING_RETENTION;
            }
            float efficiency = (float) entity.getAttributeValue(Attributes.WATER_MOVEMENT_EFFICIENCY);
            if (!entity.onGround()) {
                efficiency *= 0.5F;
            }
            if (efficiency > 0.0F) {
                horizontalRetention += (DEPTH_STRIDER_RETENTION - horizontalRetention) * efficiency;
                acceleration += (entity.getSpeed() - acceleration) * efficiency;
            }
            if (entity.hasEffect(MobEffects.DOLPHINS_GRACE)) {
                horizontalRetention = DOLPHINS_GRACE_RETENTION;
            }
        }
        acceleration *= (float) entity.getAttributeValue(NeoForgeMod.SWIM_SPEED);

        entity.moveRelative(acceleration, movementVector);
        entity.move(MoverType.SELF, entity.getDeltaMovement());
        Vec3 motion = entity.getDeltaMovement();
        if (entity.horizontalCollision && entity.onClimbable()) {
            motion = new Vec3(motion.x, CLIMBING_SPEED, motion.z);
        }
        double vertical = motion.y * physics.verticalRetention() + buoyantAcceleration(entity, gravity);
        motion = new Vec3(motion.x * horizontalRetention, vertical, motion.z * horizontalRetention);
        entity.setDeltaMovement(motion);
        if (entity.horizontalCollision && entity.isFree(motion.x, motion.y + STEP_UP_HEIGHT - entity.getY() + startY, motion.z)) {
            entity.setDeltaMovement(motion.x, STEP_UP_SPEED, motion.z);
        }
    }

    /**
     * Mirrors vanilla air travel, because any non-air fluid makes vanilla treat the entity as swimming.
     * NeoForge's fluid jump impulse is undone here: on the ground it becomes a real jump, in mid-air it
     * is removed, detected against the vertical speed this method left on the previous tick.
     */
    private void moveInGas(FluidState state, LivingEntity entity, Vec3 movementVector, double gravity) {
        Level level = entity.level();
        removeFluidJumpImpulse(entity, gravity);

        BlockPos belowPos = entity.getBlockPosBelowThatAffectsMyMovement();
        float friction = level.getBlockState(belowPos).getFriction(level, belowPos, entity);
        float drag = (entity.onGround() ? friction * AIR_DRAG : AIR_DRAG) * physics.horizontalRetention();
        Vec3 motion = entity.handleRelativeFrictionAndCalculateMovement(movementVector, friction);

        double vertical = motion.y;
        MobEffectInstance levitation = entity.getEffect(MobEffects.LEVITATION);
        if (levitation != null) {
            vertical += (LEVITATION_SPEED * (levitation.getAmplifier() + 1) - vertical) * LEVITATION_APPROACH;
        } else if (isRisingGas(state, level, entity.blockPosition()) && !entity.isShiftKeyDown() && vertical < GAS_MAX_RISE_SPEED) {
            vertical = Math.min(vertical + GAS_LIFT, GAS_MAX_RISE_SPEED);
        } else {
            vertical -= gravity;
        }
        vertical *= AIR_VERTICAL_DRAG;

        Vec3 flowPush = state.getFlow(level, entity.blockPosition()).multiply(GAS_FLOW_PUSH, 0.0D, GAS_FLOW_PUSH);
        entity.setDeltaMovement(motion.x * drag + flowPush.x, vertical, motion.z * drag + flowPush.z);
        entity.setData(VEAttachments.GAS_VERTICAL_SPEED, vertical);
    }

    private static void removeFluidJumpImpulse(LivingEntity entity, double gravity) {
        Vec3 motion = entity.getDeltaMovement();
        double previous = entity.onGround() ? -gravity * AIR_VERTICAL_DRAG : entity.getData(VEAttachments.GAS_VERTICAL_SPEED);
        if (Math.abs(previous) < MOTION_ZEROING_THRESHOLD) {
            previous = 0.0D;
        }
        if (Double.isNaN(previous) || Math.abs(motion.y - previous - FLUID_JUMP_IMPULSE) > JUMP_IMPULSE_TOLERANCE) {
            return;
        }
        entity.setDeltaMovement(motion.x, previous, motion.z);
        if (entity.onGround()) {
            entity.jumpFromGround();
        }
    }

    /**
     * Whether the gas at {@code pos} is part of a rising column: flowing gas fed from the same gas below,
     * or any gas, including the source at the column's base, feeding flowing gas above it.
     */
    private static boolean isRisingGas(FluidState state, Level level, BlockPos pos) {
        FluidState above = level.getFluidState(pos.above());
        boolean feedsColumnAbove = !above.isSource() && above.getType().isSame(state.getType());
        boolean fedFromBelow = !state.isSource() && level.getFluidState(pos.below()).getType().isSame(state.getType());
        return feedsColumnAbove || fedFromBelow;
    }

    private double buoyantAcceleration(Entity entity, double gravity) {
        double submerged = Math.min(entity.getFluidTypeHeight(this) / Math.max(entity.getBbHeight(), 1.0E-3F), 1.0D);
        double acceleration = gravity * FLUID_GRAVITY_SCALE * physics.buoyancyScale() * (getDensity() * submerged / BODY_DENSITY - 1.0D);
        return Mth.clamp(acceleration, -MAX_BUOYANT_ACCELERATION, MAX_BUOYANT_ACCELERATION);
    }
}
