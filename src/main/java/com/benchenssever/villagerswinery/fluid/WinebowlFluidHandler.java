package com.benchenssever.villagerswinery.fluid;

import com.benchenssever.villagerswinery.drinkable.Drinks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

import static com.benchenssever.villagerswinery.registration.DrinksRegistry.drinksCollection;

public class WinebowlFluidHandler extends FluidHandlerItemStack {
    /**
     * @param container The container itemStack, data is stored on it directly as NBT.
     * @param capacity  The maximum capacity of this fluid tank.
     */
    public WinebowlFluidHandler(ItemStack container, int capacity) {
        super(container, capacity);
    }

    @Override
    protected void setFluid(FluidStack fluid) {
        super.setFluid(fluid);
        fluid.getFluid().getAttributes();
        for (Drinks drinks : drinksCollection) {
            if (fluid.getFluid().isEquivalentTo(drinks.getFluid())) {
                if (drinks.potion != null) {
                    this.container.getOrCreateTag().putString("Potion", drinks.potion.get().getRegistryName().toString());
                }
                return;
            }
        }
        this.container.removeChildTag("Potion");
    }

    @Override
    protected void setContainerToEmpty() {
        super.setContainerToEmpty();
        this.container.removeChildTag("Potion");
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return stack.getFluid().getAttributes().getTemperature() < 500;
    }

    @Override
    public boolean canDrainFluidType(FluidStack fluid) {
        return this.isFluidValid(0, fluid);
    }

    @Override
    public boolean canFillFluidType(FluidStack fluid) {
        return this.isFluidValid(0, fluid);
    }
}
