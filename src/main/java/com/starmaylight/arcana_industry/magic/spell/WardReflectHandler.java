package com.starmaylight.arcana_industry.magic.spell;

import com.starmaylight.arcana_industry.Arcana_industry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Bounces damage taken by an Aegis Ward holder back onto whoever struck them. */
@Mod.EventBusSubscriber(modid = Arcana_industry.MODID)
public final class WardReflectHandler {
    // Guards against a reflect hit on another warded attacker bouncing back and forth forever.
    private static boolean reflecting = false;

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
        if (reflecting) return;
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(victim.level() instanceof ServerLevel level) || !AegisWardSpell.hasWard(level, victim)) return;
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker) || attacker == victim || !attacker.isAlive()) return;

        reflecting = true;
        try {
            attacker.hurt(level.damageSources().indirectMagic(victim, victim), AegisWardSpell.REFLECT_DAMAGE);
        } finally {
            reflecting = false;
        }
        level.sendParticles(ParticleTypes.CRIT,
            attacker.getX(), attacker.getY() + attacker.getBbHeight() * 0.5, attacker.getZ(),
            10, 0.2, 0.3, 0.2, 0.02);
        SpellImpactCues.play(level, attacker, "aegis_ward");
    }

    private WardReflectHandler() {}
}
