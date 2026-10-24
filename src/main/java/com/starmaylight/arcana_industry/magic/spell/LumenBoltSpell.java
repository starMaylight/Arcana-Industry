package com.starmaylight.arcana_industry.magic.spell;

import com.starmaylight.arcana_industry.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** A straight-line lumen bolt: the first customizable magical attack, one of at least twenty planned. */
public final class LumenBoltSpell implements Spell {
    public static final int COST = 40;
    public static final double RANGE = 12.0;
    public static final float DAMAGE = 6.0F;

    @Override public String id() { return "lumen_bolt"; }

    @Override public int lumenCost() { return COST; }

    @Override public void cast(ServerLevel level, Player caster) {
        Vec3 start = caster.getEyePosition();
        Vec3 look = caster.getViewVector(1.0F);
        Vec3 end = start.add(look.scale(RANGE));
        HitResult blockHit = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, caster));
        double reach = blockHit.getType() == HitResult.Type.MISS ? RANGE : start.distanceTo(blockHit.getLocation());
        AABB searchBox = caster.getBoundingBox().expandTowards(look.scale(reach)).inflate(1.0);
        Entity target = null;
        double closest = reach;
        for (Entity candidate : level.getEntities(caster, searchBox, e -> e instanceof LivingEntity && e.isAlive())) {
            var hit = candidate.getBoundingBox().inflate(0.3).clip(start, end);
            if (hit.isEmpty()) continue;
            double distance = start.distanceTo(hit.get());
            if (distance < closest) { closest = distance; target = candidate; }
        }
        for (int i = 0; i < 20; i++) {
            Vec3 point = start.add(look.scale(closest * i / 19.0));
            level.sendParticles(ParticleTypes.END_ROD, point.x, point.y, point.z, 1, 0.0, 0.0, 0.0, 0.0);
        }
        level.playSound(null, caster.blockPosition(), ModSounds.SPELL_LUMEN_BOLT.get(),
            SoundSource.PLAYERS, 0.6F, 1.0F);
        if (target != null) target.hurt(level.damageSources().indirectMagic(caster, caster), DAMAGE);
    }
}
