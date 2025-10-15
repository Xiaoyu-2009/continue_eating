package com.xiaoyu.continue_eating;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class ServerEvents {

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent playerLoggedInEvent) {
        PacketDistributor.sendToAllPlayers(SyncSettings.fromConfig());
    }

    public void onConfigReloaded(ModConfigEvent.Reloading event) {
        if(event.getConfig().getSpec() == Config.SERVER_CONFIG) {
            PacketDistributor.sendToAllPlayers(SyncSettings.fromConfig());
        }
    }
}