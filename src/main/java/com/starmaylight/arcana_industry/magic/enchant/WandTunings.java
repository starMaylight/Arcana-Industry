package com.starmaylight.arcana_industry.magic.enchant;

import com.starmaylight.arcana_industry.Arcana_industry;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registers the three wand tunings and turns their levels into cast modifiers.
 * Overchannel: +50% lumen and +25% spell damage per level.
 * Quickcast: -25% cooldown and -15% spell damage per level.
 * Thrift: -20% lumen and +50% cooldown per level.
 */
public final class WandTunings {
    public static final double OVERCHANNEL_COST = 0.5, OVERCHANNEL_POWER = 0.25;
    public static final double QUICKCAST_COOLDOWN = 0.25, QUICKCAST_POWER = 0.15;
    public static final double THRIFT_COST = 0.2, THRIFT_COOLDOWN = 0.5;

    private static final DeferredRegister<Enchantment> ENCHANTMENTS =
        DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Arcana_industry.MODID);

    public static final RegistryObject<Enchantment> OVERCHANNEL = ENCHANTMENTS.register("overchannel",
        () -> new WandTuningEnchantment(Enchantment.Rarity.UNCOMMON));
    public static final RegistryObject<Enchantment> QUICKCAST = ENCHANTMENTS.register("quickcast",
        () -> new WandTuningEnchantment(Enchantment.Rarity.UNCOMMON));
    public static final RegistryObject<Enchantment> THRIFT = ENCHANTMENTS.register("thrift",
        () -> new WandTuningEnchantment(Enchantment.Rarity.COMMON));

    public static void register(IEventBus bus) { ENCHANTMENTS.register(bus); }

    private static int level(ItemStack wand, RegistryObject<Enchantment> tuning) {
        return wand.getEnchantmentLevel(tuning.get());
    }

    /** Lumen for one cast after tuning; never below one. */
    public static int tunedCost(ItemStack wand, int baseCost) {
        double scale = (1 + OVERCHANNEL_COST * level(wand, OVERCHANNEL)) * (1 - THRIFT_COST * level(wand, THRIFT));
        return Math.max(1, (int) Math.round(baseCost * scale));
    }

    /** Cooldown ticks after tuning; never below one. */
    public static int tunedCooldown(ItemStack wand, int baseTicks) {
        double scale = (1 - QUICKCAST_COOLDOWN * level(wand, QUICKCAST)) * (1 + THRIFT_COOLDOWN * level(wand, THRIFT));
        return Math.max(1, (int) Math.round(baseTicks * scale));
    }

    /** Multiplier applied to every hit the spell deals during its cast. */
    public static float power(ItemStack wand) {
        return (float) (1 + OVERCHANNEL_POWER * level(wand, OVERCHANNEL) - QUICKCAST_POWER * level(wand, QUICKCAST));
    }

    /** Adds one effect line per tuning on the wand; vanilla only shows the enchantment name. */
    public static void describe(ItemStack wand, List<Component> tooltip) {
        if (level(wand, OVERCHANNEL) > 0) tooltip.add(Component.translatable(
            "enchantment.arcana_industry.overchannel.desc").withStyle(ChatFormatting.GRAY));
        if (level(wand, QUICKCAST) > 0) tooltip.add(Component.translatable(
            "enchantment.arcana_industry.quickcast.desc").withStyle(ChatFormatting.GRAY));
        if (level(wand, THRIFT) > 0) tooltip.add(Component.translatable(
            "enchantment.arcana_industry.thrift.desc").withStyle(ChatFormatting.GRAY));
    }

    private WandTunings() {}
}
