package com.benchenssever.villagerswinery;

import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(VillagersWineryMod.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class VillagersWineryMod
{
    public static final String MODID = "villagerswinery";
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger(MODID);

    public static VillagersWineryMod instance;

    public VillagersWineryMod() {
        instance = this;

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("examplemod", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    /* Utils */

    /**
     * Gets a resource location
     * @param name  Resource path
     * @return  Location for tinkers
     */
    public static ResourceLocation getResource(String name) {
        return new ResourceLocation(MODID, name);
    }

    /**
     * Returns the given Resource prefixed with resource location. Use this function instead of hardcoding
     * resource locations.
     */
    public static String resourceString(String res) {
        return String.format("%s:%s", MODID, res);
    }

    /**
     * Prefixes the given unlocalized name with prefix. Use this when passing unlocalized names for a uniform
     * namespace.
     */
    public static String prefix(String name) {
        return String.format("%s.%s", MODID, name.toLowerCase(Locale.US));
    }

    /**
     * Makes a translation key for the given name
     * @param base  Base name, such as "block" or "gui"
     * @param name  Object name
     * @return  Translation key
     */
    public static String makeTranslationKey(String base, String name) {
        return Util.makeTranslationKey(base, getResource(name));
    }

    /**
     * Makes a translation text component for the given name
     * @param base  Base name, such as "block" or "gui"
     * @param name  Object name
     * @return  Translation key
     */
    public static IFormattableTextComponent makeTranslation(String base, String name) {
        return new TranslationTextComponent(makeTranslationKey(base, name));
    }

    /**
     * Makes a translation text component for the given name
     * @param base       Base name, such as "block" or "gui"
     * @param name       Object name
     * @param arguments  Additional arguments to the translation
     * @return  Translation key
     */
    public static IFormattableTextComponent makeTranslation(String base, String name, Object... arguments) {
        return new TranslationTextComponent(makeTranslationKey(base, name), arguments);
    }
}
