package com.starmaylight.arcana_industry.magic.spell;

import com.starmaylight.arcana_industry.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

/** A defensive wave that weakens multiple hostile creatures around the caster. */
public final class ResonancePulseSpell implements Spell {
    public static final int COST = 65;
    public static final double RADIUS = 4.0;
    public static final float DAMAGE = 3.0F;
    public static final int SLOW_TICKS = 80;

    @Override public String id() { return "resonance_pulse"; }

    @Override public int lumenCost() { return COST; }

    @Override public void cast(ServerLevel level, Player caster) {
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class,
                new AABB(caster.blockPosition()).inflate(RADIUS),
                entity -> entity instanceof Enemy && entity.isAlive()
                    && entity.distanceToSqr(caster) <= RADIUS * RADIUS)) {
            if (target.hurt(level.damageSources().indirectMagic(caster, caster), DAMAGE)) {
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, SLOW_TICKS, 1));
            }
        }
        for (int i = 0; i < 24; i++) {
            double angle = i * Math.PI * 2.0 / 24.0;
            level.sendParticles(ParticleTypes.ENCHANT,
                caster.getX() + Math.cos(angle) * RADIUS, caster.getY() + 0.2,
                caster.getZ() + Math.sin(angle) * RADIUS, 1, 0, 0, 0, 0);
        }
        level.playSound(null, caster.blockPosition(), ModSounds.SPELL_RESONANCE_PULSE.get(),
            SoundSource.PLAYERS, 0.7F, 1.0F);
    }
}
