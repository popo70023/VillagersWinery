package com.benchenssever.villagerswinery.client;

import com.benchenssever.villagerswinery.model.WineBowlBackedModel;
import com.benchenssever.villagerswinery.registration.DrinksRegistry;
import com.benchenssever.villagerswinery.registration.RegistryEvents;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Map;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // 客户端初始化代码
        RegistryEvents.setRender(event);
        DrinksRegistry.setRender(event);
    }

    @SubscribeEvent
    public static void onModelBaked(ModelBakeEvent event) {
        Map<ResourceLocation, IBakedModel> modelRegistry = event.getModelRegistry();
        registerWineBowlModel(modelRegistry, event);
    }

    private static void registerWineBowlModel(Map<ResourceLocation, IBakedModel> modelRegistry, ModelBakeEvent event) {
        ModelResourceLocation winebowllocation = new ModelResourceLocation(DrinksRegistry.winebowl.get().getRegistryName(), "inventory");
        IBakedModel winebowlexistingModel = modelRegistry.get(winebowllocation);
        if (winebowlexistingModel == null) {
            throw new RuntimeException("Did not find WineBowl in registry");
        } else if (winebowlexistingModel instanceof WineBowlBackedModel) {
            throw new RuntimeException("Tried to WineBowl twice");
        } else {
            WineBowlBackedModel backedModel = new WineBowlBackedModel(winebowlexistingModel, FluidStack.EMPTY);
            event.getModelRegistry().put(winebowllocation, backedModel);
        }
    }
}
