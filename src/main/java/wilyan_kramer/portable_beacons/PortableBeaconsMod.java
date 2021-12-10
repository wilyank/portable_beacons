package wilyan_kramer.portable_beacons;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.serialization.Codec;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.curios.api.SlotTypeMessage;
import wilyan_kramer.portable_beacons.common.EventListeners;
import wilyan_kramer.portable_beacons.common.config.Config;
import wilyan_kramer.portable_beacons.common.structures.ModConfiguredStructures;
import wilyan_kramer.portable_beacons.common.structures.ModStructures;
import wilyan_kramer.portable_beacons.setup.ClientSetup;
import wilyan_kramer.portable_beacons.setup.CommonSetup;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


@Mod("portable_beacons")
public class PortableBeaconsMod {
	public static final String MODID = "portable_beacons";

	// Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public PortableBeaconsMod() {
    	
    	Config.init();
    	
    	IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
    	ModStructures.DEFERRED_REGISTRY_STRUCTURE.register(modEventBus);
        
        modEventBus.addListener(this::init);
        modEventBus.addListener(this::enqueueIMC);
        modEventBus.addListener(this::processIMC);
        modEventBus.addListener(ClientSetup::init);

        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        forgeBus.register(new CommonSetup());
        forgeBus.register(new EventListeners());
        forgeBus.addListener(EventPriority.NORMAL, this::addDimensionalSpacing);

        // The comments for BiomeLoadingEvent and StructureSpawnListGatherEvent says to do HIGH for additions.
        forgeBus.addListener(EventPriority.HIGH, this::biomeModification);
    }
    
    private void init(final FMLCommonSetupEvent event) {
    	event.enqueueWork(() -> {
            ModStructures.setupStructures();
            ModConfiguredStructures.registerConfiguredStructures();
        });
	}
    
    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        InterModComms.sendTo("curios", "register_type", () -> new SlotTypeMessage.Builder("necklace").build());
    	InterModComms.sendTo("curios", "register_type", () -> new SlotTypeMessage.Builder("back").build());
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
//        LOGGER.info("Got IMC {}", event.getIMCStream().
//                map(m->m.getMessageSupplier().get()).
//                collect(Collectors.toList()));
    }
    
    public void biomeModification(final BiomeLoadingEvent event) {
    	if (event.getCategory() != Biome.Category.BEACH 
    			&& event.getCategory() != Biome.Category.OCEAN 
    			&& event.getCategory() != Biome.Category.NETHER 
    			&& event.getCategory() != Biome.Category.THEEND) {
            event.getGeneration().getStructures().add(() -> ModConfiguredStructures.configured_workshop_room);
    	}
    	if (event.getCategory() == Biome.Category.NETHER && false) {
            event.getGeneration().getStructures().add(() -> ModConfiguredStructures.configured_workshop_room_nether);
    	}
    }
    private static Method GETCODEC_METHOD;
    @SuppressWarnings("unchecked")
	public void addDimensionalSpacing(final WorldEvent.Load event) {
    	if(event.getWorld() instanceof ServerWorld){
            ServerWorld serverWorld = (ServerWorld)event.getWorld();
            ServerChunkProvider chunkSource = serverWorld.getChunkSource();
			try {
                if(GETCODEC_METHOD == null) GETCODEC_METHOD = ObfuscationReflectionHelper.findMethod(ChunkGenerator.class, "func_230347_a_");
                ResourceLocation cgRL = Registry.CHUNK_GENERATOR.getKey((Codec<? extends ChunkGenerator>) GETCODEC_METHOD.invoke(chunkSource.generator));
                if(cgRL != null && cgRL.getNamespace().equals("terraforged")) return;
            }
            catch(Exception e){
                PortableBeaconsMod.LOGGER.error("Was unable to check if " + serverWorld.dimension().location() + " is using Terraforged's ChunkGenerator.");
            }

            if(chunkSource.getGenerator() instanceof FlatChunkGenerator &&
                serverWorld.dimension().equals(World.OVERWORLD)){
                return;
            }
            if(!serverWorld.dimension().equals(World.OVERWORLD)) {
            	return;
            }

            /*
             * putIfAbsent so people can override the spacing with dimension datapacks themselves if they wish to customize spacing more precisely per dimension.
             * Requires AccessTransformer  (see resources/META-INF/accesstransformer.cfg)
             *
             * NOTE: if you add per-dimension spacing configs, you can't use putIfAbsent as WorldGenRegistries.NOISE_GENERATOR_SETTINGS in FMLCommonSetupEvent
             * already added your default structure spacing to some dimensions. You would need to override the spacing with .put(...)
             * And if you want to do dimension blacklisting, you need to remove the spacing entry entirely from the map below to prevent generation safely.
             */
            Map<Structure<?>, StructureSeparationSettings> tempMap = new HashMap<>(chunkSource.generator.getSettings().structureConfig());
            if (serverWorld.dimension().equals(World.OVERWORLD)) {
                tempMap.putIfAbsent(ModStructures.workshop_room.get(), DimensionStructuresSettings.DEFAULTS.get(ModStructures.workshop_room.get()));
            }
            else if (serverWorld.dimension().equals(World.NETHER) && false) {
                tempMap.putIfAbsent(ModStructures.workshop_room_nether.get(), DimensionStructuresSettings.DEFAULTS.get(ModStructures.workshop_room_nether.get()));
            }
            chunkSource.generator.getSettings().structureConfig = tempMap;
        }
    }
}
