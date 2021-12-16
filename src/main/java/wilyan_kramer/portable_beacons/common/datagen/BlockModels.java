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
		
		//potioneer bottle
		//TODO
		
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
					
	}
	private void addBottle(BlockModelBuilder builder, float x, float y, float z, float scale) {
		
	}
	private void addTransparentBlock(BlockModelBuilder builder, float fx, float fy, float fz, float tx, float ty, float tz) {
		builder.element().from(fx, fy, fz).to(tx, ty, tz).allFaces((direction, faceBuilder) -> faceBuilder.texture("#glass")).end().texture("glass", mcLoc("block/black_stained_glass"))
	}
	
}