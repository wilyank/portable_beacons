package wilyan_kramer.portable_beacons.common.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import wilyan_kramer.portable_beacons.common.config.Config;
import wilyan_kramer.portable_beacons.common.effect.EffectHelper;

public class InfusedSwordItem extends SwordItem {

	public InfusedSwordItem(IItemTier tier, int atkdmg, float atkspeed, Properties prop) {
		super(tier, atkdmg, atkspeed, prop);
	}
	
	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity enemy, LivingEntity player) {
		if (!player.level.isClientSide && stack.hasTag()) {
			for (EffectInstance effInst : EffectHelper.setProperties(EffectHelper.getAllEffects(stack), Config.COMMON.effectDuration.get())) {
				enemy.addEffect(effInst);
			}
		}
		return super.hurtEnemy(stack, enemy, player);
	}
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
		super.appendHoverText(stack, world, list, flag);
		EffectHelper.addPotionTooltip(stack, list, 0, "necklace");
	}
}
