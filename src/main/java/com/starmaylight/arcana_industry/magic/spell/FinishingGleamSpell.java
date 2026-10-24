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

/** A single-target finisher whose damage rises with the hostile's missing health. */
public final class FinishingGleamSpell implements Spell {
    public static final int COST = 90;
    public static final double RANGE = 8.0;
    public static final float BASE_DAMAGE = 2.0F;
    public static final float MAX_DAMAGE = 10.0F;

    @Override public String id() { return "finishing_gleam"; }

    @Override public int lumenCost() { return COST; }

    @Override public void cast(ServerLevel level, Player caster) {
        level.getEntitiesOfClass(LivingEntity.class,
                new AABB(caster.blockPosition()).inflate(RANGE),
                entity -> entity instanceof Enemy && entity.isAlive()
                    && entity.distanceToSqr(caster) <= RANGE * RANGE)
            .stream().min(Comparator.comparingDouble(caster::distanceToSqr)).ifPresent(target -> {
                float missingHealth = target.getMaxHealth() - target.getHealth();
                float damage = Math.min(MAX_DAMAGE, BASE_DAMAGE + missingHealth * 0.5F);
                if (target.hurt(level.damageSources().indirectMagic(caster, caster), damage)) {
                    level.sendParticles(ParticleTypes.END_ROD,
                        target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(),
                        16, 0.25, 0.4, 0.25, 0.02);
                }
            });
        level.playSound(null, caster.blockPosition(), ModSounds.SPELL_FINISHING_GLEAM.get(),
            SoundSource.PLAYERS, 0.6F, 1.0F);
    }
}
