package wilyan_kramer.portable_beacons.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import wilyan_kramer.portable_beacons.common.container.BenchContainer;
import wilyan_kramer.portable_beacons.common.tileentity.BenchTileEntity;

public class BenchBlock extends Block {

	public static final IntegerProperty[] LEVELS = new IntegerProperty[]{
			ModBlockStateProperties.POTIONEER_LEVEL, 
			ModBlockStateProperties.ARTIFICER_LEVEL, 
			ModBlockStateProperties.SUMMONER_LEVEL};
	
	private static final VoxelShape SHAPE = VoxelShapes.or(Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D), Block.box(1.0D, 16.0D, 1.0D, 5.0D, 18.0D, 5.0D));

	
	public BenchBlock(Properties prop) {
		super(prop);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(LEVELS[0], Integer.valueOf(0))
				.setValue(LEVELS[1], Integer.valueOf(0))
				.setValue(LEVELS[2], Integer.valueOf(0))
				);
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(LEVELS[0], LEVELS[1], LEVELS[2]);
	}
	public BlockRenderType getRenderShape(BlockState p_149645_1_) {
	      return BlockRenderType.MODEL;
	   }
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}
	
	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
		if (stack.hasCustomHoverName()) {
			TileEntity tileEntity = world.getBlockEntity(pos);
			if (tileEntity instanceof BenchTileEntity) {
				((BenchTileEntity) tileEntity).setCustomName(stack.getHoverName());
			}
		}
	}
	
	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult posOnFace) {
		if (!(world.isClientSide)) {
			TileEntity tileEntity = world.getBlockEntity(pos);
			if (tileEntity instanceof BenchTileEntity) {
				INamedContainerProvider containerProvider = new INamedContainerProvider() {
					@Override
					public ITextComponent getDisplayName() {
						return ((BenchTileEntity) tileEntity).getName();
					}

					@Override
					public Container createMenu(int i, PlayerInventory inv, PlayerEntity player) {
						return new BenchContainer(i, world, pos, inv, player);
					}
				};
				NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, tileEntity.getBlockPos());
			}
			else {
				throw new IllegalStateException("huh? You right clicked on a workbench, but it does not have an associated workbench tile entity");
			}
		}
		return ActionResultType.SUCCESS;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
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
}
