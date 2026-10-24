package com.starmaylight.arcana_industry.magic.spell;

import com.starmaylight.arcana_industry.sound.ModSounds;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

/** Strikes the closest hostile and dispels its beneficial effects on a successful hit. */
public final class NullifyingBrandSpell implements Spell {
    public static final int COST = 105;
    public static final double RANGE = 8.0;
    public static final float DAMAGE = 4.0F;

    @Override public String id() { return "nullifying_brand"; }

    @Override public int lumenCost() { return COST; }

    @Override public void cast(ServerLevel level, Player caster) {
        level.getEntitiesOfClass(LivingEntity.class,
                new AABB(caster.blockPosition()).inflate(RANGE),
                entity -> entity instanceof Enemy && entity.isAlive()
                    && entity.distanceToSqr(caster) <= RANGE * RANGE)
            .stream().min(Comparator.comparingDouble(caster::distanceToSqr)).ifPresent(target -> {
                if (target.hurt(level.damageSources().indirectMagic(caster, caster), DAMAGE)) {
                    List<MobEffect> beneficial = target.getActiveEffects().stream()
                        .map(instance -> instance.getEffect())
                        .filter(MobEffect::isBeneficial).toList();
                    beneficial.forEach(target::removeEffect);
                    level.sendParticles(ParticleTypes.ENCHANT,
                        target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(),
                        16, 0.3, 0.4, 0.3, 0.03);
                }
            });
        level.playSound(null, caster.blockPosition(), ModSounds.SPELL_NULLIFYING_BRAND.get(),
            SoundSource.PLAYERS, 0.6F, 1.0F);
    }
}
