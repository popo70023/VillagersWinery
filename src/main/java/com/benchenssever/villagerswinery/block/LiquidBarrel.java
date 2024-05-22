package com.benchenssever.villagerswinery.block;

import com.benchenssever.villagerswinery.fluid.FluidTransferUtil;
import com.benchenssever.villagerswinery.tileentity.LiquidBarrelTileEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
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
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new LiquidBarrelTileEntity();
    }

    @Nonnull
    @Override
    public ActionResultType onBlockActivated(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand, BlockRayTraceResult hit) {
        if(hit.getFace() == getLiquidBarrelDirection(state)) {
            if (!FluidTransferUtil.interactWithTank(world, pos, player, hand, hit) && !world.isRemote && hand == Hand.MAIN_HAND) {
                LiquidBarrelTileEntity tileentity = (LiquidBarrelTileEntity)world.getTileEntity(pos);
                if(tileentity != null)
                    NetworkHooks.openGui((ServerPlayerEntity) player, tileentity, (packerBuffer) -> tileentity.getTank().getFluid().writeToPacket(packerBuffer));
            }
            return ActionResultType.SUCCESS;
        }
        return super.onBlockActivated(state, world, pos, player, hand, hit);
    }

    @Override
    public void onBlockPlacedBy(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (stack.hasDisplayName()) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof LiquidBarrelTileEntity) {
                ((LiquidBarrelTileEntity)tileentity).setCustomName(stack.getDisplayName());
            }
        }
    }

    public static Direction getLiquidBarrelDirection(BlockState state) {
        return state.get(VERTICAL)? Direction.UP : state.get(HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite()).with(VERTICAL, context.getNearestLookingDirection().getOpposite() == Direction.UP);
    }

    @Override
    protected void fillStateContainer(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(HORIZONTAL_FACING, VERTICAL);
    }
}
