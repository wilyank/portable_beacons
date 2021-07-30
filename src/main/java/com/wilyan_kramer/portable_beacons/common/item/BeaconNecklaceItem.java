package com.wilyan_kramer.portable_beacons.common.item;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.wilyan_kramer.portable_beacons.PortableBeaconsMod;
import com.wilyan_kramer.portable_beacons.client.render.model.NecklaceModel;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
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

public class BeaconNecklaceItem extends Item implements ICurioItem {
	private Object model;

	private static final ResourceLocation BEACON_NECKLACE_TEXTURE = new ResourceLocation(PortableBeaconsMod.MODID, "textures/entity/beacon_necklace.png");
	public BeaconNecklaceItem() {
		super(new Item.Properties().tab(PortableBeaconsMod.TAB_PORTABLE_BEACONS).stacksTo(1).durability(0).rarity(Rarity.EPIC));
		this.setRegistryName("portable_beacons", "beacon_necklace");
	}

	@Override
	public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
		if (!livingEntity.level.isClientSide && (livingEntity.tickCount % 20) == 0) {
			if (stack.getItem() == ItemList.beacon_necklace) {
				if (stack.hasTag()) {
					for (EffectInstance effInst : PotionUtils.getMobEffects(stack)) {
						livingEntity.addEffect(new EffectInstance(effInst.getEffect(), 300, effInst.getAmplifier(), true, true, true));
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
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
		super.appendHoverText(stack, world, list, flag);
		EffectHelper.addPotionTooltip(stack, list, 0, "necklace");
	}
}
