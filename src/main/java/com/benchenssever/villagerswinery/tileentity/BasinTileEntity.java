package com.benchenssever.villagerswinery.tileentity;

import com.benchenssever.villagerswinery.registration.RegistryEvents;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BasinTileEntity extends TileEntity implements ITickableTileEntity {

    public static final ModelProperty<FluidStack> FLUID_STACK_MODEL_PROPERTY = new ModelProperty<>();
    public static final int DEFAULT_CAPACITY = FluidAttributes.BUCKET_VOLUME;
    public final ItemStackHandler inputInventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            markDirtyAndUpdate();
        }
    };
    private final LazyOptional<IItemHandlerModifiable> itemHolder = LazyOptional.of(() -> inputInventory);
    private final FluidTank outputFluidTank = new FluidTank(DEFAULT_CAPACITY, (F) -> F.getFluid().getAttributes().getTemperature() < 500) {
        @Override
        protected void onContentsChanged() {
            markDirtyAndUpdate();
        }
    };
    private final LazyOptional<IFluidHandler> fluidHolder = LazyOptional.of(() -> outputFluidTank);

    public BasinTileEntity() {
        super(RegistryEvents.basinTileEntity.get());
    }

    @Override
    public void tick() {

    }

    public ItemStack insertItem(ItemStack stack) {
        return inputInventory.insertItem(0, stack, false);
    }

    public ItemStack extractItem(int amount) {
        return inputInventory.extractItem(0, amount, false);
    }

    public boolean isItemCanInsert(ItemStack stack) {
        for (int i = 0; i < inputInventory.getSlots(); i++) {
            ItemStack remaining = inputInventory.insertItem(i, stack, true);
            if (remaining.isEmpty() || remaining.getCount() < stack.getCount()) {
                return true;
            }
        }
        return false;
    }

    public boolean isItemEmpty() {
        for (int i = 0; i < inputInventory.getSlots(); i++) {
            if (!inputInventory.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public void read(@NotNull BlockState state, @NotNull CompoundNBT nbt) {
        super.read(state, nbt);

        if (nbt.contains("inputItem", Constants.NBT.TAG_COMPOUND)) {
            inputInventory.deserializeNBT(nbt.getCompound("inputItem"));
        }

        if (nbt.contains("outputFluid", Constants.NBT.TAG_COMPOUND)) {
            outputFluidTank.readFromNBT(nbt.getCompound("outputFluid"));
        }
    }

    @Override
    public @NotNull CompoundNBT write(@Nonnull CompoundNBT compound) {
        compound = super.write(compound);

        CompoundNBT inputItemNBT = inputInventory.serializeNBT();
        CompoundNBT outputFluidNBT = new CompoundNBT();
        outputFluidTank.writeToNBT(outputFluidNBT);

        compound.put("inputItem", inputItemNBT);
        compound.put("outputFluid", outputFluidNBT);
        return compound;
    }

    @Override
    public @NotNull IModelData getModelData() {
        return new ModelDataMap.Builder()
                .withInitial(FLUID_STACK_MODEL_PROPERTY, outputFluidTank.getFluid())
                .build();
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(pos, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        handleUpdateTag(world.getBlockState(pkt.getPos()), pkt.getNbtCompound());
    }

    @Override
    public @NotNull CompoundNBT getUpdateTag() {
        CompoundNBT compound = super.getUpdateTag();
        return write(compound);
    }

    public FluidStack getFluid() {
        return outputFluidTank.getFluid();
    }

    private void markDirtyAndUpdate() {
        markDirty();
        if (world != null) {
            world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.RERENDER_MAIN_THREAD);
        }
    }


    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemHolder.cast();
        }
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return fluidHolder.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void remove() {
        super.remove();
        itemHolder.invalidate();
        fluidHolder.invalidate();
    }
}
