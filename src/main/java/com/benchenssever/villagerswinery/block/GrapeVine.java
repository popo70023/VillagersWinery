package com.benchenssever.villagerswinery.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.block.VineBlock;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class GrapeVine extends VineBlock implements IGrowable, ICrop {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_0_7;

    public GrapeVine(Properties properties) {
        super(properties);
        this.setDefaultState(this.getDefaultState().with(AGE, 0));
    }

    @Override
    public IntegerProperty getAgeProperty() { return AGE; }

    @Override
    public int getMaxAge() { return 7; }

    @Override
    public float getGrowthChance(Block blockIn, IBlockReader worldIn, BlockPos pos) { return 1.0F; }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        super.randomTick(state, worldIn, pos, random);
        this.growth(state, worldIn, pos, random);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(AGE);
    }

    @Override
    public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) { return !this.isMaxAge(state); }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
        int age = this.getAge(state) + MathHelper.nextInt(worldIn.rand, 2, 5);
        if (age > this.getMaxAge()) {age = this.getMaxAge();}
        worldIn.setBlockState(pos, this.withAge(state, age), 2);
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockState ground = worldIn.getBlockState(pos.down());
        return super.isValidPosition(state, worldIn, pos) && ICrop.isDirtGround(ground);
    }
}