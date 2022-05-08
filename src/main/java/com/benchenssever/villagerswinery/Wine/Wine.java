package com.benchenssever.villagerswinery.Wine;

import net.minecraft.fluid.Fluid;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;

import javax.annotation.Nullable;

public class Wine extends Potion {
    private final Fluid liquid;

    public Wine(@Nullable String baseNameIn, Fluid liquid, EffectInstance... effectsIn) {
        super(baseNameIn, effectsIn);

        this.liquid = liquid;
    }

    public Fluid getLiquid() {
        return liquid;
    }
}
