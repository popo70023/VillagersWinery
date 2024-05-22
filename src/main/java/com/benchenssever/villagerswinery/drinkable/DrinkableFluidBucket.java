package com.benchenssever.villagerswinery.drinkable;

import com.benchenssever.villagerswinery.VillagersWineryMod;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
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
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("item."+VillagersWineryMod.MODID+"."+drinks.id+".information"));
    }

    @Override
    public ITextComponent getDisplayName(@Nonnull ItemStack stack) {
        return new TranslationTextComponent("item."+VillagersWineryMod.MODID+".bucket", new TranslationTextComponent(this.getFluid().getAttributes().getTranslationKey()));
    }
}
