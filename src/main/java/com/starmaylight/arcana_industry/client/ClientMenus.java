package com.starmaylight.arcana_industry.client;

import com.starmaylight.arcana_industry.Arcana_industry;
import com.starmaylight.arcana_industry.machine.MachineRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;

@Mod.EventBusSubscriber(modid = Arcana_industry.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientMenus {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(com.starmaylight.arcana_industry.mob.MobRegistry.RUST_CRAWLER.get(), RustCrawlerRenderer::new);
        event.registerEntityRenderer(com.starmaylight.arcana_industry.mob.MobRegistry.CINDER_HUSK.get(), CinderHuskRenderer::new);
        event.registerEntityRenderer(com.starmaylight.arcana_industry.mob.MobRegistry.FLUX_ARCHER.get(), FluxArcherRenderer::new);
        event.registerEntityRenderer(com.starmaylight.arcana_industry.mob.MobRegistry.STATIC_CREEPER.get(), StaticCreeperRenderer::new);
        event.registerEntityRenderer(com.starmaylight.arcana_industry.mob.MobRegistry.ORE_MITE.get(), OreMiteRenderer::new);
    }
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(MachineRegistry.CRUSHER_MENU.get(), CrusherScreen::new);
            MenuScreens.register(MachineRegistry.SIEVE_MENU.get(), SieveScreen::new);
            MenuScreens.register(MachineRegistry.QUERN_MENU.get(), QuernScreen::new);
            MenuScreens.register(MachineRegistry.KILN_MENU.get(), KilnScreen::new);
            MenuScreens.register(MachineRegistry.PRESS_MENU.get(), PressScreen::new);
            MenuScreens.register(MachineRegistry.LEACHING_VAT_MENU.get(), LeachingVatScreen::new);
            MenuScreens.register(MachineRegistry.ELECTROLYZER_MENU.get(), ElectrolyzerScreen::new);
            MenuScreens.register(MachineRegistry.RESONANCE_INFUSER_MENU.get(), ResonanceInfuserScreen::new);
            MenuScreens.register(MachineRegistry.HELIOSTAT_ETCHER_MENU.get(), HeliostatEtcherScreen::new);
            MenuScreens.register(MachineRegistry.NOCTILUCENT_CONDENSER_MENU.get(), NoctilucentCondenserScreen::new);
            MenuScreens.register(MachineRegistry.ASTRAL_RELAY_MENU.get(), AstralRelayScreen::new);
            MenuScreens.register(MachineRegistry.STELLAR_CRUCIBLE_MENU.get(), StellarCrucibleScreen::new);
            MenuScreens.register(MachineRegistry.SOLAR_SMELTER_MENU.get(), SolarSmelterScreen::new);
            MenuScreens.register(MachineRegistry.SOLAR_PHOTOLYZER_MENU.get(), SolarPhotolyzerScreen::new);
            MenuScreens.register(MachineRegistry.LENS_GRINDER_MENU.get(), LensGrinderScreen::new);
            MenuScreens.register(MachineRegistry.CORONA_COLLECTOR_MENU.get(), CoronaCollectorScreen::new);
            MenuScreens.register(MachineRegistry.PRISM_SEPARATOR_MENU.get(), PrismSeparatorScreen::new);
            MenuScreens.register(MachineRegistry.LATTICE_COMPOSER_MENU.get(), LatticeComposerScreen::new);
            MenuScreens.register(MachineRegistry.PHASE_ANNEALER_MENU.get(), PhaseAnnealerScreen::new);
            MenuScreens.register(MachineRegistry.PHASE_RECLAIMER_MENU.get(), PhaseReclaimerScreen::new);
            MenuScreens.register(MachineRegistry.REFRACTORY_MIXER_MENU.get(), RefractoryMixerScreen::new);
            MenuScreens.register(MachineRegistry.QUENCH_VAT_MENU.get(), QuenchVatScreen::new);
            MenuScreens.register(MachineRegistry.LENS_COLLIMATOR_MENU.get(), LensCollimatorScreen::new);
            MenuScreens.register(MachineRegistry.CONTAINMENT_FORGE_MENU.get(), ContainmentForgeScreen::new);
            MenuScreens.register(MachineRegistry.CHAR_RETORT_MENU.get(), CharRetortScreen::new);
            MenuScreens.register(MachineRegistry.EMBER_DRYER_MENU.get(), EmberDryerScreen::new);
            MenuScreens.register(MachineRegistry.CLINKER_OVEN_MENU.get(), ClinkerOvenScreen::new);
            MenuScreens.register(MachineRegistry.STASIS_WEAVER_MENU.get(), StasisWeaverScreen::new);
            MenuScreens.register(MachineRegistry.LATTICE_ENGRAVER_MENU.get(), LatticeEngraverScreen::new);
            MenuScreens.register(MachineRegistry.LATTICE_RECLAIMER_MENU.get(), LatticeReclaimerScreen::new);
            MenuScreens.register(MachineRegistry.LATTICE_SCRIBER_MENU.get(), LatticeScriberScreen::new);
            MenuScreens.register(MachineRegistry.LATTICE_WELDER_MENU.get(), LatticeWelderScreen::new);
            MenuScreens.register(MachineRegistry.LATTICE_LAMINATOR_MENU.get(), LatticeLaminatorScreen::new);
            MenuScreens.register(MachineRegistry.PHASE_COLLAPSER_MENU.get(), PhaseCollapserScreen::new);
            MenuScreens.register(MachineRegistry.PHASE_SUBLIMATOR_MENU.get(), PhaseSublimatorScreen::new);
            MenuScreens.register(MachineRegistry.PHASE_DIFFUSER_MENU.get(), PhaseDiffuserScreen::new);
            MenuScreens.register(MachineRegistry.PHASE_INVERTER_MENU.get(), PhaseInverterScreen::new);
            MenuScreens.register(MachineRegistry.PHASE_MODULATOR_MENU.get(), PhaseModulatorScreen::new);
            MenuScreens.register(MachineRegistry.PHASE_STABILIZER_MENU.get(), PhaseStabilizerScreen::new);
            MenuScreens.register(MachineRegistry.PHASE_PRESERVER_MENU.get(), PhasePreserverScreen::new);
            MenuScreens.register(MachineRegistry.PHASE_EFFUSER_MENU.get(), PhaseEffuserScreen::new);
            MenuScreens.register(MachineRegistry.PHASE_CONFLUX_MENU.get(), PhaseConfluxScreen::new);
            MenuScreens.register(MachineRegistry.PHASE_REVIVIFIER_MENU.get(), PhaseRevivifierScreen::new);
            MenuScreens.register(MachineRegistry.PHASE_REFRACTOR_MENU.get(), PhaseRefractorScreen::new);
            MenuScreens.register(MachineRegistry.PHASE_FERMENTER_MENU.get(), PhaseFermenterScreen::new);
            MenuScreens.register(MachineRegistry.SPECTRAL_FILTER_MENU.get(), SpectralFilterScreen::new);
            MenuScreens.register(MachineRegistry.DIFFRACTION_GRATE_MENU.get(), DiffractionGrateScreen::new);
            MenuScreens.register(MachineRegistry.PRISM_SCORER_MENU.get(), PrismScorerScreen::new);
            MenuScreens.register(MachineRegistry.PRISM_BURNISHER_MENU.get(), PrismBurnisherScreen::new);
            MenuScreens.register(MachineRegistry.PRISM_LAPIDARY_MENU.get(), PrismLapidaryScreen::new);
            MenuScreens.register(MachineRegistry.PRISM_POLISHER_MENU.get(), PrismPolisherScreen::new);
            MenuScreens.register(MachineRegistry.PRISM_TUMBLER_MENU.get(), PrismTumblerScreen::new);
            MenuScreens.register(MachineRegistry.CLAY_EXTRUDER_MENU.get(), ClayExtruderScreen::new);
            MenuScreens.register(MachineRegistry.COPPER_HOT_PRESS_MENU.get(), CopperHotPressScreen::new);
            MenuScreens.register(MachineRegistry.BASALT_TEMPERER_MENU.get(), BasaltTempererScreen::new);
            MenuScreens.register(MachineRegistry.ASH_BELLOWS_MENU.get(), AshBellowsScreen::new);
            MenuScreens.register(MachineRegistry.FIBER_CARDER_MENU.get(), FiberCarderScreen::new);
            MenuScreens.register(MachineRegistry.SAWMILL_MENU.get(), SawmillScreen::new);
            MenuScreens.register(MachineRegistry.LATTICE_INFUSER_MENU.get(), LatticeInfuserScreen::new);
            MenuScreens.register(MachineRegistry.MOLD_CASTER_MENU.get(), MoldCasterScreen::new);
        });
    }
    private ClientMenus() {}
}
