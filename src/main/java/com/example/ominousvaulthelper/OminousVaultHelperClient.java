package com.example.ominousvaulthelper;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

public class OminousVaultHelperClient implements ClientModInitializer {
    private static final MinecraftClient MC = MinecraftClient.getInstance();
    private static final Set<String> watchedItems = new HashSet<>();
    private static boolean watching = false;

    private static KeyBinding toggleKey;

    @Override
    public void onInitializeClient() {
        // Load config
        watchedItems.addAll(OminousVaultHelperConfig.loadConfig());

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.ominousvaulthelper.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                "category.ominousvaulthelper"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                watching = !watching;
                client.player.sendMessage(Text.literal("Vault watching: " + (watching ? "ON" : "OFF")), true);
            }
        });

        // Packet interception
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver(
                BlockEntityUpdateS2CPacket.PACKET_ID,
                (client, handler, buf, responseSender) -> {
                    BlockEntityUpdateS2CPacket packet = new BlockEntityUpdateS2CPacket(buf);
                    client.execute(() -> handleVaultPacket(packet));
                }
        );
    }

    private void handleVaultPacket(BlockEntityUpdateS2CPacket packet) {
        if (!watching || MC.player == null || packet.getNbt() == null) return;

        // Check block type
        String blockId = MC.world.getBlockState(packet.getPos()).getBlock().toString();
        if (!blockId.contains("ominous_vault")) return;

        // Extract preview item
        if (packet.getNbt().contains("PreviewItem")) {
            String id = packet.getNbt().getCompound("PreviewItem").getString("id");
            if (watchedItems.contains(id) && PlayerUtils.hasTrialKey(MC.player) && PlayerUtils.inUseRange(MC.player, packet.getPos())) {
                MC.interactionManager.interactBlock(MC.player, MC.world, net.minecraft.util.Hand.MAIN_HAND,
                        new net.minecraft.util.hit.BlockHitResult(MC.player.getPos(), MC.player.getHorizontalFacing(), packet.getPos(), false));
            }
        }
    }
}