package com.starmaylight.arcana_industry.mob;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import com.starmaylight.arcana_industry.sound.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;

/** A corroded tunnel scavenger whose bite briefly stiffens moving machinery crews. */
public class RustCrawler extends Spider {
    public RustCrawler(EntityType<? extends Spider> type, Level level) { super(type, level); }

    @Override public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 0), this);
        }
        return hit;
    }

    // Original generated voice; public so validation can confirm it replaced the inherited vanilla one.
    @Override public SoundEvent getAmbientSound() { return ModSounds.RUST_CRAWLER_AMBIENT.get(); }
    @Override public SoundEvent getHurtSound(DamageSource source) { return ModSounds.RUST_CRAWLER_HURT.get(); }
    @Override public SoundEvent getDeathSound() { return ModSounds.RUST_CRAWLER_DEATH.get(); }
    public SoundEvent footstepSound() { return ModSounds.RUST_CRAWLER_STEP.get(); }
    @Override protected void playStepSound(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        playSound(footstepSound(), 0.15F, 1.0F);
    }
}
