package com.benchenssever.villagerswinery.tileentity;

import com.benchenssever.villagerswinery.fluid.LiquidBarrelContainer;
import com.benchenssever.villagerswinery.fluid.LiquidBarrelTank;
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
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import static com.benchenssever.villagerswinery.registration.RegistryEvents.wineRecipe;

public class LiquidBarrelTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerProvider, INameable {
    public static final int DEFAULT_CAPACITY = FluidAttributes.BUCKET_VOLUME * 8;
    private final LiquidBarrelTank tank = new LiquidBarrelTank(DEFAULT_CAPACITY, (e) -> e.getFluid().getAttributes().getTemperature() < 500);
    private final LazyOptional<IFluidHandler> holder = LazyOptional.of(() -> tank);
    private ITextComponent customName;
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
        tank.readFromNBT(nbt);
        this.winemakingTime = nbt.getInt("WinemakingTime");
        this.winemakingTimeTotal = nbt.getInt("WinemakingTimeTotal");
        this.winemakingStatus = nbt.getInt("WinemakingStatus");
        if (nbt.contains("CustomName", 8)) {
            this.customName = ITextComponent.Serializer.getComponentFromJson(nbt.getString("CustomName"));
        }
    }

    @Override
    public @NotNull CompoundNBT write(@NotNull CompoundNBT compound) {
        compound = super.write(compound);
        tank.writeToNBT(compound);
        compound.putInt("WinemakingTime", this.winemakingTime);
        compound.putInt("WinemakingTimeTotal", this.winemakingTimeTotal);
        compound.putInt("WinemakingStatus", this.winemakingStatus);
        if (this.customName != null) {
            compound.putString("CustomName", ITextComponent.Serializer.toJson(this.customName));
        }
        return compound;
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            if (tank.isChanged()) {
                tank.resetChanged();
                syncToClient();
                this.winemakingStatus = 0;
                this.winemakingTime = 0;
                this.winemakingTimeTotal = 0;
                this.winemakingRecipe = this.getTank().getFluid().isEmpty() ? null : this.getRecipe();
            }

            if (this.winemakingRecipe != null) {
                this.winemakingTimeTotal = this.getRecipeSpendTime();
                this.winemakingTime++;
                if (this.winemakingTime >= this.winemakingTimeTotal) {
                    Fluid output = this.getRecipeOutput().getFluid();
                    int ratio = this.getRecipeOutput().getAmount() / this.getRecipeInput().getAmount();
                    this.tank.setFluid(new FluidStack(output, this.getTank().getFluidAmount() * ratio));
                    this.winemakingRecipe = null;
                    this.winemakingStatus = 0;
                    this.winemakingTime = 0;
                    this.winemakingTimeTotal = 0;
                }
            }
        }
    }

    private WineRecipe getRecipe() {
        return world.getRecipeManager().getRecipes(wineRecipe, new Inventory(), world)
                .stream()
                .filter(recipe -> recipe.matches(tank.getFluid(), world))
                .findFirst()
                .orElse(null);
    }

    private FluidStack getRecipeInput() {
        return this.winemakingRecipe != null ? this.winemakingRecipe.getFluidRecipeInput() : FluidStack.EMPTY;
    }

    private FluidStack getRecipeOutput() {
        return this.winemakingRecipe != null ? this.winemakingRecipe.getFluidRecipeOutput() : FluidStack.EMPTY;
    }

    private int getRecipeSpendTime() {
        return this.winemakingRecipe != null ? this.winemakingRecipe.getSpendTime() : 0;
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
        return this.customName != null ? this.customName : this.getBlockState().getBlock().getTranslatedName();
    }

    @Override
    public @NotNull ITextComponent getDisplayName() {
        return this.getName();
    }

    @Override
    public ITextComponent getCustomName() {
        return this.customName;
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
                PacketDistributor.TRACKING_CHUNK.with(() -> this.world.getChunkAt(this.pos)),
                new SyncLiquidBarrelPacket(this.tank.getFluid(), this.getWorldAndPos())
        );
    }

    public String getWorldAndPos() {
        return this.world.getDimensionKey().getLocation() + ":" + this.pos.toString();
    }
}
