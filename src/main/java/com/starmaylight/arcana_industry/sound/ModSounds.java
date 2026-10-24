package com.starmaylight.arcana_industry.sound;

import com.starmaylight.arcana_industry.Arcana_industry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModSounds {
    private static final DeferredRegister<SoundEvent> SOUNDS =
        DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Arcana_industry.MODID);

    public static final RegistryObject<SoundEvent> ASTRAL_RITE = register("astral_rite");
    public static final RegistryObject<SoundEvent> CRUSHER_BATCH = register("crusher_batch");

    // Batch-completion cues, one per processing machine (tools/generate_machine_sounds.py).
    public static final RegistryObject<SoundEvent> ASH_BELLOWS_BATCH = register("ash_bellows_batch");
    public static final RegistryObject<SoundEvent> CHAR_RETORT_BATCH = register("char_retort_batch");
    public static final RegistryObject<SoundEvent> CLINKER_OVEN_BATCH = register("clinker_oven_batch");
    public static final RegistryObject<SoundEvent> EMBER_DRYER_BATCH = register("ember_dryer_batch");
    public static final RegistryObject<SoundEvent> BASALT_TEMPERER_BATCH = register("basalt_temperer_batch");
    public static final RegistryObject<SoundEvent> CONTAINMENT_FORGE_BATCH = register("containment_forge_batch");
    public static final RegistryObject<SoundEvent> PRESS_BATCH = register("press_batch");
    public static final RegistryObject<SoundEvent> COPPER_HOT_PRESS_BATCH = register("copper_hot_press_batch");
    public static final RegistryObject<SoundEvent> MOLD_CASTER_BATCH = register("mold_caster_batch");
    public static final RegistryObject<SoundEvent> CLAY_EXTRUDER_BATCH = register("clay_extruder_batch");
    public static final RegistryObject<SoundEvent> ELECTROLYZER_BATCH = register("electrolyzer_batch");
    public static final RegistryObject<SoundEvent> LEACHING_VAT_BATCH = register("leaching_vat_batch");
    public static final RegistryObject<SoundEvent> QUENCH_VAT_BATCH = register("quench_vat_batch");
    public static final RegistryObject<SoundEvent> REFRACTORY_MIXER_BATCH = register("refractory_mixer_batch");
    public static final RegistryObject<SoundEvent> STELLAR_CRUCIBLE_BATCH = register("stellar_crucible_batch");
    public static final RegistryObject<SoundEvent> QUERN_BATCH = register("quern_batch");
    public static final RegistryObject<SoundEvent> SIEVE_BATCH = register("sieve_batch");
    public static final RegistryObject<SoundEvent> SAWMILL_BATCH = register("sawmill_batch");
    public static final RegistryObject<SoundEvent> FIBER_CARDER_BATCH = register("fiber_carder_batch");
    public static final RegistryObject<SoundEvent> LENS_GRINDER_BATCH = register("lens_grinder_batch");
    public static final RegistryObject<SoundEvent> PRISM_BURNISHER_BATCH = register("prism_burnisher_batch");
    public static final RegistryObject<SoundEvent> PRISM_LAPIDARY_BATCH = register("prism_lapidary_batch");
    public static final RegistryObject<SoundEvent> PRISM_POLISHER_BATCH = register("prism_polisher_batch");
    public static final RegistryObject<SoundEvent> PRISM_SCORER_BATCH = register("prism_scorer_batch");
    public static final RegistryObject<SoundEvent> PRISM_SEPARATOR_BATCH = register("prism_separator_batch");
    public static final RegistryObject<SoundEvent> PRISM_TUMBLER_BATCH = register("prism_tumbler_batch");
    public static final RegistryObject<SoundEvent> LENS_COLLIMATOR_BATCH = register("lens_collimator_batch");
    public static final RegistryObject<SoundEvent> SPECTRAL_FILTER_BATCH = register("spectral_filter_batch");
    public static final RegistryObject<SoundEvent> DIFFRACTION_GRATE_BATCH = register("diffraction_grate_batch");
    public static final RegistryObject<SoundEvent> HELIOSTAT_ETCHER_BATCH = register("heliostat_etcher_batch");
    public static final RegistryObject<SoundEvent> SOLAR_PHOTOLYZER_BATCH = register("solar_photolyzer_batch");
    public static final RegistryObject<SoundEvent> SOLAR_SMELTER_BATCH = register("solar_smelter_batch");
    public static final RegistryObject<SoundEvent> LATTICE_COMPOSER_BATCH = register("lattice_composer_batch");
    public static final RegistryObject<SoundEvent> LATTICE_ENGRAVER_BATCH = register("lattice_engraver_batch");
    public static final RegistryObject<SoundEvent> LATTICE_INFUSER_BATCH = register("lattice_infuser_batch");
    public static final RegistryObject<SoundEvent> LATTICE_LAMINATOR_BATCH = register("lattice_laminator_batch");
    public static final RegistryObject<SoundEvent> LATTICE_RECLAIMER_BATCH = register("lattice_reclaimer_batch");
    public static final RegistryObject<SoundEvent> LATTICE_SCRIBER_BATCH = register("lattice_scriber_batch");
    public static final RegistryObject<SoundEvent> LATTICE_WELDER_BATCH = register("lattice_welder_batch");
    public static final RegistryObject<SoundEvent> PHASE_ANNEALER_BATCH = register("phase_annealer_batch");
    public static final RegistryObject<SoundEvent> PHASE_COLLAPSER_BATCH = register("phase_collapser_batch");
    public static final RegistryObject<SoundEvent> PHASE_CONFLUX_BATCH = register("phase_conflux_batch");
    public static final RegistryObject<SoundEvent> PHASE_REVIVIFIER_BATCH = register("phase_revivifier_batch");
    public static final RegistryObject<SoundEvent> PHASE_REFRACTOR_BATCH = register("phase_refractor_batch");
    public static final RegistryObject<SoundEvent> PHASE_FERMENTER_BATCH = register("phase_fermenter_batch");
    public static final RegistryObject<SoundEvent> PHASE_DIFFUSER_BATCH = register("phase_diffuser_batch");
    public static final RegistryObject<SoundEvent> PHASE_EFFUSER_BATCH = register("phase_effuser_batch");
    public static final RegistryObject<SoundEvent> PHASE_INVERTER_BATCH = register("phase_inverter_batch");
    public static final RegistryObject<SoundEvent> PHASE_MODULATOR_BATCH = register("phase_modulator_batch");
    public static final RegistryObject<SoundEvent> PHASE_PRESERVER_BATCH = register("phase_preserver_batch");
    public static final RegistryObject<SoundEvent> PHASE_RECLAIMER_BATCH = register("phase_reclaimer_batch");
    public static final RegistryObject<SoundEvent> PHASE_STABILIZER_BATCH = register("phase_stabilizer_batch");
    public static final RegistryObject<SoundEvent> PHASE_SUBLIMATOR_BATCH = register("phase_sublimator_batch");
    public static final RegistryObject<SoundEvent> ASTRAL_RELAY_BATCH = register("astral_relay_batch");
    public static final RegistryObject<SoundEvent> RESONANCE_INFUSER_BATCH = register("resonance_infuser_batch");
    public static final RegistryObject<SoundEvent> NOCTILUCENT_CONDENSER_BATCH = register("noctilucent_condenser_batch");
    public static final RegistryObject<SoundEvent> CORONA_COLLECTOR_BATCH = register("corona_collector_batch");
    public static final RegistryObject<SoundEvent> STASIS_WEAVER_BATCH = register("stasis_weaver_batch");
    /** Streamed track for the Lattice Hymn music disc (tools/generate_music_disc.py). */
    public static final RegistryObject<SoundEvent> MUSIC_DISC_LATTICE_HYMN = register("music_disc.lattice_hymn");
    /** Streamed track for the Corroded Waltz music disc (tools/generate_music_disc_waltz.py). */
    public static final RegistryObject<SoundEvent> MUSIC_DISC_CORRODED_WALTZ = register("music_disc.corroded_waltz");
    /** Streamed track for the Vein Echo music disc (tools/generate_music_disc_vein_echo.py). */
    public static final RegistryObject<SoundEvent> MUSIC_DISC_VEIN_ECHO = register("music_disc.vein_echo");

    // Kiln working-state cues tied to its LIT state (tools/generate_machine_sounds.py kiln_*).
    public static final RegistryObject<SoundEvent> KILN_FIRE = register("kiln_fire");
    public static final RegistryObject<SoundEvent> KILN_IGNITE = register("kiln_ignite");
    public static final RegistryObject<SoundEvent> KILN_COOL = register("kiln_cool");

    // Electrolyzer working-state cues tied to its ACTIVE state (tools/generate_machine_sounds.py electrolyzer_*).
    public static final RegistryObject<SoundEvent> ELECTROLYZER_HUM = register("electrolyzer_hum");
    public static final RegistryObject<SoundEvent> ELECTROLYZER_START = register("electrolyzer_start");
    public static final RegistryObject<SoundEvent> ELECTROLYZER_STOP = register("electrolyzer_stop");
    public static final RegistryObject<SoundEvent> LEACHING_VAT_BUBBLE = register("leaching_vat_bubble");
    public static final RegistryObject<SoundEvent> LEACHING_VAT_START = register("leaching_vat_start");
    public static final RegistryObject<SoundEvent> LEACHING_VAT_STOP = register("leaching_vat_stop");
    public static final RegistryObject<SoundEvent> QUENCH_VAT_START = register("quench_vat_start");
    public static final RegistryObject<SoundEvent> QUENCH_VAT_STOP = register("quench_vat_stop");

    // Astral Wand cast cues, one per spell (tools/generate_spell_sounds.py).
    public static final RegistryObject<SoundEvent> SPELL_LUMEN_BOLT = register("spell_lumen_bolt");
    public static final RegistryObject<SoundEvent> SPELL_RESONANCE_PULSE = register("spell_resonance_pulse");
    public static final RegistryObject<SoundEvent> SPELL_ASTRAL_LANCE = register("spell_astral_lance");
    public static final RegistryObject<SoundEvent> SPELL_STARFALL = register("spell_starfall");
    public static final RegistryObject<SoundEvent> SPELL_ARC_CHAIN = register("spell_arc_chain");
    public static final RegistryObject<SoundEvent> SPELL_DUSK_FAN = register("spell_dusk_fan");
    public static final RegistryObject<SoundEvent> SPELL_GRAVITY_WELL = register("spell_gravity_well");
    public static final RegistryObject<SoundEvent> SPELL_REPULSION_BURST = register("spell_repulsion_burst");
    public static final RegistryObject<SoundEvent> SPELL_SKYWARD_SURGE = register("spell_skyward_surge");
    public static final RegistryObject<SoundEvent> SPELL_WITHERING_SIGIL = register("spell_withering_sigil");
    public static final RegistryObject<SoundEvent> SPELL_SIPHON_MARK = register("spell_siphon_mark");
    public static final RegistryObject<SoundEvent> SPELL_NULLIFYING_BRAND = register("spell_nullifying_brand");
    public static final RegistryObject<SoundEvent> SPELL_BINDING_SNARE = register("spell_binding_snare");
    public static final RegistryObject<SoundEvent> SPELL_FINISHING_GLEAM = register("spell_finishing_gleam");
    public static final RegistryObject<SoundEvent> SPELL_AEGIS_WARD = register("spell_aegis_ward");
    public static final RegistryObject<SoundEvent> SPELL_HARMONIC_REVERSAL = register("spell_harmonic_reversal");
    public static final RegistryObject<SoundEvent> SPELL_BLINK_STRIKE = register("spell_blink_strike");
    public static final RegistryObject<SoundEvent> SPELL_DISCORDANT_CALL = register("spell_discordant_call");
    public static final RegistryObject<SoundEvent> SPELL_RIFT_EXCHANGE = register("spell_rift_exchange");
    public static final RegistryObject<SoundEvent> SPELL_ARCANE_FURY = register("spell_arcane_fury");

    // Hostile mob voices: ambient/hurt/death per mob (tools/generate_mob_sounds.py).
    public static final RegistryObject<SoundEvent> CINDER_HUSK_AMBIENT = register("cinder_husk_ambient");
    public static final RegistryObject<SoundEvent> CINDER_HUSK_HURT = register("cinder_husk_hurt");
    public static final RegistryObject<SoundEvent> CINDER_HUSK_DEATH = register("cinder_husk_death");
    public static final RegistryObject<SoundEvent> FLUX_ARCHER_AMBIENT = register("flux_archer_ambient");
    public static final RegistryObject<SoundEvent> FLUX_ARCHER_HURT = register("flux_archer_hurt");
    public static final RegistryObject<SoundEvent> FLUX_ARCHER_DEATH = register("flux_archer_death");
    public static final RegistryObject<SoundEvent> STATIC_CREEPER_AMBIENT = register("static_creeper_ambient");
    public static final RegistryObject<SoundEvent> STATIC_CREEPER_HURT = register("static_creeper_hurt");
    public static final RegistryObject<SoundEvent> STATIC_CREEPER_DEATH = register("static_creeper_death");
    public static final RegistryObject<SoundEvent> ORE_MITE_AMBIENT = register("ore_mite_ambient");
    public static final RegistryObject<SoundEvent> ORE_MITE_HURT = register("ore_mite_hurt");
    public static final RegistryObject<SoundEvent> ORE_MITE_DEATH = register("ore_mite_death");
    public static final RegistryObject<SoundEvent> RUST_CRAWLER_AMBIENT = register("rust_crawler_ambient");
    public static final RegistryObject<SoundEvent> RUST_CRAWLER_HURT = register("rust_crawler_hurt");
    public static final RegistryObject<SoundEvent> RUST_CRAWLER_DEATH = register("rust_crawler_death");

    // Distinct movement cues (tools/generate_mob_steps.py).
    public static final RegistryObject<SoundEvent> CINDER_HUSK_STEP = register("cinder_husk_step");
    public static final RegistryObject<SoundEvent> FLUX_ARCHER_STEP = register("flux_archer_step");
    public static final RegistryObject<SoundEvent> STATIC_CREEPER_STEP = register("static_creeper_step");
    public static final RegistryObject<SoundEvent> ORE_MITE_STEP = register("ore_mite_step");
    public static final RegistryObject<SoundEvent> RUST_CRAWLER_STEP = register("rust_crawler_step");

    // Attack cues replacing inherited bow/fuse sounds (tools/generate_mob_actions.py).
    public static final RegistryObject<SoundEvent> FLUX_ARCHER_SHOOT = register("flux_archer_shoot");
    public static final RegistryObject<SoundEvent> STATIC_CREEPER_FUSE = register("static_creeper_fuse");

    // Multiblock controller completion cues, one per structure (tools/generate_machine_sounds.py MULTIBLOCKS).
    private static final String[] MULTIBLOCK_WORK_EVENTS = {
        "arc_press_work",
        "armor_delaminator_work",
        "blast_column_work",
        "broach_press_work",
        "capacitor_array_work",
        "chromatic_kiln_work",
        "clamping_press_work",
        "compaction_spire_work",
        "concordance_altar_work",
        "crimp_press_work",
        "degauss_coil_work",
        "flange_press_work",
        "flux_rectifier_work",
        "foundry_ring_work",
        "heraldic_frame_work",
        "hydraulic_riveter_work",
        "induction_coiler_work",
        "iridescent_basin_work",
        "magnetizer_work",
        "milling_gantry_work",
        "silvering_bath_work",
        "enamel_kiln_work",
        "drawing_bench_work",
        "assay_hearth_work",
        "coal_forge_work",
        "ore_sluice_work",
        "polarity_inverter_work",
        "prismatic_press_work",
        "provision_hearth_work",
        "reliquary_stand_work",
        "resonance_basin_work",
        "resonant_cultivator_work",
        "rotary_screen_work",
        "sanctum_pylon_work",
        "seam_press_work",
        "signal_imprinter_work",
        "smoke_rack_work",
        "soaking_hearth_work",
        "spectral_loom_work",
        "swage_press_work",
        "twin_channel_washer_work",
        "verdant_distiller_work",
    };
    private static final java.util.Map<String, RegistryObject<SoundEvent>> MULTIBLOCK_WORK = new java.util.HashMap<>();
    static {
        for (String event : MULTIBLOCK_WORK_EVENTS) MULTIBLOCK_WORK.put(event, register(event));
    }

    // Spell impact cues played at each struck creature (tools/generate_spell_impact_sounds.py).
    // Full event ids are spelled out so each sounds.json declaration has a literal code reference.
    private static final String SPELL_IMPACT_PREFIX = "spell_impact_";
    private static final String[] SPELL_IMPACT_EVENTS = {
        "spell_impact_lumen_bolt", "spell_impact_resonance_pulse", "spell_impact_astral_lance",
        "spell_impact_starfall", "spell_impact_arc_chain", "spell_impact_dusk_fan", "spell_impact_gravity_well",
        "spell_impact_repulsion_burst", "spell_impact_skyward_surge", "spell_impact_withering_sigil",
        "spell_impact_siphon_mark", "spell_impact_nullifying_brand", "spell_impact_binding_snare",
        "spell_impact_finishing_gleam", "spell_impact_aegis_ward", "spell_impact_harmonic_reversal",
        "spell_impact_blink_strike", "spell_impact_discordant_call", "spell_impact_rift_exchange",
        "spell_impact_arcane_fury",
    };
    private static final java.util.Map<String, RegistryObject<SoundEvent>> SPELL_IMPACT = new java.util.HashMap<>();
    static {
        for (String event : SPELL_IMPACT_EVENTS) {
            SPELL_IMPACT.put(event.substring(SPELL_IMPACT_PREFIX.length()), register(event));
        }
    }

    /** The impact cue for a spell id, or null when the spell has none. */
    public static SoundEvent spellImpact(String spellId) {
        RegistryObject<SoundEvent> cue = SPELL_IMPACT.get(spellId);
        return cue == null ? null : cue.get();
    }

    private ModSounds() { }

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(
            new ResourceLocation(Arcana_industry.MODID, name)));
    }

    /** Plays a machine's batch-completion cue on the server so nearby players hear it. */
    public static void playBatch(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos,
                                 RegistryObject<SoundEvent> cue) {
        level.playSound(null, pos, cue.get(), net.minecraft.sounds.SoundSource.BLOCKS, 0.6F, 1.0F);
    }

    /** Plays a multiblock controller's completion cue, keyed by its multiblock id (e.g. "milling_gantry"). */
    public static void playMultiblock(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos,
                                      String multiblock) {
        RegistryObject<SoundEvent> cue = MULTIBLOCK_WORK.get(multiblock + "_work");
        if (cue == null) throw new IllegalArgumentException("No work cue for multiblock " + multiblock);
        level.playSound(null, pos, cue.get(), net.minecraft.sounds.SoundSource.BLOCKS, 0.7F, 1.0F);
    }

    public static void register(IEventBus bus) { SOUNDS.register(bus); }
}
