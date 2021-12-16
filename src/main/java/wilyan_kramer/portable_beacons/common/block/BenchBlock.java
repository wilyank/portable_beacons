package wilyan_kramer.portable_beacons.common.block;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;
import wilyan_kramer.portable_beacons.PortableBeaconsMod;
import wilyan_kramer.portable_beacons.common.container.BenchContainer;
import wilyan_kramer.portable_beacons.common.tileentity.BenchTileEntity;

public class BenchBlock extends HorizontalBlock {

	public static final IntegerProperty[] LEVELS = new IntegerProperty[]{
			ModBlockStateProperties.POTIONEER_LEVEL, 
			ModBlockStateProperties.ARTIFICER_LEVEL, 
			ModBlockStateProperties.SUMMONER_LEVEL};
	public static final EnumProperty<BenchPart> PART = ModBlockStateProperties.BENCH_PART;	
		
	protected static final VoxelShape BASE = Block.box(0.0D, 6.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape LEG_NORTH_WEST = Block.box(0.0D, 0.0D, 0.0D, 3.0D, 14.0D, 3.0D);
	protected static final VoxelShape LEG_SOUTH_WEST = Block.box(0.0D, 0.0D, 13.0D, 3.0D, 14.0D, 16.0D);
	protected static final VoxelShape LEG_NORTH_EAST = Block.box(13.0D, 0.0D, 0.0D, 16.0D, 14.0D, 3.0D);
	protected static final VoxelShape LEG_SOUTH_EAST = Block.box(13.0D, 0.0D, 13.0D, 16.0D, 14.0D, 16.0D);
	protected static final VoxelShape NORTH_SHAPE = VoxelShapes.or(BASE, LEG_NORTH_WEST, LEG_NORTH_EAST);
	protected static final VoxelShape SOUTH_SHAPE = VoxelShapes.or(BASE, LEG_SOUTH_WEST, LEG_SOUTH_EAST);
	protected static final VoxelShape WEST_SHAPE = VoxelShapes.or(BASE, LEG_NORTH_WEST, LEG_SOUTH_WEST);
	protected static final VoxelShape EAST_SHAPE = VoxelShapes.or(BASE, LEG_NORTH_EAST, LEG_SOUTH_EAST);
	
	public BenchBlock(Properties prop) {
		super(prop);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(LEVELS[0], Integer.valueOf(0))
				.setValue(LEVELS[1], Integer.valueOf(0))
				.setValue(LEVELS[2], Integer.valueOf(0))
				.setValue(PART, BenchPart.LEFT)
				);
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(LEVELS[0], LEVELS[1], LEVELS[2], PART, FACING);
	}
	public BlockRenderType getRenderShape(BlockState p_149645_1_) {
	      return BlockRenderType.MODEL;
	   }
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader blockReader, BlockPos pos,
			ISelectionContext context) {
		Direction direction = getConnectedDirection(state).getOpposite();
		switch (direction) {
		case NORTH:
			return NORTH_SHAPE;
		case SOUTH:
			return SOUTH_SHAPE;
		case WEST:
			return WEST_SHAPE;
		default:
			return EAST_SHAPE;
		}
	}

	@Nullable
	@OnlyIn(Dist.CLIENT)
	public static Direction getBenchOrientation(IBlockReader blockReader, BlockPos blockPos) {
		BlockState blockstate = blockReader.getBlockState(blockPos);
		return blockstate.getBlock() instanceof BenchBlock ? blockstate.getValue(FACING) : null;
	}
	public static Direction getConnectedDirection(BlockState state) {
		Direction direction = state.getValue(FACING);
		return state.getValue(PART) == BenchPart.RIGHT ? direction.getOpposite() : direction;
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
		super.setPlacedBy(world, pos, state, entity, stack);
		
		if (!world.isClientSide) {
			BlockPos blockpos = pos.relative(state.getValue(FACING));
			world.setBlock(blockpos, state.setValue(PART, BenchPart.RIGHT), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
			world.blockUpdated(pos, Blocks.AIR);
			state.updateNeighbourShapes(world, pos, 3);
		}
		
		if (stack.hasCustomHoverName()) {
			TileEntity tileEntity = world.getBlockEntity(pos);
			if (tileEntity instanceof BenchTileEntity) {
				((BenchTileEntity) tileEntity).setCustomName(stack.getHoverName());
			}
		}
	}
	
	public void printBlockState(BlockState state) {
		ImmutableMap<Property<?>, Comparable<?>> map = state.getValues();
		map.forEach((p,v) -> PortableBeaconsMod.LOGGER.info(p.getName() + " " + v));
	}
	
	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult posOnFace) {
		if (!(world.isClientSide)) {
			int[] currentLevels = new int[] {
					state.getValue(LEVELS[0]), 
					state.getValue(LEVELS[1]),
					state.getValue(LEVELS[2])};
			
			PortableBeaconsMod.LOGGER.info("pos:" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
			printBlockState(state);
			
			BlockPos neighborPos = pos.relative(this.getNeighbourDirection(state.getValue(PART), state.getValue(FACING)));
			PortableBeaconsMod.LOGGER.info("pos:" + neighborPos.getX() + ", " + neighborPos.getY() + ", " + neighborPos.getZ());
			printBlockState(world.getBlockState(neighborPos));
			
			if (player.getItemInHand(hand).getItem() == Items.DIAMOND ||
					player.getItemInHand(hand).getItem() == Items.EMERALD ||
					player.getItemInHand(hand).getItem() == Items.ENDER_EYE) {
				int index = -1;
				if (player.getItemInHand(hand).getItem() == Items.DIAMOND) {
					index = 0;
				}
				else if (player.getItemInHand(hand).getItem() == Items.EMERALD) {
					index = 1;
				}
				else if (player.getItemInHand(hand).getItem() == Items.ENDER_EYE) {
					index = 2;
				}
				if (index != -1 && currentLevels[index] < 5) {
					world.setBlock(pos, state.setValue(LEVELS[index], currentLevels[index] + 1), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
					world.setBlock(neighborPos, world.getBlockState(neighborPos).setValue(LEVELS[index], currentLevels[index] + 1), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
				}
			}
			
			BlockPos leftPartPos = (state.getValue(PART) == BenchPart.LEFT) ? pos : neighborPos;
			
			if (this.hasTileEntity(world.getBlockState(leftPartPos))) {
				TileEntity tileEntity = world.getBlockEntity(leftPartPos);
				if (tileEntity instanceof BenchTileEntity) {
					INamedContainerProvider containerProvider = new INamedContainerProvider() {
						@Override
						public ITextComponent getDisplayName() {
							return ((BenchTileEntity) tileEntity).getName();
						}
						@Override
						public Container createMenu(int i, PlayerInventory inv, PlayerEntity player) {
							return new BenchContainer(i, world, leftPartPos, inv, player);
						}
					};
					NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, tileEntity.getBlockPos());
				}
			}
		}
		return ActionResultType.SUCCESS;
	}

	@SuppressWarnings("deprecation")
	public BlockState updateShape(BlockState blockState, Direction direction, BlockState neighborBlock, IWorld world, BlockPos blockPos, BlockPos neighborBlockPos) {
		if (direction == getNeighbourDirection(blockState.getValue(PART), blockState.getValue(FACING))) {
			if (neighborBlock.is(this) && neighborBlock.getValue(PART) != blockState.getValue(PART)) {
				blockState.setValue(LEVELS[0], neighborBlock.getValue(LEVELS[0]));
				blockState.setValue(LEVELS[1], neighborBlock.getValue(LEVELS[1]));
				blockState.setValue(LEVELS[2], neighborBlock.getValue(LEVELS[2]));
				return blockState;
			}
			else {
				return Blocks.AIR.defaultBlockState();
			}
		} 
		else {
			return super.updateShape(blockState, direction, neighborBlock, world, blockPos, neighborBlockPos);
		}
	}
	
	private Direction getNeighbourDirection(BenchPart benchPart, Direction direction) {
		
		return benchPart == BenchPart.LEFT ? direction : direction.getOpposite();
	}
	public void playerWillDestroy(World world, BlockPos blockPos, BlockState blockState, PlayerEntity player) {
		if (!world.isClientSide && player.isCreative()) {
			BenchPart benchpart = blockState.getValue(PART);
			if (benchpart == BenchPart.LEFT) {
				BlockPos blockpos = blockPos.relative(getNeighbourDirection(benchpart, blockState.getValue(FACING)));
				BlockState blockstate = world.getBlockState(blockpos);
				if (blockstate.getBlock() == this && blockstate.getValue(PART) == BenchPart.RIGHT) {
					world.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
					world.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
				}
			}
		}
		super.playerWillDestroy(world, blockPos, blockState, player);
	}
	
	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		Direction direction = context.getHorizontalDirection().getClockWise();
		BlockPos blockpos = context.getClickedPos();
		BlockPos blockpos1 = blockpos.relative(direction);
		return context.getLevel().getBlockState(blockpos1).canBeReplaced(context) ? this.defaultBlockState().setValue(FACING, direction) : null;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return state.getValue(PART) == BenchPart.LEFT;
	}
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new BenchTileEntity();
	}
	
	//make the TE drop the items in the inventory when the block is destroyed
	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState blockStateOld, World world, BlockPos blockPos, BlockState blockStateNew, boolean bool) {
		if (!blockStateOld.getBlock().is(blockStateNew.getBlock())) {
			TileEntity tileentity = world.getBlockEntity(blockPos);

			if (tileentity instanceof BenchTileEntity) 
				((BenchTileEntity) tileentity).dropItems();
		}
		super.onRemove(blockStateOld, world, blockPos, blockStateNew, bool);
	}
	public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
		return false;
	}
}
