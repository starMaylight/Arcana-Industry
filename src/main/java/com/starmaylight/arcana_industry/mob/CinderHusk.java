package com.starmaylight.arcana_industry.mob;

import net.minecraft.world.entity.Entity;
import com.starmaylight.arcana_industry.sound.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

/** A heat-hardened wasteland hunter that ignites anything it strikes. */
public class CinderHusk extends Zombie {
    public CinderHusk(EntityType<? extends Zombie> type, Level level) { super(type, level); }

    @Override public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity) target.setSecondsOnFire(4);
        return hit;
    }

    // Original generated voice; public so validation can confirm it replaced the inherited vanilla one.
    @Override public SoundEvent getAmbientSound() { return ModSounds.CINDER_HUSK_AMBIENT.get(); }
    @Override public SoundEvent getHurtSound(DamageSource source) { return ModSounds.CINDER_HUSK_HURT.get(); }
    @Override public SoundEvent getDeathSound() { return ModSounds.CINDER_HUSK_DEATH.get(); }
    public SoundEvent footstepSound() { return ModSounds.CINDER_HUSK_STEP.get(); }
    @Override protected void playStepSound(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        playSound(footstepSound(), 0.15F, 1.0F);
    }
}
