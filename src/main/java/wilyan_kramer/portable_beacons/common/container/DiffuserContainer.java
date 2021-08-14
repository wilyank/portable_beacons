package wilyan_kramer.portable_beacons.common.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import wilyan_kramer.portable_beacons.common.block.BlockList;
import wilyan_kramer.portable_beacons.common.tileentity.DiffuserTileEntity;

public class DiffuserContainer extends Container {

	private PlayerEntity playerEntity;
	private IItemHandler playerInventory;
	private TileEntity tileEntity;

	public DiffuserContainer(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
		super(ContainerList.diffuserContainer, windowId);
		tileEntity = world.getBlockEntity(pos);
		this.playerEntity = player;
		this.playerInventory = new InvWrapper(playerInventory);

		if (tileEntity != null) {
			tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
				addSlot(new SlotItemHandler(h, 0, 26, 35));
			});
		}
		layoutPlayerInventorySlots(8, 84);
		trackEffect();
	}
	
	private void trackEffect() {
		// first half of the duration data slot
		addDataSlot(new IntReferenceHolder() {
			@Override
			public int get() {
				return getDuration() & 0xffff;
			}
			@Override
			public void set(int value) {
				if (tileEntity instanceof DiffuserTileEntity) {
					int durationStored = ((DiffuserTileEntity) tileEntity).getDuration() & 0xffff0000;
					((DiffuserTileEntity) tileEntity).setDuration(durationStored + (value & 0xffff));
				}
			}
		});
		// second half of the duration data slot
		addDataSlot(new IntReferenceHolder() {
			@Override
			public int get() {
				return (getDuration() >> 16) & 0xffff;
			}
			@Override
			public void set(int value) {
				if (tileEntity instanceof DiffuserTileEntity) {
					int durationStored = ((DiffuserTileEntity) tileEntity).getDuration() & 0x0000ffff;
					((DiffuserTileEntity) tileEntity).setDuration(durationStored | (value << 16));
				}
			}
		});

	}
	public int getDuration() {
		if (tileEntity instanceof DiffuserTileEntity) {
			return ((DiffuserTileEntity) tileEntity).getDuration();
		}
		else {
			return 0;
		}
	}
	public int[] getEffectIds() {
		if (tileEntity instanceof DiffuserTileEntity) {
			return ((DiffuserTileEntity) tileEntity).getEffectIds();
		}
		else {
			return new int[] {-1};
		}
	}
	public int[] getAmplifiers() {
		if (tileEntity instanceof DiffuserTileEntity) {
			return ((DiffuserTileEntity) tileEntity).getAmplifiers();
		}
		else {
			return new int[] {0};
		}
	}

	@Override
	public ContainerType<?> getType() {
		return ContainerList.diffuserContainer;
	}


	@Override
	public boolean stillValid(PlayerEntity p_75145_1_) {
        return super.stillValid(IWorldPosCallable.create(tileEntity.getLevel(), tileEntity.getBlockPos()), playerEntity, BlockList.diffuser);
	}
	
	@Override
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();
            if (index == 0) {
                if (!this.moveItemStackTo(stack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, itemstack);
            } else {
                if (stack.getItem() == Items.POTION || stack.getItem() == Items.GLASS_BOTTLE) {
                    if (!this.moveItemStackTo(stack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 28) {
                    if (!this.moveItemStackTo(stack, 28, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 37 && !this.moveItemStackTo(stack, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, stack);
        }

        return itemstack;
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

}
