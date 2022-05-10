package com.benchenssever.villagerswinery.block;

import com.benchenssever.villagerswinery.fluid.FluidTransferUtil;
import com.benchenssever.villagerswinery.tileentity.LiquidBarrelTileEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class LiquidBarrel extends HorizontalBlock {
    public static final BooleanProperty VERTICAL = BooleanProperty.create("vertical");

    public LiquidBarrel(AbstractBlock.Properties properties) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, Direction.NORTH).with(VERTICAL, false));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new LiquidBarrelTileEntity();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if((state.get(VERTICAL) && hit.getFace() == Direction.UP) || state.get(HORIZONTAL_FACING) == hit.getFace()) {
            if (FluidTransferUtil.interactWithTank(world, pos, player, hand, hit)) {
                return ActionResultType.SUCCESS;
            }
        }
        return super.onBlockActivated(state, world, pos, player, hand, hit);
    }

    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite()).with(VERTICAL, context.getNearestLookingDirection().getOpposite() == Direction.UP);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(HORIZONTAL_FACING, VERTICAL);
    }
}
