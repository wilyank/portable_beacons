package wilyan_kramer.portable_beacons.common.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import wilyan_kramer.portable_beacons.PortableBeaconsMod;

public class BlockRegistry {
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
    	@SubscribeEvent
    	public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
    		blockRegistryEvent.getRegistry().registerAll(
    				BlockList.nether_star_block = new Block(
    						AbstractBlock.Properties
    						.of(Material.METAL, MaterialColor.METAL)
    						.harvestTool(ToolType.PICKAXE)
    						.strength(5.0F, 6.0F)
    						.sound(SoundType.METAL)
    						).setRegistryName(new ResourceLocation(PortableBeaconsMod.MODID, "nether_star_block")) 
    				);
    	}
    }
}