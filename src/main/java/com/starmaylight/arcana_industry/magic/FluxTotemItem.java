package com.starmaylight.arcana_industry.magic;

import com.starmaylight.arcana_industry.Arcana_industry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/** Reusable endgame survival device. Its stored FE is stack NBT, so charge survives save/load. */
@Mod.EventBusSubscriber(modid = Arcana_industry.MODID)
public final class FluxTotemItem extends FluxStoreItem {
    public static final int CAPACITY = 200_000;
    public static final int RESCUE_COST = 100_000;

    public FluxTotemItem() { super(new Properties().stacksTo(1).rarity(Rarity.EPIC), CAPACITY, 8_000, 0x65D5F0); }

    public static int stored(ItemStack stack) {
        return stack.getItem() instanceof FluxTotemItem totem ? totem.charge(stack) : 0;
    }

    public static void setStored(ItemStack stack, int amount) {
        if (stack.getItem() instanceof FluxTotemItem totem) totem.setCharge(stack, amount);
    }

    public static boolean rescue(Player player, ItemStack stack) {
        if (stored(stack) < RESCUE_COST) return false;
        setStored(stack, stored(stack) - RESCUE_COST);
        player.setHealth(1.0F);
        player.removeAllEffects();
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800));
        return true;
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide) return;
        for (ItemStack stack : player.getHandSlots()) {
            if (rescue(player, stack)) {
                event.setCanceled(true);
                player.level().levelEvent(2005, player.blockPosition(), 0);
                return;
            }
        }
    }

    @Override public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("message.arcana_industry.flux_totem", stored(stack), CAPACITY, RESCUE_COST));
    }
}
