package com.starmaylight.arcana_industry.magic;

import com.starmaylight.arcana_industry.Arcana_industry;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * Endgame FE shield. Carried anywhere in the inventory, it pays for post-armor damage with stored FE
 * before health is lost. Void and /kill damage (bypasses invulnerability) is never absorbed.
 */
@Mod.EventBusSubscriber(modid = Arcana_industry.MODID)
public final class FluxAegisItem extends FluxStoreItem {
    public static final int CAPACITY = 400_000;
    public static final int COST_PER_HEALTH = 2_000;

    public FluxAegisItem() { super(new Properties().stacksTo(1).rarity(Rarity.EPIC), CAPACITY, 16_000, 0xB98CF2); }

    /** Absorbs as much of {@code amount} as the stack's charge pays for and returns the damage left over. */
    public float absorb(ItemStack stack, float amount) {
        float affordable = (float) charge(stack) / COST_PER_HEALTH;
        float absorbed = Math.min(amount, affordable);
        if (absorbed <= 0) return amount;
        setCharge(stack, charge(stack) - Mth.ceil(absorbed * COST_PER_HEALTH));
        return amount - absorbed;
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide) return;
        if (event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY) || event.getAmount() <= 0) return;
        var inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (!(stack.getItem() instanceof FluxAegisItem aegis) || aegis.charge(stack) <= 0) continue;
            float left = aegis.absorb(stack, event.getAmount());
            event.setAmount(left);
            if (left <= 0) event.setCanceled(true);
            return;
        }
    }

    @Override public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("message.arcana_industry.flux_aegis", charge(stack), CAPACITY, COST_PER_HEALTH));
    }
}
