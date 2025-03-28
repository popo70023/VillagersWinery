package com.benchenssever.villagerswinery.api;

import net.minecraft.entity.merchant.IReputationTracking;
import net.minecraft.entity.villager.IVillagerDataHolder;

public interface IVillagerEntityMixin extends IReputationTracking, IVillagerDataHolder {
    void villagersWinery$addFoodLevel(int foodLevel);

    int getVillagerXp();

    void setVillagerXp(int xp);

    void villagersWinery$refreshMerchantOffers();
}
