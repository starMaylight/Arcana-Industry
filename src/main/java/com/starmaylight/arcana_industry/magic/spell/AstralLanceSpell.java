package com.starmaylight.arcana_industry.magic.spell;

import com.starmaylight.arcana_industry.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** A piercing lance that damages every living target along its line, unlike the single-target Lumen Bolt. */
public final class AstralLanceSpell implements Spell {
    public static final int COST = 90;
    public static final double RANGE = 16.0;
    public static final float DAMAGE = 4.0F;
    public static final int GLOW_TICKS = 100;

    @Override public String id() { return "astral_lance"; }

    @Override public int lumenCost() { return COST; }

    @Override public void cast(ServerLevel level, Player caster) {
        Vec3 start = caster.getEyePosition();
        Vec3 look = caster.getViewVector(1.0F);
        Vec3 end = start.add(look.scale(RANGE));
        HitResult blockHit = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, caster));
        double reach = blockHit.getType() == HitResult.Type.MISS ? RANGE : start.distanceTo(blockHit.getLocation());
        AABB searchBox = caster.getBoundingBox().expandTowards(look.scale(reach)).inflate(1.0);
        for (Entity candidate : level.getEntities(caster, searchBox, e -> e instanceof LivingEntity && e.isAlive())) {
            var hit = candidate.getBoundingBox().inflate(0.3).clip(start, start.add(look.scale(reach)));
            if (hit.isEmpty()) continue;
            candidate.hurt(level.damageSources().indirectMagic(caster, caster), DAMAGE);
            ((LivingEntity) candidate).addEffect(new MobEffectInstance(MobEffects.GLOWING, GLOW_TICKS, 0));
        }
        for (int i = 0; i < 24; i++) {
            Vec3 point = start.add(look.scale(reach * i / 23.0));
            level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, point.x, point.y, point.z, 1, 0.0, 0.0, 0.0, 0.0);
        }
        level.playSound(null, caster.blockPosition(), ModSounds.SPELL_ASTRAL_LANCE.get(),
            SoundSource.PLAYERS, 0.8F, 1.0F);
    }
}
