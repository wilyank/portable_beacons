package wilyan_kramer.portable_beacons.common.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurio.SoundInfo;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import wilyan_kramer.portable_beacons.PortableBeaconsMod;
import wilyan_kramer.portable_beacons.client.render.model.BackpackModel;
import wilyan_kramer.portable_beacons.common.config.Config;
import wilyan_kramer.portable_beacons.common.effect.EffectHelper;
import wilyan_kramer.portable_beacons.setup.CommonSetup;

public class BeaconBackpackItem extends Item implements ICurioItem {

	private static final Logger LOGGER = LogManager.getLogger();

	private Object model;
	private final int beaconTier;
	private static final ResourceLocation BEACON_BACKPACK_TEXTURE = new ResourceLocation(PortableBeaconsMod.MODID, "textures/entity/beacon_backpack.png");

	public BeaconBackpackItem() {
		this("beacon_backpack", 0);
	}

	public BeaconBackpackItem(String name, int tier) {
		super(new Item.Properties().tab(InventoryTabs.TAB_PORTABLE_BEACONS).stacksTo(1).durability(0).rarity(Rarity.EPIC));
		this.setRegistryName("portable_beacons", name);
		this.beaconTier = tier;
	}
	
	public int getTier() {
		return this.beaconTier;
	}
	public double getRange() {
		return this.beaconTier * Config.COMMON.beaconRangeA.get() + Config.COMMON.beaconRangeB.get();
	}
	public boolean isFireResistant() {
		return true;
	}

	@Override
	public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
		if (!livingEntity.level.isClientSide) {
			if (livingEntity.level.getGameTime() %  Config.COMMON.effectCooldown.get() == 0) {
				if (stack.hasTag()) {
					if (Config.COMMON.beaconOthers.get() || Config.COMMON.beaconSelf.get() ) {
						AxisAlignedBB axisalignedbb = (new AxisAlignedBB(livingEntity.blockPosition())).inflate(this.getRange());
						List<PlayerEntity> playerList = livingEntity.level.getEntitiesOfClass(PlayerEntity.class, axisalignedbb);
						List<EffectInstance> effList = EffectHelper.getAllEffects(stack);
						for (PlayerEntity player : playerList) {
							if (player == livingEntity && !(Config.COMMON.beaconSelf.get())) {
								continue;
							}
							if (player != livingEntity && !(Config.COMMON.beaconOthers.get())) {
								continue;
							}
							for (EffectInstance effInst : effList) {
								LOGGER.info("Applying {} to {}", effInst.getEffect().getRegistryName(), player);
								player.addEffect(effInst);
							}
						}
					}
				}

			}
		}
	}

	@Override
	public ActionResultType useOn(ItemUseContext context) {
		ItemStack backpack = context.getItemInHand();
		Block block = context.getLevel().getBlockState(context.getClickedPos()).getBlock();
		// add effects from beacon to backpack when backpack is not full if Beacon is right clicked
		if(block == Blocks.BEACON) {
			
			List<EffectInstance> beaconEffects = EffectHelper.getBeaconEffects((BeaconTileEntity) context.getLevel().getBlockEntity(context.getClickedPos()));
			int tier = ((BeaconBackpackItem) backpack.getItem()).getTier();
			
			boolean flag = false;
			for (int i = 0; i < beaconEffects.size(); i++) {
				ItemStack stack = backpack.copy();
				if (EffectHelper.getAllEffects(
						EffectHelper.addUniqueCustomPotionEffects(stack, 
								new ArrayList<>(Arrays.asList(beaconEffects.get(i))))).size() <= tier + 1) {
					EffectHelper.addUniqueCustomPotionEffects(
							backpack, 
							new ArrayList<>(Arrays.asList(beaconEffects.get(i))));
					flag = true;
				}
			}
			if (flag) {
				context.getPlayer().hurt(DamageSource.MAGIC, context.getPlayer().getMaxHealth() - 1);
				return ActionResultType.SUCCESS;
			}
			return ActionResultType.CONSUME;
		}
		// add Conduit Power to backpack when right clicking Conduit and backpack is not full
		if (block == Blocks.CONDUIT) {
			if (EffectHelper.getAllEffects(backpack).size() <= ((BeaconBackpackItem) backpack.getItem()).getTier() ) {
				if (Config.COMMON.canCopyFromConduit.get()) {
					EffectHelper.addUniqueCustomPotionEffects(backpack, new ArrayList<>(Arrays.asList(new EffectInstance(Effects.CONDUIT_POWER, 300, 0, true, true, true))));
					context.getPlayer().hurt(DamageSource.MAGIC, context.getPlayer().getMaxHealth() - 1);
				}
				return ActionResultType.SUCCESS;
			}
			return ActionResultType.CONSUME;
		}
		// clear effects when right clicking Cauldron
		if(block == Blocks.CAULDRON) {
			int waterLevel = context.getLevel().getBlockState(context.getClickedPos()).getBlockState().getValue(BlockStateProperties.LEVEL_CAULDRON);				
			if (waterLevel > 0) {
				((CauldronBlock) block).setWaterLevel(context.getLevel(), context.getClickedPos(), context.getLevel().getBlockState(context.getClickedPos()), waterLevel - 1);
				EffectHelper.removeEffects(backpack);
				return ActionResultType.SUCCESS;
			}
			return ActionResultType.CONSUME;
		}
		return super.useOn(context);
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

		if (!(this.model instanceof BackpackModel)) {
			this.model = new BackpackModel<>();
		}
		BackpackModel<?> backpackModel = (BackpackModel<?>) this.model;
		IVertexBuilder vertexBuilder = ItemRenderer.getFoilBuffer(renderTypeBuffer, backpackModel.renderType(BEACON_BACKPACK_TEXTURE), false, stack.hasFoil());
		backpackModel.renderToBuffer(matrixStack, vertexBuilder, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
	}


	@Nonnull
	@Override
	public SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
		return new SoundInfo(SoundEvents.ARMOR_EQUIP_DIAMOND, 1.0f, 1.0f);
	}

	@Override
	public boolean canEquipFromUse(SlotContext slot, ItemStack stack) {
		return true;
	}
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
		super.appendHoverText(stack, world, list, flag);
		EffectHelper.addPotionTooltip(stack, list, 0, "back");
		if (!stack.hasTag()) {
			if (Config.COMMON.canCopyFromBeacon.get() && Config.COMMON.canCopyFromConduit.get()) {
				list.add(new TranslationTextComponent(
						"item.portable_beacons.beacon_backpack.tooltip.help", new TranslationTextComponent("item.portable_beacons.tooltip.or", 
								new TranslationTextComponent(Items.BEACON.getDescriptionId()).withStyle(TextFormatting.AQUA),
								new TranslationTextComponent(Items.CONDUIT.getDescriptionId()).withStyle(TextFormatting.AQUA)
						)).withStyle(TextFormatting.GRAY));
			}
			else if (Config.COMMON.canCopyFromBeacon.get()) {
				list.add(new TranslationTextComponent(
						"item.portable_beacons.beacon_backpack.tooltip.help", 
						new TranslationTextComponent(Items.BEACON.getDescriptionId()).withStyle(TextFormatting.AQUA)
						).withStyle(TextFormatting.GRAY) );
			}
			else if (Config.COMMON.canCopyFromConduit.get()) {
				list.add(new TranslationTextComponent(
						"item.portable_beacons.beacon_backpack.tooltip.help", 
						new TranslationTextComponent(Items.CONDUIT.getDescriptionId()).withStyle(TextFormatting.AQUA)
						).withStyle(TextFormatting.GRAY) );
			}

		}
	}
}
