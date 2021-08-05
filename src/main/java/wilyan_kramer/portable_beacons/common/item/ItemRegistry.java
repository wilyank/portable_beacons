package wilyan_kramer.portable_beacons.common.item;

import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockNamedItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemTier;
import net.minecraft.item.Rarity;
import net.minecraft.item.Item.Properties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import wilyan_kramer.portable_beacons.PortableBeaconsMod;
import wilyan_kramer.portable_beacons.common.block.BlockList;

public class ItemRegistry {
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
    	@SubscribeEvent
    	public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
    		itemRegistryEvent.getRegistry().registerAll(
    				ItemList.infused_star = new EffectApplierItem(new Item.Properties()).setRegistryName(new ResourceLocation("portable_beacons","infused_star")),
    				ItemList.potion_necklace = new PotionNecklaceItem(),
    				ItemList.beacon_backpack_0 = new BeaconBackpackItem("beacon_backpack_0", 0),
    				ItemList.beacon_backpack_1 = new BeaconBackpackItem("beacon_backpack_1", 1),
    				ItemList.beacon_backpack_2 = new BeaconBackpackItem("beacon_backpack_2", 2),
    				ItemList.beacon_backpack_3 = new BeaconBackpackItem("beacon_backpack_3", 3),
    				ItemList.beacon_backpack_4 = new BeaconBackpackItem("beacon_backpack_4", 4),
    				ItemList.glowberries = new BlockNamedItem(BlockList.glowberry_bush, new Properties().stacksTo(16).tab(PortableBeaconsMod.TAB_PORTABLE_BEACONS).food(ItemList.GLOWBERRIES)).setRegistryName(PortableBeaconsMod.MODID, "glowberries"),
    				ItemList.bonk_stick = new BonkStickItem(ItemTier.WOOD, 0, -1F, new Properties().stacksTo(1).defaultDurability(100).tab(PortableBeaconsMod.TAB_PORTABLE_BEACONS)).setRegistryName(PortableBeaconsMod.MODID, "bonk_stick"),
    				ItemList.infused_dagger = new InfusedSwordItem(ItemTier.IRON, 1, -2F, new Properties().stacksTo(1).defaultDurability(100).tab(PortableBeaconsMod.TAB_PORTABLE_BEACONS)).setRegistryName(PortableBeaconsMod.MODID, "infused_dagger"),
    				ItemList.nether_star_block = new BlockItem(BlockList.nether_star_block, new Properties().tab(PortableBeaconsMod.TAB_PORTABLE_BEACONS).rarity(Rarity.EPIC)).setRegistryName(BlockList.nether_star_block.getRegistryName())
    				);
    	}
    }
}
