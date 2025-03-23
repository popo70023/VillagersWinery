package com.benchenssever.villagerswinery.block;

import com.benchenssever.villagerswinery.fluid.FluidTransferUtil;
import com.benchenssever.villagerswinery.tileentity.BasinTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class Basin extends Block {
    public static final AxisAlignedBB INSIDE_AABB = new AxisAlignedBB(2.0 / 16.0, 4.0 / 16.0, 2.0 / 16.0, 14.0 / 16.0, 14.0 / 16.0, 14.0 / 16.0);
    private static final VoxelShape INSIDE = VoxelShapes.create(INSIDE_AABB);
    protected static final VoxelShape SHAPE = VoxelShapes.join(box(0, 0, 0, 16, 14, 16), VoxelShapes.or(INSIDE, box(2, 0, 0, 14, 2, 16), box(0, 0, 2, 16, 2, 14)), IBooleanFunction.ONLY_FIRST);

    public Basin(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new BasinTileEntity();
    }

    @Override
    public @NotNull ActionResultType use(@NotNull BlockState state, @NotNull World worldIn, @NotNull BlockPos pos, @NotNull PlayerEntity player, @NotNull Hand handIn, @NotNull BlockRayTraceResult hit) {
        if (INSIDE_AABB.contains(hit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ()))) {
            if (!FluidTransferUtil.interactWithTank(worldIn, pos, player, handIn, hit)) {
                if (!worldIn.isClientSide) {
                    insertOrRextractItem(worldIn, pos, player, handIn);
                }
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    private boolean insertOrRextractItem(World world, BlockPos pos, PlayerEntity player, Hand handIn) {
        ItemStack heldItem = player.getItemInHand(handIn);
        TileEntity tile = world.getBlockEntity(pos);
        if (tile instanceof BasinTileEntity) {
            BasinTileEntity basin = (BasinTileEntity) tile;

            if (!heldItem.isEmpty() && basin.isItemCanInsert(heldItem)) {
                ItemStack remaining = basin.insertItem(heldItem);
                player.setItemInHand(handIn, remaining);
                return true;
            }

            if (heldItem.isEmpty() && !basin.isItemEmpty()) {
                ItemStack remaining = basin.extractItem(64);
                player.setItemInHand(handIn, remaining);
                return true;
            }
        }
        return false;
    }

    @Override
    public void stepOn(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull Entity entityIn) {
        if (!worldIn.isClientSide && (entityIn instanceof PlayerEntity || entityIn instanceof VillagerEntity)) {
            Vector3d bottomCenter = getEntityBottomCenter(entityIn, pos);
            if (INSIDE_AABB.contains(bottomCenter)) {
                TileEntity tile = worldIn.getBlockEntity(pos);
                if (tile instanceof BasinTileEntity) {
                    BasinTileEntity basin = (BasinTileEntity) tile;
                    basin.basinWalk(entityIn);
                }
            }
        }
        super.stepOn(worldIn, pos, entityIn);
    }

    @Override
    public void fallOn(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull Entity entityIn, float fallDistance) {

        if (!worldIn.isClientSide && entityIn instanceof LivingEntity) {
            Vector3d bottomCenter = getEntityBottomCenter(entityIn, pos);
            if (INSIDE_AABB.contains(bottomCenter)) {
                TileEntity tile = worldIn.getBlockEntity(pos);
                if (tile instanceof BasinTileEntity) {
                    BasinTileEntity basin = (BasinTileEntity) tile;
                    basin.basinCrush();
                }
            }
        }
        super.fallOn(worldIn, pos, entityIn, fallDistance);
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull World worldIn, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        if (!worldIn.isClientSide && !state.is(newState.getBlock())) {
            TileEntity tileentity = worldIn.getBlockEntity(pos);
            if (tileentity instanceof BasinTileEntity) {
                BasinTileEntity basin = (BasinTileEntity) tileentity;
                for (int i = 0; i < basin.getInventorySize(); i++) {
                    InventoryHelper.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), basin.getItemStack(i));
                }
                worldIn.removeBlockEntity(pos);
            }
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull IBlockReader worldIn, @NotNull BlockPos pos, @NotNull ISelectionContext context) {
        return SHAPE;
    }

    private static Vector3d getEntityBottomCenter(Entity entityIn, BlockPos pos) {
        AxisAlignedBB boundingBox = entityIn.getBoundingBox();
        Vector3d entitylocal = entityIn.position();
        return new Vector3d(entitylocal.x() - pos.getX(), boundingBox.minY - pos.getY(), entitylocal.z() - pos.getZ());
    }
}
