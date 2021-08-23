package wilyan_kramer.portable_beacons.common.command;

import java.util.ArrayList;
import java.util.Arrays;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.PotionArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import wilyan_kramer.portable_beacons.common.effect.EffectHelper;
import wilyan_kramer.portable_beacons.common.item.ItemList;

public class InfuseCommand {
	
	public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
		return Commands.literal("infuse")
				.then(Commands.argument("targetPlayer", EntityArgument.player())
						.then(Commands.literal("add")
								.then(Commands.argument("effect", PotionArgument.effect())
										.then(Commands.argument("duration", IntegerArgumentType.integer())
												.then(Commands.argument("amplifier", IntegerArgumentType.integer())
												.executes(context -> infuse(context.getSource(), EntityArgument.getPlayer(context, "targetPlayer"), PotionArgument.getEffect(context, "effect"), IntegerArgumentType.getInteger(context, "duration"), IntegerArgumentType.getInteger(context, "amplifier"))))
												)
										)
								)
						.then(Commands.literal("clear")
						.executes(context -> clearEffects(context.getSource(), EntityArgument.getPlayer(context, "targetPlayer"))))
						)
				;
	}

	private static int infuse(CommandSource source, ServerPlayerEntity player, Effect effect, int duration, int amplifier) {
		ItemStack itemStack = player.getItemInHand(Hand.MAIN_HAND);
		if (!itemStack.isEmpty()) {
			if (
					itemStack.getItem() == ItemList.beacon_backpack_0 ||
					itemStack.getItem() == ItemList.beacon_backpack_1 ||
					itemStack.getItem() == ItemList.beacon_backpack_2 ||
					itemStack.getItem() == ItemList.beacon_backpack_3 ||
					itemStack.getItem() == ItemList.infused_dagger ||
					itemStack.getItem() == ItemList.potion_necklace ||
					itemStack.getItem() == ItemList.infused_star ||
					itemStack.getItem() == Items.POTION ||
					itemStack.getItem() == Items.LINGERING_POTION ||
					itemStack.getItem() == Items.SPLASH_POTION
					) {
			EffectHelper.addUniqueCustomPotionEffects(itemStack, new ArrayList<EffectInstance>(Arrays.asList(new EffectInstance(effect, duration, amplifier))));
			source.sendSuccess(
					new TranslationTextComponent("message.portable_beacons.command.infuse.add", 
							new TranslationTextComponent("potion.withAmplifier", new TranslationTextComponent(effect.getDescriptionId()), new TranslationTextComponent("potion.potency." + amplifier)),
							new TranslationTextComponent(itemStack.getDescriptionId())
							), false);
			}
		}
		else {
			source.sendFailure(new TranslationTextComponent("message.portable_beacons.command.infuse.error"));
		}
		return 0;
	}
	private static int clearEffects(CommandSource source, ServerPlayerEntity player) {
		ItemStack itemStack = player.getItemInHand(Hand.MAIN_HAND);
		if (!itemStack.isEmpty()) {
			EffectHelper.removeEffects(itemStack);
			source.sendSuccess(new TranslationTextComponent("message.portable_beacons.command.infuse.clear", new TranslationTextComponent(itemStack.getDescriptionId())), false);
		}
		else {
			source.sendFailure(new TranslationTextComponent("message.portable_beacons.command.infuse.error"));
		}
		return 0;
	}
	
}
