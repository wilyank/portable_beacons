package wilyan_kramer.portable_beacons.common.config;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import wilyan_kramer.portable_beacons.PortableBeaconsMod;

public class Config {
	private static final Logger LOGGER = LogManager.getLogger();
	public static class Common {
		public final BooleanValue canCopyFromBeacon;
		public final BooleanValue canCopyFromConduit;
//		public final BooleanValue canCopyFromNetheriteBeacon;
		public final DoubleValue beaconRangeA;
		public final DoubleValue beaconRangeB;
		public final BooleanValue beaconSelf;
		public final BooleanValue beaconOthers;
		public final IntValue effectCooldown;
		public final IntValue effectDuration;
		public final DoubleValue diffuserRange;
		public final IntValue bonkStickDamage;
		public final IntValue bonkStickDurability;
		public final IntValue infusedDaggerDamage; 
		public final IntValue infusedDaggerDurability;
		public final BooleanValue witchArmor;
		public final IntValue witchArmorChance;

		Common(ForgeConfigSpec.Builder builder) {
			builder.comment("Common config for Portable Beacons").push("Beacon Backpack Config");
			this.canCopyFromBeacon = builder
					.comment("Whether effects can be copied from Beacons unto Beacon Backpacks")
					.worldRestart()
					.define("canCopyFromBeacon", true);
			this.canCopyFromConduit = builder
					.comment("Whether the Conduit Power effect can be applied to Beacon Backpacks by right clicking a Conduit")
					.worldRestart()
					.define("canCopyFromConduit", true);
//			this.canCopyFromNetheriteBeacon = builder
//					.comment("Whether effects can be copied from Netherite Beacons (from Netherite Plus mod) unto Beacon Backpacks ")
//					.worldRestart()
//					.define("canCopyFromNetheriteBeacon", false);
			this.beaconRangeA = builder
					.comment("Parameter A in calculating the Beacon Backpack's range. Range = A*(Tier-1) + B. Vanilla Beacons have A = 10")
					.worldRestart()
					.defineInRange("beaconRangeA", 5.0F, 0.0F, 100.0F);
			this.beaconRangeB = builder
					.comment("Parameter B in calculating the Beacon Backpack's range. Range = A*(Tier-1) + B. Vanilla Beacons have B = 10")
					.worldRestart()
					.defineInRange("beaconRangeB", 5.0F, 0.0F, 0.0F);
			this.beaconSelf = builder
					.comment("Whether the Beacon Backpack applies effects to the wearer")
					.worldRestart()
					.define("beaconSelf", true);
			this.beaconOthers = builder
					.comment("Whether the Beacon Backpack applies effects to players around the wearer")
					.worldRestart()
					.define("beaconOthers", true);
			this.effectCooldown = builder
					.comment("The time in ticks between refreshing the effects on a Beacon Backpack wearer")
					.worldRestart()
					.defineInRange("effectCooldown", 200, 2, 10000);
			this.effectDuration = builder
					.comment("The length in ticks of the effect applied by the Beacon Backpack")
					.worldRestart()
					.defineInRange("effectDuration", 400, 1, 10000);
			this.diffuserRange = builder
					.comment("The range of the Diffuser")
					.worldRestart()
					.defineInRange("diffuserRange", 50F, 1F, 128);
			builder.pop();
			builder.push("Weapons Config");
			this.bonkStickDamage = builder
					.comment("Base damage of the Bonk Stick")
					.worldRestart()
					.defineInRange("bonkStickDamage", 0, 0, 500);
			this.bonkStickDurability = builder
					.comment("Base durability of the Bonk Stick")
					.worldRestart()
					.defineInRange("bonkStickDurability", 100, 1, 1000000);
			this.infusedDaggerDamage = builder
					.comment("Base damage of the Bonk Stick")
					.worldRestart()
					.defineInRange("infusedDaggerDamage", 3, 0, 500);
			this.infusedDaggerDurability = builder
					.comment("Base durability of the Infused Dagger")
					.worldRestart()
					.defineInRange("infusedDaggerDurability", 100, 1, 1000000);
			builder.pop();
			builder.push("Gameplay Config");
			this.witchArmor = builder
					.comment("Whether the Witch applies potion effects to entities hitting it")
					.worldRestart()
					.define("witchArmor", true);
			this.witchArmorChance = builder
					.comment("One in this many chance that witch armor activates.")
					.worldRestart()
					.defineInRange("witchAmorChance", 3, 1, 10000000);
			builder.pop();
		}
	}
//	public static final ForgeConfigSpec clientSpec;
//	public static final Client CLIENT;
//
//	static {
//		final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
//		clientSpec = specPair.getRight();
//		CLIENT = specPair.getLeft();
//	}

	public static final ForgeConfigSpec commonSpec;
	public static final Common COMMON;

	static {
		final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
		commonSpec = specPair.getRight();
		COMMON = specPair.getLeft();
	}

	/** Registers any relevant listeners for config */
	public static void init() {
		LOGGER.info("Registering config");
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.commonSpec);
//		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.clientSpec);

		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(Config::configChanged);
		LOGGER.info("Config registered");
	}

	private static void configChanged(ModConfig.Reloading event) {
		ModConfig config = event.getConfig();
		if (config.getModId().equals(PortableBeaconsMod.MODID)) {
			ForgeConfigSpec spec = config.getSpec();
			if (spec == Config.commonSpec) {
				//maybe do something idk
			}
		}
	}
}
