package wilyan_kramer.portable_beacons.common.item;

import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockNamedItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import wilyan_kramer.portable_beacons.common.effect.ModDamageSource;

public class BlockNamedItemWithFoil extends BlockNamedItem {

	public BlockNamedItemWithFoil(Block block, Properties prop) {
		super(block, prop);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity entity) {
		ItemStack returnStack = super.finishUsingItem(stack, world, entity);
		entity.hurt(ModDamageSource.starberry_bush, 1.0F);
		return returnStack;
	}
	
}
