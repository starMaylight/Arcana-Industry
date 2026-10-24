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
import net.minecraft.world.phys.Vec3;

/** Repositions a hostile pair so their approach and attack lines are disrupted. */
public final class RiftExchangeSpell implements Spell {
    public static final int COST = 110;
    public static final double RANGE = 8.0;
    public static final float DAMAGE = 3.0F;

    @Override public String id() { return "rift_exchange"; }

    @Override public int lumenCost() { return COST; }

    @Override public void cast(ServerLevel level, Player caster) {
        List<Mob> hostiles = level.getEntitiesOfClass(Mob.class,
            new AABB(caster.blockPosition()).inflate(RANGE),
            mob -> mob instanceof Enemy && mob.isAlive()
                && mob.distanceToSqr(caster) <= RANGE * RANGE);
        hostiles.sort(Comparator.comparingDouble(caster::distanceToSqr));
        if (hostiles.size() >= 2) {
            Mob first = hostiles.get(0);
            Mob second = hostiles.get(1);
            Vec3 firstPosition = first.position();
            Vec3 secondPosition = second.position();
            level.sendParticles(ParticleTypes.REVERSE_PORTAL, firstPosition.x, firstPosition.y + 1,
                firstPosition.z, 12, 0.3, 0.5, 0.3, 0.02);
            level.sendParticles(ParticleTypes.REVERSE_PORTAL, secondPosition.x, secondPosition.y + 1,
                secondPosition.z, 12, 0.3, 0.5, 0.3, 0.02);
            first.moveTo(secondPosition.x, secondPosition.y, secondPosition.z,
                first.getYRot(), first.getXRot());
            second.moveTo(firstPosition.x, firstPosition.y, firstPosition.z,
                second.getYRot(), second.getXRot());
            first.hurt(level.damageSources().indirectMagic(caster, caster), DAMAGE);
            second.hurt(level.damageSources().indirectMagic(caster, caster), DAMAGE);
        }
        level.playSound(null, caster.blockPosition(), ModSounds.SPELL_RIFT_EXCHANGE.get(),
            SoundSource.PLAYERS, 0.8F, 1.0F);
    }
}
