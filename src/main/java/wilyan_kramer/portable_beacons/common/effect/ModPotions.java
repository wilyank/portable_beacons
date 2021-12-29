package wilyan_kramer.portable_beacons.common.effect;

import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import wilyan_kramer.portable_beacons.PortableBeaconsMod;

public class ModPotions {

	public static Potion glowing = new Potion("glowing", new EffectInstance(Effects.GLOWING, 3600)).setRegistryName(PortableBeaconsMod.MODID, "glowing");
	public static Potion long_glowing = new Potion("glowing", new EffectInstance(Effects.GLOWING, 9600)).setRegistryName(PortableBeaconsMod.MODID, "long_glowing");

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class RegistryEvents {
		@SubscribeEvent
		public static void onPotionsRegistry(final RegistryEvent.Register<Potion> potionRegistryEvent) {
			potionRegistryEvent.getRegistry().registerAll(
					ModPotions.glowing, 
					ModPotions.long_glowing
					);
		}
	}
}
