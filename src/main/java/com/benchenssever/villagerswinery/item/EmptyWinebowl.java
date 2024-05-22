package com.benchenssever.villagerswinery.item;

import com.benchenssever.villagerswinery.drinkable.WinebowlFluidHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

import static com.benchenssever.villagerswinery.item.Winebowl.DEFAULT_CAPACITY;

public class EmptyWinebowl extends Item {
    public EmptyWinebowl(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new WinebowlFluidHandler(stack, DEFAULT_CAPACITY);
    }
}
