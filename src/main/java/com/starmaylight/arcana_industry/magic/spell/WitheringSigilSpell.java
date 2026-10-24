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

import java.util.Comparator;

/** Curses the nearest hostile creature, trading immediate damage for a lasting wound. */
public final class WitheringSigilSpell implements Spell {
    public static final int COST = 95;
    public static final double RANGE = 8.0;
    public static final float DAMAGE = 2.0F;
    public static final int WITHER_TICKS = 100;

    @Override public String id() { return "withering_sigil"; }

    @Override public int lumenCost() { return COST; }

    @Override public void cast(ServerLevel level, Player caster) {
        level.getEntitiesOfClass(LivingEntity.class,
                new AABB(caster.blockPosition()).inflate(RANGE),
                entity -> entity instanceof Enemy && entity.isAlive()
                    && entity.distanceToSqr(caster) <= RANGE * RANGE)
            .stream().min(Comparator.comparingDouble(caster::distanceToSqr)).ifPresent(target -> {
                if (target.hurt(level.damageSources().indirectMagic(caster, caster), DAMAGE)) {
                    target.addEffect(new MobEffectInstance(MobEffects.WITHER, WITHER_TICKS, 0));
                }
                level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                    target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(),
                    18, 0.3, 0.5, 0.3, 0.02);
            });
        level.playSound(null, caster.blockPosition(), ModSounds.SPELL_WITHERING_SIGIL.get(),
            SoundSource.PLAYERS, 0.4F, 1.0F);
    }
}
