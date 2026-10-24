package com.starmaylight.arcana_industry.magic.spell;

import com.starmaylight.arcana_industry.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

/** A short-range pulse that harms hostiles and mends nearby non-hostiles. */
public final class HarmonicReversalSpell implements Spell {
    public static final int COST = 115;
    public static final double RADIUS = 5.0;
    public static final float DAMAGE = 3.0F;
    public static final float HEAL = 2.0F;

    @Override public String id() { return "harmonic_reversal"; }

    @Override public int lumenCost() { return COST; }

    @Override public void cast(ServerLevel level, Player caster) {
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class,
                new AABB(caster.blockPosition()).inflate(RADIUS),
                entity -> entity != caster && entity.isAlive()
                    && entity.distanceToSqr(caster) <= RADIUS * RADIUS)) {
            if (target instanceof Enemy) {
                if (target.hurt(level.damageSources().indirectMagic(caster, caster), DAMAGE)) {
                    level.sendParticles(ParticleTypes.ENCHANT,
                        target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(),
                        8, 0.2, 0.3, 0.2, 0.02);
                }
            } else {
                target.heal(HEAL);
                level.sendParticles(ParticleTypes.HEART,
                    target.getX(), target.getY() + target.getBbHeight(), target.getZ(),
                    3, 0.2, 0.15, 0.2, 0.01);
            }
        }
        caster.heal(HEAL);
        level.sendParticles(ParticleTypes.HEART,
            caster.getX(), caster.getY() + caster.getBbHeight(), caster.getZ(),
            3, 0.2, 0.15, 0.2, 0.01);
        level.playSound(null, caster.blockPosition(), ModSounds.SPELL_HARMONIC_REVERSAL.get(),
            SoundSource.PLAYERS, 0.7F, 1.0F);
    }
}
