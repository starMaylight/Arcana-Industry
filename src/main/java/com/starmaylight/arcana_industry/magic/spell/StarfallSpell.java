package com.starmaylight.arcana_industry.magic.spell;

import com.starmaylight.arcana_industry.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** A ranged impact spell: damage lands at the aimed point rather than along a line, unlike Lumen Bolt/Astral Lance. */
public final class StarfallSpell implements Spell {
    public static final int COST = 110;
    public static final double RANGE = 20.0;
    public static final double IMPACT_RADIUS = 3.0;
    public static final float DAMAGE = 7.0F;
    public static final int FIRE_SECONDS = 3;

    @Override public String id() { return "starfall"; }

    @Override public int lumenCost() { return COST; }

    @Override public void cast(ServerLevel level, Player caster) {
        Vec3 start = caster.getEyePosition();
        Vec3 look = caster.getViewVector(1.0F);
        Vec3 end = start.add(look.scale(RANGE));
        HitResult blockHit = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, caster));
        Vec3 impact = blockHit.getType() == HitResult.Type.MISS ? end : blockHit.getLocation();

        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class,
                new AABB(impact.x, impact.y, impact.z, impact.x, impact.y, impact.z).inflate(IMPACT_RADIUS),
                entity -> entity.isAlive() && entity != caster
                    && entity.distanceToSqr(impact.x, impact.y, impact.z) <= IMPACT_RADIUS * IMPACT_RADIUS)) {
            target.hurt(level.damageSources().indirectMagic(caster, caster), DAMAGE);
            target.setSecondsOnFire(FIRE_SECONDS);
        }

        level.sendParticles(ParticleTypes.FLASH, impact.x, impact.y, impact.z, 1, 0.0, 0.0, 0.0, 0.0);
        for (int i = 0; i < 30; i++) {
            double angle = i * Math.PI * 2.0 / 30.0;
            level.sendParticles(ParticleTypes.FLAME,
                impact.x + Math.cos(angle) * IMPACT_RADIUS, impact.y + 0.1,
                impact.z + Math.sin(angle) * IMPACT_RADIUS, 1, 0, 0, 0, 0);
        }
        level.playSound(null, caster.blockPosition(), ModSounds.SPELL_STARFALL.get(),
            SoundSource.PLAYERS, 0.5F, 1.0F);
    }
}
