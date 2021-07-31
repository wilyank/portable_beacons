package wilyan_kramer.portable_beacons.common.item;

import java.awt.Color;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import wilyan_kramer.portable_beacons.common.effect.EffectHelper;

public class ItemColorizer implements IItemColor {

	@Override
	public int getColor(ItemStack stack, int index) {
		if (index == 1 && stack.hasTag()) {
			return EffectHelper.getPotionColor(stack);
		}
		else {
			return Color.WHITE.getRGB();
		}
	}
}
