package com.benchenssever.villagerswinery.tileentity;

import com.benchenssever.villagerswinery.fluid.CombinedTankWrapper;
import com.benchenssever.villagerswinery.registration.RegistryEvents;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BasinTileEntity extends TileEntity implements ITickableTileEntity {

    public static final int DEFAULT_CAPACITY = FluidAttributes.BUCKET_VOLUME;
    public final ItemStackHandler inputInventory = new ItemStackHandler(2);;
    private final ItemStackHandler outputInventory = new ItemStackHandler(2);;
    public final FluidTank inputFluidTank = new FluidTank(DEFAULT_CAPACITY, (F)->F.getFluid().getAttributes().getTemperature() < 500);
    private final FluidTank outputFluidTank = new FluidTank(DEFAULT_CAPACITY, (F)->F.getFluid().getAttributes().getTemperature() < 500);
    private final LazyOptional<IItemHandlerModifiable> itemCapability = LazyOptional.of(() -> new CombinedInvWrapper(this.inputInventory, this.outputInventory));
    private final LazyOptional<IFluidHandler> fluidCapability = LazyOptional.of(() -> new CombinedTankWrapper(this.inputFluidTank, this.outputFluidTank));
    public BasinTileEntity() { super(RegistryEvents.basinTileEntity.get()); }

    @Override
    public void tick() {

    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        return super.write(compound);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) { return itemCapability.cast(); }
        if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) { return fluidCapability.cast(); }
        return super.getCapability(cap, side);
    }
}
