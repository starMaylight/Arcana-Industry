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

/** Strikes and roots every nearby hostile in place. */
public final class BindingSnareSpell implements Spell {
    public static final int COST = 60;
    public static final float DAMAGE = 2.0F;
    public static final double RADIUS = 5.0;
    public static final int ROOT_TICKS = 60;
    public static final int ROOT_AMPLIFIER = 9;

    @Override public String id() { return "binding_snare"; }

    @Override public int lumenCost() { return COST; }

    @Override public void cast(ServerLevel level, Player caster) {
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class,
                new AABB(caster.blockPosition()).inflate(RADIUS),
                entity -> entity instanceof Enemy && entity.isAlive()
                    && entity.distanceToSqr(caster) <= RADIUS * RADIUS)) {
            target.hurt(level.damageSources().indirectMagic(caster, caster), DAMAGE);
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, ROOT_TICKS, ROOT_AMPLIFIER));
            target.setDeltaMovement(0.0, target.getDeltaMovement().y, 0.0);
            target.hasImpulse = true;
            level.sendParticles(ParticleTypes.ITEM_SNOWBALL,
                target.getX(), target.getY() + 0.1, target.getZ(), 10, 0.3, 0.05, 0.3, 0.01);
        }
        level.playSound(null, caster.blockPosition(), ModSounds.SPELL_BINDING_SNARE.get(),
            SoundSource.PLAYERS, 0.6F, 1.0F);
    }
}
