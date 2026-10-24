package com.starmaylight.arcana_industry.magic.spell;

import com.starmaylight.arcana_industry.Arcana_industry;
import com.starmaylight.arcana_industry.sound.ModSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Plays a spell's impact cue where it actually lands. Spells deal damage synchronously inside
 * {@link Spell#cast}, so the wand opens a cast scope and every damage event from that caster in
 * the scope is a real hit; misses, blocked or fully absorbed-to-zero hits stay silent.
 */
@Mod.EventBusSubscriber(modid = Arcana_industry.MODID)
public final class SpellImpactCues {
    /** Caps cues per cast so a crowd-hitting spell does not stack dozens of identical sounds. */
    public static final int MAX_CUES_PER_CAST = 4;

    private static Player activeCaster;
    private static String activeSpell;
    private static int cuesThisCast;
    private static int lastCastCues;
    private static float activePower = 1.0F;

    /** Runs a spell cast with impact tracking. Server thread only, like the wand use path. */
    public static void cast(Spell spell, ServerLevel level, Player caster) { cast(spell, level, caster, 1.0F); }

    /** Casts with a wand-tuning damage multiplier applied to every hit the caster deals in the scope. */
    public static void cast(Spell spell, ServerLevel level, Player caster, float power) {
        Player outerCaster = activeCaster;
        float outerPower = activePower;
        String outerSpell = activeSpell;
        int outerCues = cuesThisCast;
        activeCaster = caster;
        activeSpell = spell.id();
        cuesThisCast = 0;
        activePower = power;
        try {
            spell.cast(level, caster);
        } finally {
            lastCastCues = cuesThisCast;
            activeCaster = outerCaster;
            activeSpell = outerSpell;
            cuesThisCast = outerCues;
            activePower = outerPower;
        }
    }

    /** Impact cues the most recent completed cast played; used by the spell GameTest. */
    public static int lastCastCues() { return lastCastCues; }

    /** Plays one spell's impact cue at a struck creature; also used for Aegis Ward reflections. */
    public static void play(ServerLevel level, LivingEntity struck, String spellId) {
        SoundEvent cue = ModSounds.spellImpact(spellId);
        if (cue == null) return;
        level.playSound(null, struck.getX(), struck.getY() + struck.getBbHeight() * 0.5, struck.getZ(),
            cue, SoundSource.PLAYERS, 0.7F, 0.95F + level.random.nextFloat() * 0.1F);
    }

    // Scales the raw hit before armor, so tunings change spell strength like any damage bonus would.
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onHurt(LivingHurtEvent event) {
        if (activeCaster == null || activePower == 1.0F || event.getSource().getEntity() != activeCaster) return;
        event.setAmount(event.getAmount() * activePower);
    }

    // Lowest priority so other handlers' cancellations and reductions apply before deciding it landed.
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDamage(LivingDamageEvent event) {
        if (activeCaster == null || event.isCanceled() || event.getAmount() <= 0) return;
        if (event.getSource().getEntity() != activeCaster || cuesThisCast >= MAX_CUES_PER_CAST) return;
        if (!(event.getEntity().level() instanceof ServerLevel level)) return;
        cuesThisCast++;
        play(level, event.getEntity(), activeSpell);
    }

    private SpellImpactCues() {}
}
