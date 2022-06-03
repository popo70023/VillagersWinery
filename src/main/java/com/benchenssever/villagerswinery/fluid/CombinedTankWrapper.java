package com.benchenssever.villagerswinery.fluid;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.wrapper.EmptyHandler;

import javax.annotation.Nonnull;

public class CombinedTankWrapper implements IFluidHandler {

    protected final IFluidHandler[] fluidHandler; // the handlers
    protected final int[] baseIndex; // index-offsets of the different handlers
    protected final int slotCount; // number of total slots
    public CombinedTankWrapper(IFluidHandler... fluidHandler) {
        this.fluidHandler = fluidHandler;
        this.baseIndex = new int[fluidHandler.length];
        int index = 0;
        for (int i = 0; i < fluidHandler.length; i++)
        {
            index += fluidHandler[i].getTanks();
            baseIndex[i] = index;
        }
        this.slotCount = index;
    }

    protected int getIndexForSlot(int slot) {
        if (slot < 0) return -1;

        for (int i = 0; i < baseIndex.length; i++) {
            if (slot - baseIndex[i] < 0) { return i; }
        }
        return -1;
    }

    protected IFluidHandler getHandlerFromIndex(int index) {
        if (index < 0 || index >= fluidHandler.length) { return (FluidTank) EmptyHandler.INSTANCE; }
        return fluidHandler[index];
    }

    protected int getSlotFromIndex(int slot, int index) {
        if (index <= 0 || index >= baseIndex.length) {
            return slot;
        }
        return slot - baseIndex[index - 1];
    }

    @Override
    public int getTanks() {
        return slotCount;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        int index = getIndexForSlot(tank);
        IFluidHandler handler = getHandlerFromIndex(index);
        tank = getSlotFromIndex(tank, index);
        return handler.getFluidInTank(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        int index = getIndexForSlot(tank);
        IFluidHandler handler = getHandlerFromIndex(index);
        int localSlot = getSlotFromIndex(tank, index);
        return handler.getTankCapacity(localSlot);
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        int index = getIndexForSlot(tank);
        IFluidHandler handler = getHandlerFromIndex(index);
        int localSlot = getSlotFromIndex(tank, index);
        return handler.isFluidValid(localSlot, stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty())
            return 0;

        int filled = 0;
        resource = resource.copy();

        boolean fittingHandlerFound = false;
        boolean[] trueAndFalse = { true, false };
        Outer: for (boolean searchPass : trueAndFalse) {
            for (IFluidHandler iFluidHandler : fluidHandler) {

                for (int i = 0; i < iFluidHandler.getTanks(); i++)
                    if (searchPass && iFluidHandler.getFluidInTank(i)
                            .isFluidEqual(resource))
                        fittingHandlerFound = true;

                if (searchPass && !fittingHandlerFound)
                    continue;

                int filledIntoCurrent = iFluidHandler.fill(resource, action);
                resource.shrink(filledIntoCurrent);
                filled += filledIntoCurrent;

                if (resource.isEmpty() || fittingHandlerFound || filledIntoCurrent != 0)
                    break Outer;
            }
        }

        return filled;
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty())
            return resource;

        FluidStack drained = FluidStack.EMPTY;
        resource = resource.copy();

        for (IFluidHandler iFluidHandler : fluidHandler) {
            FluidStack drainedFromCurrent = iFluidHandler.drain(resource, action);
            int amount = drainedFromCurrent.getAmount();
            resource.shrink(amount);

            if (!drainedFromCurrent.isEmpty() && (drained.isEmpty() || drainedFromCurrent.isFluidEqual(drained)))
                drained = new FluidStack(drainedFromCurrent.getFluid(), amount + drained.getAmount(),
                        drainedFromCurrent.getTag());
            if (resource.isEmpty())
                break;
        }

        return drained;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        FluidStack drained = FluidStack.EMPTY;

        for (IFluidHandler iFluidHandler : fluidHandler) {
            FluidStack drainedFromCurrent = iFluidHandler.drain(maxDrain, action);
            int amount = drainedFromCurrent.getAmount();
            maxDrain -= amount;

            if (!drainedFromCurrent.isEmpty() && (drained.isEmpty() || drainedFromCurrent.isFluidEqual(drained)))
                drained = new FluidStack(drainedFromCurrent.getFluid(), amount + drained.getAmount(),
                        drainedFromCurrent.getTag());
            if (maxDrain == 0)
                break;
        }

        return drained;
    }
}
