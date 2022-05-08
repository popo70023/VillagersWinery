package com.benchenssever.villagerswinery.tileentity;

import com.benchenssever.villagerswinery.registration.RegistryEvents;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
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

public class LiquidBarrelTileEntity extends TileEntity {

    public static final int DEFAULT_CAPACITY = FluidAttributes.BUCKET_VOLUME * 8;

    private final FluidTank tank;
    private final LazyOptional<IFluidHandler> holder;

    public LiquidBarrelTileEntity() {
        super(RegistryEvents.liquidBarrelTileEntity.get());
        this.tank = new FluidTank(DEFAULT_CAPACITY, (F)->F.getFluid().getAttributes().getTemperature() < 500);
        holder = LazyOptional.of(() -> tank);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return holder.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();
        holder.invalidate();
    }

    public void updateTank(CompoundNBT nbt) {
        if (nbt.isEmpty()) {
            tank.setFluid(FluidStack.EMPTY);
        } else {
            tank.readFromNBT(nbt);
            if (world != null) {
                world.getLightManager().checkBlock(pos);
            }
        }
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        tank.setCapacity(DEFAULT_CAPACITY);
        updateTank(nbt.getCompound("tank"));
        super.read(state, nbt);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound = super.write(compound);

        if (!tank.isEmpty()) {
            compound.put("tank", tank.writeToNBT(new CompoundNBT()));
        }

        return compound;
    }
}
