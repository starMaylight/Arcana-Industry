package com.starmaylight.arcana_industry.magic.spell;

import com.starmaylight.arcana_industry.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** An aimed gravity point that gathers hostile mobs for follow-up attacks. */
public final class GravityWellSpell implements Spell {
    public static final int COST = 85;
    public static final double RANGE = 16.0;
    public static final double RADIUS = 4.0;
    public static final float DAMAGE = 2.0F;
    public static final int SLOW_TICKS = 60;
    public static final double PULL_SPEED = 0.55;

    @Override public String id() { return "gravity_well"; }

    @Override public int lumenCost() { return COST; }

    @Override public void cast(ServerLevel level, Player caster) {
        Vec3 start = caster.getEyePosition();
        Vec3 end = start.add(caster.getViewVector(1.0F).scale(RANGE));
        HitResult hit = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, caster));
        Vec3 center = hit.getType() == HitResult.Type.MISS ? end : hit.getLocation();

        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class,
                new AABB(center, center).inflate(RADIUS),
                entity -> entity instanceof Enemy && entity.isAlive()
                    && entity.distanceToSqr(center) <= RADIUS * RADIUS)) {
            Vec3 toward = new Vec3(center.x - target.getX(), 0, center.z - target.getZ());
            if (toward.lengthSqr() > 0.01) {
                target.setDeltaMovement(target.getDeltaMovement().add(toward.normalize().scale(PULL_SPEED)));
                target.hasImpulse = true;
            }
            if (target.hurt(level.damageSources().indirectMagic(caster, caster), DAMAGE)) {
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, SLOW_TICKS, 1));
            }
        }

        for (int i = 0; i < 24; i++) {
            double angle = i * Math.PI * 2.0 / 24.0;
            level.sendParticles(ParticleTypes.PORTAL,
                center.x + Math.cos(angle) * RADIUS, center.y,
                center.z + Math.sin(angle) * RADIUS, 1, 0, 0, 0, 0);
        }
        level.playSound(null, caster.blockPosition(), ModSounds.SPELL_GRAVITY_WELL.get(),
            SoundSource.PLAYERS, 0.65F, 1.0F);
    }
}
