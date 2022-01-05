package wilyan_kramer.portable_beacons.common.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.potion.PotionBrewing;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.INameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class BenchTileEntity extends TileEntity implements ITickableTileEntity, INameable {

	public static int[] PROGRESSION_THRESHOLDS = new int[] {10,100,1000,10000, 1000000};
	
    private ItemStackHandler itemHandler = createItemHandler();
    private LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
    private ITextComponent name;
    private int brewTime;
    private int[] progression = new int[] {0,0,0};
    
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
		this.brewTime = compound.getShort("BrewTime");
		for (int i = 0; i < progression.length; i++) {
			this.progression[i] = compound.getShort("Level_" + i);
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
		compound.putShort("BrewTime", (short) this.brewTime);
		for (int i = 0; i < progression.length; i++) {
			compound.putShort("Level_" + i, (short) this.progression[i]);
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
		this.level.sendBlockUpdated(worldPosition, 
				this.level.getBlockState(worldPosition), 
				this.level.getBlockState(worldPosition), 
				Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
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
				if (slot == 9) {
					return net.minecraftforge.common.brewing.BrewingRecipeRegistry.isValidIngredient(stack);
				}
				else if (slot == 10) {
					return net.minecraftforge.common.brewing.BrewingRecipeRegistry.isValidInput(stack);
				}
				return true;
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
	public IIntArray dataAccess = new IIntArray() {
		public int get(int index) {
			switch(index) {
			case 0:
				return BenchTileEntity.this.progression[0];
			case 1:
				return BenchTileEntity.this.progression[1];
			case 2:
				return BenchTileEntity.this.progression[2];
			case 3:
				return BenchTileEntity.this.brewTime;
			default:
				return 0;
			}
		}
		public void set(int index, int value) {
			switch(index) {
			case 0:
				BenchTileEntity.this.progression[0] = value;
				break;
			case 1:
				BenchTileEntity.this.progression[1] = value;
				break;
			case 2:
				BenchTileEntity.this.progression[2] = value;
				break;
			case 3:
				BenchTileEntity.this.brewTime = value;
			}
		}
		public int getCount() {
			return 4;
		}
	};

	// get the inventory size
	public int getContainerSize() {
		return 11; // only the inventory row and brewing stand, crafting slots are in container
	}

	// check if the inventory is empty
	public boolean isEmpty() {
		for (int i = 0; i < this.getContainerSize(); i++) {
			if (!itemHandler.getStackInSlot(i).isEmpty()) {
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
		if (this.progression[0] > BenchTileEntity.PROGRESSION_THRESHOLDS[0]) {
			if (this.isBrewable() && this.brewTime > 0) {
				brewTime--;
			}
			else if (brewTime == 0 && this.isBrewable()) {
				this.doBrew();
				this.brewTime = 400;
			}
			else {
				this.brewTime = 400;
			}
			this.setChanged();
		}
		else {
			brewTime = 400;
		}
	}
	
	// functions for brewing
	private boolean isBrewable() {
		ItemStack ingredient = this.itemHandler.getStackInSlot(9);
		ItemStack bottle = this.itemHandler.getStackInSlot(10);
		if (!ingredient.isEmpty()) return net.minecraftforge.common.brewing.BrewingRecipeRegistry.hasOutput(bottle, ingredient); // divert to VanillaBrewingRegistry
		if (ingredient.isEmpty()) {
			return false;
		} else if (!PotionBrewing.isIngredient(ingredient)) {
			return false;
		} else {
			if (!bottle.isEmpty() && PotionBrewing.hasMix(bottle, ingredient)) {
				return true;
			}
			return false;
		}
	}
	private void doBrew() {
	      //if (net.minecraftforge.event.ForgeEventFactory.onPotionAttemptBrew(items)) return;
	      ItemStack ingredient = this.itemHandler.getStackInSlot(9);
	      ItemStack bottle = this.itemHandler.getStackInSlot(10);
	      	      
	      ItemStack output = net.minecraftforge.common.brewing.BrewingRecipeRegistry.getOutput(bottle, ingredient);
          if (!output.isEmpty())
          {
              this.itemHandler.setStackInSlot(10, output);
          }
	      
	      //net.minecraftforge.event.ForgeEventFactory.onPotionBrewed(items);
	      BlockPos blockpos = this.getBlockPos();
	      if (ingredient.hasContainerItem()) {
	         ItemStack itemstack1 = ingredient.getContainerItem();
	         ingredient.shrink(1);
	         if (ingredient.isEmpty()) {
	            ingredient = itemstack1;
	         } else if (!this.level.isClientSide) {
	            InventoryHelper.dropItemStack(this.level, (double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), itemstack1);
	         }
	      }
	      else ingredient.shrink(1);

	      //this.itemHandler.setStackInSlot(9, ingredient);
	      this.level.levelEvent(1035, blockpos, 0);
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
	
	// add experience to the bench
	public void addExperience(int profession, int amount) {
		if (this.progression[profession] < BenchTileEntity.PROGRESSION_THRESHOLDS[-1]) {
			this.progression[profession] += amount;
		}
	}

}
