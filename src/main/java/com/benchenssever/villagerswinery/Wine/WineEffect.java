package com.benchenssever.villagerswinery.Wine;

import com.benchenssever.villagerswinery.registration.RegistryEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

import javax.annotation.Nullable;

public class WineEffect extends Effect {

    public WineEffect(EffectType typeIn, int liquidColorIn) {
        super(typeIn, liquidColorIn);
    }

    @Override
    public void performEffect(LivingEntity entityLivingBaseIn, int amplifier) {
        if(entityLivingBaseIn instanceof IMerchant) {
            if(this == RegistryEvents.hapiness) {
                IMerchant Villager = (IMerchant)entityLivingBaseIn;
                Villager.setXP(Villager.getXp() + 100 + 100 * amplifier);
            }
        }
    }

    @Override
    public void affectEntity(@Nullable Entity source, @Nullable Entity indirectSource, LivingEntity entityLivingBaseIn, int amplifier, double health) {
        super.affectEntity(source, indirectSource, entityLivingBaseIn, amplifier, health);
    }
}
