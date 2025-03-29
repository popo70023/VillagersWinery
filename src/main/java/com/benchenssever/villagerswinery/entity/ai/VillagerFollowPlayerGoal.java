package com.benchenssever.villagerswinery.entity.ai;

import com.benchenssever.villagerswinery.drinkable.IDrinkable;
import com.benchenssever.villagerswinery.fluid.ItemStackFluidHandler;
import com.benchenssever.villagerswinery.item.LiquidBarrelItem;
import com.benchenssever.villagerswinery.item.Winebowl;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class VillagerFollowPlayerGoal extends TemptGoal {

    public VillagerFollowPlayerGoal(CreatureEntity mob, double speedModifier, boolean canScare) {
        super(mob, speedModifier, canScare, Ingredient.EMPTY);
    }

    @Override
    protected boolean shouldFollowItem(@NotNull ItemStack heldItem) {
        Brain<?> brain = mob.getBrain();
        if(brain.isActive(Activity.REST) || brain.isActive(Activity.PANIC) || brain.isActive(Activity.HIDE)) {
            return false;
        }

        if (heldItem.getItem() instanceof LiquidBarrelItem || heldItem.getItem() instanceof Winebowl) {
            FluidStack fluidInside = ItemStackFluidHandler.getFluid(heldItem);
            if (fluidInside.getFluid() instanceof IDrinkable) {
                return preferenceByAge((IDrinkable) fluidInside.getFluid(), mob);
            }
        }
        return false;
    }

    private boolean preferenceByAge(IDrinkable drinkable, LivingEntity entity) {
        return drinkable.isForDrink() && entity.isBaby() != drinkable.isAlcohol();
    }
}
