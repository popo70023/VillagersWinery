package com.benchenssever.villagerswinery.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class GrapeVineStand extends VineStand implements IGrowable, ICrop {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_0_7;

    public GrapeVineStand(Properties properties) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(AGE, 0));
    }

    @Override
    public IntegerProperty getAgeProperty() { return AGE;}

    @Override
    public int getMaxAge() { return 7; }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(AGE);
    }

    @Override
    public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) { return this.isMaxAge(state); }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) { return true; }

    @Override
    public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
        int age = this.getAge(state) + MathHelper.nextInt(worldIn.rand, 2, 5);
        if(age > this.getMaxAge()) age = this.getMaxAge();
        worldIn.setBlockState(pos, this.withAge(state, age), 2);
    }
}
