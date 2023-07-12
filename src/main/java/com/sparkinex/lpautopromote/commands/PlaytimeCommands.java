package com.sparkinex.lpautopromote.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.sparkinex.lpautopromote.utils.PermissionLevels;
import com.sparkinex.lpautopromote.capability.*;
import com.sparkinex.lpautopromote.utils.Toolkit;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class PlaytimeCommands {
    public PlaytimeCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("playtime")
                .requires((source) -> source.hasPermission(PermissionLevels.getPermissionLevel(PermissionLevels.ALL)))
                .executes(context -> getPlaytime(context.getSource().getPlayerOrException(), null))
                .requires((source) -> source.hasPermission(PermissionLevels.getPermissionLevel(PermissionLevels.ADMIN)))
                .then(Commands.argument("target", EntityArgument.player())
                        .executes(context -> getPlaytime(context.getSource().getPlayerOrException(), EntityArgument.getPlayer(context, "target")))
                )
        );

        dispatcher.register(Commands.literal("setplaytime")
                .requires((source) -> source.hasPermission(PermissionLevels.getPermissionLevel(PermissionLevels.ADMIN)))
                .then(Commands.argument("newTimeInMinutes", IntegerArgumentType.integer())
                        .executes(context -> setPlaytime(context.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(context, "newTimeInMinutes"), null))
                )
                .then(Commands.argument("target", EntityArgument.player())
                        .executes(context -> setPlaytime(context.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(context, "newTimeInMinutes"), EntityArgument.getPlayer(context, "target")))
                )
        );

        dispatcher.register(Commands.literal("setplaytime")
                .requires((source) -> source.hasPermission(PermissionLevels.getPermissionLevel(PermissionLevels.ADMIN)))
                .then(Commands.argument("newTimeInMinutes", IntegerArgumentType.integer()))
                .then(Commands.argument("target", EntityArgument.player())
                        .executes(context -> setPlaytime(context.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(context, "newTimeInMinutes"), EntityArgument.getPlayer(context, "target")))
                )
        );
    }

    private int getPlaytime(ServerPlayer player, @Nullable ServerPlayer target) throws CommandSyntaxException {
        ServerPlayer targetPlayer = target == null ? player : target;

        IAutoRank realPlayTimeCapability = targetPlayer.getCapability(CapabilityAutoRankProvider.REAL_PLAY_TIME_CAPABILITY).orElseThrow(IllegalStateException::new);

        int playtimeTicks = realPlayTimeCapability.getRealPlayTime();

        int playtimeSeconds = playtimeTicks / 20;
        int playtimeHours = playtimeSeconds / 3600;
        int playtimeMinutes = (playtimeSeconds % 3600) / 60;

        if (target == null) {
            player.sendSystemMessage(Component.literal(Toolkit.translateColorCodes("&7Your playtime: &e" + playtimeHours + " &7hours, &e" + playtimeMinutes + " &7minutes.")));
        } else {
            player.sendSystemMessage(Component.literal(Toolkit.translateColorCodes("&e" + targetPlayer.getDisplayName().getString() + "'s &7playtime: &e" + playtimeHours + " &7hours, &e" + playtimeMinutes + " &7minutes.")));
        }

        return 1;
    }

    private int setPlaytime(ServerPlayer player, int newTimeInMinutes, @Nullable ServerPlayer target) throws CommandSyntaxException {
        ServerPlayer targetPlayer = target == null ? player : target;

        if (newTimeInMinutes <= 0) {
            player.sendSystemMessage(Component.literal(Toolkit.translateColorCodes("&7Cannot set playtime to less than or equal to 0 minutes.")));
            return 1;
        }

        IAutoRank realPlayTimeCapability = targetPlayer.getCapability(CapabilityAutoRankProvider.REAL_PLAY_TIME_CAPABILITY).orElseThrow(IllegalStateException::new);

        realPlayTimeCapability.setRealPlayTime(newTimeInMinutes);

        return 1;
    }
}
