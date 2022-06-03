package com.benchenssever.villagerswinery.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IOnStand {
    boolean putOnStand(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn);
}
