package com.benchenssever.villagerswinery.drinkable;

import com.benchenssever.villagerswinery.api.IVillagerEntityMixin;
import com.benchenssever.villagerswinery.registration.DrinksRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import org.jetbrains.annotations.NotNull;

import static com.benchenssever.villagerswinery.VillagersWineryMod.LOGGER;

public class WineEffect extends Effect {
    private final boolean instant;

    public WineEffect(EffectType typeIn, int liquidColorIn) {
        super(typeIn, liquidColorIn);
        this.instant = false;
    }

    public WineEffect(EffectType typeIn, int liquidColorIn, boolean instant) {
        super(typeIn, liquidColorIn);
        this.instant = instant;
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity entityLivingBaseIn, int amplifier) {
    }

    @Override
    public void applyInstantenousEffect(Entity source, Entity indirectSource, @NotNull LivingEntity entityLivingBaseIn, int amplifier, double health) {
        if (entityLivingBaseIn instanceof IVillagerEntityMixin) {

            if (this == DrinksRegistry.getIMerchantXp.get()) {
                VillagerEntity villager = (VillagerEntity) entityLivingBaseIn;
                int newXp = villager.getVillagerXp() + 1 + amplifier;
                villager.setVillagerXp(newXp);

                LOGGER.debug("Villager XP updated: {}, Level: {}", villager.getVillagerXp(), villager.getVillagerData().getLevel());
            } else if(this == DrinksRegistry.addFoodLevel.get()) {
                IVillagerEntityMixin villager = (IVillagerEntityMixin) entityLivingBaseIn;
                villager.villagersWinery$addFoodLevel(amplifier * 12 + 12);
            }
        }
    }

    @Override
    public boolean isInstantenous() {
        return this.instant;
    }
}
