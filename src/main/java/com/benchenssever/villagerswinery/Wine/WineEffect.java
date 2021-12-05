package com.benchenssever.villagerswinery.Wine;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

import javax.annotation.Nullable;

public class WineEffect extends Effect {

    public WineEffect(EffectType type, int color) {
        super(type, color);
    }

    @Override
    public void applyEffectTick(LivingEntity p_76394_1_, int p_76394_2_) {
        super.applyEffectTick(p_76394_1_, p_76394_2_);
    }

    @Override
    public void applyInstantenousEffect(@Nullable Entity p_180793_1_, @Nullable Entity p_180793_2_, LivingEntity p_180793_3_, int p_180793_4_, double p_180793_5_) {
        super.applyInstantenousEffect(p_180793_1_, p_180793_2_, p_180793_3_, p_180793_4_, p_180793_5_);
    }
}
