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

import java.util.ArrayList;
import java.util.List;

/** A hitscan bolt that leaps from its first victim to nearby targets in sequence, unlike the single hit of Lumen Bolt or the straight pierce of Astral Lance. */
public final class ArcChainSpell implements Spell {
    public static final int COST = 55;
    public static final double RANGE = 14.0;
    public static final double CHAIN_RADIUS = 5.0;
    public static final int MAX_CHAINS = 2;
    public static final float INITIAL_DAMAGE = 5.0F;
    public static final float CHAIN_DAMAGE = 3.0F;
    public static final int WEAKNESS_TICKS = 100;

    @Override public String id() { return "arc_chain"; }

    @Override public int lumenCost() { return COST; }

    @Override public void cast(ServerLevel level, Player caster) {
        Vec3 start = caster.getEyePosition();
        Vec3 look = caster.getViewVector(1.0F);
        Vec3 end = start.add(look.scale(RANGE));
        HitResult blockHit = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, caster));
        double reach = blockHit.getType() == HitResult.Type.MISS ? RANGE : start.distanceTo(blockHit.getLocation());
        AABB searchBox = caster.getBoundingBox().expandTowards(look.scale(reach)).inflate(1.0);

        LivingEntity initial = null;
        double closest = reach;
        for (Entity candidate : level.getEntities(caster, searchBox, e -> e instanceof LivingEntity && e.isAlive())) {
            var hit = candidate.getBoundingBox().inflate(0.3).clip(start, end);
            if (hit.isEmpty()) continue;
            double distance = start.distanceTo(hit.get());
            if (distance < closest) { closest = distance; initial = (LivingEntity) candidate; }
        }

        for (int i = 0; i < 20; i++) {
            Vec3 point = start.add(look.scale(closest * i / 19.0));
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, point.x, point.y, point.z, 1, 0.0, 0.0, 0.0, 0.0);
        }
        level.playSound(null, caster.blockPosition(), ModSounds.SPELL_ARC_CHAIN.get(),
            SoundSource.PLAYERS, 0.5F, 1.0F);

        if (initial == null) return;

        List<LivingEntity> struck = new ArrayList<>();
        LivingEntity current = initial;
        for (int chain = 0; chain <= MAX_CHAINS; chain++) {
            float damage = chain == 0 ? INITIAL_DAMAGE : CHAIN_DAMAGE;
            current.hurt(level.damageSources().indirectMagic(caster, caster), damage);
            current.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, WEAKNESS_TICKS, 0));
            struck.add(current);

            LivingEntity next = null;
            double nextDistance = CHAIN_RADIUS;
            for (LivingEntity candidate : level.getEntitiesOfClass(LivingEntity.class,
                    new AABB(current.blockPosition()).inflate(CHAIN_RADIUS),
                    e -> e.isAlive() && e != caster && !struck.contains(e))) {
                double distance = candidate.distanceTo(current);
                if (distance < nextDistance) { nextDistance = distance; next = candidate; }
            }
            if (next == null) break;
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                (current.getX() + next.getX()) / 2.0, (current.getY() + next.getY()) / 2.0 + 0.5,
                (current.getZ() + next.getZ()) / 2.0, 6, 0.2, 0.2, 0.2, 0.0);
            current = next;
        }
    }
}
