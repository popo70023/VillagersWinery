package com.benchenssever.villagerswinery.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class Stand extends BushBlock {
    public static final List<IOnStand> listOnStandBlock = new ArrayList<>();

    private static final VoxelShape STAND_SHAPE = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public Stand(Properties properties) {
        super(properties);
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) { return STAND_SHAPE; }

    @Nonnull
    @Override
    public ActionResultType onBlockActivated(@Nonnull BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand handIn, @Nonnull BlockRayTraceResult hit) {
        ItemStack stack = player.getHeldItem(handIn);
        IOnStand theStandBlock = null;
        for(IOnStand onStandBlock : listOnStandBlock) {
            if(onStandBlock.itemOnStand() == stack.getItem()) {
                theStandBlock = onStandBlock;
                break;
            }
        }

        if(theStandBlock != null && theStandBlock.putOnStand(state, worldIn, pos, player, handIn, stack)) { return ActionResultType.SUCCESS; }

        return ActionResultType.PASS;
    }

    @Override
    protected boolean isValidGround(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos) {
        return super.isValidGround(state, worldIn, pos)  || state.getBlock() instanceof Stand;
    }
}
