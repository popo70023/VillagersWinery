package com.benchenssever.villagerswinery.tileentity;

import com.benchenssever.villagerswinery.registration.RegistryEvents;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LiquidBarrelTileEntity extends TileEntity implements ITickableTileEntity {

    public static final int DEFAULT_CAPACITY = FluidAttributes.BUCKET_VOLUME * 8;

    private final FluidTank tank = new FluidTank(DEFAULT_CAPACITY, (F)->F.getFluid().getAttributes().getTemperature() < 500);;
    private final LazyOptional<IFluidHandler> holder = LazyOptional.of(() -> tank);

    public LiquidBarrelTileEntity() { super(RegistryEvents.liquidBarrelTileEntity.get()); }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        tank.readFromNBT(nbt);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound = super.write(compound);
        tank.writeToNBT(compound);
        return compound;
    }

    @Override
    public void tick() {

    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) { return holder.cast(); }
        return super.getCapability(cap, side);
    }
}
