package com.benchenssever.villagerswinery;

import com.benchenssever.villagerswinery.model.WineBowlBackedModel;
import com.benchenssever.villagerswinery.registration.DrinksRegistry;
import com.benchenssever.villagerswinery.registration.RegistryEvents;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(VillagersWineryMod.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class VillagersWineryMod
{
    public static final String MODID = "villagerswinery";
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static VillagersWineryMod instance;

    public VillagersWineryMod() {
        instance = this;

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Register the setup method for modloading
        eventBus.addListener(this::setup);
        // Register the enqueueIMC method for modloading
        eventBus.addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        eventBus.addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        eventBus.addListener(this::doClientStuff);

        RegistryEvents.setRegister(eventBus);
        DrinksRegistry.setRegister(eventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        RegistryEvents.setRender(event);
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // some example code to dispatch IMC to another mod
    }

    private void processIMC(final InterModProcessEvent event) {
        // some example code to receive and process InterModComms from other mods
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
    }

    //TODO: move to client side only
    @SubscribeEvent
    public static void onModelBaked(ModelBakeEvent event) {
        Map<ResourceLocation, IBakedModel> modelRegistry = event.getModelRegistry();
        ModelResourceLocation location = new ModelResourceLocation(RegistryEvents.winebowl.get().getRegistryName(), "inventory");
        IBakedModel existingModel = modelRegistry.get(location);
        if (existingModel == null) {
            throw new RuntimeException("Did not find WineBowl in registry");
        } else if (existingModel instanceof WineBowlBackedModel) {
            throw new RuntimeException("Tried to WineBowl twice");
        } else {
            WineBowlBackedModel backedModel = new WineBowlBackedModel(existingModel);
            event.getModelRegistry().put(location, backedModel);
        }
    }
}
