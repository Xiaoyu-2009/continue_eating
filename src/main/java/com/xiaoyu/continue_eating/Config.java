package com.xiaoyu.continue_eating;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Config {

    public static final String CATEGORY_EATABLE = "eatable";

    public static ModConfigSpec SERVER_CONFIG;

    public static ModConfigSpec.ConfigValue<List<? extends String>> ITEM_LIST;
    public static ModConfigSpec.ConfigValue<List<? extends String>> UNEATABLE_ITEMS;
    public static ModConfigSpec.EnumValue<Mode> MODE;

    public enum Mode {
        BLACKLIST,
        WHITELIST
    }

    static {
        ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();

        SERVER_BUILDER.comment("Eatable settings").push(CATEGORY_EATABLE);

        ITEM_LIST = SERVER_BUILDER
                .comment(
                        "List of items",
                        "Depending on the mode only these items will be made eatable (WHITELIST) or these items will keep their vanilla behaviour (BLACKLIST)",
                        "If an item is not affected according to the rules above they will keep their vanilla behaviour"
                )
                .defineListAllowEmpty(List.of("item_list"), (Supplier<List<? extends String>>) ArrayList::new, (Predicate<Object>) Config::isValidResourceLocation);
        UNEATABLE_ITEMS = SERVER_BUILDER
                .comment(
                        "List of items",
                        "These items will be made uneatable while full (Overrides vanilla behaviour)"
                )
                .defineListAllowEmpty(List.of("uneatable_list"), (Supplier<List<? extends String>>) ArrayList::new, (Predicate<Object>) Config::isValidResourceLocation);
        MODE = SERVER_BUILDER
                .comment("Mode as explained in other settings")
                .defineEnum("mode", Mode.BLACKLIST);

        SERVER_BUILDER.pop();

        SERVER_CONFIG = SERVER_BUILDER.build();
    }

    static boolean isValidResourceLocation(Object o) {
        if(o instanceof ResourceLocation) return true;

        if(o instanceof String resourceName) {
            return ResourceLocation.tryParse(resourceName) != null;
        }

        return false;
    }
}