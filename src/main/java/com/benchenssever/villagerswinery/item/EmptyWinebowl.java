package com.benchenssever.villagerswinery.item;

import com.benchenssever.villagerswinery.fluid.WinebowlFluidHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import static com.benchenssever.villagerswinery.item.Winebowl.DEFAULT_CAPACITY;

public class EmptyWinebowl extends Item {
    public EmptyWinebowl(Properties properties) {
        super(properties);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        return new WinebowlFluidHandler(stack, DEFAULT_CAPACITY);
    }
}
