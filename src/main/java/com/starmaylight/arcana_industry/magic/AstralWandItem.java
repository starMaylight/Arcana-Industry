package com.starmaylight.arcana_industry.magic;

import com.starmaylight.arcana_industry.magic.enchant.WandTunings;
import com.starmaylight.arcana_industry.magic.spell.Spell;
import com.starmaylight.arcana_industry.magic.spell.SpellRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import java.util.List;

/** Casts a customizable Lumen-powered attack, drawing charge from Astral Fonts or carried Lumen Vessels. */
public class AstralWandItem extends Item {
    public static final int SEARCH_RADIUS = 6;

    public AstralWandItem() { super(new Item.Properties().stacksTo(1)); }

    /** Block radius searched for an Astral Font holding enough lumen. */
    protected int searchRadius() { return SEARCH_RADIUS; }

    /** Lumen actually drawn for one cast of {@code spell}. */
    public int castCost(Spell spell) { return spell.lumenCost(); }

    /** Lumen drawn for one cast from this wand stack, after its tuning enchantment. */
    public int castCost(ItemStack stack, Spell spell) { return WandTunings.tunedCost(stack, castCost(spell)); }

    /** Ticks before the item can cast again. */
    protected int cooldownTicks() { return 10; }

    /** Cooldown of this wand stack after its tuning enchantment. */
    public int cooldownTicks(ItemStack stack) { return WandTunings.tunedCooldown(stack, cooldownTicks()); }

    @Override public boolean isEnchantable(ItemStack stack) { return true; }

    @Override public int getEnchantmentValue() { return 15; }

    private static String currentSpellId(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains("Spell") ? tag.getString("Spell") : SpellRegistry.first().id();
    }

    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                String nextId = SpellRegistry.next(currentSpellId(stack));
                stack.getOrCreateTag().putString("Spell", nextId);
                player.displayClientMessage(Component.translatable("message.arcana_industry.astral_wand_mode",
                    Component.translatable("spell.arcana_industry." + nextId)), true);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        if (level.isClientSide) return InteractionResultHolder.consume(stack);
        if (player.getCooldowns().isOnCooldown(this)) return InteractionResultHolder.fail(stack);
        Spell spell = SpellRegistry.byId(currentSpellId(stack));
        int cost = castCost(stack, spell);
        AstralFontBlockEntity font = findFont(level, player, cost);
        // Fonts in reach are preferred; a carried Lumen Vessel pays only when none can.
        ItemStack vessel = font == null ? LumenVesselItem.findCharged(player, cost) : ItemStack.EMPTY;
        if (font == null && vessel.isEmpty()) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.astral_wand_empty", cost), true);
            return InteractionResultHolder.fail(stack);
        }
        if (font != null) font.spend(cost);
        else LumenVesselItem.spend(vessel, cost);
        com.starmaylight.arcana_industry.magic.spell.SpellImpactCues.cast(spell, (ServerLevel) level, player, WandTunings.power(stack));
        player.getCooldowns().addCooldown(this, cooldownTicks(stack));
        return InteractionResultHolder.consume(stack);
    }

    private AstralFontBlockEntity findFont(Level level, Player player, int cost) {
        BlockPos origin = player.blockPosition();
        int radius = searchRadius();
        AstralFontBlockEntity best = null;
        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-radius, -radius, -radius),
                origin.offset(radius, radius, radius))) {
            if (level.getBlockEntity(pos) instanceof AstralFontBlockEntity font && font.lumen() >= cost
                && (best == null || font.lumen() > best.lumen())) best = font;
        }
        return best;
    }

    @Override public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        Spell spell = SpellRegistry.byId(currentSpellId(stack));
        tooltip.add(Component.translatable("message.arcana_industry.astral_wand_tooltip",
            Component.translatable("spell.arcana_industry." + spell.id()), castCost(stack, spell)));
        float power = WandTunings.power(stack);
        if (power != 1.0F || cooldownTicks(stack) != cooldownTicks()) tooltip.add(Component.translatable(
            "message.arcana_industry.astral_wand_tuning", Math.round(power * 100), cooldownTicks(stack)));
        WandTunings.describe(stack, tooltip);
    }
}
