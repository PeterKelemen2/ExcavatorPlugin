package dev.peti.excavator.mining;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.Random;

public class DurabilityManager {
    private static final Random RANDOM = new Random();

    public static int simulateUnbreaking(ItemStack tool, int validBlocks) {
        int unbreaking = tool.getEnchantmentLevel(Enchantment.UNBREAKING);
        if (unbreaking <= 0) return validBlocks;
        int cost = 0;
        for (int i = 0; i < validBlocks; i++) {
            if (RANDOM.nextInt(unbreaking + 1) == 0) cost++;
        }
        return cost;
    }

    public static boolean hasEnoughDurability(ItemStack tool, int cost) {
        Damageable meta = (Damageable) tool.getItemMeta();
        int current = meta.getDamage();
        int max = tool.getType().getMaxDurability();
        return max - current > cost;
    }

    public static void applyDamage(ItemStack tool, int cost) {
        Damageable meta = (Damageable) tool.getItemMeta();
        meta.setDamage(meta.getDamage() + cost);
        tool.setItemMeta(meta);
    }
}
