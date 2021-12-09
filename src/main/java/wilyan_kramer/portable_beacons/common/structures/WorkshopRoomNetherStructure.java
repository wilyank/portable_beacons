package wilyan_kramer.portable_beacons.common.structures;

import org.apache.logging.log4j.Level;

import com.mojang.serialization.Codec;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;
import wilyan_kramer.portable_beacons.PortableBeaconsMod;


public class WorkshopRoomNetherStructure extends Structure<NoFeatureConfig>{

	public WorkshopRoomNetherStructure(Codec<NoFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public IStartFactory<NoFeatureConfig> getStartFactory() {
		return WorkshopRoomNetherStructure.Start::new;
	}
	@Override
    public GenerationStage.Decoration step() {
        return GenerationStage.Decoration.UNDERGROUND_DECORATION;
    }
	
	@Override
	public boolean isFeatureChunk(ChunkGenerator chunkGenerator, BiomeProvider biomeSource, long seed, SharedSeedRandom chunkRandom, int chunkX, int chunkZ, Biome biome, ChunkPos chunkPos, NoFeatureConfig featureConfig) {
//		BlockPos centerOfChunk = new BlockPos(chunkX * 16, 0, chunkZ * 16);

//        int landHeight = chunkGenerator.getFirstOccupiedHeight(centerOfChunk.getX(), centerOfChunk.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
//        IBlockReader columnOfBlocks = chunkGenerator.getBaseColumn(centerOfChunk.getX(), centerOfChunk.getZ());
//        BlockState topBlock = columnOfBlocks.getBlockState(centerOfChunk.above(landHeight));

        return true;
	}

	public static class Start extends StructureStart<NoFeatureConfig> {
		public Start(Structure<NoFeatureConfig> structureIn, int chunkX, int chunkZ, MutableBoundingBox mutableBoundingBox, int referenceIn, long seedIn) {
			super(structureIn, chunkX, chunkZ, mutableBoundingBox, referenceIn, seedIn);
		}

		@Override
		public void generatePieces(DynamicRegistries dynamicRegistryManager, ChunkGenerator chunkGenerator,
				TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biome,
				NoFeatureConfig config) {
			int x = chunkX * 16;
            int z = chunkZ * 16;
            
            int structureStartHeight = random.nextInt(25) + 6;
            BlockPos centerPos = new BlockPos(x, structureStartHeight, z);            
            
            JigsawManager.addPieces(dynamicRegistryManager, 
            		new VillageConfig(() -> dynamicRegistryManager.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY)
            				.get(new ResourceLocation(PortableBeaconsMod.MODID, "workshop_room_nether/start_pool")), 10),
            		AbstractVillagePiece::new,
            		chunkGenerator,
            		templateManagerIn,
            		centerPos,
            		this.pieces,
            		this.random,
            		false,
            		false);
            //Vector3i structureCenter = this.pieces.get(0).getBoundingBox().getCenter();
            int xOffset = 0; //centerPos.getX() - structureCenter.getX();
            int zOffset = 0; //centerPos.getZ() - structureCenter.getZ();
            for(StructurePiece structurePiece : this.pieces){
            	structurePiece.move(xOffset, 0, zOffset);
            }

            // Sets the bounds of the structure once you are finished.
            this.calculateBoundingBox();

            // I use to debug and quickly find out if the structure is spawning or not and where it is.
            // This is returning the coordinates of the center starting piece.
            // looks more like the negative corner of the bounding box tbh
            PortableBeaconsMod.LOGGER.log(Level.DEBUG, "structure at " +
            		this.pieces.get(0).getBoundingBox().x0 + " " +
            		this.pieces.get(0).getBoundingBox().y0 + " " +
            		this.pieces.get(0).getBoundingBox().z0);
		}
	}

}

