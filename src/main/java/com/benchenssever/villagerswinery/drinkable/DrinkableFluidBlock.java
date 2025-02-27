package com.benchenssever.villagerswinery.drinkable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class DrinkableFluidBlock extends FlowingFluidBlock {
    final private static Direction[] searchOrder = {Direction.UP, Direction.NORTH, Direction.EAST, Direction.WEST, Direction.SOUTH};
    public final Drinks drinks;

    public DrinkableFluidBlock(Supplier<? extends FlowingFluid> supplier, Properties properties, Drinks drinks) {
        super(supplier, properties);
        this.drinks = drinks;
    }

    @Override
    public void onEntityCollision(@NotNull BlockState state, World worldIn, @NotNull BlockPos pos, @NotNull Entity entityIn) {
        if (worldIn.isRemote || !(drinks.getFood() != null && entityIn instanceof LivingEntity)) {
            return;
        }
        BlockPos sourceDrinkPos = backtraceSource(state, worldIn, pos);
        if (sourceDrinkPos == null) return;
        LivingEntity entityLiving = (LivingEntity) entityIn;

        if (Drinks.isCanConsumed(entityLiving, (IDrinkable) drinks.getFluid())) {
            Drinks.onDrinkConsumed(entityLiving, (IDrinkable) drinks.getFluid());
            worldIn.setBlockState(sourceDrinkPos, Blocks.AIR.getDefaultState());
        }
    }

    private BlockPos backtraceSource(BlockState current, World worldIn, BlockPos pos) {
        if (current.getFluidState().isSource()) {
            return pos;
        }
        while (true) {
            BlockPos lastPos = pos;
            for (Direction direction : searchOrder) {
                BlockPos offset = pos.offset(direction);
                BlockState nextBlockState = worldIn.getBlockState(offset);
                if (nextBlockState.getBlock() == this) {
                    if (nextBlockState.getFluidState().isSource()) {
                        return offset;
                    }
                    if (direction == Direction.UP) {
                        current = nextBlockState;
                        pos = offset;
                        break;
                    }
                    if (nextBlockState.getFluidState().getLevel() > current.getFluidState().getLevel()) {
                        current = nextBlockState;
                        pos = offset;
                        break;
                    }
                }
            }
            if (lastPos.equals(pos)) {
                return null;
            } else {
                lastPos = pos;
            }
        }

    }
}
