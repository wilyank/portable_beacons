package wilyan_kramer.portable_beacons.common.tileentity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import wilyan_kramer.portable_beacons.common.block.BlockList;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class TileEntityRegistry {

	@SubscribeEvent
	public static void onRegisterTileEntityTypes(RegistryEvent.Register<TileEntityType<?>> event) {
		event.getRegistry().registerAll(
				TileEntityType.Builder.of(DiffuserTileEntity::new, BlockList.diffuser).build(null).setRegistryName("diffuser"),
				TileEntityType.Builder.of(WorkbenchTileEntity::new, BlockList.workbench).build(null).setRegistryName("workbench")
				);
	}
}
