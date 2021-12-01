package wilyan_kramer.portable_beacons.common.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.SwordItem;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.PotionUtils;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import wilyan_kramer.portable_beacons.common.effect.EffectHelper;

public class InfusedSwordItem extends SwordItem {

	private static final List<EffectInstance> allowedEffects = new ArrayList<EffectInstance>(Arrays.asList(
			new EffectInstance(Effects.WEAKNESS, 400),
			new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 50),
			new EffectInstance(Effects.BLINDNESS, 50),
			new EffectInstance(Effects.SLOW_FALLING, 400),
			new EffectInstance(Effects.DIG_SLOWDOWN, 100),
			new EffectInstance(Effects.POISON, 60),
			new EffectInstance(Effects.REGENERATION, 100),
			new EffectInstance(Effects.LEVITATION, 200),
			new EffectInstance(Effects.GLOWING, 400)));
	public InfusedSwordItem(IItemTier tier, int atkdmg, float atkspeed, Properties prop) {
		super(tier, atkdmg, atkspeed, prop);
	}
	
	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity enemy, LivingEntity player) {
		if (!player.level.isClientSide && stack.hasTag()) {
			for (EffectInstance effInst : EffectHelper.getAllEffects(stack)) {
				enemy.addEffect(effInst);
			}
		}
		return super.hurtEnemy(stack, enemy, player);
	}
	@Override
	public ActionResultType useOn(ItemUseContext context) {
		ItemStack sword = context.getItemInHand();
		Block block = context.getLevel().getBlockState(context.getClickedPos()).getBlock();
		
		// clear effects when right clicking Cauldron
		if(block == Blocks.CAULDRON) {
			int waterLevel = context.getLevel().getBlockState(context.getClickedPos()).getBlockState().getValue(BlockStateProperties.LEVEL_CAULDRON);				
			if (waterLevel > 0) {
				((CauldronBlock) block).setWaterLevel(context.getLevel(), context.getClickedPos(), context.getLevel().getBlockState(context.getClickedPos()), waterLevel - 1);
				EffectHelper.removeEffects(sword);
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.PASS;
	}
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
		super.appendHoverText(stack, world, list, flag);
		EffectHelper.addPotionTooltip(stack, list, 0, "hit");
	}
	public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> stacksInGroup) {
      if (this.allowdedIn(group)) {
    	  stacksInGroup.add(new ItemStack(this));
         for(EffectInstance effInst : allowedEffects ) {
               stacksInGroup.add(PotionUtils.setCustomEffects(new ItemStack(this), new ArrayList<EffectInstance>(Arrays.asList(effInst))));
         }
         if (ModList.get().isLoaded("alexsmobs")) {
				stacksInGroup.add(
						PotionUtils.setCustomEffects(
								new ItemStack(this), new ArrayList<EffectInstance>(
										Arrays.asList(
												new EffectInstance(
														ForgeRegistries.POTIONS.getValue(
																new ResourceLocation("alexsmobs", "sunbird_curse")
																),
														100,
														0
														)
												)
										)
								)
						);
         }
      }
   }
}
