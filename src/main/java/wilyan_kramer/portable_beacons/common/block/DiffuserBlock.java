package wilyan_kramer.portable_beacons.common.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;
import wilyan_kramer.portable_beacons.common.container.DiffuserContainer;
import wilyan_kramer.portable_beacons.common.tileentity.DiffuserTileEntity;

public class DiffuserBlock extends Block {
	@SuppressWarnings("deprecation")
	@Override
	 public void onRemove(BlockState blockStateOld, World world, BlockPos blockPos, BlockState blockStateNew, boolean bool) {
	      if (!blockStateOld.getBlock().is(blockStateNew.getBlock())) {
	         TileEntity tileentity = world.getBlockEntity(blockPos);
	         if (tileentity instanceof DiffuserTileEntity) {
	        	 DiffuserTileEntity diffuserTE = (DiffuserTileEntity) tileentity;
	        	 ItemStack itemstack = diffuserTE.getInventoryContent();
	        	 if (!itemstack.isEmpty()) {
	                 world.levelEvent(1010, blockPos, 0);
	                 double d0 = (double)(world.random.nextFloat() * 0.7F) + (double)0.15F;
	                 double d1 = (double)(world.random.nextFloat() * 0.7F) + (double)0.060000002F + 0.6D;
	                 double d2 = (double)(world.random.nextFloat() * 0.7F) + (double)0.15F;
	                 ItemStack itemstack1 = itemstack.copy();
	                 ItemEntity itementity = new ItemEntity(world, (double)blockPos.getX() + d0, (double)blockPos.getY() + d1, (double)blockPos.getZ() + d2, itemstack1);
	                 itementity.setDefaultPickUpDelay();
	                 world.addFreshEntity(itementity);
	              }
	            world.updateNeighbourForOutputSignal(blockPos, this);
	         }
	         super.onRemove(blockStateOld, world, blockPos, blockStateNew, bool);
	      }
	   }

	private static final VoxelShape SHAPE = VoxelShapes.or(Block.box(0.5D, 0D, 0.5D, 15.5D, 1D, 15.5D), Block.box(3.5, 1, 3.5, 12.5, 16, 12.5));
	public DiffuserBlock(Properties prop) {
		super(prop);
		this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.TRIGGERED, false));
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new DiffuserTileEntity();
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.TRIGGERED);
	}
	
	@Override
	public BlockRenderType getRenderShape(BlockState p_149645_1_) {
		return BlockRenderType.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, World world, BlockPos pos) {
		TileEntity tileEntity = world.getBlockEntity(pos);
		if (tileEntity instanceof DiffuserTileEntity) {
			int value = (int) Math.floor( (Math.log10( ((DiffuserTileEntity) tileEntity).getDuration() / 20) )/(Math.log10(2F)));
			value = value > 0 ? value < 15 ? value : 15 : 0;
			return value;
		}
		else {
			return 0;
		}
	}

	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos pos2, boolean bool) {
		boolean flag = world.hasNeighborSignal(pos);
		boolean flag1 = state.getValue(BlockStateProperties.TRIGGERED);
		if (flag && !flag1) {
			world.getBlockTicks().scheduleTick(pos, this, 4);
			world.setBlock(pos, state.setValue(BlockStateProperties.TRIGGERED, Boolean.valueOf(true)), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
		} else if (!flag && flag1) {
			world.getBlockTicks().scheduleTick(pos, this, 4);
			world.setBlock(pos, state.setValue(BlockStateProperties.TRIGGERED, Boolean.valueOf(false)), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
		}

	}
	
	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
		if (stack.hasCustomHoverName()) {
			TileEntity tileEntity = world.getBlockEntity(pos);
			if (tileEntity instanceof DiffuserTileEntity) {
				((DiffuserTileEntity) tileEntity).setCustomName(stack.getHoverName());
			}
			
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
		if (state.getValue(BlockStateProperties.TRIGGERED)) {
			for (int i = 0; i < 5; i++) {
				world.addParticle(
			    		ParticleTypes.SMOKE, 
			    		(double)pos.getX() + 0.4D + (double)random.nextFloat() * 0.4D, 
			    		(double)pos.getY() + 0.7D + (double)random.nextFloat() * 0.3D, 
			    		(double)pos.getZ() + 0.4D + (double)random.nextFloat() * 0.4D, 
			    		0.0D, 
			    		0.0D, 
			    		0.0D);
			}
		}
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult posOnFace) {
		if (!(world.isClientSide)) {
			TileEntity tileEntity = world.getBlockEntity(pos);
			if (tileEntity instanceof DiffuserTileEntity) {
				INamedContainerProvider containerProvider = new INamedContainerProvider() {
					@Override
					public ITextComponent getDisplayName() {
						return ((DiffuserTileEntity) tileEntity).getName();
					}

					@Override
					public Container createMenu(int i, PlayerInventory inv, PlayerEntity player) {
						return new DiffuserContainer(i, world, pos, inv, player);
					}
				};
				NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, tileEntity.getBlockPos());
			}
			else {
				throw new IllegalStateException("huh? You right clicked on a diffuser, but it does not have an associated diffuser tile entity");
			}
		}
		return ActionResultType.SUCCESS;
	}

}
