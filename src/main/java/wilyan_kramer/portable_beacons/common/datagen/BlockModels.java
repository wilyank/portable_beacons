package wilyan_kramer.portable_beacons.common.datagen;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import wilyan_kramer.portable_beacons.PortableBeaconsMod;
import wilyan_kramer.portable_beacons.common.block.BenchBlock;
import wilyan_kramer.portable_beacons.common.block.BenchPart;
import wilyan_kramer.portable_beacons.common.block.BlockList;

public class BlockModels extends BlockStateProvider {

	public BlockModels(DataGenerator gen, ExistingFileHelper exFileHelper) {
		super(gen, PortableBeaconsMod.MODID, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		this.simpleBlock(BlockList.nether_star_block.getBlock());
		this.registerBenchMultipart();
	}

	private void registerBenchMultipart() {
		// create the base model, which is the table top.
		BlockModelBuilder plain_table = models().getBuilder("block/bench/main");
		
		assembleBenchModel(BlockList.bench, plain_table);
	}

	private void assembleBenchModel(Block block, BlockModelBuilder modelIn) {
		
		// defining sub-models that are conditionally added and rotated
		
		// table leg
		BlockModelBuilder tableLeg = models().getBuilder("block/bench/leg")
				.element().from(0, 0, 0).to(3, 14, 3)
				.allFaces((direction, faceBuilder) -> faceBuilder.texture("#planks"))
				.end()
				.texture("planks", mcLoc("block/birch_planks"));
		// right side of the table top
		BlockModelBuilder tableTopLeft = models().getBuilder("block/bench/top_left")
				.element().from(0, 14, 0).to(16, 16, 16)
				.allFaces((direction, faceBuilder) -> faceBuilder.texture("#side"))
				.face(Direction.UP).texture("#top").end()
				.face(Direction.DOWN).texture("#bottom").end()
				.end()
				.texture("top", modLoc("block/bench_top_left"))
				.texture("bottom", mcLoc("block/birch_planks"))
				.texture("side", mcLoc("block/smithing_table_side"));
		
		// left side of the table top
		BlockModelBuilder tableTopRight = models().getBuilder("block/bench/top_right")
				.element().from(0, 14, 0).to(16, 16, 16)
				.allFaces((direction, faceBuilder) -> faceBuilder.texture("#side"))
				.face(Direction.UP).texture("#top").end()
				.face(Direction.DOWN).texture("#bottom").end()
				.end()
				.texture("top", modLoc("block/bench_top_right"))
				.texture("bottom", mcLoc("block/birch_planks"))
				.texture("side", mcLoc("block/smithing_table_side"));
		
		// the little drawer thing
		BlockModelBuilder drawer = models().getBuilder("block/bench/drawer")
				.element().from(1, 6, 3).to(10, 14, 16)
				.face(Direction.DOWN).texture("#bottom").end()
				.face(Direction.WEST).texture("#front").end()
				.face(Direction.EAST).texture("#back").end()
				.face(Direction.NORTH).texture("#side_left").end()
				.face(Direction.SOUTH).texture("#side_right").end()
				.end()
				.texture("side_left", modLoc("block/bench_drawer_side_left"))
				.texture("side_right", modLoc("block/bench_drawer_side_right"))
				.texture("bottom", modLoc("block/bench_drawer_bottom"))
				.texture("back", modLoc("block/bench_drawer_back"))
				.texture("front", modLoc("block/bench_drawer_front"));
		
		// potioneer bottle
		BlockModelBuilder bottle = models().getBuilder("block/bench/bottle");
		addBottle(bottle, 3.0F, 16.0F, 2.0F, 0.5F);
		
		MultiPartBlockStateBuilder bld = getMultipartBuilder(block);
		BlockModelBuilder[] models = new BlockModelBuilder[] { tableTopLeft, tableTopRight };
		for (BenchPart part : BenchPart.values()) {
			bld.part().modelFile(models[part.ordinal()]).rotationY(0).addModel().condition(BenchBlock.PART, part).condition(BenchBlock.FACING, Direction.EAST);
			bld.part().modelFile(models[part.ordinal()]).rotationY(90).addModel().condition(BenchBlock.PART, part).condition(BenchBlock.FACING, Direction.SOUTH);
			bld.part().modelFile(models[part.ordinal()]).rotationY(180).addModel().condition(BenchBlock.PART, part).condition(BenchBlock.FACING, Direction.WEST);
			bld.part().modelFile(models[part.ordinal()]).rotationY(270).addModel().condition(BenchBlock.PART, part).condition(BenchBlock.FACING, Direction.NORTH);
		}
		
		// two legs for left - south
		bld.part().modelFile(tableLeg).addModel().condition(BenchBlock.PART, BenchPart.LEFT).condition(BenchBlock.FACING, Direction.SOUTH);
		bld.part().modelFile(tableLeg).rotationY(90).addModel().condition(BenchBlock.PART, BenchPart.LEFT).condition(BenchBlock.FACING, Direction.SOUTH);
		
		// two legs for left - west
		bld.part().modelFile(tableLeg).rotationY(90).addModel().condition(BenchBlock.PART, BenchPart.LEFT).condition(BenchBlock.FACING, Direction.WEST);
		bld.part().modelFile(tableLeg).rotationY(180).addModel().condition(BenchBlock.PART, BenchPart.LEFT).condition(BenchBlock.FACING, Direction.WEST);
		
		// two legs for left - north
		bld.part().modelFile(tableLeg).rotationY(180).addModel().condition(BenchBlock.PART, BenchPart.LEFT).condition(BenchBlock.FACING, Direction.NORTH);
		bld.part().modelFile(tableLeg).rotationY(270).addModel().condition(BenchBlock.PART, BenchPart.LEFT).condition(BenchBlock.FACING, Direction.NORTH);
		
		// two legs for left - east
		bld.part().modelFile(tableLeg).rotationY(0).addModel().condition(BenchBlock.PART, BenchPart.LEFT).condition(BenchBlock.FACING, Direction.EAST);
		bld.part().modelFile(tableLeg).rotationY(270).addModel().condition(BenchBlock.PART, BenchPart.LEFT).condition(BenchBlock.FACING, Direction.EAST);
		
		// two legs for right - south
		bld.part().modelFile(tableLeg).rotationY(180).addModel().condition(BenchBlock.PART, BenchPart.RIGHT).condition(BenchBlock.FACING, Direction.SOUTH);
		bld.part().modelFile(tableLeg).rotationY(270).addModel().condition(BenchBlock.PART, BenchPart.RIGHT).condition(BenchBlock.FACING, Direction.SOUTH);
		// two legs for right - west
		bld.part().modelFile(tableLeg).rotationY(0).addModel().condition(BenchBlock.PART, BenchPart.RIGHT).condition(BenchBlock.FACING, Direction.WEST);
		bld.part().modelFile(tableLeg).rotationY(270).addModel().condition(BenchBlock.PART, BenchPart.RIGHT).condition(BenchBlock.FACING, Direction.WEST);
		// two legs for right - north
		bld.part().modelFile(tableLeg).rotationY(0).addModel().condition(BenchBlock.PART, BenchPart.RIGHT).condition(BenchBlock.FACING, Direction.NORTH);
		bld.part().modelFile(tableLeg).rotationY(90).addModel().condition(BenchBlock.PART, BenchPart.RIGHT).condition(BenchBlock.FACING, Direction.NORTH);
		// two legs for right - east
		bld.part().modelFile(tableLeg).rotationY(90).addModel().condition(BenchBlock.PART, BenchPart.RIGHT).condition(BenchBlock.FACING, Direction.EAST);
		bld.part().modelFile(tableLeg).rotationY(180).addModel().condition(BenchBlock.PART, BenchPart.RIGHT).condition(BenchBlock.FACING, Direction.EAST);

		// the drawer
		bld.part().modelFile(drawer).rotationY(0).addModel().condition(BenchBlock.PART, BenchPart.LEFT).condition(BenchBlock.FACING, Direction.SOUTH);
		bld.part().modelFile(drawer).rotationY(90).addModel().condition(BenchBlock.PART, BenchPart.LEFT).condition(BenchBlock.FACING, Direction.WEST);
		bld.part().modelFile(drawer).rotationY(180).addModel().condition(BenchBlock.PART, BenchPart.LEFT).condition(BenchBlock.FACING, Direction.NORTH);
		bld.part().modelFile(drawer).rotationY(270).addModel().condition(BenchBlock.PART, BenchPart.LEFT).condition(BenchBlock.FACING, Direction.EAST);
		
		// the decorations
		bld.part().modelFile(bottle).addModel().condition(BenchBlock.PART, BenchPart.RIGHT).condition(BenchBlock.LEVELS[0], Integer.valueOf(1));
					
	}
	private void addBottle(BlockModelBuilder builder, float x, float y, float z, float scale) {
		
		// bottle floor
		addOffsetAndScaledTransparentCuboid(builder, x, y, z,  5, 0, 5, 6, 1, 6, scale);
		//bottle walls
		addOffsetAndScaledTransparentCuboid(builder, x, y ,z, 5, 1, 4, 6, 6, 1, scale);
		addOffsetAndScaledTransparentCuboid(builder, x, y, z, 4, 1, 5, 1, 6, 6, scale);
		addOffsetAndScaledTransparentCuboid(builder, x, y, z, 11, 1, 5, 1, 6, 6, scale);
		addOffsetAndScaledTransparentCuboid(builder, x, y, z, 5, 1, 11, 6, 6, 1, scale);
		// bottle roof
		addOffsetAndScaledTransparentCuboid(builder, x, y, z, 5, 7, 5, 6, 1, 6, scale);
		// bottle neck
		addOffsetAndScaledTransparentCuboid(builder, x, y, z, 7, 8, 6, 2, 3, 1, scale);
		addOffsetAndScaledTransparentCuboid(builder, x, y, z, 6, 8, 7, 1, 3, 2, scale);
		addOffsetAndScaledTransparentCuboid(builder, x, y, z, 9, 8, 6, 2, 3, 1, scale);
		addOffsetAndScaledTransparentCuboid(builder, x, y, z, 6, 8, 9, 1, 3, 2, scale);
		// bottle head
		addOffsetAndScaledTransparentCuboid(builder, x, y, z, 6, 10, 6, 1, 2, 1, scale);
		addOffsetAndScaledTransparentCuboid(builder, x, y, z, 6, 10, 9, 1, 2, 1, scale);
		addOffsetAndScaledTransparentCuboid(builder, x, y, z, 9, 10, 6, 1, 2, 1, scale);
		addOffsetAndScaledTransparentCuboid(builder, x, y, z, 9, 10, 9, 1, 2, 1, scale);
		
		addOffsetAndScaledTransparentCuboid(builder, x, y, z, 7, 10, 5, 2, 2, 1, scale);
		addOffsetAndScaledTransparentCuboid(builder, x, y, z, 7, 10, 10, 2, 2, 1, scale);
		addOffsetAndScaledTransparentCuboid(builder, x, y, z, 5, 10, 7, 1, 2, 2, scale);
		addOffsetAndScaledTransparentCuboid(builder, x, y, z, 10, 10, 7, 1, 2, 2, scale);
		//cork
		addOffsetAndScaledCuboid(builder, x, y, z, 7, 11, 7, 2, 2, 2, scale, "#cork");
		
		//bottle liquid
		addOffsetAndScaledCuboid(builder, x, y, z, 5, 1, 5, 6, 4, 6, scale, "#color");
		

		builder.texture("glass", mcLoc("block/glass"))
		.texture("color", mcLoc("block/purple_stained_glass"))
		.texture("cork", mcLoc("block/terracotta"));

	}
	private void addOffsetAndScaledTransparentCuboid(BlockModelBuilder builder, float ox, float oy, float oz, float sx, float sy, float sz, float dx, float dy, float dz, float scale) {
		addOffsetAndScaledCuboid(builder, ox, oy, oz, sx, sy, sz, dx, dy, dz, scale, "#glass");
	}
	private void addOffsetAndScaledCuboid(BlockModelBuilder builder, float ox, float oy, float oz, float sx, float sy, float sz, float dx, float dy, float dz, float scale, String texture) {
		builder.element()
		.from(ox + sx * scale, oy +  sy * scale, oz + sz * scale)
		.to(ox + (sx + dx)* scale, oy + (sy + dy) * scale, oz + (sz +dz) * scale)
		.allFaces((direction, faceBuilder) -> faceBuilder.texture(texture).uvs(0, 0, 16, 16));
	}
	private void addOffsetAndScaledCuboid2(BlockModelBuilder builder, float ox, float oy, float oz, float sx, float sy, float sz, float dx, float dy, float dz, float scale, String texture, String texture2) {
		builder.element()
		.from(ox + sx * scale, oy +  sy * scale, oz + sz * scale)
		.to(ox + (sx + dx)* scale, oy + (sy + dy) * scale, oz + (sz +dz) * scale)
		.allFaces((direction, faceBuilder) -> faceBuilder.texture(texture).uvs(0, 0, 16, 16))
		.face(Direction.UP).texture(texture2).uvs(0, 0, 16, 16).end()
		.face(Direction.UP).texture(texture2).uvs(0, 0, 16, 16).end();
	}
}