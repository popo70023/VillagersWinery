package com.benchenssever.villagerswinery.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public interface ICrop {
    static boolean isDirtGround(BlockState state) {
        return state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT) || state.is(Blocks.COARSE_DIRT) || state.is(Blocks.PODZOL) || state.is(Blocks.FARMLAND);
    }

    IntegerProperty getAgeProperty();

    default int getAge(BlockState state) {
        return state.getValue(this.getAgeProperty());
    }

    default int getMaxAge() {
        return this.getAgeProperty().getPossibleValues().size() - 1;
    }

    default BlockState withAge(BlockState state, int age) {
        return state.setValue(this.getAgeProperty(), age);
    }

    default boolean isMaxAge(BlockState state) {
        return this.getAge(state) >= this.getMaxAge();
    }

    Item getProduct();

    default void growth(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        if (!worldIn.isAreaLoaded(pos, 1))
            return;
        if (worldIn.getRawBrightness(pos, 0) >= 9) {
            int i = this.getAge(state);
            if (i < this.getMaxAge()) {
                float f = this.getGrowthChance((Block) this, worldIn, pos);
                if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, random.nextInt((int) (25.0F / f) + 1) == 0)) {
                    worldIn.setBlock(pos, this.withAge(state, i + 1), 2);
                    net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state);
                }
            }
        }
    }

    float getGrowthChance(Block blockIn, IBlockReader worldIn, BlockPos pos);
}
