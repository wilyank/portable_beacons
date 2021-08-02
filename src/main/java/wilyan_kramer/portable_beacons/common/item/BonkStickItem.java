package wilyan_kramer.portable_beacons.common.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import wilyan_kramer.portable_beacons.PortableBeaconsMod;

public class BonkStickItem extends SwordItem {
//	private static final Logger LOGGER = LogManager.getLogger();
	
	public BonkStickItem(IItemTier tier, int atkDmg, float atkSpeed, Properties prop) {
		super(tier, atkDmg, atkSpeed, prop);
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity enemy, LivingEntity player) {
		enemy.removeAllEffects();
		return super.hurtEnemy(stack, enemy, player);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
		super.appendHoverText(stack, world, list, flag);
		list.add(new TranslationTextComponent("item." + PortableBeaconsMod.MODID + ".bonk_stick.tooltip"));
	}
	
}
