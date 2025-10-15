package com.xiaoyu.continue_eating;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record SyncSettings(Config.Mode mode, List<String> itemList, List<String> uneatableList) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(ContinueEating.MOD_ID, "sync_settings");
    public static final Type<SyncSettings> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, SyncSettings> STREAM_CODEC = StreamCodec.of(
            (buf, pkt) -> {
                buf.writeEnum(pkt.mode);
                buf.writeCollection(pkt.itemList, FriendlyByteBuf::writeUtf);
                buf.writeCollection(pkt.uneatableList, FriendlyByteBuf::writeUtf);
            },
            buf -> new SyncSettings(
                buf.readEnum(Config.Mode.class),
                buf.readCollection(ArrayList::new, FriendlyByteBuf::readUtf),
                buf.readCollection(ArrayList::new, FriendlyByteBuf::readUtf)
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncSettings msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            Config.MODE.set(msg.mode());
            Config.ITEM_LIST.set((List<? extends String>) msg.itemList());
            Config.UNEATABLE_ITEMS.set((List<? extends String>) msg.uneatableList());
        });
    }

    public static SyncSettings fromConfig() {
        return new SyncSettings(
            Config.MODE.get(),
            (List<String>) Config.ITEM_LIST.get(),
            (List<String>) Config.UNEATABLE_ITEMS.get()
        );
    }
}