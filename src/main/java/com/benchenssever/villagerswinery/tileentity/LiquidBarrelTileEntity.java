package com.benchenssever.villagerswinery.tileentity;

import com.benchenssever.villagerswinery.VillagersWineryMod;
import com.benchenssever.villagerswinery.fluid.LiquidBarrelContainer;
import com.benchenssever.villagerswinery.registration.RegistryEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LiquidBarrelTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

    public static final int DEFAULT_CAPACITY = FluidAttributes.BUCKET_VOLUME * 8;
    private IIntArray liquidBarrelData = new IntArray(3);

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
        if (!world.isRemote) {
            this.liquidBarrelData.set(0, this.liquidBarrelData.get(0) + 1);
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) { return holder.cast(); }
        return super.getCapability(cap, side);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("gui." + VillagersWineryMod.MODID + ".liquidbarrel");
    }

    @Nullable
    @Override
    public Container createMenu(int sycID, PlayerInventory inventory, PlayerEntity player) {
        return new LiquidBarrelContainer(sycID, inventory, tank, liquidBarrelData);
    }
}
