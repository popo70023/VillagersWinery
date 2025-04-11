package com.benchenssever.villagerswinery.content.drinkable;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Food;
import net.minecraft.state.StateContainer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.jetbrains.annotations.NotNull;

public abstract class DrinkableFluid extends ForgeFlowingFluid implements IDrinkable {
    public Drinkable drinkable;

    protected DrinkableFluid(Properties properties, Drinkable drinkable) {
        super(properties);
        this.drinkable = drinkable;
    }

    @Override
    public Food getFood() {
        return drinkable.getFood();
    }

    @Override
    public TranslationTextComponent getTooltip() {
        return drinkable.getTooltip();
    }

    @Override
    public boolean isAlcohol() {
        return drinkable.isAlcohol();
    }

    @Override
    public boolean isForDrink() {
        return drinkable.isForDrink();
    }

    public static class Flowing extends DrinkableFluid {
        public Flowing(Properties properties, Drinkable drinkable) {
            super(properties, drinkable);
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
        public Source(Properties properties, Drinkable drinkable) {
            super(properties, drinkable);
        }

        public int getAmount(@NotNull FluidState state) {
            return 8;
        }

        public boolean isSource(@NotNull FluidState state) {
            return true;
        }
    }
}

