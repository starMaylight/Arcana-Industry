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

/** Wards the caster and flares outward, striking hostiles already pressed against the ward. */
public final class AegisWardSpell implements Spell {
    public static final int COST = 120;
    public static final int DURATION_TICKS = 200;
    public static final int ABSORPTION_AMPLIFIER = 1;
    public static final float REFLECT_DAMAGE = 3.0F;
    public static final float FLARE_DAMAGE = 2.0F;
    public static final double FLARE_RADIUS = 3.0;
    private static final String WARD_TAG = "arcana_industry_aegis_ward_until";

    @Override public String id() { return "aegis_ward"; }

    @Override public int lumenCost() { return COST; }

    @Override public void cast(ServerLevel level, Player caster) {
        caster.getPersistentData().putLong(WARD_TAG, level.getGameTime() + DURATION_TICKS);
        caster.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, DURATION_TICKS, ABSORPTION_AMPLIFIER, false, true));
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class,
                new AABB(caster.blockPosition()).inflate(FLARE_RADIUS),
                entity -> entity instanceof Enemy && entity.isAlive()
                    && entity.distanceToSqr(caster) <= FLARE_RADIUS * FLARE_RADIUS)) {
            target.hurt(level.damageSources().indirectMagic(caster, caster), FLARE_DAMAGE);
            level.sendParticles(ParticleTypes.FLASH, target.getX(), target.getY() + target.getBbHeight() * 0.5,
                target.getZ(), 1, 0, 0, 0, 0);
        }
        for (int i = 0; i < 24; i++) {
            double angle = i * Math.PI * 2.0 / 24.0;
            level.sendParticles(ParticleTypes.END_ROD,
                caster.getX() + Math.cos(angle) * 1.2, caster.getY() + 1.0,
                caster.getZ() + Math.sin(angle) * 1.2, 1, 0, 0, 0, 0);
        }
        level.playSound(null, caster.blockPosition(), ModSounds.SPELL_AEGIS_WARD.get(),
            SoundSource.PLAYERS, 0.6F, 1.0F);
    }

    /** Whether a reflecting ward is still active on the given player. */
    public static boolean hasWard(ServerLevel level, Player player) {
        return level.getGameTime() < player.getPersistentData().getLong(WARD_TAG);
    }
}
