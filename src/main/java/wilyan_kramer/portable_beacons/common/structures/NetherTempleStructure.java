package wilyan_kramer.portable_beacons.common.structures;

import org.apache.logging.log4j.Level;

import com.mojang.serialization.Codec;

import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
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
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import wilyan_kramer.portable_beacons.PortableBeaconsMod;


public class NetherTempleStructure extends Structure<NoFeatureConfig>{

	public NetherTempleStructure(Codec<NoFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public IStartFactory<NoFeatureConfig> getStartFactory() {
		return NetherTempleStructure.Start::new;
	}
	@Override
    public GenerationStage.Decoration step() {
        return GenerationStage.Decoration.SURFACE_STRUCTURES;
    }
	
//	@Override
//	public boolean isFeatureChunk(ChunkGenerator chunkGenerator, BiomeProvider biomeSource, long seed, SharedSeedRandom chunkRandom, int chunkX, int chunkZ, Biome biome, ChunkPos chunkPos, NoFeatureConfig featureConfig) {
//		
//        //return true;
//	}
	protected boolean isFeatureChunk(ChunkGenerator chunkGenerator, BiomeProvider biomeSource, 
			long seed, SharedSeedRandom chunkRandom, 
			int chunkX, int chunkZ, Biome biome, ChunkPos chunkPos, NoFeatureConfig featureConfig) {
			return !this.isNearFortress(chunkGenerator, seed, chunkRandom, chunkX, chunkZ) && this.isTopBlockLava(chunkGenerator, seed, chunkRandom, chunkX, chunkZ);
	}

	private boolean isTopBlockLava(ChunkGenerator chunkGenerator, long seed, SharedSeedRandom chunkRandom, int chunkX,
			int chunkZ) {
		BlockPos centerOfChunk = new BlockPos(chunkX * 16, chunkGenerator.getSeaLevel() - 1, chunkZ * 16);
		//
		IBlockReader columnOfBlocks = chunkGenerator.getBaseColumn(centerOfChunk.getX(), centerOfChunk.getZ());
		BlockState block = columnOfBlocks.getBlockState(centerOfChunk);
		//return block.getFluidState().is(FluidTags.LAVA);
		return !block.getFluidState().isEmpty();
	}

	private boolean isNearFortress(ChunkGenerator chunkGenerator, long seed, SharedSeedRandom chunkRandom, int chunkX, int chunkZ) {
	      StructureSeparationSettings structureseparationsettings = chunkGenerator.getSettings().getConfig(Structure.NETHER_BRIDGE);
	      if (structureseparationsettings == null) {
	         return false;
	      } else {
	         for(int i = chunkX - 5; i <= chunkX + 5; ++i) {
	            for(int j = chunkZ - 5; j <= chunkZ + 5; ++j) {
	               ChunkPos chunkpos = Structure.NETHER_BRIDGE.getPotentialFeatureChunk(structureseparationsettings, seed, chunkRandom, i, j);
	               if (i == chunkpos.x && j == chunkpos.z) {
	                  return true;
	               }
	            }
	         }

	         return false;
	      }
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
            
            BlockPos centerPos = new BlockPos(x, chunkGenerator.getSeaLevel() + 1, z);            
            
            JigsawManager.addPieces(dynamicRegistryManager, 
            		new VillageConfig(() -> dynamicRegistryManager.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY)
            				.get(new ResourceLocation(PortableBeaconsMod.MODID, "nether_temple/start_pool")), 10),
            		AbstractVillagePiece::new,
            		chunkGenerator,
            		templateManagerIn,
            		centerPos,
            		this.pieces,
            		this.random,
            		false,
            		false);
            //Vector3i structureCenter = this.pieces.get(0).getBoundingBox().getCenter();
            int xOffset = 0;
            int zOffset = 0;
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

