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
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class LiquidBarrel extends HorizontalBlock {
    public static final BooleanProperty VERTICAL = BooleanProperty.create("vertical");

    public LiquidBarrel(AbstractBlock.Properties properties) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, Direction.NORTH).with(VERTICAL, false));
    }

    public static Direction getLiquidBarrelDirection(BlockState state) {
        return state.get(VERTICAL) ? Direction.UP : state.get(HORIZONTAL_FACING);
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
    public @NotNull ActionResultType onBlockActivated(@NotNull BlockState state, @NotNull World world, @NotNull BlockPos pos, @NotNull PlayerEntity player, @NotNull Hand hand, BlockRayTraceResult hit) {
        if (hit.getFace() == getLiquidBarrelDirection(state)) {
            if (!FluidTransferUtil.interactWithTank(world, pos, player, hand, hit) && hand == Hand.MAIN_HAND) {
                world.playSound(player, pos, SoundEvents.BLOCK_BARREL_OPEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
                if (!world.isRemote) {
                    LiquidBarrelTileEntity tileentity = (LiquidBarrelTileEntity) world.getTileEntity(pos);
                    if (tileentity != null) {
                        NetworkHooks.openGui((ServerPlayerEntity) player, tileentity, (packerBuffer) -> {
                            tileentity.getTank().getFluid().writeToPacket(packerBuffer);
                            packerBuffer.writeString(tileentity.getWorldAndPos());
                        });
                    }
                }
            }
            return ActionResultType.SUCCESS;
        }
        return super.onBlockActivated(state, world, pos, player, hand, hit);
    }

    @Override
    public void onBlockPlacedBy(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull BlockState state, LivingEntity placer, ItemStack stack) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof LiquidBarrelTileEntity) {
            LiquidBarrelTileEntity barrelTile = (LiquidBarrelTileEntity) tileentity;
            barrelTile.refreshRecipe();
            if (stack.hasDisplayName()) {
                barrelTile.setCustomName(stack.getDisplayName());
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite()).with(VERTICAL, context.getNearestLookingDirection().getOpposite() == Direction.UP);
    }

    @Override
    protected void fillStateContainer(StateContainer.@NotNull Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(HORIZONTAL_FACING, VERTICAL);
    }
}
