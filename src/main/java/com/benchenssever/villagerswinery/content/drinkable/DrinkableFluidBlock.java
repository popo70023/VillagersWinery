package com.benchenssever.villagerswinery.content.drinkable;

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
    public final Drinkable drinkable;

    public DrinkableFluidBlock(Supplier<? extends FlowingFluid> supplier, Properties properties, Drinkable drinkable) {
        super(supplier, properties);
        this.drinkable = drinkable;
    }

    @Override
    public void entityInside(@NotNull BlockState state, World worldIn, @NotNull BlockPos pos, @NotNull Entity entityIn) {
        if (worldIn.isClientSide || !(drinkable.getFood() != null && entityIn instanceof LivingEntity)) {
            return;
        }
        BlockPos sourceDrinkPos = backtraceSource(state, worldIn, pos);
        if (sourceDrinkPos == null) return;
        LivingEntity entityLiving = (LivingEntity) entityIn;

        if (Drinkable.isCanConsumed(entityLiving, (IDrinkable) drinkable.getFluid())) {
            Drinkable.onDrinkConsumed(entityLiving, (IDrinkable) drinkable.getFluid());
            worldIn.setBlockAndUpdate(sourceDrinkPos, Blocks.AIR.defaultBlockState());
        }
    }

    private BlockPos backtraceSource(BlockState current, World worldIn, BlockPos pos) {
        if (current.getFluidState().isSource()) {
            return pos;
        }
        while (true) {
            BlockPos lastPos = pos;
            for (Direction direction : searchOrder) {
                BlockPos offset = pos.relative(direction);
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
                    if (nextBlockState.getFluidState().getAmount() > current.getFluidState().getAmount()) {
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
