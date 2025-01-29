package com.benchenssever.villagerswinery.drinkable;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.StateContainer;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.jetbrains.annotations.NotNull;

public abstract class DrinkableFluid extends ForgeFlowingFluid {
    public Drinks drinks;

    protected DrinkableFluid(Properties properties, Drinks drinks) {
        super(properties);
        this.drinks = drinks;
    }

    public static class Flowing extends DrinkableFluid {
        public Flowing(Properties properties, Drinks drinks) {
            super(properties, drinks);
            setDefaultState(getStateContainer().getBaseState().with(LEVEL_1_8, 7));
        }

        protected void fillStateContainer(StateContainer.@NotNull Builder<Fluid, FluidState> builder) {
            super.fillStateContainer(builder);
            builder.add(LEVEL_1_8);
        }

        public int getLevel(FluidState state) {
            return state.get(LEVEL_1_8);
        }

        public boolean isSource(@NotNull FluidState state) {
            return false;
        }
    }

    public static class Source extends DrinkableFluid {
        public Source(Properties properties, Drinks drinks) {
            super(properties, drinks);
        }

        public int getLevel(@NotNull FluidState state) {
            return 8;
        }

        public boolean isSource(@NotNull FluidState state) {
            return true;
        }
    }
}

