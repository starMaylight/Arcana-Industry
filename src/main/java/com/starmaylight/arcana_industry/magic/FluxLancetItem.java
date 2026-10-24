package com.starmaylight.arcana_industry.magic;

import com.starmaylight.arcana_industry.Arcana_industry;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/** A charged melee instrument. Its post-mitigation pulse penetrates armor, but respects damage immunity. */
@Mod.EventBusSubscriber(modid = Arcana_industry.MODID)
public final class FluxLancetItem extends FluxStoreItem {
    public static final int CAPACITY = 300_000_000;
    public static final int HIT_COST = 100;
    public static final float PULSE_DAMAGE = 128.0F;

    public FluxLancetItem() { super(new Properties().stacksTo(1).rarity(Rarity.EPIC), CAPACITY, 12_000, 0xE8A667); }

    /** Applied after vanilla armor and other reductions, only for a positive direct melee hit. */
    public boolean pulse(ItemStack stack, LivingDamageEvent event) {
        if (charge(stack) < HIT_COST || event.getAmount() <= 0) return false;
        setCharge(stack, charge(stack) - HIT_COST);
        event.setAmount(event.getAmount() + PULSE_DAMAGE);
        return true;
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player) || player.level().isClientSide
            || event.getSource().getDirectEntity() != player || event.getEntity() == player
            || event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof FluxLancetItem lancet) lancet.pulse(stack, event);
    }

    @Override public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("message.arcana_industry.flux_lancet", charge(stack), CAPACITY, HIT_COST, (int) PULSE_DAMAGE));
    }
}
