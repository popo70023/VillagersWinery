package com.benchenssever.villagerswinery.Wine;

import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;

import javax.annotation.Nullable;

public class Wine extends Potion {

    public Wine(@Nullable String baseNameIn, EffectInstance... effectsIn) {
        super(baseNameIn, effectsIn);
    }
}
