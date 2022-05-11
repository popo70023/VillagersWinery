package com.benchenssever.villagerswinery.drinkable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class DrinkableFluidBucket extends BucketItem {
    public final Drinks drinks;

    public DrinkableFluidBucket(Supplier<? extends Fluid> supplier, Properties builder,Drinks drinks) {
        super(supplier, builder);
        this.drinks = drinks;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        return super.getDisplayName(stack);
    }
}
