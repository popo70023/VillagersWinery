package com.benchenssever.villagerswinery.content.equipment;

import com.benchenssever.villagerswinery.content.capability.ItemStackFluidHandler;
import com.benchenssever.villagerswinery.content.drinkable.IDrinkable;
import com.benchenssever.villagerswinery.content.capability.FluidUtils;
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

import static com.benchenssever.villagerswinery.content.equipment.LiquidBarrelTileEntity.DEFAULT_CAPACITY;
import static net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack.FLUID_NBT_KEY;

public class LiquidBarrelItem extends BlockItem {
    public LiquidBarrelItem(Block blockIn, Properties builder) {
        super(blockIn, builder);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        FluidStack fluidStack = ItemStackFluidHandler.getFluid(stack);
        if (!fluidStack.isEmpty()) {
            tooltip.add(FluidUtils.addFluidStackTooltip(fluidStack));

            if (fluidStack.getFluid() instanceof IDrinkable) {
                tooltip.add(((IDrinkable) fluidStack.getFluid()).getTooltip());
            }
        }
    }

    @Override
    protected boolean updateCustomBlockEntityTag(@NotNull BlockPos pos, @NotNull World worldIn, PlayerEntity player, ItemStack stack, @NotNull BlockState state) {
        CompoundNBT tagCompound = stack.getTag();
        if (tagCompound != null && tagCompound.contains(FLUID_NBT_KEY)) {
            stack.getOrCreateTagElement("BlockEntityTag").put(FLUID_NBT_KEY, tagCompound.getCompound(FLUID_NBT_KEY));
        }
        return super.updateCustomBlockEntityTag(pos, worldIn, player, stack, state);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        return new ItemStackFluidHandler(stack, DEFAULT_CAPACITY, FluidUtils.WOODEN_CONTAINER_VALIDATOR);
    }
}
