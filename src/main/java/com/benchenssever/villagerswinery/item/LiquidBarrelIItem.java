package com.benchenssever.villagerswinery.item;

import com.benchenssever.villagerswinery.fluid.FluidTransferUtil;
import com.benchenssever.villagerswinery.fluid.WinebowlFluidHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static com.benchenssever.villagerswinery.tileentity.LiquidBarrelTileEntity.DEFAULT_CAPACITY;
import static net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack.FLUID_NBT_KEY;

public class LiquidBarrelIItem  extends BlockItem {
    public LiquidBarrelIItem(Block blockIn, Properties builder) {
        super(blockIn, builder);
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        FluidStack fluidStack = new WinebowlFluidHandler(stack, DEFAULT_CAPACITY).getFluid();
        if(!fluidStack.isEmpty()) {
            tooltip.add(FluidTransferUtil.addFluidTooltip(fluidStack));
        }
    }

    @Override
    protected boolean onBlockPlaced(@Nonnull BlockPos pos, @Nonnull World worldIn, @Nullable PlayerEntity player, ItemStack stack, @Nonnull BlockState state) {
        CompoundNBT tagCompound = stack.getTag();
        if(tagCompound != null && tagCompound.contains(FLUID_NBT_KEY)) {
            stack.getOrCreateChildTag("BlockEntityTag").put(FLUID_NBT_KEY, tagCompound.getCompound(FLUID_NBT_KEY));
        }
        return super.onBlockPlaced(pos, worldIn, player, stack, state);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new WinebowlFluidHandler(stack, DEFAULT_CAPACITY);
    }
}
