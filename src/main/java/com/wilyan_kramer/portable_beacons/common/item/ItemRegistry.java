package com.wilyan_kramer.portable_beacons.common.item;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ItemRegistry {
    private static final Logger LOGGER = LogManager.getLogger();
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
    	@SubscribeEvent
    	public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
    		LOGGER.info("HELLO from Register Item");
    		itemRegistryEvent.getRegistry().registerAll(
    				ItemList.infused_star = new EffectApplierItem(new Item.Properties()).setRegistryName(new ResourceLocation("portable_beacons","infused_star")),
    				ItemList.beacon_necklace = new BeaconNecklaceItem(),
    				ItemList.beacon_backpack_0 = new BeaconBackpackItem("beacon_backpack_0", 0),
    				ItemList.beacon_backpack_1 = new BeaconBackpackItem("beacon_backpack_1", 1),
    				ItemList.beacon_backpack_2 = new BeaconBackpackItem("beacon_backpack_2", 2),
    				ItemList.beacon_backpack_3 = new BeaconBackpackItem("beacon_backpack_3", 3),
    				ItemList.beacon_backpack_4 = new BeaconBackpackItem("beacon_backpack_4", 4)
    				);
    	}
    }
}
