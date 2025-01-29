package com.benchenssever.villagerswinery.drinkable;

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
    public void performEffect(@NotNull LivingEntity entityLivingBaseIn, int amplifier) {
    }

    @Override
    public void affectEntity(Entity source, Entity indirectSource, @NotNull LivingEntity entityLivingBaseIn, int amplifier, double health) {
        if (entityLivingBaseIn instanceof VillagerEntity) {
            VillagerEntity Villager = (VillagerEntity) entityLivingBaseIn;

            if (this == DrinksRegistry.getIMerchantXp.get()) {
                Villager.setXP(Villager.getXp() + 1 + amplifier);
                LOGGER.debug("Villager have {}", Villager.getXp());
            }
        }
    }

    @Override
    public boolean isInstant() {
        return this.instant;
    }
}
