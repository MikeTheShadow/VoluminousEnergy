package com.veteam.voluminousenergy.util;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockStateMatchTest;

import java.util.ArrayList;
import java.util.Random;

public class MultiBlockStateMatchRuleTest extends BlockStateMatchTest {
    private final BlockState[] stateList;

    public MultiBlockStateMatchRuleTest(BlockState... states) {
        super(states[0]);
        this.stateList = states;
    }

    public MultiBlockStateMatchRuleTest(ArrayList<BlockState> stateList){
        super(stateList.get(0));
        this.stateList = new BlockState[stateList.size()];
        for (int i = 0; i < stateList.size(); i++){
            this.stateList[i] = stateList.get(i);
        }
    }

    @Override
    public boolean test(BlockState blockState, RandomSource random) {
        for (BlockState state : stateList){
            if (state.getBlock() == blockState.getBlock()){
                return true;
            }
        }
        return false;
    }
}
