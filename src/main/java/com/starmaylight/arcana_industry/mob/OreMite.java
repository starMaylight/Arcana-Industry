package com.starmaylight.arcana_industry.mob;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import com.starmaylight.arcana_industry.sound.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.level.Level;

/** A small cave ambusher whose bite drains food during long mining trips. */
public final class OreMite extends Silverfish {
    public OreMite(EntityType<? extends Silverfish> type, Level level) { super(type, level); }

    @Override public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 0), this);
        }
        return hit;
    }

    // Original generated voice; public so validation can confirm it replaced the inherited vanilla one.
    @Override public SoundEvent getAmbientSound() { return ModSounds.ORE_MITE_AMBIENT.get(); }
    @Override public SoundEvent getHurtSound(DamageSource source) { return ModSounds.ORE_MITE_HURT.get(); }
    @Override public SoundEvent getDeathSound() { return ModSounds.ORE_MITE_DEATH.get(); }
    public SoundEvent footstepSound() { return ModSounds.ORE_MITE_STEP.get(); }
    @Override protected void playStepSound(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        playSound(footstepSound(), 0.15F, 1.0F);
    }
}
