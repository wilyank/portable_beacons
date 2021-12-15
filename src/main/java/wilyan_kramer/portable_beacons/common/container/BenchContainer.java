package wilyan_kramer.portable_beacons.common.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.common.capability.CurioItemCapability;
import wilyan_kramer.portable_beacons.common.block.BlockList;
import wilyan_kramer.portable_beacons.common.tileentity.BenchTileEntity;

public class BenchContainer extends Container {

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
    	for (int i = 3; i >= 0; i--) {
    		addSlot(new SlotItemHandler(playerInventory, 36 + i, leftCol, topRow));
    		topRow += 18;
    	}
    	// Crap. now you can put whatever you want in those slots... I guess this is a reason why the capability system is better than the vanilla inventory system.
    	//addSlotBox(playerInventory, 36, leftCol, topRow, 1, 18, 4, 18);
    	topRow += 4; // add the spacing between inventory and hotbar
    	addSlot(new SlotItemHandler(playerInventory, 40, leftCol, topRow));
    	
    }

}
