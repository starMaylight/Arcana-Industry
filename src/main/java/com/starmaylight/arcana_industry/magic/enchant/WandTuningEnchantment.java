package com.starmaylight.arcana_industry.magic.enchant;

import com.starmaylight.arcana_industry.magic.AstralWandItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * A wand tuning: every level buys one spell attribute with another. Tunings exclude each other,
 * so a wand carries one deliberate trade-off rather than stacking them into a free upgrade.
 */
public class WandTuningEnchantment extends Enchantment {
    public static final EnchantmentCategory WAND = EnchantmentCategory.create("ARCANA_INDUSTRY_WAND",
        item -> item instanceof AstralWandItem);
    public static final int MAX_LEVEL = 3;

    public WandTuningEnchantment(Rarity rarity) {
        super(rarity, WAND, new EquipmentSlot[] {EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override public int getMaxLevel() { return MAX_LEVEL; }

    @Override public int getMinCost(int level) { return 5 + (level - 1) * 10; }

    @Override public int getMaxCost(int level) { return getMinCost(level) + 30; }

    @Override protected boolean checkCompatibility(Enchantment other) {
        return !(other instanceof WandTuningEnchantment) && super.checkCompatibility(other);
    }
}
