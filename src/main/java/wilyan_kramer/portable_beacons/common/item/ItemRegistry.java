package wilyan_kramer.portable_beacons.common.item;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraft.item.ItemTier;
import net.minecraft.item.Rarity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import wilyan_kramer.portable_beacons.PortableBeaconsMod;
import wilyan_kramer.portable_beacons.common.block.BlockList;
import wilyan_kramer.portable_beacons.common.config.Config;

public class ItemRegistry {
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
    	@SubscribeEvent
    	public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
    		itemRegistryEvent.getRegistry().registerAll(
    				ItemList.infused_star = new EffectApplierItem(new Item.Properties()).setRegistryName(PortableBeaconsMod.MODID,"infused_star"),
    				ItemList.data_item = new Item(new Item.Properties().stacksTo(1)).setRegistryName(PortableBeaconsMod.MODID, "data_item"),
    				ItemList.potion_necklace = new PotionNecklaceItem(),
    				ItemList.beacon_backpack_0 = new BeaconBackpackItem("beacon_backpack_0", 0),
    				ItemList.beacon_backpack_1 = new BeaconBackpackItem("beacon_backpack_1", 1),
    				ItemList.beacon_backpack_2 = new BeaconBackpackItem("beacon_backpack_2", 2),
    				ItemList.beacon_backpack_3 = new BeaconBackpackItem("beacon_backpack_3", 3),
    				ItemList.beacon_backpack_4 = new BeaconBackpackItem("beacon_backpack_4", 4),
    				ItemList.starberries = new BlockNamedItemWithFoil(BlockList.starberry_bush, new Properties().stacksTo(16).tab(InventoryTabs.TAB_PORTABLE_BEACONS).food(ItemList.GLOWBERRIES)).setRegistryName(PortableBeaconsMod.MODID, "starberries"),
    				ItemList.bonk_stick = new BonkStickItem(ItemTier.WOOD, Config.COMMON.bonkStickDamage.get(), -1F, new Properties().stacksTo(1).defaultDurability(Config.COMMON.bonkStickDurability.get()).tab(InventoryTabs.TAB_PORTABLE_BEACONS)).setRegistryName(PortableBeaconsMod.MODID, "bonk_stick"),
    				ItemList.infused_dagger = new InfusedSwordItem(ItemTier.WOOD, Config.COMMON.infusedDaggerDamage.get(), -2F, new Properties().stacksTo(1).defaultDurability(Config.COMMON.infusedDaggerDurability.get()).tab(InventoryTabs.TAB_PORTABLE_BEACONS)).setRegistryName(PortableBeaconsMod.MODID, "infused_dagger"),
    				ItemList.nether_star_block = new BlockItem(BlockList.nether_star_block, new Properties().tab(InventoryTabs.TAB_PORTABLE_BEACONS).rarity(Rarity.EPIC)).setRegistryName(BlockList.nether_star_block.getRegistryName()),
    				ItemList.diffuser = new BlockItem(BlockList.diffuser, new Properties().tab(InventoryTabs.TAB_PORTABLE_BEACONS)).setRegistryName(BlockList.diffuser.getRegistryName()),
    				ItemList.bench = new BlockItem(BlockList.bench, new Properties().tab(InventoryTabs.TAB_PORTABLE_BEACONS)).setRegistryName(BlockList.bench.getRegistryName())

    				);
    	}
    }
}
