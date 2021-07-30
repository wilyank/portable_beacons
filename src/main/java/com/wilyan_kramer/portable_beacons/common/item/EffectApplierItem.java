package com.wilyan_kramer.portable_beacons.common.item;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.wilyan_kramer.portable_beacons.PortableBeaconsMod;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EffectApplierItem extends Item {
    
	public EffectApplierItem(Properties p_i48487_1_) {
		super(p_i48487_1_.stacksTo(1).durability(0).tab(PortableBeaconsMod.TAB_PORTABLE_BEACONS).rarity(Rarity.RARE));
	}
	
	public boolean isFoil(@Nonnull ItemStack stack) {
	    return stack.hasTag();
	 }
	
	public ActionResultType useOn(ItemUseContext context) {
		ItemStack offhandItemStack = context.getPlayer().getOffhandItem();
		
		if (offhandItemStack.getItem() == Items.POTION && offhandItemStack.hasTag()) {
			if (offhandItemStack.getTag().contains("Potion") || offhandItemStack.getTag().contains("CustomPotionEffects")) {
				EffectHelper.copyEffectsFromItem(context.getItemInHand(), offhandItemStack, context.getPlayer());
				EffectHelper.removeEffects(offhandItemStack);
				return ActionResultType.CONSUME;
			}
			else {
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.PASS;
		
	}
	

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		if (player.getOffhandItem().getItem() == Items.POTION && player.getOffhandItem().hasTag()) {
			if (player.getOffhandItem().getTag().contains("Potion") || player.getOffhandItem().getTag().contains("CustomPotionEffects")) {
				EffectHelper.copyEffectsFromItem(player.getMainHandItem(), player.getOffhandItem(), player);
			}
		}
		return super.use(world, player, hand);
	}

	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
		super.appendHoverText(stack, world, list, flag);
		EffectHelper.addPotionTooltip(stack, list, 0, "hands");
		if (!stack.hasTag()) {
			list.add(new TranslationTextComponent(this.getDescriptionId().concat(".tooltip.help")).withStyle(TextFormatting.GRAY));
		}
	}
}
