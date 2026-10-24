package com.starmaylight.arcana_industry.magic.spell;

import com.starmaylight.arcana_industry.sound.ModSounds;
import java.util.Comparator;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

/** Drains one nearby hostile to restore the caster; blocked damage grants no healing. */
public final class SiphonMarkSpell implements Spell {
    public static final int COST = 100;
    public static final double RANGE = 6.0;
    public static final float DAMAGE = 3.0F;
    public static final float HEAL = 2.0F;

    @Override public String id() { return "siphon_mark"; }

    @Override public int lumenCost() { return COST; }

    @Override public void cast(ServerLevel level, Player caster) {
        level.getEntitiesOfClass(LivingEntity.class,
                new AABB(caster.blockPosition()).inflate(RANGE),
                entity -> entity instanceof Enemy && entity.isAlive()
                    && entity.distanceToSqr(caster) <= RANGE * RANGE)
            .stream().min(Comparator.comparingDouble(caster::distanceToSqr)).ifPresent(target -> {
                if (target.hurt(level.damageSources().indirectMagic(caster, caster), DAMAGE)) {
                    caster.heal(HEAL);
                    level.sendParticles(ParticleTypes.ENCHANT,
                        target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(),
                        12, 0.25, 0.4, 0.25, 0.02);
                    level.sendParticles(ParticleTypes.HEART,
                        caster.getX(), caster.getY() + caster.getBbHeight(), caster.getZ(),
                        3, 0.25, 0.2, 0.25, 0.01);
                }
            });
        level.playSound(null, caster.blockPosition(), ModSounds.SPELL_SIPHON_MARK.get(),
            SoundSource.PLAYERS, 0.5F, 1.0F);
    }
}
