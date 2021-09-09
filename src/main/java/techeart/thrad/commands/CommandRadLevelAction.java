package techeart.thrad.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import techeart.thrad.config.Configuration;
import techeart.thrad.utils.RadiationManager;

public class CommandRadLevelAction
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("rad")
                .requires((commandSourceStack -> commandSourceStack.hasPermission(2)))
                .then(Branches.set())
                .then(Branches.reset())
                .then(Branches.add())
                .then(Branches.get());

        dispatcher.register(command);
    }

    protected static void sendMessage(CommandContext<CommandSourceStack> context, String message) throws CommandSyntaxException
    {
        Player player = context.getSource().getPlayerOrException();
        context.getSource().getServer().getPlayerList()
                .broadcastMessage(new TextComponent(message), ChatType.CHAT, player.getUUID());
    }

    private static class Branches
    {
        public static LiteralArgumentBuilder<CommandSourceStack> set()
        {
            return Commands.literal("set")
                    .then(Commands.argument("player", EntityArgument.player())
                            .then(Commands.argument("value", IntegerArgumentType.integer(Configuration.minRadLevel.get()))
                                    .executes((commandSourceStack) -> {
                                        RadiationManager.setRadLevel(
                                                EntityArgument.getPlayer(commandSourceStack, "player"),
                                                IntegerArgumentType.getInteger(commandSourceStack, "value")
                                        );
                                        return 1;
                                    })
                            )
                    );
        }

        public static LiteralArgumentBuilder<CommandSourceStack> reset()
        {
            return Commands.literal("reset")
                    .then(Commands.argument("player", EntityArgument.player())
                            .executes((commandSourceStack) -> {
                                RadiationManager.setRadLevel(
                                        EntityArgument.getPlayer(commandSourceStack, "player"),
                                        Configuration.defaultRadLevel.get()
                                );
                                return 1;
                            })
                    );
        }

        public static LiteralArgumentBuilder<CommandSourceStack> add()
        {
            return Commands.literal("add")
                    .then(Commands.argument("player", EntityArgument.player())
                            .then(Commands.argument("value", IntegerArgumentType.integer())
                                    .executes((commandSourceStack) -> {
                                        int i = RadiationManager.getRadLevel(EntityArgument.getPlayer(commandSourceStack, "player"));
                                        RadiationManager.setRadLevel(
                                                EntityArgument.getPlayer(commandSourceStack, "player"),
                                                i + IntegerArgumentType.getInteger(commandSourceStack, "value")
                                        );
                                        return 1;
                                    })
                            )
                    );
        }

        public static LiteralArgumentBuilder<CommandSourceStack> get()
        {
            return Commands.literal("get")
                    .then(Commands.argument("player", EntityArgument.player())
                            .executes((commandSourceStack) -> {
                                int i = RadiationManager.getRadLevel(EntityArgument.getPlayer(commandSourceStack, "player"));
                                sendMessage(commandSourceStack, Integer.toString(i));
                                return 1;
                            })
                    );
        }
    }
}
