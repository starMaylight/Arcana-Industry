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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** A forward-facing fan of shadow that blinds hostile mobs without striking behind the caster. */
public final class DuskFanSpell implements Spell {
    public static final int COST = 75;
    public static final double RANGE = 6.0;
    public static final float DAMAGE = 4.0F;
    public static final int BLIND_TICKS = 80;
    private static final double MIN_DOT = 0.7;

    @Override public String id() { return "dusk_fan"; }

    @Override public int lumenCost() { return COST; }

    @Override public void cast(ServerLevel level, Player caster) {
        Vec3 facing = caster.getLookAngle().multiply(1, 0, 1).normalize();
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class,
                new AABB(caster.blockPosition()).inflate(RANGE), e -> e instanceof Enemy && e.isAlive())) {
            Vec3 offset = target.position().subtract(caster.position()).multiply(1, 0, 1);
            if (offset.lengthSqr() > RANGE * RANGE || offset.lengthSqr() < 0.001
                    || facing.dot(offset.normalize()) < MIN_DOT) continue;
            if (target.hurt(level.damageSources().indirectMagic(caster, caster), DAMAGE)) {
                target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, BLIND_TICKS, 0));
            }
        }
        for (int i = -4; i <= 4; i++) {
            Vec3 point = caster.position().add(facing.yRot((float) (i * Math.PI / 20.0)).scale(3.0));
            level.sendParticles(ParticleTypes.WITCH, point.x, point.y + 1.0, point.z, 3, 0.2, 0.2, 0.2, 0);
        }
        level.playSound(null, caster.blockPosition(), ModSounds.SPELL_DUSK_FAN.get(),
            SoundSource.PLAYERS, 0.7F, 1.0F);
    }
}
