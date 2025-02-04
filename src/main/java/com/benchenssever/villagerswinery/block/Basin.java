package com.benchenssever.villagerswinery.block;

import com.benchenssever.villagerswinery.fluid.FluidTransferUtil;
import com.benchenssever.villagerswinery.tileentity.BasinTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
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
    private static final VoxelShape INSIDE = makeCuboidShape(2.0D, 3.0D, 2.0D, 14.0D, 16.0D, 14.0D);
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
        if (isClickInsideBottom(hit.getHitVec(), pos)) {
            if (!FluidTransferUtil.interactWithTank(worldIn, pos, player, handIn, hit)) {
                if (!worldIn.isRemote) {
                    insertOrRextractItem(worldIn, pos, player, handIn);
                }
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    private boolean isClickInsideBottom(Vector3d hitPos, BlockPos basinPos) {
        double localX = hitPos.x - basinPos.getX();
        double localY = hitPos.y - basinPos.getY();
        double localZ = hitPos.z - basinPos.getZ();

        boolean insideX = localX >= 2.0 / 16.0 && localX <= 14.0 / 16.0;
        boolean insideZ = localZ >= 2.0 / 16.0 && localZ <= 14.0 / 16.0;

        boolean isBottom = localY >= 2.0 / 16.0 && localY <= 14.0 / 16.0;

        return insideX && insideZ && isBottom;
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

    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState state, @NotNull IBlockReader worldIn, @NotNull BlockPos pos, @NotNull ISelectionContext context) {
        return SHAPE;
    }
}
