package com.benchenssever.villagerswinery.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BushBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class Stand extends BushBlock {
    public static final List<IOnStand> listOnStandBlock = new ArrayList<>();

    private static final VoxelShape STAND_SHAPE = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public static final IntegerProperty DISTANCE = BlockStateProperties.DISTANCE_0_7;

    public static final Direction[] HORIZONTAL_DIRECTION = {Direction.NORTH,Direction.SOUTH,Direction.WEST,Direction.EAST};

    public Stand(Properties properties) {
        super(properties);
        this.setDefaultState(this.getDefaultState().with(DISTANCE, 0));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) { return STAND_SHAPE; }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        for(IOnStand onStandBlock : listOnStandBlock) {
            if(onStandBlock.putOnStand(state, worldIn, pos, player, handIn)) { return ActionResultType.SUCCESS; }
        }

        return ActionResultType.PASS;
    }

//TODO:支架支撐邏輯還不夠好
    @Override
    protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
        int theDistance = this.checkDanglingDistance((World) worldIn, pos.up());
        BlockState upState = worldIn.getBlockState(pos.up());
        if(upState.getBlock() instanceof Stand && theDistance < upState.get(DISTANCE)) {((World) worldIn).setBlockState(pos.up(), upState.with(DISTANCE, theDistance), 3);}
        return state.matchesBlock(Blocks.AIR) ? theDistance < ((upState.getBlock() instanceof Stand) ? upState.get(DISTANCE) + 1 : DISTANCE.getAllowedValues().size()):
                super.isValidGround(state, worldIn, pos)  || state.getBlock() instanceof Stand;
    }

    public BlockState getStateForPlacement(BlockItemUseContext context) {
        int i = this.checkDanglingDistance(context.getWorld(), context.getPos());
        if(i > DISTANCE.getAllowedValues().size() - 1) i = DISTANCE.getAllowedValues().size() - 1;
        return this.getDefaultState().with(DISTANCE, i);
    }

    private int checkDanglingDistance(World worldIn, BlockPos pos) {
        if(worldIn.getBlockState(pos.down()).getBlock() == Blocks.AIR) {
            int distance = Integer.MAX_VALUE;
            for(Direction direction: HORIZONTAL_DIRECTION) {
                BlockState checkBlockState = worldIn.getBlockState(pos.offset(direction));
                if(checkBlockState.getBlock() instanceof Stand && checkBlockState.get(DISTANCE) < distance) {
                    distance = checkBlockState.get(DISTANCE);
                }
            }
            if(distance != Integer.MAX_VALUE)distance++;
            return distance;
        } else { return 0;}
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(DISTANCE);
    }
}
