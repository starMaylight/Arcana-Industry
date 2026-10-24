package com.starmaylight.arcana_industry.compat.jei;

import com.starmaylight.arcana_industry.Arcana_industry;
import com.starmaylight.arcana_industry.client.CrusherScreen;
import com.starmaylight.arcana_industry.client.KilnScreen;
import com.starmaylight.arcana_industry.thermal.FiringRecipe;
import com.starmaylight.arcana_industry.machine.MachineRegistry;
import com.starmaylight.arcana_industry.recipe.CrushingRecipe;
import com.starmaylight.arcana_industry.recipe.PressingRecipe;
import com.starmaylight.arcana_industry.client.PressScreen;
import com.starmaylight.arcana_industry.client.LeachingVatScreen;
import com.starmaylight.arcana_industry.recipe.LeachingRecipe;
import com.starmaylight.arcana_industry.recipe.ElectrolysisRecipe;
import com.starmaylight.arcana_industry.client.ElectrolyzerScreen;
import com.starmaylight.arcana_industry.recipe.ResonatingRecipe;
import com.starmaylight.arcana_industry.client.ResonanceInfuserScreen;
import com.starmaylight.arcana_industry.recipe.EtchingRecipe;
import com.starmaylight.arcana_industry.client.HeliostatEtcherScreen;
import com.starmaylight.arcana_industry.recipe.SeparationRecipe;
import com.starmaylight.arcana_industry.client.PrismSeparatorScreen;
import com.starmaylight.arcana_industry.recipe.CompositionRecipe;
import com.starmaylight.arcana_industry.client.LatticeComposerScreen;
import com.starmaylight.arcana_industry.recipe.AnnealingRecipe;
import com.starmaylight.arcana_industry.client.PhaseAnnealerScreen;
import com.starmaylight.arcana_industry.recipe.ReclaimingRecipe;
import com.starmaylight.arcana_industry.client.PhaseReclaimerScreen;
import com.starmaylight.arcana_industry.recipe.ElectrolysisRecipe;
import com.starmaylight.arcana_industry.client.ElectrolyzerScreen;
import com.starmaylight.arcana_industry.recipe.SievingRecipe;
import com.starmaylight.arcana_industry.client.SieveScreen;
import com.starmaylight.arcana_industry.recipe.MixingRecipe;
import com.starmaylight.arcana_industry.client.RefractoryMixerScreen;
import com.starmaylight.arcana_industry.recipe.QuenchingRecipe;
import com.starmaylight.arcana_industry.client.QuenchVatScreen;
import com.starmaylight.arcana_industry.recipe.ClampingRecipe;
import com.starmaylight.arcana_industry.recipe.InductionWindingRecipe;
import com.starmaylight.arcana_industry.recipe.CapacitorChargingRecipe;
import com.starmaylight.arcana_industry.recipe.MagnetizationRecipe;
import com.starmaylight.arcana_industry.recipe.ArcDischargeRecipe;
import com.starmaylight.arcana_industry.recipe.FluxRectificationRecipe;
import com.starmaylight.arcana_industry.recipe.PolarityInversionRecipe;
import com.starmaylight.arcana_industry.recipe.DegaussingRecipe;
import com.starmaylight.arcana_industry.recipe.ArmorDelaminationRecipe;
import com.starmaylight.arcana_industry.recipe.SignalImprintingRecipe;
import com.starmaylight.arcana_industry.recipe.ResonantCultivationRecipe;
import com.starmaylight.arcana_industry.recipe.VerdantDistillationRecipe;
import com.starmaylight.arcana_industry.recipe.ChromaticGlazingRecipe;
import com.starmaylight.arcana_industry.recipe.PrismaticReclamationRecipe;
import com.starmaylight.arcana_industry.recipe.TextileWeavingRecipe;
import com.starmaylight.arcana_industry.recipe.ResonanceCuttingRecipe;
import com.starmaylight.arcana_industry.recipe.MillingRecipe;
import com.starmaylight.arcana_industry.recipe.SluicingRecipe;
import com.starmaylight.arcana_industry.recipe.CompactionRecipe;
import com.starmaylight.arcana_industry.recipe.HearthSmokingRecipe;
import com.starmaylight.arcana_industry.recipe.FoundrySmeltingRecipe;
import com.starmaylight.arcana_industry.recipe.CondensingRecipe;
import com.starmaylight.arcana_industry.recipe.RelayingRecipe;
import com.starmaylight.arcana_industry.recipe.GrindingRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import java.util.Comparator;

/** Only JEI discovers this class. Common registration never links optional client APIs. */
@JeiPlugin
public final class ArcanaJeiPlugin implements IModPlugin {
    public static final RecipeType<CrushingRecipe> CRUSHING = RecipeType.create(Arcana_industry.MODID, "crushing", CrushingRecipe.class);
    public static final RecipeType<SievingRecipe> SIEVING = RecipeType.create(Arcana_industry.MODID, "sieving", SievingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.QuerningRecipe> QUERNING = RecipeType.create(Arcana_industry.MODID, "querning", com.starmaylight.arcana_industry.recipe.QuerningRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.CombustionRecipe> COMBUSTION = RecipeType.create(Arcana_industry.MODID, "combustion", com.starmaylight.arcana_industry.recipe.CombustionRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.SteamRaisingRecipe> STEAM_RAISING = RecipeType.create(Arcana_industry.MODID, "steam_raising", com.starmaylight.arcana_industry.recipe.SteamRaisingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.AshingRecipe> ASHING = RecipeType.create(Arcana_industry.MODID, "ashing", com.starmaylight.arcana_industry.recipe.AshingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.CardingRecipe> CARDING = RecipeType.create(Arcana_industry.MODID, "carding", com.starmaylight.arcana_industry.recipe.CardingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.SawingRecipe> SAWING = RecipeType.create(Arcana_industry.MODID, "sawing", com.starmaylight.arcana_industry.recipe.SawingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.MoldingRecipe> MOLDING = RecipeType.create(Arcana_industry.MODID, "molding", com.starmaylight.arcana_industry.recipe.MoldingRecipe.class);
    public static final RecipeType<FiringRecipe> FIRING = RecipeType.create(Arcana_industry.MODID, "firing", FiringRecipe.class);
    public static final RecipeType<PressingRecipe> PRESSING = RecipeType.create(Arcana_industry.MODID, "pressing", PressingRecipe.class);
    public static final RecipeType<LeachingRecipe> LEACHING = RecipeType.create(Arcana_industry.MODID, "leaching", LeachingRecipe.class);
    public static final RecipeType<ElectrolysisRecipe> ELECTROLYSIS = RecipeType.create(Arcana_industry.MODID, "electrolysis", ElectrolysisRecipe.class);
    public static final RecipeType<ResonatingRecipe> RESONATING = RecipeType.create(Arcana_industry.MODID, "resonating", ResonatingRecipe.class);
    public static final RecipeType<EtchingRecipe> ETCHING = RecipeType.create(Arcana_industry.MODID, "etching", EtchingRecipe.class);
    public static final RecipeType<CondensingRecipe> CONDENSING = RecipeType.create(Arcana_industry.MODID, "condensing", CondensingRecipe.class);
    public static final RecipeType<RelayingRecipe> RELAYING = RecipeType.create(Arcana_industry.MODID, "relaying", RelayingRecipe.class);
    public static final RecipeType<SeparationRecipe> SEPARATION = RecipeType.create(Arcana_industry.MODID, "separation", SeparationRecipe.class);
    public static final RecipeType<CompositionRecipe> COMPOSITION = RecipeType.create(Arcana_industry.MODID, "composition", CompositionRecipe.class);
    public static final RecipeType<AnnealingRecipe> ANNEALING = RecipeType.create(Arcana_industry.MODID, "annealing", AnnealingRecipe.class);
    public static final RecipeType<ReclaimingRecipe> RECLAIMING = RecipeType.create(Arcana_industry.MODID, "reclaiming", ReclaimingRecipe.class);
    public static final RecipeType<MixingRecipe> MIXING = RecipeType.create(Arcana_industry.MODID, "mixing", MixingRecipe.class);
    public static final RecipeType<QuenchingRecipe> QUENCHING = RecipeType.create(Arcana_industry.MODID, "quenching", QuenchingRecipe.class);
    public static final RecipeType<ClampingRecipe> CLAMPING = RecipeType.create(Arcana_industry.MODID, "clamping", ClampingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.SeamingRecipe> SEAMING = RecipeType.create(Arcana_industry.MODID, "seaming", com.starmaylight.arcana_industry.recipe.SeamingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.SwagingRecipe> SWAGING = RecipeType.create(Arcana_industry.MODID, "swaging", com.starmaylight.arcana_industry.recipe.SwagingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.CrimpingRecipe> CRIMPING = RecipeType.create(Arcana_industry.MODID, "crimping", com.starmaylight.arcana_industry.recipe.CrimpingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.BroachingRecipe> BROACHING = RecipeType.create(Arcana_industry.MODID, "broaching", com.starmaylight.arcana_industry.recipe.BroachingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.RivetingRecipe> RIVETING = RecipeType.create(Arcana_industry.MODID, "riveting", com.starmaylight.arcana_industry.recipe.RivetingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.FlangingRecipe> FLANGING = RecipeType.create(Arcana_industry.MODID, "flanging", com.starmaylight.arcana_industry.recipe.FlangingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.SoakingRecipe> SOAKING = RecipeType.create(Arcana_industry.MODID, "soaking", com.starmaylight.arcana_industry.recipe.SoakingRecipe.class);
    public static final RecipeType<InductionWindingRecipe> INDUCTION_WINDING = RecipeType.create(Arcana_industry.MODID, "induction_winding", InductionWindingRecipe.class);
    public static final RecipeType<CapacitorChargingRecipe> CAPACITOR_CHARGING = RecipeType.create(Arcana_industry.MODID, "capacitor_charging", CapacitorChargingRecipe.class);
    public static final RecipeType<MagnetizationRecipe> MAGNETIZATION = RecipeType.create(Arcana_industry.MODID, "magnetization", MagnetizationRecipe.class);
    public static final RecipeType<ArcDischargeRecipe> ARC_DISCHARGE = RecipeType.create(Arcana_industry.MODID, "arc_discharge", ArcDischargeRecipe.class);
    public static final RecipeType<FluxRectificationRecipe> FLUX_RECTIFICATION = RecipeType.create(Arcana_industry.MODID, "flux_rectification", FluxRectificationRecipe.class);
    public static final RecipeType<PolarityInversionRecipe> POLARITY_INVERSION = RecipeType.create(Arcana_industry.MODID, "polarity_inversion", PolarityInversionRecipe.class);
    public static final RecipeType<DegaussingRecipe> DEGAUSSING = RecipeType.create(Arcana_industry.MODID, "degaussing", DegaussingRecipe.class);
    public static final RecipeType<ArmorDelaminationRecipe> ARMOR_DELAMINATION = RecipeType.create(Arcana_industry.MODID, "armor_delamination", ArmorDelaminationRecipe.class);
    public static final RecipeType<SignalImprintingRecipe> SIGNAL_IMPRINTING = RecipeType.create(Arcana_industry.MODID, "signal_imprinting", SignalImprintingRecipe.class);
    public static final RecipeType<ResonantCultivationRecipe> RESONANT_CULTIVATION = RecipeType.create(Arcana_industry.MODID, "resonant_cultivation", ResonantCultivationRecipe.class);
    public static final RecipeType<ResonanceCuttingRecipe> RESONANCE_CUTTING = RecipeType.create(Arcana_industry.MODID, "resonance_cutting", ResonanceCuttingRecipe.class);
    public static final RecipeType<MillingRecipe> MILLING = RecipeType.create(Arcana_industry.MODID, "milling", MillingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.CupellationRecipe> CUPELLATION = RecipeType.create(Arcana_industry.MODID, "cupellation", com.starmaylight.arcana_industry.recipe.CupellationRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.ForgingRecipe> FORGING = RecipeType.create(Arcana_industry.MODID, "forging", com.starmaylight.arcana_industry.recipe.ForgingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.WireDrawingRecipe> WIRE_DRAWING = RecipeType.create(Arcana_industry.MODID, "wire_drawing", com.starmaylight.arcana_industry.recipe.WireDrawingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.EnamelingRecipe> ENAMELING = RecipeType.create(Arcana_industry.MODID, "enameling", com.starmaylight.arcana_industry.recipe.EnamelingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.SilveringRecipe> SILVERING = RecipeType.create(Arcana_industry.MODID, "silvering", com.starmaylight.arcana_industry.recipe.SilveringRecipe.class);
    public static final RecipeType<SluicingRecipe> SLUICING = RecipeType.create(Arcana_industry.MODID, "sluicing", SluicingRecipe.class);
    public static final RecipeType<CompactionRecipe> COMPACTION = RecipeType.create(Arcana_industry.MODID, "compaction", CompactionRecipe.class);
    public static final RecipeType<HearthSmokingRecipe> HEARTH_SMOKING = RecipeType.create(Arcana_industry.MODID, "hearth_smoking", HearthSmokingRecipe.class);
    public static final RecipeType<FoundrySmeltingRecipe> FOUNDRY_SMELTING = RecipeType.create(Arcana_industry.MODID, "foundry_smelting", FoundrySmeltingRecipe.class);
    public static final RecipeType<VerdantDistillationRecipe> VERDANT_DISTILLATION = RecipeType.create(Arcana_industry.MODID, "verdant_distillation", VerdantDistillationRecipe.class);
    public static final RecipeType<ChromaticGlazingRecipe> CHROMATIC_GLAZING = RecipeType.create(Arcana_industry.MODID, "chromatic_glazing", ChromaticGlazingRecipe.class);
    public static final RecipeType<PrismaticReclamationRecipe> PRISMATIC_RECLAMATION = RecipeType.create(Arcana_industry.MODID, "prismatic_reclamation", PrismaticReclamationRecipe.class);
    public static final RecipeType<TextileWeavingRecipe> TEXTILE_WEAVING = RecipeType.create(Arcana_industry.MODID, "textile_weaving", TextileWeavingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.HeraldicWeavingRecipe> HERALDIC_WEAVING = RecipeType.create(Arcana_industry.MODID, "heraldic_weaving", com.starmaylight.arcana_industry.recipe.HeraldicWeavingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.ReliquaryBindingRecipe> RELIQUARY_BINDING = RecipeType.create(Arcana_industry.MODID, "reliquary_binding", com.starmaylight.arcana_industry.recipe.ReliquaryBindingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.ConcordanceRecipe> CONCORDANCE = RecipeType.create(Arcana_industry.MODID, "concordance", com.starmaylight.arcana_industry.recipe.ConcordanceRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.IridescentRecipe> IRIDESCENT = RecipeType.create(Arcana_industry.MODID, "iridescent", com.starmaylight.arcana_industry.recipe.IridescentRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.FusingRecipe> FUSING = RecipeType.create(Arcana_industry.MODID, "fusing", com.starmaylight.arcana_industry.recipe.FusingRecipe.class);
    public static final RecipeType<GrindingRecipe> GRINDING = RecipeType.create(Arcana_industry.MODID, "grinding", GrindingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.CollectionRecipe> COLLECTION = RecipeType.create(Arcana_industry.MODID, "collection", com.starmaylight.arcana_industry.recipe.CollectionRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.SanctumAttunementRecipe> SANCTUM_ATTUNEMENT = RecipeType.create(Arcana_industry.MODID, "sanctum_attunement", com.starmaylight.arcana_industry.recipe.SanctumAttunementRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.CollimationRecipe> COLLIMATION = RecipeType.create(Arcana_industry.MODID, "collimation", com.starmaylight.arcana_industry.recipe.CollimationRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.LaminationRecipe> LATTICE_LAMINATION = RecipeType.create(Arcana_industry.MODID, "lattice_lamination", com.starmaylight.arcana_industry.recipe.LaminationRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.EngravingRecipe> LATTICE_ENGRAVING = RecipeType.create(Arcana_industry.MODID, "lattice_engraving", com.starmaylight.arcana_industry.recipe.EngravingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.StasisRecipe> STASIS_WEAVING = RecipeType.create(Arcana_industry.MODID, "stasis_weaving", com.starmaylight.arcana_industry.recipe.StasisRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.ContainmentRecipe> CONTAINMENT = RecipeType.create(Arcana_industry.MODID, "containment", com.starmaylight.arcana_industry.recipe.ContainmentRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.CharringRecipe> CHARRING = RecipeType.create(Arcana_industry.MODID, "charring", com.starmaylight.arcana_industry.recipe.CharringRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.DryingRecipe> DRYING = RecipeType.create(Arcana_industry.MODID, "drying", com.starmaylight.arcana_industry.recipe.DryingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.ClinkeringRecipe> CLINKERING = RecipeType.create(Arcana_industry.MODID, "clinkering", com.starmaylight.arcana_industry.recipe.ClinkeringRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.CollapseRecipe> COLLAPSE = RecipeType.create(Arcana_industry.MODID, "collapse", com.starmaylight.arcana_industry.recipe.CollapseRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.SublimationRecipe> SUBLIMATION = RecipeType.create(Arcana_industry.MODID, "sublimation", com.starmaylight.arcana_industry.recipe.SublimationRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.DiffusionRecipe> DIFFUSION = RecipeType.create(Arcana_industry.MODID, "diffusion", com.starmaylight.arcana_industry.recipe.DiffusionRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.InversionRecipe> INVERSION = RecipeType.create(Arcana_industry.MODID, "inversion", com.starmaylight.arcana_industry.recipe.InversionRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.ModulationRecipe> MODULATION = RecipeType.create(Arcana_industry.MODID, "modulation", com.starmaylight.arcana_industry.recipe.ModulationRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.StabilizationRecipe> STABILIZATION = RecipeType.create(Arcana_industry.MODID, "stabilization", com.starmaylight.arcana_industry.recipe.StabilizationRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.PreservationRecipe> PRESERVATION = RecipeType.create(Arcana_industry.MODID, "preservation", com.starmaylight.arcana_industry.recipe.PreservationRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.EffusionRecipe> EFFUSION = RecipeType.create(Arcana_industry.MODID, "effusion", com.starmaylight.arcana_industry.recipe.EffusionRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.ConfluenceRecipe> CONFLUENCE = RecipeType.create(Arcana_industry.MODID, "confluence", com.starmaylight.arcana_industry.recipe.ConfluenceRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.RevivificationRecipe> REVIVIFICATION = RecipeType.create(Arcana_industry.MODID, "revivification", com.starmaylight.arcana_industry.recipe.RevivificationRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.RefractionRecipe> REFRACTION = RecipeType.create(Arcana_industry.MODID, "refraction", com.starmaylight.arcana_industry.recipe.RefractionRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.FermentationRecipe> FERMENTATION = RecipeType.create(Arcana_industry.MODID, "fermentation", com.starmaylight.arcana_industry.recipe.FermentationRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.FiltrationRecipe> FILTRATION = RecipeType.create(Arcana_industry.MODID, "filtration", com.starmaylight.arcana_industry.recipe.FiltrationRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.BurnishingRecipe> BURNISHING = RecipeType.create(Arcana_industry.MODID, "burnishing", com.starmaylight.arcana_industry.recipe.BurnishingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.ScoringRecipe> SCORING = RecipeType.create(Arcana_industry.MODID, "scoring", com.starmaylight.arcana_industry.recipe.ScoringRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.DiffractionRecipe> DIFFRACTION = RecipeType.create(Arcana_industry.MODID, "diffraction", com.starmaylight.arcana_industry.recipe.DiffractionRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.TumblingRecipe> TUMBLING = RecipeType.create(Arcana_industry.MODID, "tumbling", com.starmaylight.arcana_industry.recipe.TumblingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.PolishingRecipe> POLISHING = RecipeType.create(Arcana_industry.MODID, "polishing", com.starmaylight.arcana_industry.recipe.PolishingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.FacetingRecipe> FACETING = RecipeType.create(Arcana_industry.MODID, "faceting", com.starmaylight.arcana_industry.recipe.FacetingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.ExtrudingRecipe> EXTRUDING = RecipeType.create(Arcana_industry.MODID, "extruding", com.starmaylight.arcana_industry.recipe.ExtrudingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.HotPressingRecipe> HOT_PRESSING = RecipeType.create(Arcana_industry.MODID, "hot_pressing", com.starmaylight.arcana_industry.recipe.HotPressingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.StoneTemperingRecipe> STONE_TEMPERING = RecipeType.create(Arcana_industry.MODID, "stone_tempering", com.starmaylight.arcana_industry.recipe.StoneTemperingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.ScribingRecipe> SCRIBING = RecipeType.create(Arcana_industry.MODID, "scribing", com.starmaylight.arcana_industry.recipe.ScribingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.WeldingRecipe> WELDING = RecipeType.create(Arcana_industry.MODID, "welding", com.starmaylight.arcana_industry.recipe.WeldingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.SalvagingRecipe> SALVAGING = RecipeType.create(Arcana_industry.MODID, "salvaging", com.starmaylight.arcana_industry.recipe.SalvagingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.PhotolysisRecipe> PHOTOLYSIS = RecipeType.create(Arcana_industry.MODID, "photolysis", com.starmaylight.arcana_industry.recipe.PhotolysisRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.InfusionRecipe> INFUSION = RecipeType.create(Arcana_industry.MODID, "infusion", com.starmaylight.arcana_industry.recipe.InfusionRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.SolarSmeltingRecipe> SOLAR_SMELTING = RecipeType.create(Arcana_industry.MODID, "solar_smelting", com.starmaylight.arcana_industry.recipe.SolarSmeltingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.LumenRiteRecipe> LUMEN_RITE = RecipeType.create(Arcana_industry.MODID, "lumen_rite", com.starmaylight.arcana_industry.recipe.LumenRiteRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.PedestalRiteRecipe> PEDESTAL_RITE = RecipeType.create(Arcana_industry.MODID, "pedestal_rite", com.starmaylight.arcana_industry.recipe.PedestalRiteRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.ColumnBlastingRecipe> COLUMN_BLASTING = RecipeType.create(Arcana_industry.MODID, "column_blasting", com.starmaylight.arcana_industry.recipe.ColumnBlastingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.RackSmokingRecipe> RACK_SMOKING = RecipeType.create(Arcana_industry.MODID, "rack_smoking", com.starmaylight.arcana_industry.recipe.RackSmokingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.ChannelWashingRecipe> CHANNEL_WASHING = RecipeType.create(Arcana_industry.MODID, "channel_washing", com.starmaylight.arcana_industry.recipe.ChannelWashingRecipe.class);
    public static final RecipeType<com.starmaylight.arcana_industry.recipe.ScreeningRecipe> SCREENING = RecipeType.create(Arcana_industry.MODID, "screening", com.starmaylight.arcana_industry.recipe.ScreeningRecipe.class);
    @Override public ResourceLocation getPluginUid() { return new ResourceLocation(Arcana_industry.MODID, "jei"); }
    @Override public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new CrushingCategory(registration.getJeiHelpers().getGuiHelper()), new SievingCategory(registration.getJeiHelpers().getGuiHelper()), new FiringCategory(registration.getJeiHelpers().getGuiHelper()), new PressingCategory(registration.getJeiHelpers().getGuiHelper()), new LeachingCategory(registration.getJeiHelpers().getGuiHelper()), new ElectrolysisCategory(registration.getJeiHelpers().getGuiHelper()), new ResonatingCategory(registration.getJeiHelpers().getGuiHelper()), new EtchingCategory(registration.getJeiHelpers().getGuiHelper()), new SeparationCategory(registration.getJeiHelpers().getGuiHelper()), new CompositionCategory(registration.getJeiHelpers().getGuiHelper()), new AnnealingCategory(registration.getJeiHelpers().getGuiHelper()), new ReclaimingCategory(registration.getJeiHelpers().getGuiHelper()), new MixingCategory(registration.getJeiHelpers().getGuiHelper()), new QuenchingCategory(registration.getJeiHelpers().getGuiHelper()), new ClampingCategory(registration.getJeiHelpers().getGuiHelper()), new InductionWindingCategory(registration.getJeiHelpers().getGuiHelper()), new CapacitorChargingCategory(registration.getJeiHelpers().getGuiHelper()), new MagnetizationCategory(registration.getJeiHelpers().getGuiHelper()), new ArcDischargeCategory(registration.getJeiHelpers().getGuiHelper()), new FluxRectificationCategory(registration.getJeiHelpers().getGuiHelper()), new PolarityInversionCategory(registration.getJeiHelpers().getGuiHelper()), new DegaussingCategory(registration.getJeiHelpers().getGuiHelper()), new ArmorDelaminationCategory(registration.getJeiHelpers().getGuiHelper()), new SignalImprintingCategory(registration.getJeiHelpers().getGuiHelper()), new ResonantCultivationCategory(registration.getJeiHelpers().getGuiHelper()), new VerdantDistillationCategory(registration.getJeiHelpers().getGuiHelper()), new ChromaticGlazingCategory(registration.getJeiHelpers().getGuiHelper()), new PrismaticReclamationCategory(registration.getJeiHelpers().getGuiHelper()), new TextileWeavingCategory(registration.getJeiHelpers().getGuiHelper()), new HeraldicWeavingCategory(registration.getJeiHelpers().getGuiHelper()), new ReliquaryBindingCategory(registration.getJeiHelpers().getGuiHelper()), new ConcordanceCategory(registration.getJeiHelpers().getGuiHelper()), new IridescentCategory(registration.getJeiHelpers().getGuiHelper()), new ResonanceCuttingCategory(registration.getJeiHelpers().getGuiHelper()), new MillingCategory(registration.getJeiHelpers().getGuiHelper()), new SluicingCategory(registration.getJeiHelpers().getGuiHelper()), new CompactionCategory(registration.getJeiHelpers().getGuiHelper()), new HearthSmokingCategory(registration.getJeiHelpers().getGuiHelper()), new FoundrySmeltingCategory(registration.getJeiHelpers().getGuiHelper()), new CondensingCategory(registration.getJeiHelpers().getGuiHelper()), new RelayingCategory(registration.getJeiHelpers().getGuiHelper()), new FusingCategory(registration.getJeiHelpers().getGuiHelper()), new SanctumAttunementCategory(registration.getJeiHelpers().getGuiHelper()), new QuerningCategory(registration.getJeiHelpers().getGuiHelper()), new AshingCategory(registration.getJeiHelpers().getGuiHelper()), new CombustionCategory(registration.getJeiHelpers().getGuiHelper()), new SteamRaisingCategory(registration.getJeiHelpers().getGuiHelper()), new CardingCategory(registration.getJeiHelpers().getGuiHelper()), new SawingCategory(registration.getJeiHelpers().getGuiHelper()), new MoldingCategory(registration.getJeiHelpers().getGuiHelper()), new SeamingCategory(registration.getJeiHelpers().getGuiHelper()), new SoakingCategory(registration.getJeiHelpers().getGuiHelper()), new SwagingCategory(registration.getJeiHelpers().getGuiHelper()), new CrimpingCategory(registration.getJeiHelpers().getGuiHelper()), new BroachingCategory(registration.getJeiHelpers().getGuiHelper()), new RivetingCategory(registration.getJeiHelpers().getGuiHelper()), new FlangingCategory(registration.getJeiHelpers().getGuiHelper()), new CollimationCategory(registration.getJeiHelpers().getGuiHelper()), new ContainmentCategory(registration.getJeiHelpers().getGuiHelper()), new CharringCategory(registration.getJeiHelpers().getGuiHelper()), new DryingCategory(registration.getJeiHelpers().getGuiHelper()), new StasisWeavingCategory(registration.getJeiHelpers().getGuiHelper()), new LatticeEngravingCategory(registration.getJeiHelpers().getGuiHelper()), new SalvagingCategory(registration.getJeiHelpers().getGuiHelper()), new ScribingCategory(registration.getJeiHelpers().getGuiHelper()), new WeldingCategory(registration.getJeiHelpers().getGuiHelper()), new LatticeLaminationCategory(registration.getJeiHelpers().getGuiHelper()), new CollapseCategory(registration.getJeiHelpers().getGuiHelper()), new SublimationCategory(registration.getJeiHelpers().getGuiHelper()), new DiffusionCategory(registration.getJeiHelpers().getGuiHelper()), new InversionCategory(registration.getJeiHelpers().getGuiHelper()), new ModulationCategory(registration.getJeiHelpers().getGuiHelper()), new StabilizationCategory(registration.getJeiHelpers().getGuiHelper()), new PreservationCategory(registration.getJeiHelpers().getGuiHelper()), new EffusionCategory(registration.getJeiHelpers().getGuiHelper()), new ConfluenceCategory(registration.getJeiHelpers().getGuiHelper()), new RevivificationCategory(registration.getJeiHelpers().getGuiHelper()), new RefractionCategory(registration.getJeiHelpers().getGuiHelper()), new FermentationCategory(registration.getJeiHelpers().getGuiHelper()), new SolarSmeltingCategory(registration.getJeiHelpers().getGuiHelper()), new PhotolysisCategory(registration.getJeiHelpers().getGuiHelper()), new GrindingCategory(registration.getJeiHelpers().getGuiHelper()), new FiltrationCategory(registration.getJeiHelpers().getGuiHelper()), new CollectionCategory(registration.getJeiHelpers().getGuiHelper()), new DiffractionCategory(registration.getJeiHelpers().getGuiHelper()), new ScoringCategory(registration.getJeiHelpers().getGuiHelper()), new BurnishingCategory(registration.getJeiHelpers().getGuiHelper()), new FacetingCategory(registration.getJeiHelpers().getGuiHelper()), new PolishingCategory(registration.getJeiHelpers().getGuiHelper()), new TumblingCategory(registration.getJeiHelpers().getGuiHelper()), new ExtrudingCategory(registration.getJeiHelpers().getGuiHelper()), new HotPressingCategory(registration.getJeiHelpers().getGuiHelper()), new InfusionCategory(registration.getJeiHelpers().getGuiHelper()), new StoneTemperingCategory(registration.getJeiHelpers().getGuiHelper()), new ClinkeringCategory(registration.getJeiHelpers().getGuiHelper()), new SilveringCategory(registration.getJeiHelpers().getGuiHelper()), new EnamelingCategory(registration.getJeiHelpers().getGuiHelper()), new WireDrawingCategory(registration.getJeiHelpers().getGuiHelper()), new CupellationCategory(registration.getJeiHelpers().getGuiHelper()), new ForgingCategory(registration.getJeiHelpers().getGuiHelper()));
        var helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
            new PedestalRiteCategory(helper),
            new HandFedCategory<>(helper, LUMEN_RITE, "lumen_rite", new ItemStack(MachineRegistry.ASTRAL_FONT_ITEM.get()),
                com.starmaylight.arcana_industry.recipe.LumenRiteRecipe::input, com.starmaylight.arcana_industry.recipe.LumenRiteRecipe::count,
                com.starmaylight.arcana_industry.recipe.LumenRiteRecipe::output,
                recipe -> Component.translatable("jei.arcana_industry.lumen_cost", recipe.lumenCost()), 1),
            new HandFedCategory<>(helper, COLUMN_BLASTING, "column_blasting", new ItemStack(MachineRegistry.BLAST_COLUMN_ITEM.get()),
                com.starmaylight.arcana_industry.recipe.ColumnBlastingRecipe::input, com.starmaylight.arcana_industry.recipe.ColumnBlastingRecipe::count,
                com.starmaylight.arcana_industry.recipe.ColumnBlastingRecipe::output,
                recipe -> Component.translatable("jei.arcana_industry.form_structure"), 1),
            new HandFedCategory<>(helper, RACK_SMOKING, "rack_smoking", new ItemStack(MachineRegistry.SMOKE_RACK_ITEM.get()),
                com.starmaylight.arcana_industry.recipe.RackSmokingRecipe::input, com.starmaylight.arcana_industry.recipe.RackSmokingRecipe::count,
                com.starmaylight.arcana_industry.recipe.RackSmokingRecipe::output,
                recipe -> Component.translatable("jei.arcana_industry.form_structure"), 4),
            new HandFedCategory<>(helper, CHANNEL_WASHING, "channel_washing", new ItemStack(MachineRegistry.TWIN_CHANNEL_WASHER_ITEM.get()),
                com.starmaylight.arcana_industry.recipe.ChannelWashingRecipe::input, com.starmaylight.arcana_industry.recipe.ChannelWashingRecipe::count,
                com.starmaylight.arcana_industry.recipe.ChannelWashingRecipe::output,
                recipe -> Component.translatable("jei.arcana_industry.form_structure"), 1),
            new HandFedCategory<>(helper, SCREENING, "screening", new ItemStack(MachineRegistry.ROTARY_SCREEN_ITEM.get()),
                com.starmaylight.arcana_industry.recipe.ScreeningRecipe::input, com.starmaylight.arcana_industry.recipe.ScreeningRecipe::count,
                com.starmaylight.arcana_industry.recipe.ScreeningRecipe::output,
                recipe -> Component.translatable("jei.arcana_industry.form_structure"), 2));
    }
    @Override public void registerRecipes(IRecipeRegistration registration) {
        var level = Minecraft.getInstance().level;
        if (level != null) registration.addRecipes(LUMEN_RITE, level.getRecipeManager().getAllRecipesFor(MachineRegistry.LUMEN_RITE.get()));
        if (level != null) registration.addRecipes(PEDESTAL_RITE, level.getRecipeManager().getAllRecipesFor(MachineRegistry.PEDESTAL_RITE.get()));
        if (level != null) registration.addRecipes(COLUMN_BLASTING, level.getRecipeManager().getAllRecipesFor(MachineRegistry.COLUMN_BLASTING.get()));
        if (level != null) registration.addRecipes(RACK_SMOKING, level.getRecipeManager().getAllRecipesFor(MachineRegistry.RACK_SMOKING.get()));
        if (level != null) registration.addRecipes(CHANNEL_WASHING, level.getRecipeManager().getAllRecipesFor(MachineRegistry.CHANNEL_WASHING.get()));
        if (level != null) registration.addRecipes(SCREENING, level.getRecipeManager().getAllRecipesFor(MachineRegistry.SCREENING.get()));
        if (level != null) registration.addRecipes(RESONATING, level.getRecipeManager().getAllRecipesFor(MachineRegistry.RESONATING.get()));
        if (level != null) registration.addRecipes(ETCHING, level.getRecipeManager().getAllRecipesFor(MachineRegistry.ETCHING.get()));
        if (level != null) registration.addRecipes(CONDENSING, level.getRecipeManager().getAllRecipesFor(MachineRegistry.CONDENSING.get()));
        if (level != null) registration.addRecipes(RELAYING, level.getRecipeManager().getAllRecipesFor(MachineRegistry.RELAYING.get()));
        if (level != null) registration.addRecipes(FUSING, level.getRecipeManager().getAllRecipesFor(MachineRegistry.FUSING.get()));
        if (level != null) registration.addRecipes(SEPARATION, level.getRecipeManager().getAllRecipesFor(MachineRegistry.SEPARATION.get()));
        if (level != null) registration.addRecipes(COMPOSITION, level.getRecipeManager().getAllRecipesFor(MachineRegistry.COMPOSITION.get()));
        if (level != null) registration.addRecipes(ANNEALING, level.getRecipeManager().getAllRecipesFor(MachineRegistry.ANNEALING.get()));
        if (level != null) registration.addRecipes(RECLAIMING, level.getRecipeManager().getAllRecipesFor(MachineRegistry.RECLAIMING.get()));
        if (level != null) registration.addRecipes(ELECTROLYSIS, level.getRecipeManager().getAllRecipesFor(MachineRegistry.ELECTROLYSIS.get()));
        if (level != null) registration.addRecipes(LEACHING, level.getRecipeManager().getAllRecipesFor(MachineRegistry.LEACHING.get()));
        if (level != null) registration.addRecipes(PRESSING, level.getRecipeManager().getAllRecipesFor(MachineRegistry.PRESSING.get()));
        if (level != null) registration.addRecipes(FIRING, level.getRecipeManager().getAllRecipesFor(MachineRegistry.FIRING.get()));
        if (level != null) registration.addRecipes(CRUSHING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.CRUSHING.get()).stream()
            .sorted(Comparator.comparing(CrushingRecipe::id)).toList());
        if (level != null) registration.addRecipes(SIEVING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.SIEVING.get()).stream()
            .sorted(Comparator.comparing(SievingRecipe::id)).toList());
        if (level != null) registration.addRecipes(QUERNING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.QUERNING.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.QuerningRecipe::id)).toList());
        if (level != null) registration.addRecipes(COMBUSTION, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.COMBUSTION.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.CombustionRecipe::id)).toList());
        if (level != null) registration.addRecipes(STEAM_RAISING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.STEAM_RAISING.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.SteamRaisingRecipe::id)).toList());
        if (level != null) registration.addRecipes(ASHING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.ASHING.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.AshingRecipe::id)).toList());
        if (level != null) registration.addRecipes(CARDING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.CARDING.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.CardingRecipe::id)).toList());
        if (level != null) registration.addRecipes(SAWING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.SAWING.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.SawingRecipe::id)).toList());
        if (level != null) registration.addRecipes(MOLDING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.MOLDING.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.MoldingRecipe::id)).toList());
        if (level != null) registration.addRecipes(MIXING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.MIXING.get()).stream()
            .sorted(Comparator.comparing(MixingRecipe::id)).toList());
        if (level != null) registration.addRecipes(QUENCHING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.QUENCHING.get()).stream()
            .sorted(Comparator.comparing(QuenchingRecipe::id)).toList());
        if (level != null) registration.addRecipes(CLAMPING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.CLAMPING.get()).stream()
            .sorted(Comparator.comparing(ClampingRecipe::id)).toList());
        if (level != null) registration.addRecipes(SEAMING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.SEAMING.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.SeamingRecipe::id)).toList());
        if (level != null) registration.addRecipes(SOAKING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.SOAKING.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.SoakingRecipe::id)).toList());
        if (level != null) registration.addRecipes(SWAGING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.SWAGING.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.SwagingRecipe::id)).toList());
        if (level != null) registration.addRecipes(CRIMPING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.CRIMPING.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.CrimpingRecipe::id)).toList());
        if (level != null) registration.addRecipes(BROACHING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.BROACHING.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.BroachingRecipe::id)).toList());
        if (level != null) registration.addRecipes(RIVETING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.RIVETING.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.RivetingRecipe::id)).toList());
        if (level != null) registration.addRecipes(FLANGING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.FLANGING.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.FlangingRecipe::id)).toList());
        if (level != null) registration.addRecipes(INDUCTION_WINDING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.INDUCTION_WINDING.get()).stream()
            .sorted(Comparator.comparing(InductionWindingRecipe::id)).toList());
        if (level != null) registration.addRecipes(CAPACITOR_CHARGING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.CAPACITOR_CHARGING.get()).stream()
            .sorted(Comparator.comparing(CapacitorChargingRecipe::id)).toList());
        if (level != null) registration.addRecipes(MAGNETIZATION, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.MAGNETIZATION.get()).stream()
            .sorted(Comparator.comparing(MagnetizationRecipe::id)).toList());
        if (level != null) registration.addRecipes(ARC_DISCHARGE, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.ARC_DISCHARGE.get()).stream()
            .sorted(Comparator.comparing(ArcDischargeRecipe::id)).toList());
        if (level != null) registration.addRecipes(FLUX_RECTIFICATION, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.FLUX_RECTIFICATION.get()).stream()
            .sorted(Comparator.comparing(FluxRectificationRecipe::id)).toList());
        if (level != null) registration.addRecipes(POLARITY_INVERSION, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.POLARITY_INVERSION.get()).stream()
            .sorted(Comparator.comparing(PolarityInversionRecipe::id)).toList());
        if (level != null) registration.addRecipes(DEGAUSSING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.DEGAUSSING.get()).stream()
            .sorted(Comparator.comparing(DegaussingRecipe::id)).toList());
        if (level != null) registration.addRecipes(ARMOR_DELAMINATION, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.ARMOR_DELAMINATION.get()).stream()
            .sorted(Comparator.comparing(ArmorDelaminationRecipe::id)).toList());
        if (level != null) registration.addRecipes(RESONANT_CULTIVATION, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.RESONANT_CULTIVATION.get()).stream()
            .sorted(Comparator.comparing(ResonantCultivationRecipe::id)).toList());
        if (level != null) registration.addRecipes(VERDANT_DISTILLATION, level.getRecipeManager().getAllRecipesFor(MachineRegistry.VERDANT_DISTILLATION.get()).stream().sorted(Comparator.comparing(VerdantDistillationRecipe::id)).toList());
        if (level != null) registration.addRecipes(CHROMATIC_GLAZING, level.getRecipeManager().getAllRecipesFor(MachineRegistry.CHROMATIC_GLAZING.get()).stream().sorted(Comparator.comparing(ChromaticGlazingRecipe::id)).toList());
        if (level != null) registration.addRecipes(PRISMATIC_RECLAMATION, level.getRecipeManager().getAllRecipesFor(MachineRegistry.PRISMATIC_RECLAMATION.get()).stream().sorted(Comparator.comparing(PrismaticReclamationRecipe::id)).toList());
        if (level != null) registration.addRecipes(TEXTILE_WEAVING, level.getRecipeManager().getAllRecipesFor(MachineRegistry.TEXTILE_WEAVING.get()).stream().sorted(Comparator.comparing(TextileWeavingRecipe::id)).toList());
        if (level != null) registration.addRecipes(HERALDIC_WEAVING, level.getRecipeManager().getAllRecipesFor(MachineRegistry.HERALDIC_WEAVING.get()).stream().sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.HeraldicWeavingRecipe::id)).toList());
        if (level != null) registration.addRecipes(RELIQUARY_BINDING, level.getRecipeManager().getAllRecipesFor(MachineRegistry.RELIQUARY_BINDING.get()).stream().sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.ReliquaryBindingRecipe::id)).toList());
        if (level != null) registration.addRecipes(CONCORDANCE, level.getRecipeManager().getAllRecipesFor(MachineRegistry.CONCORDANCE.get()).stream().sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.ConcordanceRecipe::id)).toList());
        if (level != null) registration.addRecipes(IRIDESCENT, level.getRecipeManager().getAllRecipesFor(MachineRegistry.IRIDESCENT.get()).stream().sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.IridescentRecipe::id)).toList());
        if (level != null) registration.addRecipes(SIGNAL_IMPRINTING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.SIGNAL_IMPRINTING.get()).stream()
            .sorted(Comparator.comparing(SignalImprintingRecipe::id)).toList());
        if (level != null) registration.addRecipes(RESONANCE_CUTTING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.RESONANCE_CUTTING.get()).stream()
            .sorted(Comparator.comparing(ResonanceCuttingRecipe::id)).toList());
        if (level != null) registration.addRecipes(MILLING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.MILLING.get()).stream()
            .sorted(Comparator.comparing(MillingRecipe::id)).toList());
        if (level != null) registration.addRecipes(SILVERING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.SILVERING.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.SilveringRecipe::id)).toList());
        if (level != null) registration.addRecipes(ENAMELING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.ENAMELING.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.EnamelingRecipe::id)).toList());
        if (level != null) registration.addRecipes(WIRE_DRAWING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.WIRE_DRAWING.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.WireDrawingRecipe::id)).toList());
        if (level != null) registration.addRecipes(CUPELLATION, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.CUPELLATION.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.CupellationRecipe::id)).toList());
        if (level != null) registration.addRecipes(FORGING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.FORGING.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.ForgingRecipe::id)).toList());
        if (level != null) registration.addRecipes(SLUICING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.SLUICING.get()).stream()
            .sorted(Comparator.comparing(SluicingRecipe::id)).toList());
        if (level != null) registration.addRecipes(COMPACTION, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.COMPACTION.get()).stream()
            .sorted(Comparator.comparing(CompactionRecipe::id)).toList());
        if (level != null) registration.addRecipes(HEARTH_SMOKING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.HEARTH_SMOKING.get()).stream()
            .sorted(Comparator.comparing(HearthSmokingRecipe::id)).toList());
        if (level != null) registration.addRecipes(FOUNDRY_SMELTING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.FOUNDRY_SMELTING.get()).stream()
            .sorted(Comparator.comparing(FoundrySmeltingRecipe::id)).toList());
        if (level != null) registration.addRecipes(SANCTUM_ATTUNEMENT, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.SANCTUM_ATTUNEMENT.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.SanctumAttunementRecipe::id)).toList());
        if (level != null) registration.addRecipes(COLLIMATION, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.COLLIMATION.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.CollimationRecipe::id)).toList());
        if (level != null) registration.addRecipes(CONTAINMENT, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.CONTAINMENT.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.ContainmentRecipe::id)).toList());
        if (level != null) registration.addRecipes(CHARRING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.CHARRING.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.CharringRecipe::id)).toList());
        if (level != null) registration.addRecipes(DRYING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.DRYING.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.DryingRecipe::id)).toList());
        if (level != null) registration.addRecipes(CLINKERING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.CLINKERING.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.ClinkeringRecipe::id)).toList());
        if (level != null) registration.addRecipes(STASIS_WEAVING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.STASIS_WEAVING.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.StasisRecipe::id)).toList());
        if (level != null) registration.addRecipes(LATTICE_ENGRAVING, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.LATTICE_ENGRAVING.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.EngravingRecipe::id)).toList());
        if (level != null) registration.addRecipes(LATTICE_LAMINATION, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.LATTICE_LAMINATION.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.LaminationRecipe::id)).toList());
        if (level != null) registration.addRecipes(COLLAPSE, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.COLLAPSE.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.CollapseRecipe::id)).toList());
        if (level != null) registration.addRecipes(SUBLIMATION, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.SUBLIMATION.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.SublimationRecipe::id)).toList());
        if (level != null) registration.addRecipes(DIFFUSION, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.DIFFUSION.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.DiffusionRecipe::id)).toList());
        if (level != null) registration.addRecipes(INVERSION, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.INVERSION.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.InversionRecipe::id)).toList());
        if (level != null) registration.addRecipes(MODULATION, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.MODULATION.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.ModulationRecipe::id)).toList());
        if (level != null) registration.addRecipes(STABILIZATION, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.STABILIZATION.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.StabilizationRecipe::id)).toList());
        if (level != null) registration.addRecipes(PRESERVATION, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.PRESERVATION.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.PreservationRecipe::id)).toList());
        if (level != null) registration.addRecipes(EFFUSION, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.EFFUSION.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.EffusionRecipe::id)).toList());
        if (level != null) registration.addRecipes(CONFLUENCE, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.CONFLUENCE.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.ConfluenceRecipe::id)).toList());
        if (level != null) registration.addRecipes(REVIVIFICATION, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.REVIVIFICATION.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.RevivificationRecipe::id)).toList());
        if (level != null) registration.addRecipes(REFRACTION, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.REFRACTION.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.RefractionRecipe::id)).toList());
        if (level != null) registration.addRecipes(FERMENTATION, level.getRecipeManager()
            .getAllRecipesFor(MachineRegistry.FERMENTATION.get()).stream()
            .sorted(Comparator.comparing(com.starmaylight.arcana_industry.recipe.FermentationRecipe::id)).toList());
        if (level != null) registration.addRecipes(SOLAR_SMELTING, level.getRecipeManager().getAllRecipesFor(MachineRegistry.SOLAR_SMELTING.get()));
        if (level != null) registration.addRecipes(PHOTOLYSIS, level.getRecipeManager().getAllRecipesFor(MachineRegistry.PHOTOLYSIS.get()));
        if (level != null) registration.addRecipes(GRINDING, level.getRecipeManager().getAllRecipesFor(MachineRegistry.GRINDING.get()));
        if (level != null) registration.addRecipes(FILTRATION, level.getRecipeManager().getAllRecipesFor(MachineRegistry.FILTRATION.get()));
        if (level != null) registration.addRecipes(DIFFRACTION, level.getRecipeManager().getAllRecipesFor(MachineRegistry.DIFFRACTION.get()));
        if (level != null) registration.addRecipes(SALVAGING, level.getRecipeManager().getAllRecipesFor(MachineRegistry.SALVAGING.get()));
        if (level != null) registration.addRecipes(SCRIBING, level.getRecipeManager().getAllRecipesFor(MachineRegistry.SCRIBING.get()));
        if (level != null) registration.addRecipes(WELDING, level.getRecipeManager().getAllRecipesFor(MachineRegistry.WELDING.get()));
        if (level != null) registration.addRecipes(BURNISHING, level.getRecipeManager().getAllRecipesFor(MachineRegistry.BURNISHING.get()));
        if (level != null) registration.addRecipes(SCORING, level.getRecipeManager().getAllRecipesFor(MachineRegistry.SCORING.get()));
        if (level != null) registration.addRecipes(TUMBLING, level.getRecipeManager().getAllRecipesFor(MachineRegistry.TUMBLING.get()));
        if (level != null) registration.addRecipes(POLISHING, level.getRecipeManager().getAllRecipesFor(MachineRegistry.POLISHING.get()));
        if (level != null) registration.addRecipes(FACETING, level.getRecipeManager().getAllRecipesFor(MachineRegistry.FACETING.get()));
        if (level != null) registration.addRecipes(EXTRUDING, level.getRecipeManager().getAllRecipesFor(MachineRegistry.EXTRUDING.get()));
        if (level != null) registration.addRecipes(HOT_PRESSING, level.getRecipeManager().getAllRecipesFor(MachineRegistry.HOT_PRESSING.get()));
        if (level != null) registration.addRecipes(STONE_TEMPERING, level.getRecipeManager().getAllRecipesFor(MachineRegistry.STONE_TEMPERING.get()));
        if (level != null) registration.addRecipes(INFUSION, level.getRecipeManager().getAllRecipesFor(MachineRegistry.INFUSION.get()));
        if (level != null) registration.addRecipes(COLLECTION, level.getRecipeManager().getAllRecipesFor(MachineRegistry.COLLECTION.get()));
        registerElectricalInfo(registration);
        registerEquipmentInfo(registration);
    }

    /** Equipment with no recipe category. Keys are literal so the packaged-asset audit can see them. */
    private static void registerEquipmentInfo(IRecipeRegistration registration) {
        registration.addIngredientInfo(new ItemStack(MachineRegistry.STRATA_EXCAVATOR_ITEM.get()), mezz.jei.api.constants.VanillaTypes.ITEM_STACK,
            infoPages("jei.arcana_industry.info.strata_excavator.page1", "jei.arcana_industry.info.strata_excavator.page2",
                "jei.arcana_industry.info.strata_excavator.page3"));
        registration.addIngredientInfo(new ItemStack(MachineRegistry.STOCK_HATCH_ITEM.get()), mezz.jei.api.constants.VanillaTypes.ITEM_STACK,
            infoPages("jei.arcana_industry.info.stock_hatch.page1", "jei.arcana_industry.info.stock_hatch.page2"));
    }

    private static Component[] infoPages(String... keys) {
        Component[] pages = new Component[keys.length];
        for (int page = 0; page < keys.length; page++) pages[page] = Component.translatable(keys[page]);
        return pages;
    }

    /** Information pages for cables and transformers; these parts have no recipe category of their own. */
    private static void registerElectricalInfo(IRecipeRegistration registration) {
        for (var cable : MachineRegistry.CABLES.values()) {
            var block = (com.starmaylight.arcana_industry.energy.CableBlock) cable.get();
            registration.addIngredientInfo(new ItemStack(block), mezz.jei.api.constants.VanillaTypes.ITEM_STACK,
                com.starmaylight.arcana_industry.energy.ElectricalInfo.conductorPage(block.spec()).toArray(Component[]::new));
        }
        for (var transformer : MachineRegistry.TRANSFORMERS.values()) {
            var block = (com.starmaylight.arcana_industry.energy.TransformerBlock) transformer.get();
            registration.addIngredientInfo(new ItemStack(block), mezz.jei.api.constants.VanillaTypes.ITEM_STACK,
                com.starmaylight.arcana_industry.energy.ElectricalInfo.transformerPage(block).toArray(Component[]::new));
        }
    }
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.ASTRAL_FONT_ITEM.get()), LUMEN_RITE);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.ASTRAL_FONT_ITEM.get()), PEDESTAL_RITE);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.BLAST_COLUMN_ITEM.get()), COLUMN_BLASTING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.SMOKE_RACK_ITEM.get()), RACK_SMOKING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.TWIN_CHANNEL_WASHER_ITEM.get()), CHANNEL_WASHING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.ROTARY_SCREEN_ITEM.get()), SCREENING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.CRUSHER_ITEM.get()), CRUSHING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.SIEVE_ITEM.get()), SIEVING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.QUERN_ITEM.get()), QUERNING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.ASH_BELLOWS_ITEM.get()), ASHING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.FIREBOX_DYNAMO_ITEM.get()), COMBUSTION);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.BOILER_TURBINE_ITEM.get()), STEAM_RAISING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.FIBER_CARDER_ITEM.get()), CARDING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.MOLD_CASTER_ITEM.get()), MOLDING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.SAWMILL_ITEM.get()), SAWING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.KILN_ITEM.get()), FIRING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.PRESS_ITEM.get()), PRESSING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.LEACHING_VAT_ITEM.get()), LEACHING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.ELECTROLYZER_ITEM.get()), ELECTROLYSIS);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.RESONANCE_INFUSER_ITEM.get()), RESONATING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.HELIOSTAT_ETCHER_ITEM.get()), ETCHING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.NOCTILUCENT_CONDENSER_ITEM.get()), CONDENSING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.ASTRAL_RELAY_ITEM.get()), RELAYING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.PRISM_SEPARATOR_ITEM.get()), SEPARATION);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.LATTICE_COMPOSER_ITEM.get()), COMPOSITION);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.PHASE_ANNEALER_ITEM.get()), ANNEALING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.PHASE_RECLAIMER_ITEM.get()), RECLAIMING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.REFRACTORY_MIXER_ITEM.get()), MIXING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.QUENCH_VAT_ITEM.get()), QUENCHING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.CLAMPING_PRESS_ITEM.get()), CLAMPING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.SEAM_PRESS_ITEM.get()), SEAMING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.SWAGE_PRESS_ITEM.get()), SWAGING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.CRIMP_PRESS_ITEM.get()), CRIMPING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.BROACH_PRESS_ITEM.get()), BROACHING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.HYDRAULIC_RIVETER_ITEM.get()), RIVETING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.FLANGE_PRESS_ITEM.get()), FLANGING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.SOAKING_HEARTH_ITEM.get()), SOAKING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.INDUCTION_COILER_ITEM.get()), INDUCTION_WINDING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.CAPACITOR_ARRAY_ITEM.get()), CAPACITOR_CHARGING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.MAGNETIZER_ITEM.get()), MAGNETIZATION);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.ARC_PRESS_ITEM.get()), ARC_DISCHARGE);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.FLUX_RECTIFIER_ITEM.get()), FLUX_RECTIFICATION);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.POLARITY_INVERTER_ITEM.get()), POLARITY_INVERSION);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.DEGAUSS_COIL_ITEM.get()), DEGAUSSING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.ARMOR_DELAMINATOR_ITEM.get()), ARMOR_DELAMINATION);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.SIGNAL_IMPRINTER_ITEM.get()), SIGNAL_IMPRINTING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.RESONANT_CULTIVATOR_ITEM.get()), RESONANT_CULTIVATION);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.VERDANT_DISTILLER_ITEM.get()), VERDANT_DISTILLATION);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.CHROMATIC_KILN_ITEM.get()), CHROMATIC_GLAZING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.PRISMATIC_PRESS_ITEM.get()), PRISMATIC_RECLAMATION);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.SPECTRAL_LOOM_ITEM.get()), TEXTILE_WEAVING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.HERALDIC_FRAME_ITEM.get()), HERALDIC_WEAVING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.RELIQUARY_STAND_ITEM.get()), RELIQUARY_BINDING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.CONCORDANCE_ALTAR_ITEM.get()), CONCORDANCE);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.IRIDESCENT_BASIN_ITEM.get()), IRIDESCENT);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.RESONANCE_BASIN_ITEM.get()), RESONANCE_CUTTING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.MILLING_GANTRY_ITEM.get()), MILLING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.SILVERING_BATH_ITEM.get()), SILVERING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.ENAMEL_KILN_ITEM.get()), ENAMELING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.DRAWING_BENCH_ITEM.get()), WIRE_DRAWING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.ASSAY_HEARTH_ITEM.get()), CUPELLATION);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.COAL_FORGE_ITEM.get()), FORGING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.ORE_SLUICE_ITEM.get()), SLUICING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.COMPACTION_SPIRE_ITEM.get()), COMPACTION);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.PROVISION_HEARTH_ITEM.get()), HEARTH_SMOKING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.FOUNDRY_RING_ITEM.get()), FOUNDRY_SMELTING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.STELLAR_CRUCIBLE_ITEM.get()), FUSING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.SANCTUM_PYLON_ITEM.get()), SANCTUM_ATTUNEMENT);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.LENS_COLLIMATOR_ITEM.get()), COLLIMATION);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.CONTAINMENT_FORGE_ITEM.get()), CONTAINMENT);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.CHAR_RETORT_ITEM.get()), CHARRING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.EMBER_DRYER_ITEM.get()), DRYING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.CLINKER_OVEN_ITEM.get()), CLINKERING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.STASIS_WEAVER_ITEM.get()), STASIS_WEAVING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.LATTICE_ENGRAVER_ITEM.get()), LATTICE_ENGRAVING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.LATTICE_LAMINATOR_ITEM.get()), LATTICE_LAMINATION);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.PHASE_COLLAPSER_ITEM.get()), COLLAPSE);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.PHASE_SUBLIMATOR_ITEM.get()), SUBLIMATION);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.PHASE_DIFFUSER_ITEM.get()), DIFFUSION);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.PHASE_INVERTER_ITEM.get()), INVERSION);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.PHASE_MODULATOR_ITEM.get()), MODULATION);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.PHASE_STABILIZER_ITEM.get()), STABILIZATION);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.PHASE_PRESERVER_ITEM.get()), PRESERVATION);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.PHASE_EFFUSER_ITEM.get()), EFFUSION);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.PHASE_CONFLUX_ITEM.get()), CONFLUENCE);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.PHASE_REVIVIFIER_ITEM.get()), REVIVIFICATION);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.PHASE_REFRACTOR_ITEM.get()), REFRACTION);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.PHASE_FERMENTER_ITEM.get()), FERMENTATION);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.SOLAR_SMELTER_ITEM.get()), SOLAR_SMELTING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.SOLAR_PHOTOLYZER_ITEM.get()), PHOTOLYSIS);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.LENS_GRINDER_ITEM.get()), GRINDING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.SPECTRAL_FILTER_ITEM.get()), FILTRATION);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.DIFFRACTION_GRATE_ITEM.get()), DIFFRACTION);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.PRISM_SCORER_ITEM.get()), SCORING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.PRISM_BURNISHER_ITEM.get()), BURNISHING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.LATTICE_RECLAIMER_ITEM.get()), SALVAGING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.LATTICE_SCRIBER_ITEM.get()), SCRIBING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.LATTICE_WELDER_ITEM.get()), WELDING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.PRISM_LAPIDARY_ITEM.get()), FACETING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.PRISM_POLISHER_ITEM.get()), POLISHING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.PRISM_TUMBLER_ITEM.get()), TUMBLING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.CLAY_EXTRUDER_ITEM.get()), EXTRUDING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.COPPER_HOT_PRESS_ITEM.get()), HOT_PRESSING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.BASALT_TEMPERER_ITEM.get()), STONE_TEMPERING);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.LATTICE_INFUSER_ITEM.get()), INFUSION);
        registration.addRecipeCatalyst(new ItemStack(MachineRegistry.CORONA_COLLECTOR_ITEM.get()), COLLECTION);
    }
    @Override public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(CrusherScreen.class, 70, 24, 90, 9, CRUSHING);
        registration.addRecipeClickArea(SieveScreen.class, 70, 24, 90, 9, SIEVING);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.QuernScreen.class, 70, 24, 90, 9, QUERNING);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.AshBellowsScreen.class, 70, 24, 90, 9, ASHING);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.FiberCarderScreen.class, 70, 24, 90, 9, CARDING);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.SawmillScreen.class, 70, 24, 90, 9, SAWING);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.MoldCasterScreen.class, 70, 24, 90, 9, MOLDING);
        registration.addRecipeClickArea(KilnScreen.class, 70, 24, 90, 9, FIRING);
        registration.addRecipeClickArea(PressScreen.class, 70, 24, 90, 9, PRESSING);
        registration.addRecipeClickArea(LeachingVatScreen.class, 70, 24, 90, 9, LEACHING);
        registration.addRecipeClickArea(ElectrolyzerScreen.class, 70, 24, 90, 9, ELECTROLYSIS);
        registration.addRecipeClickArea(ResonanceInfuserScreen.class, 70, 24, 90, 9, RESONATING);
        registration.addRecipeClickArea(HeliostatEtcherScreen.class, 70, 24, 90, 9, ETCHING);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.NoctilucentCondenserScreen.class, 70, 24, 90, 9, CONDENSING);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.AstralRelayScreen.class, 70, 24, 90, 9, RELAYING);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.StellarCrucibleScreen.class, 70, 24, 90, 9, FUSING);
        registration.addRecipeClickArea(PrismSeparatorScreen.class, 70, 24, 90, 9, SEPARATION);
        registration.addRecipeClickArea(LatticeComposerScreen.class, 70, 24, 90, 9, COMPOSITION);
        registration.addRecipeClickArea(PhaseAnnealerScreen.class, 70, 24, 90, 9, ANNEALING);
        registration.addRecipeClickArea(PhaseReclaimerScreen.class, 70, 24, 90, 9, RECLAIMING);
        registration.addRecipeClickArea(RefractoryMixerScreen.class, 95, 24, 65, 9, MIXING);
        registration.addRecipeClickArea(QuenchVatScreen.class, 95, 24, 65, 9, QUENCHING);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.LensCollimatorScreen.class, 70, 24, 90, 9, COLLIMATION);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.ContainmentForgeScreen.class, 70, 24, 90, 9, CONTAINMENT);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.CharRetortScreen.class, 70, 24, 90, 9, CHARRING);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.EmberDryerScreen.class, 70, 24, 90, 9, DRYING);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.ClinkerOvenScreen.class, 70, 24, 90, 9, CLINKERING);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.StasisWeaverScreen.class, 70, 24, 90, 9, STASIS_WEAVING);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.LatticeEngraverScreen.class, 70, 24, 90, 9, LATTICE_ENGRAVING);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.LatticeLaminatorScreen.class, 70, 24, 90, 9, LATTICE_LAMINATION);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.PhaseCollapserScreen.class, 70, 24, 90, 9, COLLAPSE);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.PhaseSublimatorScreen.class, 70, 24, 90, 9, SUBLIMATION);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.PhaseDiffuserScreen.class, 70, 24, 90, 9, DIFFUSION);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.PhaseInverterScreen.class, 70, 24, 90, 9, INVERSION);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.PhaseModulatorScreen.class, 70, 24, 90, 9, MODULATION);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.PhaseStabilizerScreen.class, 70, 24, 90, 9, STABILIZATION);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.PhasePreserverScreen.class, 70, 24, 90, 9, PRESERVATION);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.PhaseEffuserScreen.class, 70, 24, 90, 9, EFFUSION);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.PhaseConfluxScreen.class, 70, 24, 90, 9, CONFLUENCE);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.PhaseRevivifierScreen.class, 70, 24, 90, 9, REVIVIFICATION);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.PhaseRefractorScreen.class, 70, 24, 90, 9, REFRACTION);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.PhaseFermenterScreen.class, 70, 24, 90, 9, FERMENTATION);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.SolarSmelterScreen.class, 70, 24, 90, 9, SOLAR_SMELTING);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.SolarPhotolyzerScreen.class, 70, 24, 90, 9, PHOTOLYSIS);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.LensGrinderScreen.class, 70, 24, 90, 9, GRINDING);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.SpectralFilterScreen.class, 70, 24, 90, 9, FILTRATION);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.CoronaCollectorScreen.class, 70, 24, 90, 9, COLLECTION);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.DiffractionGrateScreen.class, 70, 24, 90, 9, DIFFRACTION);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.PrismScorerScreen.class, 70, 24, 90, 9, SCORING);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.PrismBurnisherScreen.class, 70, 24, 90, 9, BURNISHING);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.LatticeReclaimerScreen.class, 70, 24, 90, 9, SALVAGING);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.LatticeScriberScreen.class, 70, 24, 90, 9, SCRIBING);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.LatticeWelderScreen.class, 70, 24, 90, 9, WELDING);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.PrismLapidaryScreen.class, 70, 24, 90, 9, FACETING);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.PrismPolisherScreen.class, 70, 24, 90, 9, POLISHING);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.PrismTumblerScreen.class, 70, 24, 90, 9, TUMBLING);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.ClayExtruderScreen.class, 70, 24, 90, 9, EXTRUDING);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.CopperHotPressScreen.class, 70, 24, 90, 9, HOT_PRESSING);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.LatticeInfuserScreen.class, 70, 24, 90, 9, INFUSION);
        registration.addRecipeClickArea(com.starmaylight.arcana_industry.client.BasaltTempererScreen.class, 70, 24, 90, 9, STONE_TEMPERING);
    }
}
