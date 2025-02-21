package com.benchenssever.villagerswinery.drinkable;

import net.minecraft.item.Food;
import net.minecraft.util.text.TranslationTextComponent;

public interface IDrinkable {
    Food getFood();

    TranslationTextComponent getTooltip();
}
