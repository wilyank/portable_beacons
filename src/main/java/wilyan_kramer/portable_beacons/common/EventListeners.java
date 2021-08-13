package wilyan_kramer.portable_beacons.common;

import java.util.Arrays;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import wilyan_kramer.portable_beacons.common.config.Config;
import wilyan_kramer.portable_beacons.common.item.ItemList;

public class EventListeners {
	private Random random = new Random();
	private static final Logger LOGGER = LogManager.getLogger();

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
				Effect effect;
				int duration;
				if (random.nextInt(Config.COMMON.witchArmorChance.get()) == 0) {
					switch(random.nextInt(8)) {
					case 0:
						effect = Effects.BLINDNESS;
						duration = 40;
						break;
					case 1:
						effect = Effects.POISON;
						duration = 40;
						break;
					case 2:
						effect = Effects.CONFUSION;
						duration = 100;
						break;
					case 3:
						effect = Effects.DIG_SLOWDOWN;
						duration = 400;
						break;
					case 4:
						effect = Effects.GLOWING;
						duration = 400;
						break;
					case 5:
						effect = Effects.WEAKNESS;
						duration = 400;
						break;
					case 6:
						effect = Effects.LEVITATION;
						duration = 200;
						break;
					case 7:
						effect = Effects.WITHER;
						duration = 40;
						break;
					default:
						effect = Effects.GLOWING;
						duration = 0;
						break;
					}
					if (event.getSource().getDirectEntity() != null) {
						if (event.getSource().getDirectEntity() instanceof LivingEntity) {
							EffectInstance effInst = new EffectInstance(effect, duration);
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