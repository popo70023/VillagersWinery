package com.benchenssever.villagerswinery.tileentity;

import com.benchenssever.villagerswinery.inventory.InventoryStackHandler;
import com.benchenssever.villagerswinery.recipe.BasinCrushRecipe;
import com.benchenssever.villagerswinery.registration.RegistryEvents;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class BasinTileEntity extends TileEntity {
    public static final int DEFAULT_CAPACITY = FluidAttributes.BUCKET_VOLUME;
    public final InventoryStackHandler inputInventory = new InventoryStackHandler(1) {
        @Override
        public void markDirty() {
            markDirtyAndUpdate();
        }
    };
    private final LazyOptional<IItemHandler> itemHolder = LazyOptional.of(() -> inputInventory);
    private final FluidTank outputFluidTank = new FluidTank(DEFAULT_CAPACITY, (F) -> F.getFluid().getAttributes().getTemperature() < 500) {
        @Override
        protected void onContentsChanged() {
            markDirtyAndUpdate();
        }
    };
    private final LazyOptional<IFluidHandler> fluidHolder = LazyOptional.of(() -> outputFluidTank);
    private BasinCrushRecipe basinCrushRecipe;
    private int basinWalkProgress;
    private int basinCrushProgress;

    public BasinTileEntity() {
        super(RegistryEvents.basinTileEntity.get());
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

    public void basinWalk() {
        if (basinCrushRecipe != null) {
            basinWalkProgress++;
            if (basinWalkProgress > 30) {
                basinWalkProgress -= 30;
                basinCrush();
            }
        }
    }

    public void basinCrush() {
        if (basinCrushRecipe != null) {
            FluidStack output = basinCrushRecipe.getFluidRecipeOutput();
            if (outputFluidTank.fill(output, IFluidHandler.FluidAction.SIMULATE) == output.getAmount()) {
                inputInventory.decrStackSize(1, 1);
                outputFluidTank.fill(output, IFluidHandler.FluidAction.EXECUTE);
                world.playSound(null, pos, SoundEvents.ENTITY_SLIME_JUMP, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        }
    }

    public boolean isItemEmpty() {
        return inputInventory.isEmpty();
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
        basinWalkProgress = nbt.getInt("basinwalkprogress");
        basinCrushProgress = nbt.getInt("basincrushprogress");
    }

    @Override
    public void onLoad() {
        basinCrushRecipe = inputInventory.isEmpty() ? null : getRecipe();
        super.onLoad();
    }

    @Override
    public @NotNull CompoundNBT write(@NotNull CompoundNBT compound) {
        compound = super.write(compound);

        compound.put("inputItem", inputInventory.serializeNBT());
        compound.put("outputFluid", outputFluidTank.writeToNBT(new CompoundNBT()));
        compound.putInt("basinwalkprogress", basinWalkProgress);
        compound.putInt("basincrushprogress", basinCrushProgress);
        return compound;
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
        return write(new CompoundNBT());
    }

    public ItemStack getItemStack(int sold) {
        return inputInventory.getStackInSlot(sold);
    }

    public int getInventorySize() {
        return inputInventory.getSlots();
    }

    public FluidStack getFluidStack() {
        return outputFluidTank.getFluid();
    }

    private void markDirtyAndUpdate() {
        markDirty();
        if (basinCrushRecipe == null || !basinCrushRecipe.matches(inputInventory, world)) {
            basinWalkProgress = 0;
            basinCrushProgress = 0;
            basinCrushRecipe = inputInventory.isEmpty() ? null : getRecipe();
        }
        world.notifyBlockUpdate(getPos(), getBlockState(), getBlockState(), Constants.BlockFlags.RERENDER_MAIN_THREAD);
    }

    private BasinCrushRecipe getRecipe() {
        return world.getRecipeManager().getRecipes(RegistryEvents.basinCrushRecipe, inputInventory, world)
                .stream()
                .filter(recipe -> recipe.matches(inputInventory, world))
                .findFirst()
                .orElse(null);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
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
