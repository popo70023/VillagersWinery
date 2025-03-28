package com.benchenssever.villagerswinery.drinkable;

import com.benchenssever.villagerswinery.api.IVillagerEntityMixin;
import com.benchenssever.villagerswinery.registration.DrinksRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import org.jetbrains.annotations.NotNull;

public class WineEffect extends Effect {
    private final boolean instant;

    public WineEffect(EffectType typeIn, int liquidColorIn, boolean instant) {
        super(typeIn, liquidColorIn);
        this.instant = instant;
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity entityLivingBaseIn, int amplifier) {
    }

    @Override
    public void applyInstantenousEffect(Entity source, Entity indirectSource, @NotNull LivingEntity entityLivingBaseIn, int amplifier, double health) {
    }

    @Override
    public boolean isInstantenous() {
        return this.instant;
    }

    public static class Villager extends WineEffect {
        public Villager(EffectType typeIn, int liquidColorIn, boolean instant) {
            super(typeIn, liquidColorIn, instant);
        }

        @Override
        public void applyInstantenousEffect(Entity source, Entity indirectSource, @NotNull LivingEntity entityLivingBaseIn, int amplifier, double health) {
            if (entityLivingBaseIn instanceof IVillagerEntityMixin) {
                IVillagerEntityMixin villagerMixin = (IVillagerEntityMixin) entityLivingBaseIn;
                if (this == DrinksRegistry.getIMerchantXp.get()) {
                    villagerMixin.setVillagerXp(villagerMixin.getVillagerXp() + amplifier + 1);
                } else if (this == DrinksRegistry.addFoodLevel.get()) {
                    villagerMixin.villagersWinery$addFoodLevel(amplifier * 12 + 12);
                } else if (this == DrinksRegistry.refreshOffers.get()) {
                    if (villagerMixin.getVillagerData().getLevel() <= amplifier * 2 + 3) {
                        villagerMixin.villagersWinery$refreshMerchantOffers();
                    }
                }
            }
        }
    }
}
