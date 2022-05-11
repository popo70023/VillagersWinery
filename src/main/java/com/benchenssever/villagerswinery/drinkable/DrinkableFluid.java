package com.benchenssever.villagerswinery.drinkable;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.StateContainer;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public abstract class DrinkableFluid extends ForgeFlowingFluid {
    public Drinks drinks;

    protected DrinkableFluid(Properties properties,Drinks drinks) {
        super(properties);
        this.drinks = drinks;
    }

    public static class Flowing extends DrinkableFluid
    {
        public Flowing(Properties properties,Drinks drinks)
        {
            super(properties,drinks);
            setDefaultState(getStateContainer().getBaseState().with(LEVEL_1_8, 7));
        }

        protected void fillStateContainer(StateContainer.Builder<Fluid, FluidState> builder) {
            super.fillStateContainer(builder);
            builder.add(LEVEL_1_8);
        }

        public int getLevel(FluidState state) {
            return state.get(LEVEL_1_8);
        }

        public boolean isSource(FluidState state) {
            return false;
        }
    }

    public static class Source extends DrinkableFluid
    {
        public Source(Properties properties,Drinks drinks)
        {
            super(properties,drinks);
        }

        public int getLevel(FluidState state) {
            return 8;
        }

        public boolean isSource(FluidState state) {
            return true;
        }
    }
}

