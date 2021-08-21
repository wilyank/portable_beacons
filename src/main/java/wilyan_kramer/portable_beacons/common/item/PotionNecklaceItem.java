package wilyan_kramer.portable_beacons.common.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Rarity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurio.SoundInfo;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import wilyan_kramer.portable_beacons.PortableBeaconsMod;
import wilyan_kramer.portable_beacons.client.render.model.NecklaceModel;
import wilyan_kramer.portable_beacons.common.config.Config;
import wilyan_kramer.portable_beacons.common.effect.EffectHelper;

public class PotionNecklaceItem extends Item implements ICurioItem {
	private Object model;

	private static final ResourceLocation BEACON_NECKLACE_TEXTURE = new ResourceLocation(PortableBeaconsMod.MODID, "textures/entity/beacon_necklace.png");
	public PotionNecklaceItem() {
		super(new Item.Properties().tab(InventoryTabs.TAB_PORTABLE_BEACONS).stacksTo(1).durability(0).rarity(Rarity.EPIC));
		this.setRegistryName("portable_beacons", "potion_necklace");
	}

	@Override
	public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
		if (!livingEntity.level.isClientSide && (livingEntity.tickCount % Config.COMMON.effectCooldown.get()) == 0) {
			if (stack.getItem() == ItemList.potion_necklace) {
				if (stack.hasTag()) {
					for (EffectInstance effInst : EffectHelper.setProperties(PotionUtils.getMobEffects(stack))) {
						livingEntity.addEffect(effInst);
					}
				}
			}
		}
	}
	@Override
	public boolean canRender(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
		return true;
	}
	@Override
	public void render(String identifier, int index, MatrixStack matrixStack,
			IRenderTypeBuffer renderTypeBuffer, int light, LivingEntity living,
			float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
			float netHeadYaw, float headPitch, ItemStack stack) {
		ICurio.RenderHelper.translateIfSneaking(matrixStack, living);
		ICurio.RenderHelper.rotateIfSneaking(matrixStack, living);

		if (!(this.model instanceof NecklaceModel)) {
			this.model = new NecklaceModel<>();
		}
		NecklaceModel<?> necklaceModel = (NecklaceModel<?>) this.model;
		IVertexBuilder vertexBuilder = ItemRenderer.getFoilBuffer(renderTypeBuffer, necklaceModel.renderType(BEACON_NECKLACE_TEXTURE), false, stack.hasFoil());
		necklaceModel.renderToBuffer(matrixStack, vertexBuilder, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Nonnull
	@Override
	public SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
		return new SoundInfo(SoundEvents.ARMOR_EQUIP_IRON, 1.0f, 1.0f);
	}

	@Override
	public boolean canEquipFromUse(SlotContext slot, ItemStack stack) {
		return true;
	}
	@Override
	public ActionResultType useOn(ItemUseContext context) {
		ItemStack necklace = context.getItemInHand();
		Block block = context.getLevel().getBlockState(context.getClickedPos()).getBlock();
		
		// clear effects when right clicking Cauldron
		if(block == Blocks.CAULDRON) {
			int waterLevel = context.getLevel().getBlockState(context.getClickedPos()).getBlockState().getValue(BlockStateProperties.LEVEL_CAULDRON);				
			if (waterLevel > 0) {
				((CauldronBlock) block).setWaterLevel(context.getLevel(), context.getClickedPos(), context.getLevel().getBlockState(context.getClickedPos()), waterLevel - 1);
				EffectHelper.removeEffects(necklace);
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.PASS;
	}
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
		super.appendHoverText(stack, world, list, flag);
		EffectHelper.addPotionTooltip(stack, list, 0, "necklace");
	}
	public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> stacksInGroup) {
		if (this.allowdedIn(group)) {
			stacksInGroup.add(new ItemStack(this));
			stacksInGroup.add(PotionUtils.setCustomEffects(new ItemStack(this), new ArrayList<EffectInstance>(Arrays.asList(new EffectInstance(Effects.GLOWING, 400)))));
			stacksInGroup.add(PotionUtils.setCustomEffects(new ItemStack(this), new ArrayList<EffectInstance>(Arrays.asList(new EffectInstance(Effects.LEVITATION, 400)))));
			stacksInGroup.add(PotionUtils.setPotion(new ItemStack(this), Potions.NIGHT_VISION));
			stacksInGroup.add(PotionUtils.setPotion(new ItemStack(this), Potions.SWIFTNESS));
			stacksInGroup.add(PotionUtils.setPotion(new ItemStack(this), Potions.STRENGTH));
			stacksInGroup.add(PotionUtils.setPotion(new ItemStack(this), Potions.LEAPING));
			stacksInGroup.add(PotionUtils.setPotion(new ItemStack(this), Potions.SLOW_FALLING));
			stacksInGroup.add(PotionUtils.setPotion(new ItemStack(this), Potions.NIGHT_VISION));
			stacksInGroup.add(PotionUtils.setPotion(new ItemStack(this), Potions.FIRE_RESISTANCE));
			stacksInGroup.add(PotionUtils.setPotion(new ItemStack(this), Potions.TURTLE_MASTER));
		}
	}
}
