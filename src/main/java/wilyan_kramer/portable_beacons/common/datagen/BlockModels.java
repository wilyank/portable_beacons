package wilyan_kramer.portable_beacons.common.datagen;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelBuilder.FaceRotation;
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
				.allFaces((direction, faceBuilder) -> faceBuilder.texture("#planks").uvs(0, 0, 3, 14))
				.face(Direction.DOWN).uvs(3, 0, 6, 3).end()
				.end()
				.texture("planks", modLoc("block/bench_leg"));
		// right side of the table top
		BlockModelBuilder tableTopLeft = models().getBuilder("block/bench/top_left")
				.element().from(0, 14, 0).to(16, 16, 16)
				.allFaces((direction, faceBuilder) -> faceBuilder.texture("#side"))
				.face(Direction.UP).texture("#top").end()
				.face(Direction.DOWN).texture("#bottom").end()
				.end()
				.texture("top", modLoc("block/bench_top_left"))
				.texture("bottom", modLoc("block/bench_bottom_left"))
				.texture("side", modLoc("block/bench_top_left"));
		
		// left side of the table top
		BlockModelBuilder tableTopRight = models().getBuilder("block/bench/top_right")
				.element().from(0, 14, 0).to(16, 16, 16)
				.allFaces((direction, faceBuilder) -> faceBuilder.texture("#side"))
				.face(Direction.UP).texture("#top").end()
				.face(Direction.DOWN).texture("#bottom").end()
				.end()
				.texture("top", modLoc("block/bench_top_right"))
				.texture("bottom", modLoc("block/bench_bottom_right"))
				.texture("side", modLoc("block/bench_top_right"));
		
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
		addBottleDecoration(bottle, 10.0F, 16.01F, 0.0F, 0.5F);
		
		// totem of undying item
		BlockModelBuilder totem = models().getBuilder("block/bench/totem");
		addTotemDecoration(totem, 0.0F, 16.01F, 0.0F, 0.4F);
		
		// wither skull
		BlockModelBuilder skull = models().getBuilder("block/bench/skull");
		addSkullDecoration(skull, 4.0F, 18.01F, 4.0F, 0.6F, 45.0F);
		
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

//		// the drawer
		bld.part().modelFile(drawer).rotationY(0).addModel().condition(BenchBlock.PART, BenchPart.LEFT).condition(BenchBlock.FACING, Direction.SOUTH);
		bld.part().modelFile(drawer).rotationY(90).addModel().condition(BenchBlock.PART, BenchPart.LEFT).condition(BenchBlock.FACING, Direction.WEST);
		bld.part().modelFile(drawer).rotationY(180).addModel().condition(BenchBlock.PART, BenchPart.LEFT).condition(BenchBlock.FACING, Direction.NORTH);
		bld.part().modelFile(drawer).rotationY(270).addModel().condition(BenchBlock.PART, BenchPart.LEFT).condition(BenchBlock.FACING, Direction.EAST);
		
		// the decorations
		bld.part().modelFile(bottle).rotationY(0).addModel().condition(BenchBlock.PART, BenchPart.RIGHT).condition(BenchBlock.LEVELS[0], Integer.valueOf(1)).condition(BenchBlock.FACING, Direction.EAST);
		bld.part().modelFile(totem).rotationY(0).addModel().condition(BenchBlock.PART, BenchPart.RIGHT).condition(BenchBlock.LEVELS[2], Integer.valueOf(2)).condition(BenchBlock.FACING, Direction.EAST);
		bld.part().modelFile(skull).rotationY(0).addModel().condition(BenchBlock.PART, BenchPart.LEFT).condition(BenchBlock.LEVELS[2], Integer.valueOf(1)).condition(BenchBlock.FACING, Direction.EAST);

		
		bld.part().modelFile(bottle).rotationY(90).addModel().condition(BenchBlock.PART, BenchPart.RIGHT).condition(BenchBlock.LEVELS[0], Integer.valueOf(1)).condition(BenchBlock.FACING, Direction.SOUTH);
		bld.part().modelFile(totem).rotationY(90).addModel().condition(BenchBlock.PART, BenchPart.RIGHT).condition(BenchBlock.LEVELS[2], Integer.valueOf(2)).condition(BenchBlock.FACING, Direction.SOUTH);
		bld.part().modelFile(skull).rotationY(90).addModel().condition(BenchBlock.PART, BenchPart.LEFT).condition(BenchBlock.LEVELS[2], Integer.valueOf(1)).condition(BenchBlock.FACING, Direction.SOUTH);

		
		bld.part().modelFile(bottle).rotationY(180).addModel().condition(BenchBlock.PART, BenchPart.RIGHT).condition(BenchBlock.LEVELS[0], Integer.valueOf(1)).condition(BenchBlock.FACING, Direction.WEST);
		bld.part().modelFile(totem).rotationY(180).addModel().condition(BenchBlock.PART, BenchPart.RIGHT).condition(BenchBlock.LEVELS[2], Integer.valueOf(2)).condition(BenchBlock.FACING, Direction.WEST);
		bld.part().modelFile(skull).rotationY(180).addModel().condition(BenchBlock.PART, BenchPart.LEFT).condition(BenchBlock.LEVELS[2], Integer.valueOf(1)).condition(BenchBlock.FACING, Direction.WEST);

		
		bld.part().modelFile(bottle).rotationY(270).addModel().condition(BenchBlock.PART, BenchPart.RIGHT).condition(BenchBlock.LEVELS[0], Integer.valueOf(1)).condition(BenchBlock.FACING, Direction.NORTH);
		bld.part().modelFile(totem).rotationY(270).addModel().condition(BenchBlock.PART, BenchPart.RIGHT).condition(BenchBlock.LEVELS[2], Integer.valueOf(2)).condition(BenchBlock.FACING, Direction.NORTH);
		bld.part().modelFile(skull).rotationY(270).addModel().condition(BenchBlock.PART, BenchPart.LEFT).condition(BenchBlock.LEVELS[2], Integer.valueOf(1)).condition(BenchBlock.FACING, Direction.NORTH);
	}
	private void addBottleDecoration(BlockModelBuilder builder, float x, float y, float z, float scale) {
		
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
	private void addTotemDecoration(BlockModelBuilder builder, float x, float y, float z, float scale) {
		// body
		addBlockWithuvs(builder, x, y, z, 5.0F, 0.0F, 1.0F, 6.0F, 1.0F, 14.0F, scale, "#totem");
		addBlockWithuvs(builder, x, y, z, 6.0F, 0.0F, 15.0F, 4.0F, 1.0F, 1.0F, scale, "#totem");
		// left
		addBlockWithuvs(builder, x, y, z, 4.0F, 0.0F, 2.0F, 1.0F, 1.0F, 11.0F, scale, "#totem");
		addBlockWithuvs(builder, x, y, z, 2.0F, 0.0F, 8.0F, 2.0F, 1.0F, 3.0F, scale, "#totem");
		addBlockWithuvs(builder, x, y, z, 1.0F, 0.0F, 8.0F, 1.0F, 1.0F, 2.0F, scale, "#totem");
		// right
		addBlockWithuvs(builder, x, y, z, 11.0F, 0.0F, 2.0F, 1.0F, 1.0F, 11.0F, scale, "#totem");
		addBlockWithuvs(builder, x, y, z, 12.0F, 0.0F, 8.0F, 2.0F, 1.0F, 3.0F, scale, "#totem");
		addBlockWithuvs(builder, x, y, z, 14.0F, 0.0F, 8.0F, 1.0F, 1.0F, 2.0F, scale, "#totem");


		builder.texture("totem", mcLoc("item/totem_of_undying"));
	}
	private void addSkullDecoration(BlockModelBuilder builder, float ox, float oy, float oz, float scale, float angleY) {
		String texture = "#wither_skeleton";
		builder.element()
		.from(ox - 4 * scale, oy - 4 * scale, oz - 4 * scale)
		.to(ox + 4 * scale, oy + 4 * scale, oz + 4 * scale)
		.rotation().angle(angleY).axis(Axis.Y).origin(ox + 4.0F * scale, oy + 4.0F * scale, oz + 4.0F * scale).end()
		.face(Direction.UP).texture(texture).uvs(8, 0, 16, 8).end()
		.face(Direction.NORTH).texture(texture).uvs(0, 0, 8, 8).end()
		.face(Direction.EAST).texture(texture).uvs(0, 8, 8, 16).rotation(FaceRotation.UPSIDE_DOWN).end()
		.face(Direction.SOUTH).texture(texture).uvs(8, 8, 16, 16).end()
		.face(Direction.WEST).texture(texture).uvs(0, 8, 8, 16).end()
		.end().texture("wither_skeleton", modLoc("block/bench_skull"))
		;
	}
	
	
	private void addBlockWithuvs(BlockModelBuilder builder, float ox, float oy, float oz, float sx, float sy, float sz, float dx, float dy, float dz, float scale, String texture) {
		builder.element()
		.from(ox + sx * scale, oy + sy * scale, oz + sz * scale)
		.to(ox + (sx + dx)* scale, oy + (sy + dy) * scale, oz + (sz + dz) * scale)
		.face(Direction.UP).texture(texture).uvs(sx, sz, sx + dx, sz + dz).end()
		.face(Direction.NORTH).texture(texture).uvs(sx, sz, sx + dx, sz + 1).end()
		.face(Direction.EAST).texture(texture).uvs(sx + dx - 1, sz, sx + dx, sz + dz).rotation(FaceRotation.CLOCKWISE_90).end()
		.face(Direction.SOUTH).texture(texture).uvs(sx, sz + dz - 1, sx + dx, sz + dz).end()
		.face(Direction.WEST).texture(texture).uvs(sx, sz, sx + 1, sz + dz).rotation(FaceRotation.COUNTERCLOCKWISE_90).end();
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
}