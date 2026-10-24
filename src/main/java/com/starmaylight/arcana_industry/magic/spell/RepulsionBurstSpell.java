package com.starmaylight.arcana_industry.magic.spell;

import com.starmaylight.arcana_industry.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** A close-range defensive burst that drives hostile creatures away from the caster. */
public final class RepulsionBurstSpell implements Spell {
    public static final int COST = 70;
    public static final double RADIUS = 4.0;
    public static final float DAMAGE = 3.0F;
    public static final double PUSH_SPEED = 0.9;

    @Override public String id() { return "repulsion_burst"; }

    @Override public int lumenCost() { return COST; }

    @Override public void cast(ServerLevel level, Player caster) {
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class,
                new AABB(caster.blockPosition()).inflate(RADIUS),
                entity -> entity instanceof Enemy && entity.isAlive()
                    && entity.distanceToSqr(caster) <= RADIUS * RADIUS)) {
            Vec3 away = target.position().subtract(caster.position()).multiply(1, 0, 1);
            if (target.hurt(level.damageSources().indirectMagic(caster, caster), DAMAGE)
                    && away.lengthSqr() > 0.01) {
                target.setDeltaMovement(target.getDeltaMovement().add(away.normalize().scale(PUSH_SPEED)));
                target.hasImpulse = true;
            }
        }
        for (int i = 0; i < 24; i++) {
            double angle = i * Math.PI * 2.0 / 24.0;
            level.sendParticles(ParticleTypes.END_ROD,
                caster.getX() + Math.cos(angle) * RADIUS, caster.getY() + 0.8,
                caster.getZ() + Math.sin(angle) * RADIUS, 1, 0, 0, 0, 0);
        }
        level.playSound(null, caster.blockPosition(), ModSounds.SPELL_REPULSION_BURST.get(),
            SoundSource.PLAYERS, 0.5F, 1.0F);
    }
}
