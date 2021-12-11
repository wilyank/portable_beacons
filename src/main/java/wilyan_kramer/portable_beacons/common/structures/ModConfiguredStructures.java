package wilyan_kramer.portable_beacons.common.structures;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import wilyan_kramer.portable_beacons.PortableBeaconsMod;

public class ModConfiguredStructures {
    public static StructureFeature<?, ?> configured_workshop_room = ModStructures.workshop_room.get().configured(IFeatureConfig.NONE);
    public static StructureFeature<?, ?> configured_nether_temple = ModStructures.nether_temple.get().configured(IFeatureConfig.NONE);

    public static void registerConfiguredStructures() {
        Registry<StructureFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE;
        Registry.register(registry, new ResourceLocation(PortableBeaconsMod.MODID, "workshop_room"), configured_workshop_room);
        Registry.register(registry, new ResourceLocation(PortableBeaconsMod.MODID, "nether_temple"), configured_nether_temple);

        FlatGenerationSettings.STRUCTURE_FEATURES.put(ModStructures.workshop_room.get(), configured_workshop_room);
        FlatGenerationSettings.STRUCTURE_FEATURES.put(ModStructures.nether_temple.get(), configured_nether_temple);

    }

}
