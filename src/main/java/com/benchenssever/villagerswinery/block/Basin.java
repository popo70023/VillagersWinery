package com.benchenssever.villagerswinery.block;

import com.benchenssever.villagerswinery.tileentity.BasinTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Basin extends Block {
    private static final VoxelShape INSIDE = makeCuboidShape(2.0D, 3.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    protected static final VoxelShape SHAPE = VoxelShapes.combineAndSimplify(makeCuboidShape(0,0,0,16,14,16), VoxelShapes.or(INSIDE,makeCuboidShape(2,0,0,14,2,16),makeCuboidShape(0,0,2,16,2,14)), IBooleanFunction.ONLY_FIRST);

    public Basin(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) { return true; }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new BasinTileEntity();
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return SHAPE;
    }
}
