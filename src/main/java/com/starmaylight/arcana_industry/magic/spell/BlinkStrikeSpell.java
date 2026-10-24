package com.starmaylight.arcana_industry.magic.spell;

import com.starmaylight.arcana_industry.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** A mobility strike: the caster blinks forward and damages hostiles around the landing spot. */
public final class BlinkStrikeSpell implements Spell {
    public static final int COST = 130;
    public static final double RANGE = 6.0;
    public static final double RADIUS = 3.0;
    public static final float DAMAGE = 4.0F;
    private static final double WALL_MARGIN = 0.3;

    @Override public String id() { return "blink_strike"; }

    @Override public int lumenCost() { return COST; }

    @Override public void cast(ServerLevel level, Player caster) {
        Vec3 look = caster.getLookAngle();
        Vec3 flat = new Vec3(look.x, 0, look.z);
        flat = flat.lengthSqr() < 1.0E-4 ? new Vec3(0, 0, 1) : flat.normalize();

        Vec3 start = caster.position();
        Vec3 end = start.add(flat.scale(RANGE));
        HitResult hit = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, caster));
        Vec3 destination = hit.getType() == HitResult.Type.MISS ? end
            : hit.getLocation().subtract(flat.scale(WALL_MARGIN));

        level.sendParticles(ParticleTypes.REVERSE_PORTAL, start.x, start.y + 1.0, start.z, 12, 0.3, 0.5, 0.3, 0.02);
        // Set the authoritative position directly, then sync real connections; ServerPlayer#teleportTo
        // alone defers entirely to the connection packet and never moves a player with no real connection.
        caster.moveTo(destination.x, destination.y, destination.z, caster.getYRot(), caster.getXRot());
        if (caster instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.teleport(destination.x, destination.y, destination.z, caster.getYRot(), caster.getXRot());
        }
        level.sendParticles(ParticleTypes.REVERSE_PORTAL, destination.x, destination.y + 1.0, destination.z, 12, 0.3, 0.5, 0.3, 0.02);

        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class,
                new AABB(destination, destination).inflate(RADIUS),
                entity -> entity instanceof Enemy && entity.isAlive()
                    && entity.distanceToSqr(destination) <= RADIUS * RADIUS)) {
            target.hurt(level.damageSources().indirectMagic(caster, caster), DAMAGE);
        }

        level.playSound(null, caster.blockPosition(), ModSounds.SPELL_BLINK_STRIKE.get(),
            SoundSource.PLAYERS, 0.8F, 1.0F);
    }
}
