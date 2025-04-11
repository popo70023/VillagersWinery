package com.benchenssever.villagerswinery.content.equipment;

import com.benchenssever.villagerswinery.content.capability.FluidUtils;
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

public class LiquidBarrelBlock extends HorizontalBlock {
    public static final BooleanProperty VERTICAL = BooleanProperty.create("vertical");

    public LiquidBarrelBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(VERTICAL, false));
    }

    public static Direction getLiquidBarrelDirection(BlockState state) {
        return state.getValue(VERTICAL) ? Direction.UP : state.getValue(FACING);
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
    public @NotNull ActionResultType use(@NotNull BlockState state, @NotNull World world, @NotNull BlockPos pos, @NotNull PlayerEntity player, @NotNull Hand hand, BlockRayTraceResult hit) {
        if (hit.getDirection() == getLiquidBarrelDirection(state)) {
            if (!FluidUtils.interactWithTank(world, pos, player, hand, hit) && hand == Hand.MAIN_HAND) {
                world.playSound(player, pos, SoundEvents.BARREL_OPEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
                if (!world.isClientSide) {
                    LiquidBarrelTileEntity tileentity = (LiquidBarrelTileEntity) world.getBlockEntity(pos);
                    if (tileentity != null) {
                        NetworkHooks.openGui((ServerPlayerEntity) player, tileentity, (packerBuffer) -> {
                            tileentity.getTank().getFluid().writeToPacket(packerBuffer);
                            packerBuffer.writeUtf(tileentity.getWorldAndPos());
                        });
                    }
                }
            }
            return ActionResultType.SUCCESS;
        }
        return super.use(state, world, pos, player, hand, hit);
    }

    @Override
    public void setPlacedBy(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull BlockState state, LivingEntity placer, @NotNull ItemStack stack) {
        TileEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof LiquidBarrelTileEntity) {
            LiquidBarrelTileEntity barrelTile = (LiquidBarrelTileEntity) tileentity;
            barrelTile.refreshRecipe();
            if (stack.hasCustomHoverName()) {
                barrelTile.setCustomName(stack.getDisplayName());
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(VERTICAL, context.getNearestLookingDirection().getOpposite() == Direction.UP);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, VERTICAL);
    }
}
