package com.benchenssever.villagerswinery.block;

import com.benchenssever.villagerswinery.registration.RegistryEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class Stand extends BushBlock {

    private static final VoxelShape STAND_SHAPE = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public Stand(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(state.matchesBlock(RegistryEvents.stand.get()) && !(worldIn.getBlockState(pos.down()).getBlock() instanceof Stand)) {
            ItemStack stack = player.getHeldItem(handIn);
            if(stack.getItem() == Items.VINE) {
                if(!worldIn.isRemote()) {
                    worldIn.setBlockState(pos, RegistryEvents.vineStand.get().getDefaultState(), 2);
                    if (!player.abilities.isCreativeMode){stack.shrink(1);}
                }
                return ActionResultType.SUCCESS;
            } else if(stack.getItem() == RegistryEvents.grapeVineItem.get()) {
                if(!worldIn.isRemote()) {
                    worldIn.setBlockState(pos, RegistryEvents.grapeVineStand.get().getDefaultState(), 2);
                    if (!player.abilities.isCreativeMode){stack.shrink(1);}
                }
                return ActionResultType.SUCCESS;
            }
        }

        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) { return STAND_SHAPE; }

    @Override
    protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return super.isValidGround(state, worldIn, pos) || state.getBlock() instanceof Stand;
    }
}
