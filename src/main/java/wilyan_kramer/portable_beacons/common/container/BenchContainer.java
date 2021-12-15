package wilyan_kramer.portable_beacons.common.container;

import com.mojang.datafixers.util.Pair;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import wilyan_kramer.portable_beacons.common.block.BlockList;
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
	
	

	public BenchContainer(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
		super(ContainerList.benchContainer, windowId);
		tileEntity = world.getBlockEntity(pos);
		this.playerEntity = player;
		this.playerInventory = new InvWrapper(playerInventory);

		if (!(this.tileEntity instanceof BenchTileEntity)) throw new IllegalStateException("WorkbenchContainer being used for non-workbench TileEntity");
		if (this.tileEntity != null) {
			this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
				//addSlot(new SlotItemHandler(h, 0, 17, 35)); // arguments: ItemHandler, slot index, x pixel coordinate, y pixel coordinate
				addSlotBox(h, 0, 24, 18, 4, 18, 4, 18); // the crafting grid
				//addSlotBox(handler, startIndex, startX, startY, horAmount, dx, verAmount, dy);
				addSlot(new SlotItemHandler(h, 16, 136, 45)); // the crafting output slot
				addSlotRange(h, 17, 24, 108, 9, 18); // the block inventory row
			});
		}
		layoutPlayerInventorySlots(24, 140); // layout the player's inventory and hotbar slots
		layoutPlayerEquipmentSlots(200, 72); // put the player's armor, offhand, back and neck slots
	}

	@Override
	public ContainerType<?> getType() {
		return ContainerList.benchContainer;
	}


	@Override
	public boolean stillValid(PlayerEntity player) {
        return super.stillValid(IWorldPosCallable.create(tileEntity.getLevel(), tileEntity.getBlockPos()), playerEntity, BlockList.bench);
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
    	//addSlotBox(playerInventory, 36, leftCol, topRow, 1, 18, 4, 18);
    	topRow += 4; // add the spacing between inventory and hotbar
    	this.addSlot(new SlotItemHandler(playerInventory, 40, leftCol, topRow) {
    		@OnlyIn(Dist.CLIENT)
    		public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
    			return Pair.of(BenchContainer.BLOCK_ATLAS, BenchContainer.EMPTY_ARMOR_SLOT_SHIELD);
    		}
    	});
    	
    }

}
