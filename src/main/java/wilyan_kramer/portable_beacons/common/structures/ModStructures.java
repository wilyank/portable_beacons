package wilyan_kramer.portable_beacons.common.structures;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import wilyan_kramer.portable_beacons.PortableBeaconsMod;
import wilyan_kramer.portable_beacons.common.config.Config;

public class ModStructures {
    
	public static final DeferredRegister<Structure<?>> DEFERRED_REGISTRY_STRUCTURE = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, PortableBeaconsMod.MODID);
	
    public static final RegistryObject<Structure<NoFeatureConfig>> workshop_room = DEFERRED_REGISTRY_STRUCTURE.register("workshop_room", () -> (new WorkshopRoomStructure(NoFeatureConfig.CODEC)));
    public static final RegistryObject<Structure<NoFeatureConfig>> nether_temple = DEFERRED_REGISTRY_STRUCTURE.register("nether_temple", () -> (new NetherTempleStructure(NoFeatureConfig.CODEC)));

    
    public static void setupStructures() {
        setupMapSpacingAndLand(
                workshop_room.get(), /* The instance of the structure */
                new StructureSeparationSettings(
                		Config.COMMON.structureWorkshopRoomMaxSpacing.get() /* average distance apart in chunks between spawn attempts */,
                        Config.COMMON.structureWorkshopRoomMinSpacing.get() /* minimum distance apart in chunks between spawn attempts. MUST BE LESS THAN ABOVE VALUE*/,
                        35848128 /* this modifies the seed of the structure so no two structures always spawn over each-other. Make this large and unique. */),
                true);
        setupMapSpacingAndLand(
        		nether_temple.get(),
        		new StructureSeparationSettings(
        				Config.COMMON.structureNetherTempleMaxSpacing.get(),
        				Config.COMMON.structureNetherTempleMinSpacing.get(),
        				35848127),
        		true);


        // Add more structures here and so on
    }

	public static <F extends Structure<?>> void setupMapSpacingAndLand(
			F structure,
			StructureSeparationSettings structureSeparationSettings, 
			boolean transformSurroundingLand) {
        
		Structure.STRUCTURES_REGISTRY.put(structure.getRegistryName().toString(), structure);
        
        DimensionStructuresSettings.DEFAULTS =
                ImmutableMap.<Structure<?>, StructureSeparationSettings>builder()
                        .putAll(DimensionStructuresSettings.DEFAULTS)
                        .put(structure, structureSeparationSettings)
                        .build();

        WorldGenRegistries.NOISE_GENERATOR_SETTINGS.entrySet().forEach(settings -> {
            Map<Structure<?>, StructureSeparationSettings> structureMap = settings.getValue().structureSettings().structureConfig();

            if(structureMap instanceof ImmutableMap){
                Map<Structure<?>, StructureSeparationSettings> tempMap = new HashMap<>(structureMap);
                tempMap.put(structure, structureSeparationSettings);
                settings.getValue().structureSettings().structureConfig = tempMap;
            }
            else{
                structureMap.put(structure, structureSeparationSettings);
            }
        });
	}
}
