package com.benchenssever.villagerswinery.tileentity;

import com.benchenssever.villagerswinery.fluid.LiquidBarrelContainer;
import com.benchenssever.villagerswinery.fluid.LiquidBarrelTank;
import com.benchenssever.villagerswinery.recipe.WineRecipe;
import com.benchenssever.villagerswinery.registration.RegistryEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.benchenssever.villagerswinery.VillagersWineryMod.LOGGER;
import static com.benchenssever.villagerswinery.registration.RegistryEvents.wineRecipe;

public class LiquidBarrelTileEntity extends TileEntity implements ITickableTileEntity, INamedContainerProvider, INameable {
    private ITextComponent customName;
    public static final int DEFAULT_CAPACITY = FluidAttributes.BUCKET_VOLUME * 8;
    private final FluidTank tank = new LiquidBarrelTank(DEFAULT_CAPACITY, (e)->e.getFluid().getAttributes().getTemperature() < 500);
    private WineRecipe winemakingRecipe;
    private Fluid winemakingFluid = Fluids.EMPTY;
    private int winemakingTime;
    private int winemakingTimeTotal;
    private int winemakingStatus;

    protected final IIntArray liquidBarrelData = new IIntArray() {
        public int get(int index) {
            switch(index) {
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
            switch(index) {
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
    private final LazyOptional<IFluidHandler> holder = LazyOptional.of(() -> tank);

    public LiquidBarrelTileEntity() { super(RegistryEvents.liquidBarrelTileEntity.get()); }

    @Override
    public void read(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
        super.read(state, nbt);
        tank.readFromNBT(nbt);
        this.winemakingTime = nbt.getInt("WinemakingTime");
        this.winemakingTimeTotal = nbt.getInt("WinemakingTimeTotal");
        this.winemakingStatus = nbt.getInt("WinemakingStatus");
        if (nbt.contains("CustomName", 8)) { this.customName = ITextComponent.Serializer.getComponentFromJson(nbt.getString("CustomName")); }
    }

    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT compound) {
        compound = super.write(compound);
        tank.writeToNBT(compound);
        compound.putInt("WinemakingTime", this.winemakingTime);
        compound.putInt("WinemakingTimeTotal", this.winemakingTimeTotal);
        compound.putInt("WinemakingStatus", this.winemakingStatus);
        if (this.customName != null) { compound.putString("CustomName", ITextComponent.Serializer.toJson(this.customName)); }
        return compound;
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
//            if (this.winemakingStatus > 0) {
                if(this.tank.getFluid().isEmpty() || !this.winemakingFluid.isEquivalentTo(this.tank.getFluid().getFluid())) {
                    this.winemakingRecipe = null;
                    this.winemakingStatus = 0;
                    this.winemakingTime = 0;
                    this.winemakingTimeTotal = 0;
                }

                if(!this.winemakingFluid.isEquivalentTo(this.tank.getFluid().getFluid())) {
                    this.winemakingRecipe = this.getRecipe();
                    LOGGER.debug(this.getRecipe());
                }

                if(this.winemakingRecipe != null) {
                    this.winemakingTimeTotal = winemakingRecipe.getSpendTime();
                    this.winemakingTime++;
                    if (this.winemakingTime >= this.winemakingTimeTotal) {
                        Fluid output = this.getRecipeOutput().getFluid();
                        this.tank.setFluid(new FluidStack(output, this.tank.getFluidAmount()));
                        this.winemakingRecipe = null;
                        this.winemakingStatus = 0;
                        this.winemakingTime = 0;
                        this.winemakingTimeTotal = 0;
                    }
                }
//            }
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

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) { return holder.cast(); }
        return super.getCapability(cap, side);
    }

    @Nonnull
    @Override
    public ITextComponent getName() { return this.customName != null ? this.customName : this.getBlockState().getBlock().getTranslatedName(); }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() { return this.getName(); }

    @Nullable
    @Override
    public ITextComponent getCustomName() { return this.customName; }

    public void setCustomName(ITextComponent customName) { this.customName = customName; }

    public FluidTank getTank() { return tank; }

    @Nullable
    @Override
    public Container createMenu(int sycID, @Nonnull PlayerInventory inventory, @Nonnull PlayerEntity player) {
        return new LiquidBarrelContainer(sycID, inventory, this.tank.getFluid(), this.liquidBarrelData);
    }
}
