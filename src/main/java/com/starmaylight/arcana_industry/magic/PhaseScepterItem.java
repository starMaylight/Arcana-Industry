package com.starmaylight.arcana_industry.magic;

import com.starmaylight.arcana_industry.magic.spell.Spell;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import java.util.List;

/**
 * Tier 9 capstone: an Astral Wand rebuilt around a Phase Heart. It casts the same spells but
 * reaches fonts twice as far away, draws three quarters of each spell's lumen (rounded up) and
 * recovers in half the time.
 */
public class PhaseScepterItem extends AstralWandItem {
    public static final int SEARCH_RADIUS = 12;
    public static final int COOLDOWN = 5;

    public PhaseScepterItem() { super(); }

    @Override protected int searchRadius() { return SEARCH_RADIUS; }

    @Override public int castCost(Spell spell) { return (spell.lumenCost() * 3 + 3) / 4; }

    @Override protected int cooldownTicks() { return COOLDOWN; }

    @Override public Rarity getRarity(ItemStack stack) { return Rarity.EPIC; }

    @Override public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("message.arcana_industry.phase_scepter_tooltip", SEARCH_RADIUS));
    }
}
