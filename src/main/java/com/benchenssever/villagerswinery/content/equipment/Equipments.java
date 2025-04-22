package com.benchenssever.villagerswinery.content.equipment;

import com.benchenssever.villagerswinery.registration.DrinksRegistry;
import com.benchenssever.villagerswinery.registration.RegistryEvents;
import net.minecraft.item.Item;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Predicate;

public class Equipments {

    public static class Drinkware {
        public final RegistryObject<Item> filled;
        public RegistryObject<Item> empty;

        public Drinkware(String id, int capacity, Predicate<FluidStack> validator, int maxStackSize) {
            filled = DrinksRegistry.ITEMS.register(
                    id,
                    () -> new DrinkwareItem(
                            new Item.Properties()
                                    .tab(RegistryEvents.wineryItemGroup)
                                    .stacksTo(1),
                            capacity,
                            validator,
                            empty
                    )
            );
            empty = DrinksRegistry.ITEMS.register(
                    "empty_" + id,
                    () -> new DrinkwareItem.Empty(
                            new Item.Properties()
                                    .tab(RegistryEvents.wineryItemGroup)
                                    .stacksTo(maxStackSize),
                            capacity,
                            validator,
                            filled
                    )
            );
        }
    }
}
