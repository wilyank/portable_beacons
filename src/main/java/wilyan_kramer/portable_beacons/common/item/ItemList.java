package wilyan_kramer.portable_beacons.common.item;

import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

public class ItemList {
	public static final Food GLOWBERRIES = (new Food
			.Builder())
			.nutrition(4)
			.saturationMod(0.3F)
			.effect(() -> new EffectInstance(Effects.GLOWING, 300, 0), 1)
			.build();

	
	public static Item infused_star;
	public static Item beacon_necklace;
	public static Item beacon_backpack_0;
	public static Item beacon_backpack_1;
	public static Item beacon_backpack_2;
	public static Item beacon_backpack_3;
	public static Item beacon_backpack_4;
	public static Item glowberries;
	public static Item bonk_stick;
}
