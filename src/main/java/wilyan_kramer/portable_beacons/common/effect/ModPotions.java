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
	public static Potion levitation = new Potion("levitation", new EffectInstance(Effects.LEVITATION, 200)).setRegistryName(PortableBeaconsMod.MODID, "levitation");
	public static Potion long_levitation = new Potion("levitation", new EffectInstance(Effects.LEVITATION, 400)).setRegistryName(PortableBeaconsMod.MODID, "long_levitation");
	public static Potion strong_levitation = new Potion("levitation", new EffectInstance(Effects.LEVITATION, 100, 1)).setRegistryName(PortableBeaconsMod.MODID, "strong_levitation");
	
	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class RegistryEvents {
		@SubscribeEvent
		public static void onPotionsRegistry(final RegistryEvent.Register<Potion> potionRegistryEvent) {
			potionRegistryEvent.getRegistry().registerAll(
					ModPotions.glowing, 
					ModPotions.long_glowing,
					ModPotions.levitation,
					ModPotions.long_levitation,
					ModPotions.strong_levitation
					);
		}
	}
}
