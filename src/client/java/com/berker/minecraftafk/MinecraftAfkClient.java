package com.berker.minecraftafk;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class MinecraftAfkClient implements ClientModInitializer {
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath("minecraftafk", "main")
    );

    private static final KeyMapping START_KEY = KeyMappingHelper.registerKeyMapping(
            new KeyMapping(
                    "key.minecraftafk.start",
                    InputConstants.Type.KEYSYM,
                    InputConstants.KEY_F6,
                    CATEGORY
            )
    );

    private static final KeyMapping STOP_KEY = KeyMappingHelper.registerKeyMapping(
            new KeyMapping(
                    "key.minecraftafk.stop",
                    InputConstants.Type.KEYSYM,
                    InputConstants.KEY_F7,
                    CATEGORY
            )
    );

    private boolean enabled = false;
    private int ticks = 0;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (START_KEY.consumeClick()) {
                enabled = true;
                ticks = 0;
                if (client.player != null) {
                    client.player.sendSystemMessage(Component.literal("§a[MinecraftAFK] Açık — F7 ile kapat."));
                }
            }

            while (STOP_KEY.consumeClick()) {
                enabled = false;
                releaseMovement(client.options.keyLeft, client.options.keyRight);
                if (client.player != null) {
                    client.player.sendSystemMessage(Component.literal("§c[MinecraftAFK] Kapalı."));
                }
            }

            if (!enabled || client.player == null || client.level == null) {
                if (!enabled) {
                    releaseMovement(client.options.keyLeft, client.options.keyRight);
                }
                return;
            }

            ticks++;

            // Her 2 saniyede bir A/D yönünü değiştirir.
            boolean moveLeft = ((ticks / 40) % 2) == 0;
            client.options.keyLeft.setDown(moveLeft);
            client.options.keyRight.setDown(!moveLeft);
        });
    }

    private static void releaseMovement(KeyMapping left, KeyMapping right) {
        left.setDown(false);
        right.setDown(false);
    }
}
