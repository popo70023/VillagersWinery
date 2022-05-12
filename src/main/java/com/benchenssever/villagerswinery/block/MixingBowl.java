package com.benchenssever.villagerswinery.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class MixingBowl extends Block {
    private static final VoxelShape INSIDE = makeCuboidShape(2.0D, 4.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    protected static final VoxelShape SHAPE = VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), VoxelShapes.or(INSIDE), IBooleanFunction.ONLY_FIRST);

    public MixingBowl(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        VoxelShape INSIDE = makeCuboidShape(2.0D, 3.0D, 2.0D, 14.0D, 16.0D, 14.0D);
        VoxelShape SHAPE = VoxelShapes.combineAndSimplify(makeCuboidShape(0,0,0,16,14,16), VoxelShapes.or(INSIDE,makeCuboidShape(2,0,0,14,2,16),makeCuboidShape(0,0,2,16,2,14)), IBooleanFunction.ONLY_FIRST);
        return SHAPE;
    }
}
