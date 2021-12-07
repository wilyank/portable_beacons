package wilyan_kramer.portable_beacons.common;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import wilyan_kramer.portable_beacons.common.config.Config;
import wilyan_kramer.portable_beacons.common.item.ItemList;

public class EventListeners {
	private Random random = new Random();

	@SubscribeEvent
	public void applyStarEffect(PlayerTickEvent event) {
		ItemStack[] heldItemStacks = {event.player.inventory.getSelected(), event.player.getOffhandItem()};
		if (event.side.isServer() && event.player.tickCount % 20 == 0) {
			for (ItemStack heldItemStack : heldItemStacks) {
				if (heldItemStack.getItem() == ItemList.infused_star) {
					if (heldItemStack.hasTag()) {
						for (EffectInstance effInst : PotionUtils.getAllEffects(heldItemStack.getTag())) {
							event.player.addEffect(new EffectInstance(effInst.getEffect(), 300, effInst.getAmplifier(), true, true, true));
						}
					}
				}
			}
		}
	}
	@SubscribeEvent
	public void doWitchArmor(LivingAttackEvent event) {
		if (Config.COMMON.witchArmor.get()) {
			if (event.getEntity() instanceof WitchEntity) {
				if (random.nextInt(Config.COMMON.witchArmorChance.get()) == 0 && Config.COMMON.witchArmorEffects.get().size() > 0) {
//					List<Pair<Integer, Integer>> config = Config.unpackList(Config.COMMON.witchArmorEffects);
					List<int[]> config = Config.unpackList(Config.COMMON.witchArmorEffects);
					int[] triplet = config.get(random.nextInt(config.size()));
					if (event.getSource().getDirectEntity() != null) {
						if (event.getSource().getDirectEntity() instanceof LivingEntity) {
							EffectInstance effInst = new EffectInstance(Effect.byId(triplet[0]), triplet[1], triplet[2]);
							effInst.setCurativeItems(Arrays.asList(new ItemStack(Items.MILK_BUCKET)));
							if (!(event.getEntity().level.isClientSide))
								((LivingEntity) event.getSource().getDirectEntity()).addEffect(effInst);
						}
					}
				}
			}
		}
	}
}