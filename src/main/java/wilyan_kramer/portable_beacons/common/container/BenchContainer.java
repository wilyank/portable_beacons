package wilyan_kramer.portable_beacons.common.container;

import java.util.Map;
import java.util.Optional;

import com.mojang.datafixers.util.Pair;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
import top.theillusivec4.curios.common.inventory.CurioSlot;
import wilyan_kramer.portable_beacons.common.block.BlockList;
import wilyan_kramer.portable_beacons.common.effect.Profession;
import wilyan_kramer.portable_beacons.common.item.recipe.BenchUpgradeRecipe;
import wilyan_kramer.portable_beacons.common.tileentity.BenchTileEntity;

public class BenchContainer extends Container {

	public static final ResourceLocation BLOCK_ATLAS = new ResourceLocation("textures/atlas/blocks.png");
	public static final ResourceLocation EMPTY_ARMOR_SLOT_HELMET = new ResourceLocation("item/empty_armor_slot_helmet");
	public static final ResourceLocation EMPTY_ARMOR_SLOT_CHESTPLATE = new ResourceLocation("item/empty_armor_slot_chestplate");
	public static final ResourceLocation EMPTY_ARMOR_SLOT_LEGGINGS = new ResourceLocation("item/empty_armor_slot_leggings");
	public static final ResourceLocation EMPTY_ARMOR_SLOT_BOOTS = new ResourceLocation("item/empty_armor_slot_boots");
	public static final ResourceLocation EMPTY_ARMOR_SLOT_SHIELD = new ResourceLocation("item/empty_armor_slot_shield");
	private static final ResourceLocation[] TEXTURE_EMPTY_SLOTS = new ResourceLocation[]{EMPTY_ARMOR_SLOT_BOOTS, EMPTY_ARMOR_SLOT_LEGGINGS, EMPTY_ARMOR_SLOT_CHESTPLATE, EMPTY_ARMOR_SLOT_HELMET};
	private static final EquipmentSlotType[] SLOT_IDS = new EquipmentSlotType[]{EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET};


	private PlayerEntity playerEntity;
	private IItemHandler playerInventory;
	private TileEntity tileEntity;
	public final LazyOptional<ICuriosItemHandler> curiosHandler;
	private final IIntArray data;
	CraftingInventory craftingInv = new CraftingInventory(this, 4, 4);
	CraftingInventory upgradeInv = new CraftingInventory(this, 1, 1);
    CraftResultInventory result = new CraftResultInventory();
    private final IWorldPosCallable access;


    public BenchContainer(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
    	this(windowId, world, pos, playerInventory, player, IWorldPosCallable.NULL);
    }
    
	public BenchContainer(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player, IWorldPosCallable access) {
		super(ContainerList.benchContainer, windowId);
		tileEntity = world.getBlockEntity(pos);
		this.playerEntity = player;
		this.playerInventory = new InvWrapper(playerInventory);
		this.curiosHandler = CuriosApi.getCuriosHelper().getCuriosHandler(this.playerEntity);
		this.access = access;

		if (this.tileEntity instanceof BenchTileEntity) {
			this.data = ((BenchTileEntity) tileEntity).dataAccess;
		}
		else {
			throw new IllegalStateException("WorkbenchContainer being used for non-workbench TileEntity");
		}
		if (this.tileEntity != null) {
			this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
				// add the crafting result inventory slot
				this.addSlot(new CraftingResultSlot(playerInventory.player, craftingInv, result, 0, 136, 45));
				// add the crafting area slots
				for(int i = 0; i < 4; ++i) {
					for(int j = 0; j < 4; ++j) {
						this.addSlot(new Slot(this.craftingInv, j + i * 4, 24 + j * 18, 18 + i * 18));
					}
				}
				// add the block inventory row
				addSlotRange(h, 0, 24, 108, 9, 18);
				this.addSlot(new SlotItemHandler(h, 9, 218, 8)); // the brewing ingredient slot
				this.addSlot(new SlotItemHandler(h, 10, 218, 41)); // the brewing bottle slot
				this.addSlot(new Slot(this.upgradeInv, 0, 232, 72));
			});
		}
		layoutPlayerInventorySlots(24, 140); // layout the player's inventory and hotbar slots
		layoutPlayerEquipmentSlots(200, 72); // put the player's armor, offhand, back and neck slots
		if (tileEntity instanceof BenchTileEntity) {
			addDataSlots(((BenchTileEntity) tileEntity).dataAccess);
		}
	}
	public int getPotioneerLevel() {
		return this.data.get(0);
	}
	public int getArtificerLevel() {
		return this.data.get(1);
	}
	public int getSummonerLevel() {
		return this.data.get(2);
	}
	public int getBrewTime() {
		return this.data.get(3);
	}

	@Override
	public ContainerType<?> getType() {
		return ContainerList.benchContainer;
	}
	
	protected static void slotChangedCraftingGrid(int id, World world, PlayerEntity player,
			CraftingInventory craftingInv, CraftResultInventory craftingResult) {
		if (!world.isClientSide) {
			ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) player;
			ItemStack itemstack = ItemStack.EMPTY;
			Optional<ICraftingRecipe> optional = world.getServer().getRecipeManager().getRecipeFor(IRecipeType.CRAFTING,
					craftingInv, world);
			if (optional.isPresent()) {
				ICraftingRecipe icraftingrecipe = optional.get();
				if (craftingResult.setRecipeUsed(world, serverplayerentity, icraftingrecipe)) {
					itemstack = icraftingrecipe.assemble(craftingInv);
				}
			}
			craftingResult.setItem(0, itemstack);
			serverplayerentity.connection.send(new SSetSlotPacket(id, 0, itemstack));
		}
	}
	protected static void slotChangedUpgradeGrid(int id, World world, PlayerEntity player, 
			CraftingInventory upgradeInv, BlockPos blockPos) {
		if (!world.isClientSide) {
//			ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) player;
			if (world.getBlockEntity(blockPos) instanceof BenchTileEntity) {
				BenchTileEntity tileEntity = (BenchTileEntity) world.getBlockEntity(blockPos);

				Optional<BenchUpgradeRecipe> optional = world.getServer().getRecipeManager().getRecipeFor(BenchUpgradeRecipe.TYPE, upgradeInv, world);
				if (optional.isPresent()) {
					BenchUpgradeRecipe recipe = optional.get();
					int professionId = Profession.valueOf(recipe.getProfession()).ordinal();
					if (tileEntity.dataAccess.get(professionId) < BenchTileEntity.PROGRESSION_THRESHOLDS[-1]) {
						int current = tileEntity.dataAccess.get(professionId);
						tileEntity.dataAccess.set(professionId, Math.max(BenchTileEntity.PROGRESSION_THRESHOLDS[-1], current + recipe.getExperience()));
						upgradeInv.getItem(0).shrink(1);
					}
				}
			}
		}
	}
	
	@Override
	public void slotsChanged(IInventory invIn) {
		access.execute((world, blockPos) -> {
			slotChangedCraftingGrid(this.containerId, this.playerEntity.level, this.playerEntity, this.craftingInv, this.result);
		});
		access.execute((world, blockPos) -> {
			slotChangedUpgradeGrid(this.containerId, this.playerEntity.level, this.playerEntity, this.upgradeInv, blockPos);
		});
	}
	@Override
	public void removed(PlayerEntity player) {
		super.removed(player);
		this.clearContainer(player, player.level, craftingInv);
	}
	@Override
	public ItemStack quickMoveStack(PlayerEntity player, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		
		int inventorySize = 26;
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (index == 0) { // if the clicked slot is the crafting result slot
	            this.access.execute((world, blockPos) -> {
	               itemstack1.getItem().onCraftedBy(itemstack1, world, player);
	            });
	            if (!this.moveItemStackTo(itemstack1, inventorySize, this.slots.size(), true)) {
	               return ItemStack.EMPTY;
	            }
	            slot.onQuickCraft(itemstack1, itemstack);
			}
	        else if (index < inventorySize) { // if the clicked slot is part of the bench's inventory
				if (!this.moveItemStackTo(itemstack1, inventorySize, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} 
	        else if (!this.moveItemStackTo(itemstack1, 0, inventorySize, false)) { // if the clicked slot is in the player's inventory
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} 
			else {
				slot.setChanged();
			}
			
			if (itemstack1.isEmpty()) {
	            slot.set(ItemStack.EMPTY);
	         } 
			else {
	            slot.setChanged();
	         }

	         if (itemstack1.getCount() == itemstack.getCount()) {
	            return ItemStack.EMPTY;
	         }
			
			ItemStack itemstack2 = slot.onTake(player, itemstack1);
	        if (index == 0) {
	           player.drop(itemstack2, false);
	        }
		}
		return itemstack;
	}
	
	@Override
	public boolean stillValid(PlayerEntity player) {
        return super.stillValid(IWorldPosCallable.create(tileEntity.getLevel(), tileEntity.getBlockPos()), playerEntity, BlockList.bench);
	}
	
	@Override
	public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
	      return slot.container != this.result && super.canTakeItemForPickAll(stack, slot);
	   }

	
	private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }
    private void layoutPlayerEquipmentSlots(int leftCol, int topRow) {
    	// add the offhand, armor slots and curios slots. not sure how i can do the curios slots with mod support

    	int curioYPos = topRow;
    	topRow += 2*18 + 14;

    	for(int k = 0; k < 4; ++k) {
    		final EquipmentSlotType equipmentslottype = SLOT_IDS[k];
    		this.addSlot(new SlotItemHandler(playerInventory, 39 - k, leftCol, topRow) {
    			public int getMaxStackSize() {
    				return 1;
    			}

    			public boolean mayPlace(ItemStack stack) {
    				return stack.canEquip(equipmentslottype, playerEntity);
    			}

    			public boolean mayPickup(PlayerEntity player) {
    				ItemStack itemstack = this.getItem();
    				return !itemstack.isEmpty() && EnchantmentHelper.hasBindingCurse(itemstack) ? false : super.mayPickup(player);
    			}

    			@OnlyIn(Dist.CLIENT)
    			public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
    				return Pair.of(BenchContainer.BLOCK_ATLAS, BenchContainer.TEXTURE_EMPTY_SLOTS[equipmentslottype.getIndex()]);
    			}
    		});
    		topRow += 18;
    	}
    	topRow += 4; // add the spacing between inventory and hotbar
    	this.addSlot(new SlotItemHandler(playerInventory, 40, leftCol, topRow) {
    		@OnlyIn(Dist.CLIENT)
    		public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
    			return Pair.of(BenchContainer.BLOCK_ATLAS, BenchContainer.EMPTY_ARMOR_SLOT_SHIELD);
    		}
    	});
    	topRow -= 4 + 4*18 + 2*18 + 14;
    	this.curiosHandler.ifPresent(curios -> {
    		Map<String, ICurioStacksHandler> curioMap = curios.getCurios();
    		ICurioStacksHandler stacksHandler = curioMap.get("back");
    		IDynamicStackHandler stackHandler = stacksHandler.getStacks();
    		this.addSlot(new CurioSlot(this.playerEntity, stackHandler, 0, "back", leftCol, curioYPos, stacksHandler.getRenders()));
    		stacksHandler = curioMap.get("necklace");
    		stackHandler = stacksHandler.getStacks();
    		this.addSlot(new CurioSlot(this.playerEntity, stackHandler, 0, "necklace", leftCol, curioYPos+18, stacksHandler.getRenders()));
    	});
    }
}
