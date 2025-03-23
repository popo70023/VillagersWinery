package com.benchenssever.villagerswinery.drinkable;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Food;
import net.minecraft.state.StateContainer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.jetbrains.annotations.NotNull;

public abstract class DrinkableFluid extends ForgeFlowingFluid implements IDrinkable {
    public Drinks drinks;

    protected DrinkableFluid(Properties properties, Drinks drinks) {
        super(properties);
        this.drinks = drinks;
    }

    @Override
    public Food getFood() {
        return drinks.getFood();
    }

    @Override
    public TranslationTextComponent getTooltip() {
        return drinks.getTooltip();
    }

    public static class Flowing extends DrinkableFluid {
        public Flowing(Properties properties, Drinks drinks) {
            super(properties, drinks);
            registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
        }

        protected void createFluidStateDefinition(StateContainer.@NotNull Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        public boolean isSource(@NotNull FluidState state) {
            return false;
        }
    }

    public static class Source extends DrinkableFluid {
        public Source(Properties properties, Drinks drinks) {
            super(properties, drinks);
        }

        public int getAmount(@NotNull FluidState state) {
            return 8;
        }

        public boolean isSource(@NotNull FluidState state) {
            return true;
        }
    }
}

