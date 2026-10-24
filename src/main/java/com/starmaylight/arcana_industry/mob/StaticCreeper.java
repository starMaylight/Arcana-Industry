package com.starmaylight.arcana_industry.mob;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import com.starmaylight.arcana_industry.sound.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;

/** A close-range saboteur that disrupts mining before its fuse can finish. */
public class StaticCreeper extends Creeper {
    public StaticCreeper(EntityType<? extends Creeper> type, Level level) { super(type, level); }

    @Override public void aiStep() {
        super.aiStep();
        LivingEntity target = getTarget();
        if (!level().isClientSide && tickCount % 20 == 0 && target != null
            && target.isAlive() && distanceToSqr(target) <= 16.0) {
            target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 0), this);
        }
    }

    // Original generated voice; public so validation can confirm it replaced the inherited vanilla one.
    @Override public SoundEvent getAmbientSound() { return ModSounds.STATIC_CREEPER_AMBIENT.get(); }
    @Override public SoundEvent getHurtSound(DamageSource source) { return ModSounds.STATIC_CREEPER_HURT.get(); }
    @Override public SoundEvent getDeathSound() { return ModSounds.STATIC_CREEPER_DEATH.get(); }
    public SoundEvent footstepSound() { return ModSounds.STATIC_CREEPER_STEP.get(); }
    @Override protected void playStepSound(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        playSound(footstepSound(), 0.15F, 1.0F);
    }

    /** Swaps the inherited fuse priming cue for the original one; every other sound passes through. */
    public SoundEvent actionSound(SoundEvent inherited) {
        return inherited == SoundEvents.CREEPER_PRIMED ? ModSounds.STATIC_CREEPER_FUSE.get() : inherited;
    }
    @Override public void playSound(SoundEvent sound, float volume, float pitch) {
        super.playSound(actionSound(sound), volume, pitch);
    }
}
