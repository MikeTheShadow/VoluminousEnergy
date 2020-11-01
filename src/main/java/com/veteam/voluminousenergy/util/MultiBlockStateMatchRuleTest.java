package com.veteam.voluminousenergy.util;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.template.BlockStateMatchRuleTest;

import java.util.Random;

public class MultiBlockStateMatchRuleTest extends BlockStateMatchRuleTest {
    private final BlockState[] stateList;

    public MultiBlockStateMatchRuleTest(BlockState... states) {
        super(states[0]);
        this.stateList = states;
    }

    @Override
    public boolean test(BlockState p_215181_1_, Random p_215181_2_) {
        for (BlockState state : stateList){
            if (state.getBlock() == p_215181_1_.getBlock()){
                return true;
            }
        }
        return false;
    }
}
