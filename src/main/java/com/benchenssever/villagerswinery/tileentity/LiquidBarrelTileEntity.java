package com.benchenssever.villagerswinery.tileentity;

import com.benchenssever.villagerswinery.fluid.LiquidBarrelContainer;
import com.benchenssever.villagerswinery.network.NetworkHandler;
import com.benchenssever.villagerswinery.network.SyncLiquidBarrelPacket;
import com.benchenssever.villagerswinery.recipe.WineRecipe;
import com.benchenssever.villagerswinery.registration.RegistryEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.INameable;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class LiquidBarrelTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerProvider, INameable {
    public static final int DEFAULT_CAPACITY = FluidAttributes.BUCKET_VOLUME * 8;
    private ITextComponent customName;
    private final FluidTank tank = new FluidTank(DEFAULT_CAPACITY, (e) -> e.getFluid().getAttributes().getTemperature() < 500) {
        @Override
        protected void onContentsChanged() {
            markDirtyAndUpdate();
        }
    };
    private final LazyOptional<IFluidHandler> holder = LazyOptional.of(() -> tank);
    private WineRecipe winemakingRecipe;
    private int winemakingTime;
    private int winemakingTimeTotal;
    private int winemakingStatus;
    protected final IIntArray liquidBarrelData = new IIntArray() {
        public int get(int index) {
            switch (index) {
                case 0:
                    return LiquidBarrelTileEntity.this.winemakingTime;
                case 1:
                    return LiquidBarrelTileEntity.this.winemakingTimeTotal;
                case 2:
                    return LiquidBarrelTileEntity.this.winemakingStatus;
                default:
                    return 0;
            }
        }

        public void set(int index, int value) {
            switch (index) {
                case 0:
                    LiquidBarrelTileEntity.this.winemakingTime = value;
                    break;
                case 1:
                    LiquidBarrelTileEntity.this.winemakingTimeTotal = value;
                    break;
                case 2:
                    LiquidBarrelTileEntity.this.winemakingStatus = value;
            }

        }

        public int size() {
            return 3;
        }
    };

    public LiquidBarrelTileEntity() {
        super(RegistryEvents.liquidBarrelTileEntity.get());
    }

    @Override
    public void read(@NotNull BlockState state, @NotNull CompoundNBT nbt) {
        super.read(state, nbt);
        if (nbt.contains(FluidHandlerItemStack.FLUID_NBT_KEY, Constants.NBT.TAG_COMPOUND)) {
            tank.readFromNBT(nbt.getCompound(FluidHandlerItemStack.FLUID_NBT_KEY));
        }
        winemakingTime = nbt.getInt("WinemakingTime");
        winemakingTimeTotal = nbt.getInt("WinemakingTimeTotal");
        winemakingStatus = nbt.getInt("WinemakingStatus");
        if (nbt.contains("CustomName", Constants.NBT.TAG_STRING)) {
            customName = ITextComponent.Serializer.getComponentFromJson(nbt.getString("CustomName"));
        }
    }

    @Override
    public void onLoad() {
        refreshRecipe();
        super.onLoad();
    }

    @Override
    public @NotNull CompoundNBT write(@NotNull CompoundNBT compound) {
        compound = super.write(compound);
        compound.put(FluidHandlerItemStack.FLUID_NBT_KEY, tank.writeToNBT(new CompoundNBT()));
        compound.putInt("WinemakingTime", winemakingTime);
        compound.putInt("WinemakingTimeTotal", winemakingTimeTotal);
        compound.putInt("WinemakingStatus", winemakingStatus);
        if (customName != null) {
            compound.putString("CustomName", ITextComponent.Serializer.toJson(customName));
        }
        return compound;
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            if (winemakingStatus == 1 && winemakingRecipe == null) {
                winemakingRecipe = getRecipe();
                winemakingStatus = 0;
            }
            if (winemakingRecipe != null) {
                winemakingTimeTotal = this.getRecipeSpendTime();
                winemakingStatus = 1;
                winemakingTime++;
                if (winemakingTime >= winemakingTimeTotal) {
                    Fluid output = getRecipeOutput().getFluid();
                    int ratio = getRecipeOutput().getAmount() / getRecipeInput().getAmount();
                    tank.setFluid(new FluidStack(output, getTank().getFluidAmount() * ratio));
                    markDirtyAndUpdate();
                }
            }
        }
    }

    private void markDirtyAndUpdate() {
        markDirty();
        syncToClient();
        winemakingStatus = 0;
        winemakingTime = 0;
        if (winemakingRecipe == null || !winemakingRecipe.matches(getTank().getFluid(), world)) {
            winemakingTimeTotal = 0;
            refreshRecipe();
        }
    }

    public void refreshRecipe() {
        winemakingRecipe = getTank().getFluid().isEmpty() ? null : getRecipe();
    }

    private WineRecipe getRecipe() {
        return world.getRecipeManager().getRecipes(RegistryEvents.wineRecipe, new Inventory(), world)
                .stream()
                .filter(recipe -> recipe.matches(getTank().getFluid(), world))
                .findFirst()
                .orElse(null);
    }

    private FluidStack getRecipeInput() {
        return winemakingRecipe != null ? winemakingRecipe.getFluidRecipeInput() : FluidStack.EMPTY;
    }

    private FluidStack getRecipeOutput() {
        return winemakingRecipe != null ? winemakingRecipe.getFluidRecipeOutput() : FluidStack.EMPTY;
    }

    private int getRecipeSpendTime() {
        return winemakingRecipe != null ? winemakingRecipe.getSpendTime() : 0;
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return holder.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public @NotNull ITextComponent getName() {
        return customName != null ? customName : getBlockState().getBlock().getTranslatedName();
    }

    @Override
    public @NotNull ITextComponent getDisplayName() {
        return getName();
    }

    @Override
    public ITextComponent getCustomName() {
        return customName;
    }

    public void setCustomName(ITextComponent customName) {
        this.customName = customName;
    }

    public FluidTank getTank() {
        return tank;
    }

    @Override
    public Container createMenu(int sycID, @NotNull PlayerInventory inventory, @NotNull PlayerEntity player) {
        return new LiquidBarrelContainer(sycID, inventory, this.getTank().getFluid(), this.getWorldAndPos(), this.liquidBarrelData);
    }

    public void syncToClient() {
        if (world == null || world.isRemote) return;
        NetworkHandler.INSTANCE.send(
                PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(getPos())),
                new SyncLiquidBarrelPacket(getTank().getFluid(), getWorldAndPos())
        );
    }

    public String getWorldAndPos() {
        return world.getDimensionKey().getLocation() + ":" + getPos();
    }
}
