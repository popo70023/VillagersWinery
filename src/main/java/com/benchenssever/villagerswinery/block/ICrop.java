package com.benchenssever.villagerswinery.block;

import net.minecraft.block.BlockState;
import net.minecraft.state.IntegerProperty;

public interface ICrop {
    IntegerProperty getAgeProperty();

    int getMaxAge();

    default int getAge(BlockState state){return state.get(this.getAgeProperty());}

    default BlockState withAge(BlockState state, int age) {return state.with(this.getAgeProperty(), age);}

    default boolean isMaxAge(BlockState state){return this.getAge(state) >= this.getMaxAge();}
}
