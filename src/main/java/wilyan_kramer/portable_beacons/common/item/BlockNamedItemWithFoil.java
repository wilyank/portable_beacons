package wilyan_kramer.portable_beacons.common.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockNamedItem;
import net.minecraft.item.ItemStack;

public class BlockNamedItemWithFoil extends BlockNamedItem {

	public BlockNamedItemWithFoil(Block block, Properties prop) {
		super(block, prop);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return true;
	}

}
