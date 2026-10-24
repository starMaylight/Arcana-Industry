package com.starmaylight.arcana_industry.magic.spell;

import com.starmaylight.arcana_industry.sound.ModSounds;
import java.util.Comparator;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

/** Empowers the caster and opens a close fight with a strike on the nearest hostile. */
public final class ArcaneFurySpell implements Spell {
    public static final int COST = 100;
    public static final int DURATION_TICKS = 200;
    public static final int STRENGTH_AMPLIFIER = 1;
    public static final int SPEED_AMPLIFIER = 1;
    public static final double STRIKE_RANGE = 4.0;
    public static final float STRIKE_DAMAGE = 4.0F;

    @Override public String id() { return "arcane_fury"; }

    @Override public int lumenCost() { return COST; }

    @Override public void cast(ServerLevel level, Player caster) {
        caster.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, DURATION_TICKS, STRENGTH_AMPLIFIER));
        caster.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, DURATION_TICKS, SPEED_AMPLIFIER));
        level.getEntitiesOfClass(LivingEntity.class,
                new AABB(caster.blockPosition()).inflate(STRIKE_RANGE),
                entity -> entity instanceof Enemy && entity.isAlive()
                    && entity.distanceToSqr(caster) <= STRIKE_RANGE * STRIKE_RANGE)
            .stream().min(Comparator.comparingDouble(caster::distanceToSqr)).ifPresent(target -> {
                if (target.hurt(level.damageSources().indirectMagic(caster, caster), STRIKE_DAMAGE)) {
                    level.sendParticles(ParticleTypes.FLAME, target.getX(),
                        target.getY() + target.getBbHeight() * 0.5, target.getZ(),
                        12, 0.25, 0.3, 0.25, 0.02);
                }
            });
        for (int i = 0; i < 16; i++) {
            double angle = i * Math.PI * 2.0 / 16.0;
            level.sendParticles(ParticleTypes.FLAME,
                caster.getX() + Math.cos(angle) * 0.6, caster.getY() + 0.1,
                caster.getZ() + Math.sin(angle) * 0.6, 1, 0, 0.1, 0, 0.01);
        }
        level.playSound(null, caster.blockPosition(), ModSounds.SPELL_ARCANE_FURY.get(),
            SoundSource.PLAYERS, 0.6F, 1.0F);
    }
}
