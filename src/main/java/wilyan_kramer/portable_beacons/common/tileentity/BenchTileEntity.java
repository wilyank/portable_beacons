package wilyan_kramer.portable_beacons.common.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
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

public class BenchTileEntity extends TileEntity implements ITickableTileEntity, INameable {

    private ItemStackHandler itemHandler = createItemHandler();
    private LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
    private ITextComponent name;
    
    //constructor
	public BenchTileEntity() {
		super(TileEntityList.bench);
	}
	
	// i think this is the markDirty() from mcp mappings
	@Override
	protected void invalidateCaps() {
		super.invalidateCaps();
		handler.invalidate();
	}
	
	// load this TE and its data from the save file
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		itemHandler.deserializeNBT(compound.getCompound("Inventory"));
		if (compound.contains("CustomName", 8)) {
	         this.name = ITextComponent.Serializer.fromJson(compound.getString("CustomName"));
	      }
		super.load(state, compound);
	}
	
	// write this TE and its data to the save file
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		compound.put("Inventory", itemHandler.serializeNBT());
		if (this.name != null) {
			compound.putString("CustomName", ITextComponent.Serializer.toJson(this.name));
	    }
		return super.save(compound);
	}
	
	// what to do when the TE is updated
	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag) {
		load(state, tag);
	}
	
	// what packet do we send to sync the TE when it updates?
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.worldPosition, 1, getUpdateTag());
	}
	
	// what to do when we receive an update packet
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		CompoundNBT tag = pkt.getTag();
		handleUpdateTag(this.level.getBlockState(worldPosition), tag);
		this.level.sendBlockUpdated(worldPosition, this.level.getBlockState(worldPosition), this.level.getBlockState(worldPosition), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
	}
	
	// get the data to put in the update packet
	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT tag = super.getUpdateTag();
		tag.put("Inventory", itemHandler.serializeNBT());
		return super.getUpdateTag();
	}

	// create the inventory of the TE
	private ItemStackHandler createItemHandler() {
		return new ItemStackHandler(this.getContainerSize()) {
			
			// when the inventory contents change, do this
			@Override
			protected void onContentsChanged(int slot) {
				setChanged();
			}
			
			// can this item be inserted in this slot?
			@Override
			public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
					return slot != 16;
			}

			// insert the item into the inventory if it is valid
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
	
	// forge capapbility magic
	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return handler.cast();
		}
		return super.getCapability(cap, side);
	}
	
	// get the inventory size
	public int getContainerSize() {
		return 16+1+9; // four crafting slots, one crafting output slot (not sure if that one should be a real slot?) and nine inventory stots
	}

	// check if the inventory is empty
	public boolean isEmpty() {
		for (int i = 0; i < this.getContainerSize(); i++) {
			if (itemHandler.getStackInSlot(i) != null) {
				return false;
			}
		}
		return true;
	}

	// get which item is in this slot
	public ItemStack getItem(int slot) {
		return itemHandler.getStackInSlot(slot);
	}

	// take an item from a slot
	public ItemStack removeItem(int slot, int amount) {
		return itemHandler.extractItem(slot, amount, false);
	}
	
	// delete an item from a slot????
	public ItemStack removeItemNoUpdate(int slot) {
		if (slot > 0 && slot < this.getContainerSize()) {
			ItemStack returnStack = itemHandler.getStackInSlot(slot).copy();
			itemHandler.setStackInSlot(slot, null);
			return returnStack;
		}
		return ItemStack.EMPTY;
	}

	// set the item in that slot to this
	public void setItem(int slot, ItemStack stack) {
		itemHandler.setStackInSlot(slot, stack);
	}
	
	// this function is called every tick
	@Override
	public void tick() {
		// Do time-based recipes here
	}

	// bunch of stuff to make anvil renaming of the block work.
	@Override
	public ITextComponent getName() {
		return this.name != null ? this.name : this.getDefaultName();
	}
	public ITextComponent getDefaultName() {
		return new TranslationTextComponent("block.portable_beacons.bench");
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

	// drop the inventory contents when the block is broken
	public void dropItems() {
		for (int i = 0; i < this.getContainerSize(); i++) {
			if (itemHandler.getStackInSlot(i) != ItemStack.EMPTY) {
				this.level.addFreshEntity(new ItemEntity(level, this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), itemHandler.getStackInSlot(i)));
			}
		}
	}

}
