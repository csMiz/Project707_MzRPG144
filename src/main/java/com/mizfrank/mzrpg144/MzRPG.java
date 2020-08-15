package com.mizfrank.mzrpg144;

import com.mizfrank.mzrpg144.block.BlockCollection;
import com.mizfrank.mzrpg144.block.ContainerCollection;
import com.mizfrank.mzrpg144.block.MzMedalBoxScreen;
import com.mizfrank.mzrpg144.entity.*;
import com.mizfrank.mzrpg144.item.ItemCollection;
import com.mizfrank.mzrpg144.item.ItemRendererCollection;
import com.mizfrank.mzrpg144.item.MzItemWeapon.MzArrow.MzArrowEntity;
import com.mizfrank.mzrpg144.item.MzItemWeapon.MzArrow.MzArrowEntityEx;
import com.mizfrank.mzrpg144.item.MzItemWeapon.MzArrow.MzArrowRenderer;
import com.mizfrank.mzrpg144.item.MzItemWeapon.MzArrow.MzArrowRendererEx;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * The main class of the mod, this is the class that looks like a mod to forge.
 */
@Mod(MzRPG.MOD_ID)
public class MzRPG {

    /**
     * The modid of this mod, this has to match the modid in the mods.toml and has to be in the format
     * defined in {@link net.minecraftforge.fml.loading.moddiscovery.ModInfo}
     */
    public static final String MOD_ID = "mzrpg144";

    public static ItemGroup MZ_ITEMGROUP;

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Main Mod program constructor
     * */
    public MzRPG(){
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
//        // Register the enqueueIMC method for modloading
//        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
//        // Register the processIMC method for modloading
//        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);


        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        MinecraftForge.EVENT_BUS.register(new CapabilityCollection());
        MinecraftForge.EVENT_BUS.register(new EventHandler());


        // register blocks (must before registering items)
        BlockCollection.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        // register items
        ItemCollection.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        MZ_ITEMGROUP = new MzItemGroup(MOD_ID, () -> new ItemStack(ItemCollection.MZ_GEM.get()));
        // register containers
        ContainerCollection.CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());

        ItemRendererCollection.ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());

        CapabilityManager.INSTANCE.register(IMzSpecialty.class, new MzSpecialtyStorage(), MzSpecialty::new);

        Networking.registerMessages();

    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);

        ScreenManager.registerFactory(ContainerCollection.MZ_MEDAL_BOX_CONTAINER.get(), MzMedalBoxScreen::new);


        RenderingRegistry.registerEntityRenderingHandler(MzArrowEntity.class, MzArrowRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(MzArrowEntityEx.class, MzArrowRendererEx::new);

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }


    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }
    }

}
