package com.example.ominousvaulthelper;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

public class PlayerUtils {
    public static boolean hasTrialKey(PlayerEntity player) {
        for (ItemStack stack : player.getInventory().main) {
            if (stack.isOf(Items.TRIAL_KEY)) return true;
        }
        return player.getOffHandStack().isOf(Items.TRIAL_KEY);
    }

    public static boolean inUseRange(PlayerEntity player, BlockPos pos) {
        double distSq = player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        return distSq <= 36.0; // 6 block reach
    }
}