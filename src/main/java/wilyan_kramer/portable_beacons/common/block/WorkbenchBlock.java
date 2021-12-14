package wilyan_kramer.portable_beacons.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import wilyan_kramer.portable_beacons.PortableBeaconsMod;
import wilyan_kramer.portable_beacons.common.tileentity.WorkbenchTileEntity;

public class WorkbenchBlock extends ContainerBlock {
	private static final ITextComponent CONTAINER_TITLE = new TranslationTextComponent("screen.workbench.title");

	public WorkbenchBlock(Properties prop) {
		super(prop);
	}
	
	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
		if (stack.hasCustomHoverName()) {
			TileEntity tileEntity = world.getBlockEntity(pos);
			if (tileEntity instanceof WorkbenchTileEntity) {
				((WorkbenchTileEntity) tileEntity).setCustomName(stack.getHoverName());
			}
		}
	}
	
	public ActionResultType use(BlockState blockState, World world, BlockPos blockPos, PlayerEntity player, Hand hand, BlockRayTraceResult posOnFace) {
		PortableBeaconsMod.LOGGER.info("right clicked on tileentity");
		if (world.isClientSide) {
			return ActionResultType.SUCCESS;
		} else {
			//player.openMenu(blockState.getMenuProvider(world, blockPos));
			return ActionResultType.CONSUME;
		}
	}

	
	// this is placeholder code from crafting table
	public INamedContainerProvider getMenuProvider(BlockState blockState, World world, BlockPos blockPos) {
		return new SimpleNamedContainerProvider((p_220270_2_, inventory, player) -> {
			return new WorkbenchContainer(p_220270_2_, inventory, IWorldPosCallable.create(world, blockPos));
		}, CONTAINER_TITLE);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	@Override
	public TileEntity newBlockEntity(IBlockReader blockReader) {
		return new WorkbenchTileEntity();
	}
	
	
	//spawn the inventory contents as items
	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState blockStateOld, World world, BlockPos blockPos, BlockState blockStateNew, boolean bool) {
		if (!blockStateOld.getBlock().is(blockStateNew.getBlock())) {
			TileEntity tileentity = world.getBlockEntity(blockPos);

			if (tileentity instanceof WorkbenchTileEntity) 
				((WorkbenchTileEntity) tileentity).dropItems();
		}
		super.onRemove(blockStateOld, world, blockPos, blockStateNew, bool);
	}
}
