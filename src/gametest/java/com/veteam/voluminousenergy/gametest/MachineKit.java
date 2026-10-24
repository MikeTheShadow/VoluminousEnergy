package com.veteam.voluminousenergy.gametest;

import com.veteam.voluminousenergy.blocks.tiles.VETileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

/**
 * Helpers shared by the machine gametests: placing machines, staging their tanks the way the GUI
 * and processors do, and reaching their capabilities.
 */
public class MachineKit {

    private MachineKit() {
    }

    public static VETileEntity place(GameTestHelper helper, BlockPos pos, Block block) {
        helper.setBlock(pos, block.defaultBlockState());
        return helper.getBlockEntity(pos, VETileEntity.class);
    }

    /**
     * The fluid handler a pipe on the given face of the machine would see.
     */
    public static IFluidHandler fluids(GameTestHelper helper, BlockPos pos, Direction side) {
        ResourceHandler<FluidResource> handler = helper.getLevel().getCapability(Capabilities.Fluid.BLOCK, helper.absolutePos(pos), side);
        helper.assertTrue(handler != null, "no fluid capability on the " + side + " face");
        return IFluidHandler.of(handler);
    }

    /**
     * Sets a tank's side status, as the side button in the machine's GUI does.
     */
    public static void setTankSide(VETileEntity tile, int tank, boolean enabled) {
        tile.updateTankPacketFromGui(enabled, tank);
    }

    public static void setTank(VETileEntity tile, int tank, FluidStack contents) {
        tile.getRelationalTank(tank).getTank().setFluid(contents);
    }
}
