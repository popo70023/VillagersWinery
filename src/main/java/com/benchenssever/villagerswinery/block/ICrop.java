package com.benchenssever.villagerswinery.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public interface ICrop {
    IntegerProperty getAgeProperty();

    int getMaxAge();

    default int getAge(BlockState state){return state.get(this.getAgeProperty());}

    default BlockState withAge(BlockState state, int age) {return state.with(this.getAgeProperty(), age);}

    default boolean isMaxAge(BlockState state){return this.getAge(state) >= this.getMaxAge();}

    default void growth(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        if (!worldIn.isAreaLoaded(pos, 1))
            return;
        if (worldIn.getLightSubtracted(pos, 0) >= 9) {
            int i = this.getAge(state);
            if (i < this.getMaxAge()) {
                float f = this.getGrowthChance((Block)this, worldIn, pos);
                if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, random.nextInt((int) (25.0F / f) + 1) == 0)) {
                    worldIn.setBlockState(pos, this.withAge(state, i + 1), 2);
                    net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state);
                }
            }
        }
    }

    float getGrowthChance(Block blockIn, IBlockReader worldIn, BlockPos pos);

    static boolean isDirtGround(BlockState state) {
        return state.matchesBlock(Blocks.GRASS_BLOCK) || state.matchesBlock(Blocks.DIRT) || state.matchesBlock(Blocks.COARSE_DIRT) || state.matchesBlock(Blocks.PODZOL) || state.matchesBlock(Blocks.FARMLAND);
    }
}
