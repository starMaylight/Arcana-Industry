package com.starmaylight.arcana_industry.magic;

import com.starmaylight.arcana_industry.magic.trait.TraitAssay;
import com.starmaylight.arcana_industry.magic.trait.TraitImprint;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import java.util.List;

/** Sanctum Pylon product; its tooltip names the trait imprinted from the attuned offering. */
public class AttunedStarshardItem extends Item {
    public AttunedStarshardItem(Properties properties) { super(properties); }

    @Override public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        TraitImprint.read(stack).ifPresentOrElse(
            imprint -> tooltip.add(Component.translatable("tooltip.arcana_industry.attuned_starshard.imprint",
                TraitAssay.name(imprint.trait()), imprint.strength()).withStyle(ChatFormatting.LIGHT_PURPLE)),
            () -> tooltip.add(Component.translatable("tooltip.arcana_industry.attuned_starshard.blank").withStyle(ChatFormatting.GRAY)));
    }
}
