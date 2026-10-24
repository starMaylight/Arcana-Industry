package com.starmaylight.arcana_industry.magic.spell;

import com.starmaylight.arcana_industry.sound.ModSounds;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

/** Provokes a pair of hostile mobs into attacking each other. */
public final class DiscordantCallSpell implements Spell {
    public static final int COST = 95;
    public static final double RANGE = 8.0;
    public static final double PAIR_RANGE = 5.0;
    public static final float DAMAGE = 2.0F;

    @Override public String id() { return "discordant_call"; }

    @Override public int lumenCost() { return COST; }

    @Override public void cast(ServerLevel level, Player caster) {
        List<Mob> hostiles = level.getEntitiesOfClass(Mob.class,
            new AABB(caster.blockPosition()).inflate(RANGE),
            mob -> mob instanceof Enemy && mob.isAlive()
                && mob.distanceToSqr(caster) <= RANGE * RANGE);
        hostiles.sort(Comparator.comparingDouble(caster::distanceToSqr));
        for (Mob first : hostiles) {
            Mob second = hostiles.stream()
                .filter(other -> other != first && other.distanceToSqr(first) <= PAIR_RANGE * PAIR_RANGE)
                .min(Comparator.comparingDouble(first::distanceToSqr)).orElse(null);
            if (second == null) continue;
            first.hurt(level.damageSources().indirectMagic(caster, caster), DAMAGE);
            second.hurt(level.damageSources().indirectMagic(caster, caster), DAMAGE);
            if (first.isAlive() && second.isAlive()) {
                first.setTarget(second);
                second.setTarget(first);
            }
            level.sendParticles(ParticleTypes.ENCHANT,
                (first.getX() + second.getX()) / 2, first.getY() + 1, (first.getZ() + second.getZ()) / 2,
                20, 0.6, 0.5, 0.6, 0.04);
            break;
        }
        level.playSound(null, caster.blockPosition(), ModSounds.SPELL_DISCORDANT_CALL.get(),
            SoundSource.PLAYERS, 0.7F, 1.0F);
    }
}
