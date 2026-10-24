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

/** A rising wave that lifts nearby hostile creatures out of melee range. */
public final class SkywardSurgeSpell implements Spell {
    public static final int COST = 80;
    public static final double RADIUS = 4.0;
    public static final float DAMAGE = 3.0F;
    public static final double LIFT_SPEED = 0.75;

    @Override public String id() { return "skyward_surge"; }

    @Override public int lumenCost() { return COST; }

    @Override public void cast(ServerLevel level, Player caster) {
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class,
                new AABB(caster.blockPosition()).inflate(RADIUS),
                entity -> entity instanceof Enemy && entity.isAlive()
                    && entity.distanceToSqr(caster) <= RADIUS * RADIUS)) {
            if (target.hurt(level.damageSources().indirectMagic(caster, caster), DAMAGE)) {
                Vec3 motion = target.getDeltaMovement();
                target.setDeltaMovement(motion.x, Math.max(motion.y, LIFT_SPEED), motion.z);
                target.hasImpulse = true;
            }
        }
        for (int i = 0; i < 20; i++) {
            double angle = i * Math.PI * 2.0 / 20.0;
            level.sendParticles(ParticleTypes.CLOUD,
                caster.getX() + Math.cos(angle) * RADIUS, caster.getY() + 0.25,
                caster.getZ() + Math.sin(angle) * RADIUS, 1, 0, 0.12, 0, 0);
        }
        level.playSound(null, caster.blockPosition(), ModSounds.SPELL_SKYWARD_SURGE.get(),
            SoundSource.PLAYERS, 0.55F, 1.0F);
    }
}
