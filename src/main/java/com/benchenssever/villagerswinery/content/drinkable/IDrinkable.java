package com.benchenssever.villagerswinery.content.drinkable;

import net.minecraft.item.Food;
import net.minecraft.util.text.TranslationTextComponent;

public interface IDrinkable {
    Food getFood();

    TranslationTextComponent getTooltip();

    boolean isAlcohol();

    boolean isForDrink();
}
