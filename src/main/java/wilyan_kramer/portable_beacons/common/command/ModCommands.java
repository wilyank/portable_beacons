package wilyan_kramer.portable_beacons.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import wilyan_kramer.portable_beacons.PortableBeaconsMod;

public class ModCommands {
	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		LiteralCommandNode<CommandSource> commands =  dispatcher.register(
				Commands.literal(PortableBeaconsMod.MODID)
				.then(InfuseCommand.register(dispatcher).requires(cs -> cs.hasPermission(2))));
		dispatcher.register(Commands.literal("pb").redirect(commands));
	}
}
