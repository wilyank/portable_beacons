package wilyan_kramer.portable_beacons.common.effect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.potion.Effects;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.Curios;
import wilyan_kramer.portable_beacons.PortableBeaconsMod;
import wilyan_kramer.portable_beacons.common.config.Config;
import wilyan_kramer.portable_beacons.common.item.BeaconBackpackItem;

public class EffectHelper {
	// a class containing helper functions for handling effects on items
	
//	private static final Logger LOGGER = LogManager.getLogger();
	private static final IFormattableTextComponent NO_EFFECT = (new TranslationTextComponent("effect.none")).withStyle(TextFormatting.GRAY);

	private static final List<ItemStack> curativeItems = new ArrayList<>(Arrays.asList(new ItemStack(Items.MILK_BUCKET)));
	
	public static List<EffectInstance> getAllEffects(ItemStack stack){
		return PotionUtils.getAllEffects(stack.getTag());
	}
	
	public static ItemStack addUniqueCustomPotionEffects(ItemStack stack, List<EffectInstance> effInstListAddition) {
		List<EffectInstance> effInstListBase = getAllEffects(stack);
		
		boolean flag = false;
		for (int i = 0; i < effInstListAddition.size(); i++) {
			for (int j = 0; j < effInstListBase.size(); j++) {
				if (effInstListAddition.get(i).getEffect() == effInstListBase.get(j).getEffect()) {
					if (effInstListAddition.get(i).getAmplifier() > effInstListBase.get(j).getAmplifier()) {
						effInstListBase.set(j, effInstListAddition.get(i));
					}
					if (effInstListAddition.get(i).getAmplifier() == effInstListBase.get(i).getAmplifier()) {
						if (effInstListAddition.get(i).getDuration() > effInstListBase.get(i).getDuration()) {
							effInstListBase.set(j, effInstListAddition.get(i));
						}
					}
					flag = true;
				}
			}
			if (!flag) {
				effInstListBase.add(effInstListAddition.get(i));
			}
		}
		PotionUtils.setPotion(stack, Potions.EMPTY);
		PotionUtils.setCustomEffects(stack, effInstListBase);
		return stack;
	}
	public static ItemStack removeEffects(ItemStack stack) {
		
		stack.removeTagKey("CustomPotionEffects");
		
		if (stack.getItem() == Items.POTION)
			PotionUtils.setPotion(stack, Potions.WATER);
		else
			PotionUtils.setPotion(stack, Potions.EMPTY);
		return stack;
	}
	public static List<EffectInstance> setProperties(List<EffectInstance> effInstList) {
		return setProperties(effInstList, Config.COMMON.effectDuration.get());
	}
	public static List<EffectInstance> setProperties(List<EffectInstance> effInstList, int duration) {
		for (int i = 0; i < effInstList.size(); i++) {
			effInstList.set(i, new EffectInstance(effInstList.get(i).getEffect(), duration, effInstList.get(i).getAmplifier(), true, true, true));
			effInstList.get(i).setCurativeItems(curativeItems);
		}
		return effInstList;
	}

	public static ItemStack copyEffectsFromItem(ItemStack mainhandItemStack, ItemStack offhandItemStack, PlayerEntity player) {
		player.hurt(DamageSource.MAGIC, 19);
		if (mainhandItemStack.getItem() instanceof BeaconBackpackItem) {
			if ( ((BeaconBackpackItem) mainhandItemStack.getItem()).getTier() < 1)
				return mainhandItemStack;
			if (EffectHelper.getAllEffects(mainhandItemStack).size() >= ((BeaconBackpackItem) mainhandItemStack.getItem()).getTier())
				return mainhandItemStack;
		}
		addUniqueCustomPotionEffects(mainhandItemStack, getAllEffects(offhandItemStack));
		EffectHelper.removeEffects(offhandItemStack);
		return mainhandItemStack;
	}
	public static List<EffectInstance> getBeaconEffects(BeaconTileEntity beaconTE) {
		if (!Config.COMMON.canCopyFromBeacon.get()) {
			return new ArrayList<EffectInstance>();
		}
		if (Config.COMMON.onlyCopyFromFullBeacon.get() && beaconTE.getUpdateTag().getInt("Levels") < 4) {
			return new ArrayList<EffectInstance>();
		}
		List<EffectInstance> effInstList = new ArrayList<EffectInstance>();
		
		int primaryEffId = beaconTE.getUpdateTag().getInt("Primary");
		int secondaryEffId = beaconTE.getUpdateTag().getInt("Secondary");
		
		if(secondaryEffId == primaryEffId) {
			if (primaryEffId != -1 && secondaryEffId != -1) {
				effInstList.add(new EffectInstance(Effect.byId(primaryEffId), 0, 1));
			}
		}
		else {
			if (primaryEffId != -1){
				effInstList.add(new EffectInstance(Effect.byId(primaryEffId)));
			}
			if (secondaryEffId != -1) {
				effInstList.add(new EffectInstance(Effect.byId(secondaryEffId)));
			}
		}
		return setProperties(effInstList);
	}
	public static List<EffectInstance> getNetheriteBeaconEffects(TileEntity tileEntity) {
		if (!Config.COMMON.canCopyFromNetheriteBeacon.get()) {
			return new ArrayList<EffectInstance>();
		}
		if (Config.COMMON.onlyCopyFromFullBeacon.get() && tileEntity.getUpdateTag().getInt("Levels") < 4) {
			return new ArrayList<EffectInstance>();
		}
		List<EffectInstance> effInstList = new ArrayList<EffectInstance>();
		int primaryId = tileEntity.getUpdateTag().getInt("Primary");
		int secondaryId = tileEntity.getUpdateTag().getInt("Secondary");
		int tertiaryId = tileEntity.getUpdateTag().getInt("Tertiary");
		if (secondaryId == primaryId) {
			if (tertiaryId == primaryId) {
				if (primaryId != -1) {
					effInstList.add(new EffectInstance(Effect.byId(primaryId), 0, 2));
				}
			}
			else {
				if (primaryId != -1) {
					effInstList.add(new EffectInstance(Effect.byId(primaryId), 0, 1));
				}
				if (tertiaryId != -1) {
					effInstList.add(new EffectInstance(Effect.byId(tertiaryId)));
				}
			}
		}
		else if (secondaryId == tertiaryId) {
			if (primaryId != -1) {
				effInstList.add(new EffectInstance(Effect.byId(primaryId)));
			}
			if (secondaryId != -1) {
				effInstList.add(new EffectInstance(Effect.byId(secondaryId), 0, 1));
			}
		}
		else {
			if (primaryId != -1) {
				effInstList.add(new EffectInstance(Effect.byId(primaryId)));
			}
			if (secondaryId != -1) {
				effInstList.add(new EffectInstance(Effect.byId(secondaryId)));
			}
			if (secondaryId != -1) {
				effInstList.add(new EffectInstance(Effect.byId(tertiaryId)));
			}
		}
		if (tileEntity.getUpdateTag().getInt("NetheriteLevel") > 9) {
			effInstList.add(new EffectInstance(Effects.FIRE_RESISTANCE));
		}
		return setProperties(effInstList);
	}
 	
	public static int getPotionColor(ItemStack stack) {
		if (PotionUtils.getPotion(stack) != Potions.EMPTY) {
			return PotionUtils.getColor(stack);
		}
		if (getAllEffects(stack).size() > 0) {
			return PotionUtils.getColor(getAllEffects(stack));
		}
		return 16253176; //default potion color
	}

	@OnlyIn(Dist.CLIENT)
	public static void addPotionTooltip(ItemStack stack, List<ITextComponent> textList, float flag, @Nullable String slotId) {
		// This is a modified version of the vanilla function PotionUtils.addPotionTooltip()
		List<EffectInstance> list = getAllEffects(stack);
		List<Pair<Attribute, AttributeModifier>> list1 = Lists.newArrayList();
		if (list.isEmpty()) {
			textList.add(NO_EFFECT);
		} else {
			for(EffectInstance effectinstance : list) {
				IFormattableTextComponent iformattabletextcomponent = new TranslationTextComponent(effectinstance.getDescriptionId());
				Effect effect = effectinstance.getEffect();
				Map<Attribute, AttributeModifier> map = effect.getAttributeModifiers();
				if (!map.isEmpty()) {
					for(Entry<Attribute, AttributeModifier> entry : map.entrySet()) {
						AttributeModifier attributemodifier = entry.getValue();
						AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), effect.getAttributeModifierValue(effectinstance.getAmplifier(), attributemodifier), attributemodifier.getOperation());
						list1.add(new Pair<>(entry.getKey(), attributemodifier1));
					}
				}

				if (effectinstance.getAmplifier() > 0) {
					iformattabletextcomponent = new TranslationTextComponent("potion.withAmplifier", iformattabletextcomponent, new TranslationTextComponent("potion.potency." + effectinstance.getAmplifier()));
				}
				if (effectinstance.getDuration() > 20 && slotId == "hit") {
		            iformattabletextcomponent = new TranslationTextComponent("potion.withDuration", iformattabletextcomponent, EffectUtils.formatDuration(effectinstance, 1F));
		        }

				textList.add(iformattabletextcomponent.withStyle(effect.getCategory().getTooltipFormatting()));
			}
		}

		if (!list1.isEmpty()) {
			textList.add(StringTextComponent.EMPTY);
			if (slotId == "hit") {
				textList.add((new TranslationTextComponent("item." + PortableBeaconsMod.MODID + ".tooltip.on_hit")).withStyle(TextFormatting.GOLD));
			}
			else {
				textList.add((new TranslationTextComponent(Curios.MODID + ".modifiers." + slotId)).withStyle(TextFormatting.GOLD));
			}
			for(Pair<Attribute, AttributeModifier> pair : list1) {
				AttributeModifier attributemodifier2 = pair.getSecond();
				double d0 = attributemodifier2.getAmount();
				double d1;
				if (attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
					d1 = attributemodifier2.getAmount();
				} else {
					d1 = attributemodifier2.getAmount() * 100.0D;
				}

				if (d0 > 0.0D) {
					textList.add((new TranslationTextComponent("attribute.modifier.plus." + attributemodifier2.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), new TranslationTextComponent(pair.getFirst().getDescriptionId()))).withStyle(TextFormatting.BLUE));
				} else if (d0 < 0.0D) {
					d1 = d1 * -1.0D;
					textList.add((new TranslationTextComponent("attribute.modifier.take." + attributemodifier2.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), new TranslationTextComponent(pair.getFirst().getDescriptionId()))).withStyle(TextFormatting.RED));
				}
			}
		}
	}
}
