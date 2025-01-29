package com.benchenssever.villagerswinery.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IOnStand {
    Item itemOnStand();

    boolean putOnStand(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, ItemStack stack);
}
