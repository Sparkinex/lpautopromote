package com.sparkinex.lpautopromote.core;

import com.sparkinex.lpautopromote.LPAutoPromote;
import com.sparkinex.lpautopromote.capability.CapabilityAutoRankProvider;
import com.sparkinex.lpautopromote.capability.IAutoRank;
import com.sparkinex.lpautopromote.capability.AutoRank;
import com.sparkinex.lpautopromote.commands.PlaytimeCommands;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

@Mod.EventBusSubscriber(modid = LPAutoPromote.MODID)
public class ModEvents {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level.isClientSide()) {
            return;
        }

        ServerPlayer player = (ServerPlayer) event.player;
        if (player == null) {
            return;
        }

        PlayerData pData = LPAutoPromote.getOrCreatePlayerData(player);
        if (pData == null) return;

        LazyOptional<IAutoRank> realPlayTimeCap = player.getCapability(CapabilityAutoRankProvider.REAL_PLAY_TIME_CAPABILITY);

        realPlayTimeCap.ifPresent(cap -> {
            cap.tick(player, pData);
        });

        pData.update(player.getX(), player.getY(), player.getZ(), player.getLookAngle());
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            Player player = (Player) event.getObject();
            LazyOptional<IAutoRank> cap = event.getObject().getCapability(CapabilityAutoRankProvider.REAL_PLAY_TIME_CAPABILITY);
            if (!cap.isPresent()) {
                event.addCapability(new ResourceLocation(LPAutoPromote.MODID, "properties"), new CapabilityAutoRankProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        event.getOriginal().reviveCaps();

        // Transfer the capability data from the old player to the new player
        event.getOriginal().getCapability(CapabilityAutoRankProvider.REAL_PLAY_TIME_CAPABILITY).ifPresent(oldCap -> {
            event.getEntity().getCapability(CapabilityAutoRankProvider.REAL_PLAY_TIME_CAPABILITY).ifPresent(newCap -> {
                newCap.deserializeNBT(oldCap.serializeNBT());
            });
        });

        event.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(AutoRank.class);

    }

    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        new PlaytimeCommands(event.getDispatcher());

        ConfigCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            LPAutoPromote.getOrCreatePlayerData(player);
        }
    }
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            LPAutoPromote.removePlayerData(player.getUUID());
        }
    }
}