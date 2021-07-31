package com.wilyan_kramer.portable_beacons.common.config;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wilyan_kramer.portable_beacons.PortableBeaconsMod;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

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

		Common(ForgeConfigSpec.Builder builder) {
			builder.comment("Common config for Portable Beacons").push("portable_beacons");
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
