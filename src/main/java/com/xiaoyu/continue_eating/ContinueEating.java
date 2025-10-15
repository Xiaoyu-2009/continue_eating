package com.xiaoyu.continue_eating;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.Objects;

@Mod(ContinueEating.MOD_ID)
public class ContinueEating {

    public static final String MOD_ID = "continue_eating";
    private final ServerEvents serverEvents;

    public ContinueEating(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        
        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);

        NeoForge.EVENT_BUS.register(this);
        
        serverEvents = new ServerEvents();
        NeoForge.EVENT_BUS.register(serverEvents);
        
        modEventBus.addListener(this::registerPackets);
        
        modEventBus.addListener(this::onConfigReloaded);
    }

    private void registerPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MOD_ID);
        registrar.playToClient(SyncSettings.TYPE, SyncSettings.STREAM_CODEC, SyncSettings::handle);
    }

    private void commonSetup(net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event) {
    }
    
    private void onConfigReloaded(ModConfigEvent.Reloading event) {
        serverEvents.onConfigReloaded(event);
    }

    @SubscribeEvent
    public void rightClickItemEvent(PlayerInteractEvent.RightClickItem event) {
        ItemStack itemstack = event.getItemStack();
        FoodProperties foodProperties = itemstack.get(DataComponents.FOOD);
        if (foodProperties == null) return;

        Player player = event.getEntity();

        if(player.canEat(ContinueEating.canEatItemWhenFull(itemstack, player))) {
            player.startUsingItem(event.getHand());
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.CONSUME);
        } else {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
        }
    }

    public static boolean canEatItemWhenFull(ItemStack item, LivingEntity livingEntity) {
        String registryName = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item.getItem())).toString();

        FoodProperties foodProperties = item.get(DataComponents.FOOD);
        if (foodProperties == null || Config.UNEATABLE_ITEMS.get().contains(registryName)) {
            return false;
        }

        if (Config.MODE.get() == Config.Mode.BLACKLIST) {
            if (!Config.ITEM_LIST.get().contains(registryName)) {
                return true;
            } else {
                return foodProperties.canAlwaysEat();
            }
        } else {
            if (Config.ITEM_LIST.get().contains(registryName)) {
                return true;
            } else {
                return foodProperties.canAlwaysEat();
            }
        }
    }
}