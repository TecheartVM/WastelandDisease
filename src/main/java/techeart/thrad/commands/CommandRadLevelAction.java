package techeart.thrad.commands;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import techeart.thrad.config.Configuration;
import techeart.thrad.RadiationManager;

import java.util.Collection;

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

    private static class Branches
    {
        public static LiteralArgumentBuilder<CommandSourceStack> set()
        {
            return Commands.literal("set")
                    //short variant must be first!
                    .then(Commands.argument("value", IntegerArgumentType.integer(Configuration.minRadLevel.get()))
                            .executes(Actions::set)
                    )
                    .then(Commands.argument("players", EntityArgument.players())
                            .then(Commands.argument("value", IntegerArgumentType.integer(Configuration.minRadLevel.get()))
                                    .executes(Actions::set)
                            )
                    );
        }

        public static LiteralArgumentBuilder<CommandSourceStack> reset()
        {
            return Commands.literal("reset")
                    .then(Commands.argument("players", EntityArgument.players())
                            .executes(Actions::reset)
                    )
                    .executes(Actions::reset);
        }

        public static LiteralArgumentBuilder<CommandSourceStack> add()
        {
            return Commands.literal("add")
                    //short variant must be first!
                    .then(Commands.argument("value", IntegerArgumentType.integer())
                            .executes(Actions::add)
                    )
                    .then(Commands.argument("players", EntityArgument.players())
                            .then(Commands.argument("value", IntegerArgumentType.integer())
                                    .executes(Actions::add)
                            )
                    );
        }

        public static LiteralArgumentBuilder<CommandSourceStack> get()
        {
            return Commands.literal("get")
                    .then(Commands.argument("player", EntityArgument.player())
                            .executes(Actions::get)
                    )
                    .executes(Actions::get);
        }
    }

    private static class Actions
    {
        public static int set(CommandContext<CommandSourceStack> commandSourceStack) throws CommandSyntaxException
        {
            Collection<ServerPlayer> players;
            try { players = EntityArgument.getPlayers(commandSourceStack, "players"); }
            catch (Exception e)
            {
                players = ImmutableList.of(commandSourceStack.getSource().getPlayerOrException());
            }
            int value = IntegerArgumentType.getInteger(commandSourceStack, "value");
            for (ServerPlayer player : players)
            {
                RadiationManager.setRadLevel(
                        player,
                        value
                );
            }
            commandSourceStack.getSource().sendSuccess(
                    players.size() > 1 ?
                            new TranslatableComponent("command.thrad.rad_level.set.multiple", players.size(), value)
                            : new TranslatableComponent(
                                    "command.thrad.rad_level.set.single",
                                    ((ServerPlayer)players.toArray()[0]).getDisplayName(),
                                    value
                            ),
                    true
            );
            return 1;
        }

        public static int reset(CommandContext<CommandSourceStack> commandSourceStack) throws CommandSyntaxException
        {
            Collection<ServerPlayer> players;
            try { players = EntityArgument.getPlayers(commandSourceStack, "players"); }
            catch (Exception e)
            {
                players = ImmutableList.of(commandSourceStack.getSource().getPlayerOrException());
            }
            for (ServerPlayer player : players)
            {
                RadiationManager.setRadLevel(
                        player,
                        Configuration.defaultRadLevel.get()
                );
            }
            commandSourceStack.getSource().sendSuccess(
                    players.size() > 1 ?
                            new TranslatableComponent("command.thrad.rad_level.reset.multiple", players.size())
                            : new TranslatableComponent(
                                    "command.thrad.rad_level.reset.single",
                                    ((ServerPlayer)players.toArray()[0]).getDisplayName()
                            ),
                    true
            );
            return 1;
        }

        public static int add(CommandContext<CommandSourceStack> commandSourceStack) throws CommandSyntaxException
        {
            Collection<ServerPlayer> players;
            try { players = EntityArgument.getPlayers(commandSourceStack, "players"); }
            catch (Exception e)
            {
                players = ImmutableList.of(commandSourceStack.getSource().getPlayerOrException());
            }
            int value = IntegerArgumentType.getInteger(commandSourceStack, "value");
            for (ServerPlayer player : players)
            {
                int i = RadiationManager.getRadLevel(player);
                RadiationManager.setRadLevel(
                        player,
                        i + value
                );
            }
            commandSourceStack.getSource().sendSuccess(
                    players.size() > 1 ?
                            new TranslatableComponent(
                                    "command.thrad.rad_level.add.multiple",
                                    players.size(),
                                    value
                            )
                            : new TranslatableComponent(
                                    "command.thrad.rad_level.add.single",
                                    ((ServerPlayer)players.toArray()[0]).getDisplayName(),
                                    value
                            ),
                    true
            );
            return 1;
        }

        public static int get(CommandContext<CommandSourceStack> commandSourceStack) throws CommandSyntaxException
        {
            ServerPlayer player;
            try { player = EntityArgument.getPlayer(commandSourceStack, "player"); }
            catch (Exception e)
            {
                player = commandSourceStack.getSource().getPlayerOrException();
            }
            int value = RadiationManager.getRadLevel(player);
            commandSourceStack.getSource().sendSuccess(
                    new TranslatableComponent("command.thrad.rad_level.get", player.getDisplayName(), value),
                    false
            );
            return 1;
        }
    }
}
