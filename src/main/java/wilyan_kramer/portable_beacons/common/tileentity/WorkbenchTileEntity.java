package wilyan_kramer.portable_beacons.common.tileentity;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.INameable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import wilyan_kramer.portable_beacons.PortableBeaconsMod;

public class WorkbenchTileEntity extends LockableTileEntity implements ISidedInventory, ITickableTileEntity, INameable {

	private static final int[] SLOTS_FOR_UP = new int[]{0,1,2,3,4,5,6,7,8};
	private static final int[] SLOTS_FOR_DOWN = new int[]{9};
	private static final int[] SLOTS_FOR_SIDES = new int[]{};
    private ItemStackHandler itemHandler = createItemHandler();
    private LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
    private ITextComponent name;
    
	private Random random = new Random();


	public WorkbenchTileEntity() {
		super(TileEntityList.workbench);
	}
	
	@Override
	protected void invalidateCaps() {
		super.invalidateCaps();
		handler.invalidate();
	}
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		itemHandler.deserializeNBT(compound.getCompound("Inventory"));
		if (compound.contains("CustomName", 8)) {
	         this.name = ITextComponent.Serializer.fromJson(compound.getString("CustomName"));
	      }
		super.load(state, compound);
	}
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		compound.put("Inventory", itemHandler.serializeNBT());
		if (this.name != null) {
			compound.putString("CustomName", ITextComponent.Serializer.toJson(this.name));
	    }
		return super.save(compound);
	}
	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag) {
		load(state, tag);
	}
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.worldPosition, 1, getUpdateTag());
	}
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		CompoundNBT tag = pkt.getTag();
		handleUpdateTag(this.level.getBlockState(worldPosition), tag);
		this.level.sendBlockUpdated(worldPosition, this.level.getBlockState(worldPosition), this.level.getBlockState(worldPosition), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
	}
	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT tag = super.getUpdateTag();
		tag.put("Inventory", itemHandler.serializeNBT());
		return super.getUpdateTag();
	}

	private ItemStackHandler createItemHandler() {
		return new ItemStackHandler(this.getContainerSize()) {
			@Override
			protected void onContentsChanged(int slot) {
				setChanged();
			}
			@Override
			public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
					return slot != 9;
			}

			@Nonnull
			@Override
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
				if (!isItemValid(slot, stack)) {
					return stack;
				}
				else {
					return super.insertItem(slot, stack, simulate);
				}
			}
		};
	}
	
	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return handler.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public int getContainerSize() {
		return 10;
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < this.getContainerSize(); i++) {
			if (itemHandler.getStackInSlot(i) != null) {
				return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack getItem(int slot) {
		return itemHandler.getStackInSlot(slot);
	}

	
	public ItemStack removeItem(int slot, int amount) {
		return itemHandler.extractItem(slot, amount, false);
	}

	public ItemStack removeItemNoUpdate(int slot) {
		if (slot > 0 && slot < this.getContainerSize()) {
			ItemStack returnStack = itemHandler.getStackInSlot(slot).copy();
			itemHandler.setStackInSlot(slot, null);
			return returnStack;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		itemHandler.setStackInSlot(slot, stack);
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return this.getBlockPos().closerThan(player.getPosition(1.0F),  5.0F);
	}

	@Override
	public void clearContent() {
		// no idea what to do here, but we have to implement it...
	}

	@Override
	public void tick() {
		// Do time-based recipes here
		PortableBeaconsMod.LOGGER.info("TileEntity at " + this.getBlockPos().toShortString() + " ticking!");
	}

	@Override
	public int[] getSlotsForFace(Direction direction) {
		if (direction == Direction.DOWN) {
			return SLOTS_FOR_DOWN;
		} else {
			return direction == Direction.UP ? SLOTS_FOR_UP : SLOTS_FOR_SIDES;
		}
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction direction) {
		return this.canPlaceItem(slot, stack);
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, @Nullable Direction direction) {
		if (slot == 9) return false;
		return true;
	}

	@Override
	public ITextComponent getName() {
		return this.name != null ? this.name : this.getDefaultName();
	}
	public ITextComponent getDefaultName() {
		return new TranslationTextComponent("block.portable_beacons.workbench");
	}
	@Override
	public boolean hasCustomName() {
		return this.getCustomName() != null;
	}
	@Override
	public ITextComponent getDisplayName() {
		return this.getName();
	}
	@Nullable
	@Override
	public ITextComponent getCustomName() {
		return this.name;
	}
	public void setCustomName(ITextComponent name) {
	   this.name = name;
	}

	@Override
	protected Container createMenu(int p_213906_1_, PlayerInventory p_213906_2_) {
		// TODO Auto-generated method stub
		return null;
	}

	public void dropItems() {
		for (int i = 0; i < this.getContainerSize(); i++) {
			if (itemHandler.getStackInSlot(i) != ItemStack.EMPTY) {
				this.level.addFreshEntity(new ItemEntity(level, this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), itemHandler.getStackInSlot(i)));
			}
		}
	}

}
