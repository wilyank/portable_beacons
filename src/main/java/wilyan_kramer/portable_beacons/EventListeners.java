package wilyan_kramer.portable_beacons;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import wilyan_kramer.portable_beacons.common.item.ItemList;

public class EventListeners {

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
}