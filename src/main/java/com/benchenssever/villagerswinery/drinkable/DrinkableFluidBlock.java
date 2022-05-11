package com.benchenssever.villagerswinery.drinkable;

import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;

import java.util.function.Supplier;

public class DrinkableFluidBlock extends FlowingFluidBlock {
    public final Drinks drinks;
    public DrinkableFluidBlock(Supplier<? extends FlowingFluid> supplier, Properties properties,Drinks drinks) {
        super(supplier, properties);
        this.drinks = drinks;
    }
}
