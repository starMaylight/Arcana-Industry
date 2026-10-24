package com.starmaylight.arcana_industry.mob;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import com.starmaylight.arcana_industry.sound.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** A ranged wasteland marksman whose arrows hamper escaping workers. */
public class FluxArcher extends Skeleton {
    public FluxArcher(EntityType<? extends Skeleton> type, Level level) { super(type, level); }

    @Override protected AbstractArrow getArrow(ItemStack arrows, float power) {
        AbstractArrow projectile = super.getArrow(arrows, power);
        if (projectile instanceof Arrow arrow) {
            arrow.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 0));
        }
        return projectile;
    }

    // Original generated voice; public so validation can confirm it replaced the inherited vanilla one.
    @Override public SoundEvent getAmbientSound() { return ModSounds.FLUX_ARCHER_AMBIENT.get(); }
    @Override public SoundEvent getHurtSound(DamageSource source) { return ModSounds.FLUX_ARCHER_HURT.get(); }
    @Override public SoundEvent getDeathSound() { return ModSounds.FLUX_ARCHER_DEATH.get(); }
    public SoundEvent footstepSound() { return ModSounds.FLUX_ARCHER_STEP.get(); }
    @Override protected void playStepSound(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        playSound(footstepSound(), 0.15F, 1.0F);
    }

    /** Swaps the inherited bow release cue for the original one; every other sound passes through. */
    public SoundEvent actionSound(SoundEvent inherited) {
        return inherited == SoundEvents.SKELETON_SHOOT ? ModSounds.FLUX_ARCHER_SHOOT.get() : inherited;
    }
    @Override public void playSound(SoundEvent sound, float volume, float pitch) {
        super.playSound(actionSound(sound), volume, pitch);
    }
}
