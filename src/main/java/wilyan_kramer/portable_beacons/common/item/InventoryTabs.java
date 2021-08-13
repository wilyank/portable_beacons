package wilyan_kramer.portable_beacons.common.item;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class InventoryTabs {
	public static final ItemGroup TAB_PORTABLE_BEACONS = new ItemGroup("portable_beacons") {
    	@Override
    	public ItemStack makeIcon() {
    		return new ItemStack(ItemList.infused_star);
    	}
    };
}
