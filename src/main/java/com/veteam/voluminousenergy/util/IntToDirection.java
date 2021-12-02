package com.veteam.voluminousenergy.util;

import net.minecraft.core.Direction;

public class IntToDirection {

    private IntToDirection() {
        throw new IllegalAccessError("Utility class");
    }

    // 3D Int value to direction object
    public static Direction IntegerToDirection(int direction){
        if(direction == 0){
            return Direction.DOWN;
        } else if (direction == 1){
            return Direction.UP;
        } else if (direction == 2){
            return Direction.NORTH;
        } else if (direction == 3){
            return Direction.SOUTH;
        } else if (direction == 4){
            return Direction.WEST;
        } else if (direction == 5){
            return Direction.EAST;
        }
        throw new IndexOutOfBoundsException("Directions can only be from 0 to 5. " + direction + ", was passed into IntegerToDirection instead.");
    }
}
