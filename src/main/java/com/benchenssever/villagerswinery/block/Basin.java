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
    protected static final VoxelShape SHAPE = VoxelShapes.combineAndSimplify(makeCuboidShape(0, 0, 0, 16, 14, 16), VoxelShapes.or(INSIDE, makeCuboidShape(2, 0, 0, 14, 2, 16), makeCuboidShape(0, 0, 2, 16, 2, 14)), IBooleanFunction.ONLY_FIRST);

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
    public @NotNull ActionResultType onBlockActivated(@NotNull BlockState state, @NotNull World worldIn, @NotNull BlockPos pos, @NotNull PlayerEntity player, @NotNull Hand handIn, @NotNull BlockRayTraceResult hit) {
        if (INSIDE_AABB.contains(hit.getHitVec().subtract(pos.getX(), pos.getY(), pos.getZ()))) {
            if (!FluidTransferUtil.interactWithTank(worldIn, pos, player, handIn, hit)) {
                if (!worldIn.isRemote) {
                    insertOrRextractItem(worldIn, pos, player, handIn);
                }
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    private boolean insertOrRextractItem(World world, BlockPos pos, PlayerEntity player, Hand handIn) {
        ItemStack heldItem = player.getHeldItem(handIn);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof BasinTileEntity) {
            BasinTileEntity basin = (BasinTileEntity) tile;

            if (!heldItem.isEmpty() && basin.isItemCanInsert(heldItem)) {
                ItemStack remaining = basin.insertItem(heldItem);
                player.setHeldItem(handIn, remaining);
                return true;
            }

            if (heldItem.isEmpty() && !basin.isItemEmpty()) {
                ItemStack remaining = basin.extractItem(64);
                player.setHeldItem(handIn, remaining);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onEntityWalk(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull Entity entityIn) {
        if (!worldIn.isRemote && (entityIn instanceof PlayerEntity || entityIn instanceof VillagerEntity)) {
            Vector3d bottomCenter = getEntityBottomCenter(entityIn, pos);
            if (INSIDE_AABB.contains(bottomCenter)) {
                TileEntity tile = worldIn.getTileEntity(pos);
                if (tile instanceof BasinTileEntity) {
                    BasinTileEntity basin = (BasinTileEntity) tile;
                    basin.basinWalk(entityIn);
                }
            }
        }
        super.onEntityWalk(worldIn, pos, entityIn);
    }

    @Override
    public void onFallenUpon(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull Entity entityIn, float fallDistance) {

        if (!worldIn.isRemote && entityIn instanceof LivingEntity) {
            Vector3d bottomCenter = getEntityBottomCenter(entityIn, pos);
            if (INSIDE_AABB.contains(bottomCenter)) {
                TileEntity tile = worldIn.getTileEntity(pos);
                if (tile instanceof BasinTileEntity) {
                    BasinTileEntity basin = (BasinTileEntity) tile;
                    basin.basinCrush();
                }
            }
        }
        super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
    }

    @Override
    public void onReplaced(@NotNull BlockState state, @NotNull World worldIn, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        if (!worldIn.isRemote && !state.matchesBlock(newState.getBlock())) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof BasinTileEntity) {
                BasinTileEntity basin = (BasinTileEntity) tileentity;
                for (int i = 0; i < basin.getInventorySize(); i++) {
                    InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), basin.getItemStack(i));
                }
                worldIn.removeTileEntity(pos);
            }
        }
        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull IBlockReader worldIn, @NotNull BlockPos pos, @NotNull ISelectionContext context) {
        return SHAPE;
    }

    private static Vector3d getEntityBottomCenter(Entity entityIn, BlockPos pos) {
        AxisAlignedBB boundingBox = entityIn.getBoundingBox();
        Vector3d entitylocal = entityIn.getPositionVec();
        return new Vector3d(entitylocal.getX() - pos.getX(), boundingBox.minY - pos.getY(), entitylocal.getZ() - pos.getZ());
    }
}
