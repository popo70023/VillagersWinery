package com.benchenssever.villagerswinery.mixin;

import com.benchenssever.villagerswinery.api.IVillagerEntityMixin;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin implements IVillagerEntityMixin {
    @Shadow private byte foodLevel;

    @Unique
    public void villagersWinery$addFoodLevel(int foodLevel) {
        this.foodLevel += (byte) foodLevel;
    }
}
