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
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.benchenssever.villagerswinery.tileentity.LiquidBarrelTileEntity.DEFAULT_CAPACITY;
import static net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack.FLUID_NBT_KEY;

public class LiquidBarrelIItem extends BlockItem {
    public LiquidBarrelIItem(Block blockIn, Properties builder) {
        super(blockIn, builder);
    }

    @Override
    public void addInformation(@NotNull ItemStack stack, World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        FluidStack fluidStack = new WinebowlFluidHandler(stack, DEFAULT_CAPACITY).getFluid();
        if (!fluidStack.isEmpty()) {
            tooltip.add(FluidTransferUtil.addFluidTooltip(fluidStack));
        }
    }

    @Override
    protected boolean onBlockPlaced(@NotNull BlockPos pos, @NotNull World worldIn, PlayerEntity player, ItemStack stack, @NotNull BlockState state) {
        CompoundNBT tagCompound = stack.getTag();
        if (tagCompound != null && tagCompound.contains(FLUID_NBT_KEY)) {
            stack.getOrCreateChildTag("BlockEntityTag").put(FLUID_NBT_KEY, tagCompound.getCompound(FLUID_NBT_KEY));
        }
        return super.onBlockPlaced(pos, worldIn, player, stack, state);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        return new WinebowlFluidHandler(stack, DEFAULT_CAPACITY);
    }
}
