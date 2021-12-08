package wilyan_kramer.portable_beacons.common.structures;

import org.apache.logging.log4j.Level;

import com.mojang.serialization.Codec;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.biome.Biome;
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

public class WorkshopRoomStructure extends Structure<NoFeatureConfig>{

	public WorkshopRoomStructure(Codec<NoFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public IStartFactory<NoFeatureConfig> getStartFactory() {
		return WorkshopRoomStructure.Start::new;
	}
	@Override
    public GenerationStage.Decoration step() {
        return GenerationStage.Decoration.UNDERGROUND_STRUCTURES;
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
            
            BlockPos centerPos = new BlockPos(x, 0, z);
            
            IBlockReader blockReader = chunkGenerator.getBaseColumn(x, z);
            for (int i = 0; i < 50; i++) {
            	BlockState thisBlock = blockReader.getBlockState(centerPos.above(i));
            	BlockState nextBlock = blockReader.getBlockState(centerPos.above(i+1));
                if (thisBlock.getMaterial().isSolid() && nextBlock.getBlock() == Blocks.CAVE_AIR) {
                	centerPos = centerPos.above(i);
                	break;
                }
            }
            if (centerPos.getY() < 6) {
            	centerPos = new BlockPos(x, 50, z);
            }
            
            JigsawManager.addPieces(dynamicRegistryManager, 
            		new VillageConfig(() -> dynamicRegistryManager.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY)
            				.get(new ResourceLocation(PortableBeaconsMod.MODID, "workshop_room/start_pool")), 10),
            		AbstractVillagePiece::new,
            		chunkGenerator,
            		templateManagerIn,
            		centerPos,
            		this.pieces,
            		this.random,
            		false,
            		false);
            Vector3i structureCenter = this.pieces.get(0).getBoundingBox().getCenter();
            int xOffset = centerPos.getX() - structureCenter.getX();
            int zOffset = centerPos.getZ() - structureCenter.getZ();
            for(StructurePiece structurePiece : this.pieces){
            	structurePiece.move(xOffset, 0, zOffset);
            }

            // Sets the bounds of the structure once you are finished.
            this.calculateBoundingBox();

            // I use to debug and quickly find out if the structure is spawning or not and where it is.
            // This is returning the coordinates of the center starting piece.
            PortableBeaconsMod.LOGGER.log(Level.DEBUG, "structure at " +
            		this.pieces.get(0).getBoundingBox().x0 + " " +
            		this.pieces.get(0).getBoundingBox().y0 + " " +
            		this.pieces.get(0).getBoundingBox().z0);
		}
	}

}

