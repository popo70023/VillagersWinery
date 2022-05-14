package com.benchenssever.villagerswinery.block;

import com.benchenssever.villagerswinery.registration.RegistryEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BushBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class Stand extends BushBlock {

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
    protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
        System.out.println(worldIn.getBlockState(pos.up()));
        return super.isValidGround(state, worldIn, pos)  || state.getBlock() instanceof Stand ||
            this.chickDistance((World) worldIn, pos.up()) < 8;
    }

    public BlockState getStateForPlacement(BlockItemUseContext context) {
        int i = this.chickDistance(context.getWorld(), context.getPos());
        i = i < 7 ? i + 1 : 7;
        return this.getDefaultState().with(DISTANCE, i);
    }

    private int chickDistance(World worldIn, BlockPos pos) {
        int distance = Integer.MAX_VALUE;
        if(worldIn.getBlockState(pos.down()).getBlock() == Blocks.AIR) {
            for(Direction direction: HORIZONTAL_DIRECTION) {
                BlockState checkBlockState = worldIn.getBlockState(pos.offset(direction));
                if(checkBlockState.getBlock() instanceof Stand && checkBlockState.get(DISTANCE) < distance) {
                    distance = checkBlockState.get(DISTANCE);
                }
            }
        } else {distance = 0;}
        return distance;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(DISTANCE);
    }
}
