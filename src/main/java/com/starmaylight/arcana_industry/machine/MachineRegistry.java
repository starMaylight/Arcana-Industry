package com.starmaylight.arcana_industry.machine;

import com.starmaylight.arcana_industry.Arcana_industry;
import com.starmaylight.arcana_industry.recipe.CrushingRecipe;
import com.starmaylight.arcana_industry.recipe.PressingRecipe;
import com.starmaylight.arcana_industry.recipe.LeachingRecipe;
import com.starmaylight.arcana_industry.recipe.SluicingRecipe;
import com.starmaylight.arcana_industry.thermal.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.*;

public final class MachineRegistry {
    private static final String MOD = Arcana_industry.MODID;
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD);
    public static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MOD);
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MOD);
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, MOD);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD);
    public static final DeferredRegister<net.minecraft.world.inventory.MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, MOD);
    public static final RegistryObject<Block> ASTRAL_FONT = BLOCKS.register("astral_font", com.starmaylight.arcana_industry.magic.AstralFontBlock::new);
    public static final RegistryObject<Item> ASTRAL_FONT_ITEM = ITEMS.register("astral_font", () -> new BlockItem(ASTRAL_FONT.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<com.starmaylight.arcana_industry.magic.AstralFontBlockEntity>> ASTRAL_FONT_ENTITY = ENTITIES.register("astral_font",
        () -> BlockEntityType.Builder.of(com.starmaylight.arcana_industry.magic.AstralFontBlockEntity::new, ASTRAL_FONT.get()).build(null));
    public static final RegistryObject<Item> ASTRAL_WAND = ITEMS.register("astral_wand", com.starmaylight.arcana_industry.magic.AstralWandItem::new);
    /** Track length in ticks; printed by tools/generate_music_disc.py and must match the generated OGG. */
    public static final int LATTICE_HYMN_TICKS = 2860;
    /** Dropped by a Static Creeper killed by a Flux Archer; a jukebox comparator reads 9. */
    public static final RegistryObject<Item> MUSIC_DISC_LATTICE_HYMN = ITEMS.register("music_disc_lattice_hymn",
        () -> new net.minecraft.world.item.RecordItem(9, com.starmaylight.arcana_industry.sound.ModSounds.MUSIC_DISC_LATTICE_HYMN,
            new Item.Properties().stacksTo(1).rarity(net.minecraft.world.item.Rarity.RARE), LATTICE_HYMN_TICKS));
    /** Track length in ticks; printed by tools/generate_music_disc_waltz.py and must match the generated OGG. */
    public static final int CORRODED_WALTZ_TICKS = 1636;
    /** Dropped by a Rust Crawler killed by a Static Creeper explosion; a jukebox comparator reads 10. */
    public static final RegistryObject<Item> MUSIC_DISC_CORRODED_WALTZ = ITEMS.register("music_disc_corroded_waltz",
        () -> new net.minecraft.world.item.RecordItem(10, com.starmaylight.arcana_industry.sound.ModSounds.MUSIC_DISC_CORRODED_WALTZ,
            new Item.Properties().stacksTo(1).rarity(net.minecraft.world.item.Rarity.RARE), CORRODED_WALTZ_TICKS));
    /** Ore Mite blast drop; generated length is printed by tools/generate_music_disc_vein_echo.py. */
    public static final int VEIN_ECHO_TICKS = 1626;
    public static final RegistryObject<Item> MUSIC_DISC_VEIN_ECHO = ITEMS.register("music_disc_vein_echo",
        () -> new net.minecraft.world.item.RecordItem(11, com.starmaylight.arcana_industry.sound.ModSounds.MUSIC_DISC_VEIN_ECHO,
            new Item.Properties().stacksTo(1).rarity(net.minecraft.world.item.Rarity.RARE), VEIN_ECHO_TICKS));
    public static final RegistryObject<Item> NIGHT_SIGIL = ITEMS.register("night_sigil", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TEMPER_GLYPH = ITEMS.register("temper_glyph", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BRINE_WARD = ITEMS.register("brine_ward", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ION_SEAL = ITEMS.register("ion_seal", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RESONANT_RUNE = ITEMS.register("resonant_rune", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DAWN_EMBLEM = ITEMS.register("dawn_emblem", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PRISM_MANDALA = ITEMS.register("prism_mandala", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CORE_HALO = ITEMS.register("core_halo", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PHASE_SIGIL = ITEMS.register("phase_sigil", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PHASE_HEART = ITEMS.register("phase_heart", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PHASE_SCEPTER = ITEMS.register("phase_scepter", com.starmaylight.arcana_industry.magic.PhaseScepterItem::new);
    public static final RegistryObject<Item> FLUX_TOTEM = ITEMS.register("flux_totem", com.starmaylight.arcana_industry.magic.FluxTotemItem::new);
    public static final RegistryObject<Item> FLUX_AEGIS = ITEMS.register("flux_aegis", com.starmaylight.arcana_industry.magic.FluxAegisItem::new);
    public static final RegistryObject<Item> FLUX_LANCET = ITEMS.register("flux_lancet", com.starmaylight.arcana_industry.magic.FluxLancetItem::new);
    /** Portable lumen tiers: t1 phial, t5 flask, t9 vessel; each recipe consumes the previous container. */
    public static final RegistryObject<Item> LUMEN_PHIAL = ITEMS.register("lumen_phial",
        () -> new com.starmaylight.arcana_industry.magic.LumenVesselItem(400, net.minecraft.world.item.Rarity.COMMON));
    public static final RegistryObject<Item> STARLIT_FLASK = ITEMS.register("starlit_flask",
        () -> new com.starmaylight.arcana_industry.magic.LumenVesselItem(1500, net.minecraft.world.item.Rarity.UNCOMMON));
    public static final RegistryObject<Item> PHASE_VESSEL = ITEMS.register("phase_vessel",
        () -> new com.starmaylight.arcana_industry.magic.LumenVesselItem(5000, net.minecraft.world.item.Rarity.EPIC));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.LumenRiteRecipe>> LUMEN_RITE = TYPES.register("lumen_rite", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":lumen_rite"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.LumenRiteRecipe>> LUMEN_RITE_SERIALIZER = SERIALIZERS.register("lumen_rite", com.starmaylight.arcana_industry.recipe.LumenRiteRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.PedestalRiteRecipe>> PEDESTAL_RITE = TYPES.register("pedestal_rite", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":pedestal_rite"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.PedestalRiteRecipe>> PEDESTAL_RITE_SERIALIZER = SERIALIZERS.register("pedestal_rite", com.starmaylight.arcana_industry.recipe.PedestalRiteRecipe.Serializer::new);
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<CrusherMenu>> CRUSHER_MENU = MENUS.register("crusher",
        () -> new net.minecraft.world.inventory.MenuType<>(CrusherMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<Block> CRUSHER = BLOCKS.register("crusher", CrusherBlock::new);
    public static final RegistryObject<Item> CRUSHER_ITEM = ITEMS.register("crusher", () -> new BlockItem(CRUSHER.get(), new Item.Properties()));
    public static final RegistryObject<Item> FIELD_GUIDE = ITEMS.register("field_guide", com.starmaylight.arcana_industry.guide.FieldGuideItem::new);
    public static final RegistryObject<Item> STRUCTURE_PROJECTOR = ITEMS.register("structure_projector", com.starmaylight.arcana_industry.multiblock.StructureProjectorItem::new);
    public static final RegistryObject<Item> IRON_GRIT = ITEMS.register("iron_grit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COPPER_GRIT = ITEMS.register("copper_grit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SCREENED_FLUX = ITEMS.register("screened_flux", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GOLD_GRIT = ITEMS.register("gold_grit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIN_GRIT = ITEMS.register("tin_grit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LEAD_GRIT = ITEMS.register("lead_grit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ZINC_GRIT = ITEMS.register("zinc_grit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> NICKEL_GRIT = ITEMS.register("nickel_grit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SILVER_GRIT = ITEMS.register("silver_grit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COBALT_GRIT = ITEMS.register("cobalt_grit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PLATINUM_GRIT = ITEMS.register("platinum_grit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> IRIDIUM_GRIT = ITEMS.register("iridium_grit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ALUMINUM_GRIT = ITEMS.register("aluminum_grit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TUNGSTEN_GRIT = ITEMS.register("tungsten_grit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BISMUTH_GRIT = ITEMS.register("bismuth_grit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MANGANESE_GRIT = ITEMS.register("manganese_grit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ANTIMONY_GRIT = ITEMS.register("antimony_grit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CHROMIUM_GRIT = ITEMS.register("chromium_grit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TITANIUM_GRIT = ITEMS.register("titanium_grit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MOLYBDENUM_GRIT = ITEMS.register("molybdenum_grit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> VANADIUM_GRIT = ITEMS.register("vanadium_grit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> NIOBIUM_GRIT = ITEMS.register("niobium_grit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TANTALUM_GRIT = ITEMS.register("tantalum_grit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ZIRCONIUM_GRIT = ITEMS.register("zirconium_grit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> OSMIUM_GRIT = ITEMS.register("osmium_grit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PALLADIUM_GRIT = ITEMS.register("palladium_grit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RUTHENIUM_GRIT = ITEMS.register("ruthenium_grit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RHENIUM_GRIT = ITEMS.register("rhenium_grit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RHODIUM_GRIT = ITEMS.register("rhodium_grit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> HAFNIUM_GRIT = ITEMS.register("hafnium_grit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> INDIUM_GRIT = ITEMS.register("indium_grit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GALLIUM_GRIT = ITEMS.register("gallium_grit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> THALLIUM_GRIT = ITEMS.register("thallium_grit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GERMANIUM_GRIT = ITEMS.register("germanium_grit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<BlockEntityType<CrusherBlockEntity>> CRUSHER_ENTITY = ENTITIES.register("crusher",
        () -> BlockEntityType.Builder.of(CrusherBlockEntity::new, CRUSHER.get()).build(null));
    public static final RegistryObject<RecipeType<CrushingRecipe>> CRUSHING = TYPES.register("crushing", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":crushing"; }
    });
    public static final RegistryObject<RecipeSerializer<CrushingRecipe>> CRUSHING_SERIALIZER = SERIALIZERS.register("crushing", CrushingRecipe.Serializer::new);
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<SieveMenu>> SIEVE_MENU = MENUS.register("sieve",
        () -> new net.minecraft.world.inventory.MenuType<>(SieveMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<Block> SIEVE = BLOCKS.register("sieve", SieveBlock::new);
    public static final RegistryObject<Item> SIEVE_ITEM = ITEMS.register("sieve", () -> new BlockItem(SIEVE.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<SieveBlockEntity>> SIEVE_ENTITY = ENTITIES.register("sieve",
        () -> BlockEntityType.Builder.of(SieveBlockEntity::new, SIEVE.get()).build(null));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.SievingRecipe>> SIEVING = TYPES.register("sieving", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":sieving"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.SievingRecipe>> SIEVING_SERIALIZER = SERIALIZERS.register("sieving", com.starmaylight.arcana_industry.recipe.SievingRecipe.Serializer::new);
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<QuernMenu>> QUERN_MENU = MENUS.register("quern",
        () -> new net.minecraft.world.inventory.MenuType<>(QuernMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<Block> QUERN = BLOCKS.register("quern", QuernBlock::new);
    public static final RegistryObject<Item> QUERN_ITEM = ITEMS.register("quern", () -> new BlockItem(QUERN.get(), new Item.Properties()));
    public static final RegistryObject<Item> GROUND_MEAL = ITEMS.register("ground_meal", () -> new Item(new Item.Properties()));
    public static final RegistryObject<BlockEntityType<QuernBlockEntity>> QUERN_ENTITY = ENTITIES.register("quern",
        () -> BlockEntityType.Builder.of(QuernBlockEntity::new, QUERN.get()).build(null));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.QuerningRecipe>> QUERNING = TYPES.register("querning", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":querning"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.QuerningRecipe>> QUERNING_SERIALIZER = SERIALIZERS.register("querning", com.starmaylight.arcana_industry.recipe.QuerningRecipe.Serializer::new);
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<AshBellowsMenu>> ASH_BELLOWS_MENU = MENUS.register("ash_bellows",
        () -> new net.minecraft.world.inventory.MenuType<>(AshBellowsMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<Block> ASH_BELLOWS = BLOCKS.register("ash_bellows", AshBellowsBlock::new);
    public static final RegistryObject<Item> ASH_BELLOWS_ITEM = ITEMS.register("ash_bellows", () -> new BlockItem(ASH_BELLOWS.get(), new Item.Properties()));
    public static final RegistryObject<Item> TEMPER_ASH = ITEMS.register("temper_ash", () -> new Item(new Item.Properties()));
    public static final RegistryObject<BlockEntityType<AshBellowsBlockEntity>> ASH_BELLOWS_ENTITY = ENTITIES.register("ash_bellows",
        () -> BlockEntityType.Builder.of(AshBellowsBlockEntity::new, ASH_BELLOWS.get()).build(null));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.AshingRecipe>> ASHING = TYPES.register("ashing", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":ashing"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.AshingRecipe>> ASHING_SERIALIZER = SERIALIZERS.register("ashing", com.starmaylight.arcana_industry.recipe.AshingRecipe.Serializer::new);
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<SawmillMenu>> SAWMILL_MENU = MENUS.register("sawmill",
        () -> new net.minecraft.world.inventory.MenuType<>(SawmillMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<Block> SAWMILL = BLOCKS.register("sawmill", SawmillBlock::new);
    public static final RegistryObject<Item> SAWMILL_ITEM = ITEMS.register("sawmill", () -> new BlockItem(SAWMILL.get(), new Item.Properties()));
    public static final RegistryObject<Item> MILLED_DOWEL = ITEMS.register("milled_dowel", () -> new Item(new Item.Properties()));
    public static final RegistryObject<BlockEntityType<SawmillBlockEntity>> SAWMILL_ENTITY = ENTITIES.register("sawmill",
        () -> BlockEntityType.Builder.of(SawmillBlockEntity::new, SAWMILL.get()).build(null));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.SawingRecipe>> SAWING = TYPES.register("sawing", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":sawing"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.SawingRecipe>> SAWING_SERIALIZER = SERIALIZERS.register("sawing", com.starmaylight.arcana_industry.recipe.SawingRecipe.Serializer::new);
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<FiberCarderMenu>> FIBER_CARDER_MENU = MENUS.register("fiber_carder",
        () -> new net.minecraft.world.inventory.MenuType<>(FiberCarderMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<Block> FIBER_CARDER = BLOCKS.register("fiber_carder", FiberCarderBlock::new);
    public static final RegistryObject<Item> FIBER_CARDER_ITEM = ITEMS.register("fiber_carder", () -> new BlockItem(FIBER_CARDER.get(), new Item.Properties()));
    public static final RegistryObject<Item> CARDED_FIBER = ITEMS.register("carded_fiber", () -> new Item(new Item.Properties()));
    public static final RegistryObject<BlockEntityType<FiberCarderBlockEntity>> FIBER_CARDER_ENTITY = ENTITIES.register("fiber_carder",
        () -> BlockEntityType.Builder.of(FiberCarderBlockEntity::new, FIBER_CARDER.get()).build(null));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.CardingRecipe>> CARDING = TYPES.register("carding", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":carding"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.CardingRecipe>> CARDING_SERIALIZER = SERIALIZERS.register("carding", com.starmaylight.arcana_industry.recipe.CardingRecipe.Serializer::new);
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<MoldCasterMenu>> MOLD_CASTER_MENU = MENUS.register("mold_caster",
        () -> new net.minecraft.world.inventory.MenuType<>(MoldCasterMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<Block> MOLD_CASTER = BLOCKS.register("mold_caster", MoldCasterBlock::new);
    public static final RegistryObject<Item> MOLD_CASTER_ITEM = ITEMS.register("mold_caster", () -> new BlockItem(MOLD_CASTER.get(), new Item.Properties()));
    public static final RegistryObject<Item> CAST_FITTING = ITEMS.register("cast_fitting", () -> new Item(new Item.Properties()));
    public static final RegistryObject<BlockEntityType<MoldCasterBlockEntity>> MOLD_CASTER_ENTITY = ENTITIES.register("mold_caster",
        () -> BlockEntityType.Builder.of(MoldCasterBlockEntity::new, MOLD_CASTER.get()).build(null));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.MoldingRecipe>> MOLDING = TYPES.register("molding", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":molding"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.MoldingRecipe>> MOLDING_SERIALIZER = SERIALIZERS.register("molding", com.starmaylight.arcana_industry.recipe.MoldingRecipe.Serializer::new);
    public static final RegistryObject<Block> KILN = BLOCKS.register("kiln", KilnBlock::new);
    public static final RegistryObject<Item> KILN_ITEM = ITEMS.register("kiln", () -> new BlockItem(KILN.get(), new Item.Properties()));
    public static final RegistryObject<Item> ANNEALED_COPPER = ITEMS.register("annealed_copper", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> KILN_HEATING_COIL = ITEMS.register("kiln_heating_coil", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> STEEL_BILLET = ITEMS.register("steel_billet", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CARBURIZED_IRON_CHARGE = ITEMS.register("carburized_iron_charge", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GALVANIZING_CHARGE = ITEMS.register("galvanizing_charge", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GALVANIZED_STEEL_BILLET = ITEMS.register("galvanized_steel_billet", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STAINLESS_VESSEL_CHARGE = ITEMS.register("stainless_vessel_charge", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STAINLESS_VESSEL_STOCK = ITEMS.register("stainless_vessel_stock", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TOOL_STEEL_CHARGE = ITEMS.register("tool_steel_charge", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TOOL_STEEL_STOCK = ITEMS.register("tool_steel_stock", () -> new Item(new Item.Properties()));
    // Rolling bearing (RP-B01): high-carbon chromium bearing steel, swaged races hardened by quenching, pressed balls.
    public static final RegistryObject<Item> BEARING_STEEL_CHARGE = ITEMS.register("bearing_steel_charge", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BEARING_STEEL_STOCK = ITEMS.register("bearing_steel_stock", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BEARING_RACE_BLANK = ITEMS.register("bearing_race_blank", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> HARDENED_BEARING_RACE = ITEMS.register("hardened_bearing_race", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BEARING_BALL = ITEMS.register("bearing_ball", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DEEP_GROOVE_BALL_BEARING = ITEMS.register("deep_groove_ball_bearing", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PLUMMER_BLOCK_HOUSING = ITEMS.register("plummer_block_housing", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CHROMIUM_THERMITE_CHARGE = ITEMS.register("chromium_thermite_charge", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> VANADIUM_THERMITE_CHARGE = ITEMS.register("vanadium_thermite_charge", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TUNGSTEN_CARBON_CHARGE = ITEMS.register("tungsten_carbon_charge", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TITANIUM_CHLORIDE_CHARGE = ITEMS.register("titanium_chloride_charge", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ZIRCONIUM_FLUX_CHARGE = ITEMS.register("zirconium_flux_charge", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> NIOBIUM_OXIDE_CAKE = ITEMS.register("niobium_oxide_cake", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TANTALUM_FLUORIDE_CONCENTRATE = ITEMS.register("tantalum_fluoride_concentrate", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SEALED_STEEL_PLATE = ITEMS.register("sealed_steel_plate", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GRADED_FILTER_MEDIA = ITEMS.register("graded_filter_media", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> FRAMED_FILTER_STRAINER = ITEMS.register("framed_filter_strainer", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> VALVE_BLANK = ITEMS.register("valve_blank", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COPPER_FLOW_VALVE = ITEMS.register("copper_flow_valve", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> HARMONIC_LENS = ITEMS.register("harmonic_lens", () -> new Item(new Item.Properties()));
    public static final RegistryObject<BlockEntityType<KilnBlockEntity>> KILN_ENTITY = ENTITIES.register("kiln",
        () -> BlockEntityType.Builder.of(KilnBlockEntity::new, KILN.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<KilnMenu>> KILN_MENU = MENUS.register("kiln",
        () -> new net.minecraft.world.inventory.MenuType<>(KilnMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<FiringRecipe>> FIRING = TYPES.register("firing", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":firing"; }
    });
    public static final RegistryObject<RecipeSerializer<FiringRecipe>> FIRING_SERIALIZER = SERIALIZERS.register("firing", FiringRecipe.Serializer::new);
    public static final RegistryObject<Block> PRESS = BLOCKS.register("press", PressBlock::new);
    public static final RegistryObject<Item> PRESS_ITEM = ITEMS.register("press", () -> new BlockItem(PRESS.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<PressBlockEntity>> PRESS_ENTITY = ENTITIES.register("press",
        () -> BlockEntityType.Builder.of(PressBlockEntity::new, PRESS.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<PressMenu>> PRESS_MENU = MENUS.register("press",
        () -> new net.minecraft.world.inventory.MenuType<>(PressMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<PressingRecipe>> PRESSING = TYPES.register("pressing", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":pressing"; }
    });
    public static final RegistryObject<RecipeSerializer<PressingRecipe>> PRESSING_SERIALIZER = SERIALIZERS.register("pressing", PressingRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.CompactionRecipe>> COMPACTION = TYPES.register("compaction", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":compaction"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.CompactionRecipe>> COMPACTION_SERIALIZER = SERIALIZERS.register("compaction", com.starmaylight.arcana_industry.recipe.CompactionRecipe.Serializer::new);
    public static final RegistryObject<Item> COMPACTED_ELECTRODE = ITEMS.register("compacted_electrode", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> LEACHING_VAT = BLOCKS.register("leaching_vat", LeachingVatBlock::new);
    public static final RegistryObject<Item> LEACHING_VAT_ITEM = ITEMS.register("leaching_vat", () -> new BlockItem(LEACHING_VAT.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<LeachingVatBlockEntity>> LEACHING_VAT_ENTITY = ENTITIES.register("leaching_vat",
        () -> BlockEntityType.Builder.of(LeachingVatBlockEntity::new, LEACHING_VAT.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<LeachingVatMenu>> LEACHING_VAT_MENU = MENUS.register("leaching_vat",
        () -> new net.minecraft.world.inventory.MenuType<>(LeachingVatMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<LeachingRecipe>> LEACHING = TYPES.register("leaching", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":leaching"; }
    });
    public static final RegistryObject<RecipeSerializer<LeachingRecipe>> LEACHING_SERIALIZER = SERIALIZERS.register("leaching", LeachingRecipe.Serializer::new);
    public static final RegistryObject<Item> PURIFIED_SALTS = ITEMS.register("purified_salts", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CONDUCTIVE_CRYSTAL = ITEMS.register("conductive_crystal", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> ELECTROLYZER = BLOCKS.register("electrolyzer", ElectrolyzerBlock::new);
    public static final RegistryObject<Item> ELECTROLYZER_ITEM = ITEMS.register("electrolyzer", () -> new BlockItem(ELECTROLYZER.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<ElectrolyzerBlockEntity>> ELECTROLYZER_ENTITY = ENTITIES.register("electrolyzer",
        () -> BlockEntityType.Builder.of(ElectrolyzerBlockEntity::new, ELECTROLYZER.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<ElectrolyzerMenu>> ELECTROLYZER_MENU = MENUS.register("electrolyzer",
        () -> new net.minecraft.world.inventory.MenuType<>(ElectrolyzerMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.ElectrolysisRecipe>> ELECTROLYSIS = TYPES.register("electrolysis", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":electrolysis"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.ElectrolysisRecipe>> ELECTROLYSIS_SERIALIZER = SERIALIZERS.register("electrolysis", com.starmaylight.arcana_industry.recipe.ElectrolysisRecipe.Serializer::new);
    public static final RegistryObject<Item> ATTUNED_QUARTZ = ITEMS.register("attuned_quartz", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> RESONANCE_INFUSER = BLOCKS.register("resonance_infuser", ResonanceInfuserBlock::new);
    public static final RegistryObject<Item> RESONANCE_INFUSER_ITEM = ITEMS.register("resonance_infuser", () -> new BlockItem(RESONANCE_INFUSER.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<ResonanceInfuserBlockEntity>> RESONANCE_INFUSER_ENTITY = ENTITIES.register("resonance_infuser",
        () -> BlockEntityType.Builder.of(ResonanceInfuserBlockEntity::new, RESONANCE_INFUSER.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<ResonanceInfuserMenu>> RESONANCE_INFUSER_MENU = MENUS.register("resonance_infuser",
        () -> new net.minecraft.world.inventory.MenuType<>(ResonanceInfuserMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.ResonatingRecipe>> RESONATING = TYPES.register("resonating", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":resonating"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.ResonatingRecipe>> RESONATING_SERIALIZER = SERIALIZERS.register("resonating", com.starmaylight.arcana_industry.recipe.ResonatingRecipe.Serializer::new);
    public static final RegistryObject<Item> SOLAR_PRISM = ITEMS.register("solar_prism", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> HELIOSTAT_ETCHER = BLOCKS.register("heliostat_etcher", HeliostatEtcherBlock::new);
    public static final RegistryObject<Item> HELIOSTAT_ETCHER_ITEM = ITEMS.register("heliostat_etcher", () -> new BlockItem(HELIOSTAT_ETCHER.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<HeliostatEtcherBlockEntity>> HELIOSTAT_ETCHER_ENTITY = ENTITIES.register("heliostat_etcher",
        () -> BlockEntityType.Builder.of(HeliostatEtcherBlockEntity::new, HELIOSTAT_ETCHER.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<HeliostatEtcherMenu>> HELIOSTAT_ETCHER_MENU = MENUS.register("heliostat_etcher",
        () -> new net.minecraft.world.inventory.MenuType<>(HeliostatEtcherMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.EtchingRecipe>> ETCHING = TYPES.register("etching", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":etching"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.EtchingRecipe>> ETCHING_SERIALIZER = SERIALIZERS.register("etching", com.starmaylight.arcana_industry.recipe.EtchingRecipe.Serializer::new);
    public static final RegistryObject<Item> PRISM_CORE = ITEMS.register("prism_core", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> PRISM_SEPARATOR = BLOCKS.register("prism_separator", PrismSeparatorBlock::new);
    public static final RegistryObject<Item> PRISM_SEPARATOR_ITEM = ITEMS.register("prism_separator", () -> new BlockItem(PRISM_SEPARATOR.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<PrismSeparatorBlockEntity>> PRISM_SEPARATOR_ENTITY = ENTITIES.register("prism_separator",
        () -> BlockEntityType.Builder.of(PrismSeparatorBlockEntity::new, PRISM_SEPARATOR.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<PrismSeparatorMenu>> PRISM_SEPARATOR_MENU = MENUS.register("prism_separator",
        () -> new net.minecraft.world.inventory.MenuType<>(PrismSeparatorMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.SeparationRecipe>> SEPARATION = TYPES.register("separation", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":separation"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.SeparationRecipe>> SEPARATION_SERIALIZER = SERIALIZERS.register("separation", com.starmaylight.arcana_industry.recipe.SeparationRecipe.Serializer::new);
    public static final RegistryObject<Item> COLLIMATED_LENS = ITEMS.register("collimated_lens", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> LENS_COLLIMATOR = BLOCKS.register("lens_collimator", LensCollimatorBlock::new);
    public static final RegistryObject<Item> LENS_COLLIMATOR_ITEM = ITEMS.register("lens_collimator", () -> new BlockItem(LENS_COLLIMATOR.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<LensCollimatorBlockEntity>> LENS_COLLIMATOR_ENTITY = ENTITIES.register("lens_collimator",
        () -> BlockEntityType.Builder.of(LensCollimatorBlockEntity::new, LENS_COLLIMATOR.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<LensCollimatorMenu>> LENS_COLLIMATOR_MENU = MENUS.register("lens_collimator",
        () -> new net.minecraft.world.inventory.MenuType<>(LensCollimatorMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.CollimationRecipe>> COLLIMATION = TYPES.register("collimation", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":collimation"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.CollimationRecipe>> COLLIMATION_SERIALIZER = SERIALIZERS.register("collimation", com.starmaylight.arcana_industry.recipe.CollimationRecipe.Serializer::new);
    public static final RegistryObject<Item> BOUND_SINGULARITY = ITEMS.register("bound_singularity", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> CONTAINMENT_FORGE = BLOCKS.register("containment_forge", ContainmentForgeBlock::new);
    public static final RegistryObject<Item> CONTAINMENT_FORGE_ITEM = ITEMS.register("containment_forge", () -> new BlockItem(CONTAINMENT_FORGE.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<ContainmentForgeBlockEntity>> CONTAINMENT_FORGE_ENTITY = ENTITIES.register("containment_forge",
        () -> BlockEntityType.Builder.of(ContainmentForgeBlockEntity::new, CONTAINMENT_FORGE.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<ContainmentForgeMenu>> CONTAINMENT_FORGE_MENU = MENUS.register("containment_forge",
        () -> new net.minecraft.world.inventory.MenuType<>(ContainmentForgeMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.ContainmentRecipe>> CONTAINMENT = TYPES.register("containment", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":containment"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.ContainmentRecipe>> CONTAINMENT_SERIALIZER = SERIALIZERS.register("containment", com.starmaylight.arcana_industry.recipe.ContainmentRecipe.Serializer::new);
    public static final RegistryObject<Item> CARBON_BRICK = ITEMS.register("carbon_brick", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> CHAR_RETORT = BLOCKS.register("char_retort", CharRetortBlock::new);
    public static final RegistryObject<Item> CHAR_RETORT_ITEM = ITEMS.register("char_retort", () -> new BlockItem(CHAR_RETORT.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<CharRetortBlockEntity>> CHAR_RETORT_ENTITY = ENTITIES.register("char_retort",
        () -> BlockEntityType.Builder.of(CharRetortBlockEntity::new, CHAR_RETORT.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<CharRetortMenu>> CHAR_RETORT_MENU = MENUS.register("char_retort",
        () -> new net.minecraft.world.inventory.MenuType<>(CharRetortMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.CharringRecipe>> CHARRING = TYPES.register("charring", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":charring"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.CharringRecipe>> CHARRING_SERIALIZER = SERIALIZERS.register("charring", com.starmaylight.arcana_industry.recipe.CharringRecipe.Serializer::new);
    public static final RegistryObject<Item> CLINKER_BRICK = ITEMS.register("clinker_brick", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> CLINKER_OVEN = BLOCKS.register("clinker_oven", ClinkerOvenBlock::new);
    public static final RegistryObject<Item> CLINKER_OVEN_ITEM = ITEMS.register("clinker_oven", () -> new BlockItem(CLINKER_OVEN.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<ClinkerOvenBlockEntity>> CLINKER_OVEN_ENTITY = ENTITIES.register("clinker_oven",
        () -> BlockEntityType.Builder.of(ClinkerOvenBlockEntity::new, CLINKER_OVEN.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<ClinkerOvenMenu>> CLINKER_OVEN_MENU = MENUS.register("clinker_oven",
        () -> new net.minecraft.world.inventory.MenuType<>(ClinkerOvenMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.ClinkeringRecipe>> CLINKERING = TYPES.register("clinkering", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":clinkering"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.ClinkeringRecipe>> CLINKERING_SERIALIZER = SERIALIZERS.register("clinkering", com.starmaylight.arcana_industry.recipe.ClinkeringRecipe.Serializer::new);
    public static final RegistryObject<Item> CURED_HEARTH_BRICK = ITEMS.register("cured_hearth_brick", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> EMBER_DRYER = BLOCKS.register("ember_dryer", EmberDryerBlock::new);
    public static final RegistryObject<Item> EMBER_DRYER_ITEM = ITEMS.register("ember_dryer", () -> new BlockItem(EMBER_DRYER.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<EmberDryerBlockEntity>> EMBER_DRYER_ENTITY = ENTITIES.register("ember_dryer",
        () -> BlockEntityType.Builder.of(EmberDryerBlockEntity::new, EMBER_DRYER.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<EmberDryerMenu>> EMBER_DRYER_MENU = MENUS.register("ember_dryer",
        () -> new net.minecraft.world.inventory.MenuType<>(EmberDryerMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.DryingRecipe>> DRYING = TYPES.register("drying", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":drying"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.DryingRecipe>> DRYING_SERIALIZER = SERIALIZERS.register("drying", com.starmaylight.arcana_industry.recipe.DryingRecipe.Serializer::new);
    public static final RegistryObject<Item> LAMINATED_LATTICE = ITEMS.register("laminated_lattice", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> LATTICE_LAMINATOR = BLOCKS.register("lattice_laminator", LatticeLaminatorBlock::new);
    public static final RegistryObject<Item> LATTICE_LAMINATOR_ITEM = ITEMS.register("lattice_laminator", () -> new BlockItem(LATTICE_LAMINATOR.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<LatticeLaminatorBlockEntity>> LATTICE_LAMINATOR_ENTITY = ENTITIES.register("lattice_laminator",
        () -> BlockEntityType.Builder.of(LatticeLaminatorBlockEntity::new, LATTICE_LAMINATOR.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<LatticeLaminatorMenu>> LATTICE_LAMINATOR_MENU = MENUS.register("lattice_laminator",
        () -> new net.minecraft.world.inventory.MenuType<>(LatticeLaminatorMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.LaminationRecipe>> LATTICE_LAMINATION = TYPES.register("lattice_lamination", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":lattice_lamination"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.LaminationRecipe>> LATTICE_LAMINATION_SERIALIZER = SERIALIZERS.register("lattice_lamination", com.starmaylight.arcana_industry.recipe.LaminationRecipe.Serializer::new);
    public static final RegistryObject<Item> ETCHED_PRISM = ITEMS.register("etched_prism", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> LATTICE_ENGRAVER = BLOCKS.register("lattice_engraver", LatticeEngraverBlock::new);
    public static final RegistryObject<Item> LATTICE_ENGRAVER_ITEM = ITEMS.register("lattice_engraver", () -> new BlockItem(LATTICE_ENGRAVER.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<LatticeEngraverBlockEntity>> LATTICE_ENGRAVER_ENTITY = ENTITIES.register("lattice_engraver",
        () -> BlockEntityType.Builder.of(LatticeEngraverBlockEntity::new, LATTICE_ENGRAVER.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<LatticeEngraverMenu>> LATTICE_ENGRAVER_MENU = MENUS.register("lattice_engraver",
        () -> new net.minecraft.world.inventory.MenuType<>(LatticeEngraverMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.EngravingRecipe>> LATTICE_ENGRAVING = TYPES.register("lattice_engraving", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":lattice_engraving"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.EngravingRecipe>> LATTICE_ENGRAVING_SERIALIZER = SERIALIZERS.register("lattice_engraving", com.starmaylight.arcana_industry.recipe.EngravingRecipe.Serializer::new);
    public static final RegistryObject<Item> STASIS_CORE = ITEMS.register("stasis_core", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> STASIS_WEAVER = BLOCKS.register("stasis_weaver", StasisWeaverBlock::new);
    public static final RegistryObject<Item> STASIS_WEAVER_ITEM = ITEMS.register("stasis_weaver", () -> new BlockItem(STASIS_WEAVER.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<StasisWeaverBlockEntity>> STASIS_WEAVER_ENTITY = ENTITIES.register("stasis_weaver",
        () -> BlockEntityType.Builder.of(StasisWeaverBlockEntity::new, STASIS_WEAVER.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<StasisWeaverMenu>> STASIS_WEAVER_MENU = MENUS.register("stasis_weaver",
        () -> new net.minecraft.world.inventory.MenuType<>(StasisWeaverMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.StasisRecipe>> STASIS_WEAVING = TYPES.register("stasis_weaving", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":stasis_weaving"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.StasisRecipe>> STASIS_WEAVING_SERIALIZER = SERIALIZERS.register("stasis_weaving", com.starmaylight.arcana_industry.recipe.StasisRecipe.Serializer::new);
    public static final RegistryObject<Item> PHASE_LATTICE = ITEMS.register("phase_lattice", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> LATTICE_COMPOSER = BLOCKS.register("lattice_composer", LatticeComposerBlock::new);
    public static final RegistryObject<Item> LATTICE_COMPOSER_ITEM = ITEMS.register("lattice_composer", () -> new BlockItem(LATTICE_COMPOSER.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<LatticeComposerBlockEntity>> LATTICE_COMPOSER_ENTITY = ENTITIES.register("lattice_composer",
        () -> BlockEntityType.Builder.of(LatticeComposerBlockEntity::new, LATTICE_COMPOSER.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<LatticeComposerMenu>> LATTICE_COMPOSER_MENU = MENUS.register("lattice_composer",
        () -> new net.minecraft.world.inventory.MenuType<>(LatticeComposerMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.CompositionRecipe>> COMPOSITION = TYPES.register("composition", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":composition"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.CompositionRecipe>> COMPOSITION_SERIALIZER = SERIALIZERS.register("composition", com.starmaylight.arcana_industry.recipe.CompositionRecipe.Serializer::new);
    public static final RegistryObject<Item> PHASE_COIL = ITEMS.register("phase_coil", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> PHASE_ANNEALER = BLOCKS.register("phase_annealer", PhaseAnnealerBlock::new);
    public static final RegistryObject<Item> PHASE_ANNEALER_ITEM = ITEMS.register("phase_annealer", () -> new BlockItem(PHASE_ANNEALER.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<PhaseAnnealerBlockEntity>> PHASE_ANNEALER_ENTITY = ENTITIES.register("phase_annealer",
        () -> BlockEntityType.Builder.of(PhaseAnnealerBlockEntity::new, PHASE_ANNEALER.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<PhaseAnnealerMenu>> PHASE_ANNEALER_MENU = MENUS.register("phase_annealer",
        () -> new net.minecraft.world.inventory.MenuType<>(PhaseAnnealerMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.AnnealingRecipe>> ANNEALING = TYPES.register("annealing", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":annealing"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.AnnealingRecipe>> ANNEALING_SERIALIZER = SERIALIZERS.register("annealing", com.starmaylight.arcana_industry.recipe.AnnealingRecipe.Serializer::new);
    public static final RegistryObject<Item> COLLAPSED_PHASE = ITEMS.register("collapsed_phase", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> PHASE_COLLAPSER = BLOCKS.register("phase_collapser", PhaseCollapserBlock::new);
    public static final RegistryObject<Item> PHASE_COLLAPSER_ITEM = ITEMS.register("phase_collapser", () -> new BlockItem(PHASE_COLLAPSER.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<PhaseCollapserBlockEntity>> PHASE_COLLAPSER_ENTITY = ENTITIES.register("phase_collapser",
        () -> BlockEntityType.Builder.of(PhaseCollapserBlockEntity::new, PHASE_COLLAPSER.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<PhaseCollapserMenu>> PHASE_COLLAPSER_MENU = MENUS.register("phase_collapser",
        () -> new net.minecraft.world.inventory.MenuType<>(PhaseCollapserMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.CollapseRecipe>> COLLAPSE = TYPES.register("collapse", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":collapse"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.CollapseRecipe>> COLLAPSE_SERIALIZER = SERIALIZERS.register("collapse", com.starmaylight.arcana_industry.recipe.CollapseRecipe.Serializer::new);
    public static final RegistryObject<Item> SUBLIMATED_PHASE = ITEMS.register("sublimated_phase", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> PHASE_SUBLIMATOR = BLOCKS.register("phase_sublimator", PhaseSublimatorBlock::new);
    public static final RegistryObject<Item> PHASE_SUBLIMATOR_ITEM = ITEMS.register("phase_sublimator", () -> new BlockItem(PHASE_SUBLIMATOR.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<PhaseSublimatorBlockEntity>> PHASE_SUBLIMATOR_ENTITY = ENTITIES.register("phase_sublimator",
        () -> BlockEntityType.Builder.of(PhaseSublimatorBlockEntity::new, PHASE_SUBLIMATOR.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<PhaseSublimatorMenu>> PHASE_SUBLIMATOR_MENU = MENUS.register("phase_sublimator",
        () -> new net.minecraft.world.inventory.MenuType<>(PhaseSublimatorMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.SublimationRecipe>> SUBLIMATION = TYPES.register("sublimation", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":sublimation"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.SublimationRecipe>> SUBLIMATION_SERIALIZER = SERIALIZERS.register("sublimation", com.starmaylight.arcana_industry.recipe.SublimationRecipe.Serializer::new);
    public static final RegistryObject<Block> PHASE_RECLAIMER = BLOCKS.register("phase_reclaimer", PhaseReclaimerBlock::new);
    public static final RegistryObject<Item> PHASE_RECLAIMER_ITEM = ITEMS.register("phase_reclaimer", () -> new BlockItem(PHASE_RECLAIMER.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<PhaseReclaimerBlockEntity>> PHASE_RECLAIMER_ENTITY = ENTITIES.register("phase_reclaimer",
        () -> BlockEntityType.Builder.of(PhaseReclaimerBlockEntity::new, PHASE_RECLAIMER.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<PhaseReclaimerMenu>> PHASE_RECLAIMER_MENU = MENUS.register("phase_reclaimer",
        () -> new net.minecraft.world.inventory.MenuType<>(PhaseReclaimerMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.ReclaimingRecipe>> RECLAIMING = TYPES.register("reclaiming", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":reclaiming"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.ReclaimingRecipe>> RECLAIMING_SERIALIZER = SERIALIZERS.register("reclaiming", com.starmaylight.arcana_industry.recipe.ReclaimingRecipe.Serializer::new);
    public static final RegistryObject<Item> DIFFUSED_PHASE = ITEMS.register("diffused_phase", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> PHASE_DIFFUSER = BLOCKS.register("phase_diffuser", PhaseDiffuserBlock::new);
    public static final RegistryObject<Item> PHASE_DIFFUSER_ITEM = ITEMS.register("phase_diffuser", () -> new BlockItem(PHASE_DIFFUSER.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<PhaseDiffuserBlockEntity>> PHASE_DIFFUSER_ENTITY = ENTITIES.register("phase_diffuser",
        () -> BlockEntityType.Builder.of(PhaseDiffuserBlockEntity::new, PHASE_DIFFUSER.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<PhaseDiffuserMenu>> PHASE_DIFFUSER_MENU = MENUS.register("phase_diffuser",
        () -> new net.minecraft.world.inventory.MenuType<>(PhaseDiffuserMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.DiffusionRecipe>> DIFFUSION = TYPES.register("diffusion", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":diffusion"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.DiffusionRecipe>> DIFFUSION_SERIALIZER = SERIALIZERS.register("diffusion", com.starmaylight.arcana_industry.recipe.DiffusionRecipe.Serializer::new);
    public static final RegistryObject<Item> INVERTED_PHASE = ITEMS.register("inverted_phase", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> PHASE_INVERTER = BLOCKS.register("phase_inverter", PhaseInverterBlock::new);
    public static final RegistryObject<Item> PHASE_INVERTER_ITEM = ITEMS.register("phase_inverter", () -> new BlockItem(PHASE_INVERTER.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<PhaseInverterBlockEntity>> PHASE_INVERTER_ENTITY = ENTITIES.register("phase_inverter",
        () -> BlockEntityType.Builder.of(PhaseInverterBlockEntity::new, PHASE_INVERTER.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<PhaseInverterMenu>> PHASE_INVERTER_MENU = MENUS.register("phase_inverter",
        () -> new net.minecraft.world.inventory.MenuType<>(PhaseInverterMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.InversionRecipe>> INVERSION = TYPES.register("inversion", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":inversion"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.InversionRecipe>> INVERSION_SERIALIZER = SERIALIZERS.register("inversion", com.starmaylight.arcana_industry.recipe.InversionRecipe.Serializer::new);
    public static final RegistryObject<Item> MODULATED_PHASE = ITEMS.register("modulated_phase", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> PHASE_MODULATOR = BLOCKS.register("phase_modulator", PhaseModulatorBlock::new);
    public static final RegistryObject<Item> PHASE_MODULATOR_ITEM = ITEMS.register("phase_modulator", () -> new BlockItem(PHASE_MODULATOR.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<PhaseModulatorBlockEntity>> PHASE_MODULATOR_ENTITY = ENTITIES.register("phase_modulator",
        () -> BlockEntityType.Builder.of(PhaseModulatorBlockEntity::new, PHASE_MODULATOR.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<PhaseModulatorMenu>> PHASE_MODULATOR_MENU = MENUS.register("phase_modulator",
        () -> new net.minecraft.world.inventory.MenuType<>(PhaseModulatorMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.ModulationRecipe>> MODULATION = TYPES.register("modulation", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":modulation"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.ModulationRecipe>> MODULATION_SERIALIZER = SERIALIZERS.register("modulation", com.starmaylight.arcana_industry.recipe.ModulationRecipe.Serializer::new);
    public static final RegistryObject<Item> STABILIZED_PHASE = ITEMS.register("stabilized_phase", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> PHASE_STABILIZER = BLOCKS.register("phase_stabilizer", PhaseStabilizerBlock::new);
    public static final RegistryObject<Item> PHASE_STABILIZER_ITEM = ITEMS.register("phase_stabilizer", () -> new BlockItem(PHASE_STABILIZER.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<PhaseStabilizerBlockEntity>> PHASE_STABILIZER_ENTITY = ENTITIES.register("phase_stabilizer",
        () -> BlockEntityType.Builder.of(PhaseStabilizerBlockEntity::new, PHASE_STABILIZER.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<PhaseStabilizerMenu>> PHASE_STABILIZER_MENU = MENUS.register("phase_stabilizer",
        () -> new net.minecraft.world.inventory.MenuType<>(PhaseStabilizerMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.StabilizationRecipe>> STABILIZATION = TYPES.register("stabilization", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":stabilization"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.StabilizationRecipe>> STABILIZATION_SERIALIZER = SERIALIZERS.register("stabilization", com.starmaylight.arcana_industry.recipe.StabilizationRecipe.Serializer::new);
    public static final RegistryObject<Item> PRESERVED_PHASE = ITEMS.register("preserved_phase", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> PHASE_PRESERVER = BLOCKS.register("phase_preserver", PhasePreserverBlock::new);
    public static final RegistryObject<Item> PHASE_PRESERVER_ITEM = ITEMS.register("phase_preserver", () -> new BlockItem(PHASE_PRESERVER.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<PhasePreserverBlockEntity>> PHASE_PRESERVER_ENTITY = ENTITIES.register("phase_preserver",
        () -> BlockEntityType.Builder.of(PhasePreserverBlockEntity::new, PHASE_PRESERVER.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<PhasePreserverMenu>> PHASE_PRESERVER_MENU = MENUS.register("phase_preserver",
        () -> new net.minecraft.world.inventory.MenuType<>(PhasePreserverMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.PreservationRecipe>> PRESERVATION = TYPES.register("preservation", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":preservation"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.PreservationRecipe>> PRESERVATION_SERIALIZER = SERIALIZERS.register("preservation", com.starmaylight.arcana_industry.recipe.PreservationRecipe.Serializer::new);
    public static final RegistryObject<Item> EFFUSED_PHASE = ITEMS.register("effused_phase", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> PHASE_EFFUSER = BLOCKS.register("phase_effuser", PhaseEffuserBlock::new);
    public static final RegistryObject<Item> PHASE_EFFUSER_ITEM = ITEMS.register("phase_effuser", () -> new BlockItem(PHASE_EFFUSER.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<PhaseEffuserBlockEntity>> PHASE_EFFUSER_ENTITY = ENTITIES.register("phase_effuser",
        () -> BlockEntityType.Builder.of(PhaseEffuserBlockEntity::new, PHASE_EFFUSER.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<PhaseEffuserMenu>> PHASE_EFFUSER_MENU = MENUS.register("phase_effuser",
        () -> new net.minecraft.world.inventory.MenuType<>(PhaseEffuserMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.EffusionRecipe>> EFFUSION = TYPES.register("effusion", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":effusion"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.EffusionRecipe>> EFFUSION_SERIALIZER = SERIALIZERS.register("effusion", com.starmaylight.arcana_industry.recipe.EffusionRecipe.Serializer::new);
    public static final RegistryObject<Item> CONVERGED_PHASE = ITEMS.register("converged_phase", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> PHASE_CONFLUX = BLOCKS.register("phase_conflux", PhaseConfluxBlock::new);
    public static final RegistryObject<Item> PHASE_CONFLUX_ITEM = ITEMS.register("phase_conflux", () -> new BlockItem(PHASE_CONFLUX.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<PhaseConfluxBlockEntity>> PHASE_CONFLUX_ENTITY = ENTITIES.register("phase_conflux",
        () -> BlockEntityType.Builder.of(PhaseConfluxBlockEntity::new, PHASE_CONFLUX.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<PhaseConfluxMenu>> PHASE_CONFLUX_MENU = MENUS.register("phase_conflux",
        () -> new net.minecraft.world.inventory.MenuType<>(PhaseConfluxMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.ConfluenceRecipe>> CONFLUENCE = TYPES.register("confluence", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":confluence"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.ConfluenceRecipe>> CONFLUENCE_SERIALIZER = SERIALIZERS.register("confluence", com.starmaylight.arcana_industry.recipe.ConfluenceRecipe.Serializer::new);
    public static final RegistryObject<Item> REVIVED_PHASE = ITEMS.register("revived_phase", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> PHASE_REVIVIFIER = BLOCKS.register("phase_revivifier", PhaseRevivifierBlock::new);
    public static final RegistryObject<Item> PHASE_REVIVIFIER_ITEM = ITEMS.register("phase_revivifier", () -> new BlockItem(PHASE_REVIVIFIER.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<PhaseRevivifierBlockEntity>> PHASE_REVIVIFIER_ENTITY = ENTITIES.register("phase_revivifier",
        () -> BlockEntityType.Builder.of(PhaseRevivifierBlockEntity::new, PHASE_REVIVIFIER.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<PhaseRevivifierMenu>> PHASE_REVIVIFIER_MENU = MENUS.register("phase_revivifier",
        () -> new net.minecraft.world.inventory.MenuType<>(PhaseRevivifierMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.RevivificationRecipe>> REVIVIFICATION = TYPES.register("revivification", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":revivification"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.RevivificationRecipe>> REVIVIFICATION_SERIALIZER = SERIALIZERS.register("revivification", com.starmaylight.arcana_industry.recipe.RevivificationRecipe.Serializer::new);
    public static final RegistryObject<Item> REFRACTED_PHASE = ITEMS.register("refracted_phase", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> PHASE_REFRACTOR = BLOCKS.register("phase_refractor", PhaseRefractorBlock::new);
    public static final RegistryObject<Item> PHASE_REFRACTOR_ITEM = ITEMS.register("phase_refractor", () -> new BlockItem(PHASE_REFRACTOR.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<PhaseRefractorBlockEntity>> PHASE_REFRACTOR_ENTITY = ENTITIES.register("phase_refractor",
        () -> BlockEntityType.Builder.of(PhaseRefractorBlockEntity::new, PHASE_REFRACTOR.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<PhaseRefractorMenu>> PHASE_REFRACTOR_MENU = MENUS.register("phase_refractor",
        () -> new net.minecraft.world.inventory.MenuType<>(PhaseRefractorMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.RefractionRecipe>> REFRACTION = TYPES.register("refraction", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":refraction"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.RefractionRecipe>> REFRACTION_SERIALIZER = SERIALIZERS.register("refraction", com.starmaylight.arcana_industry.recipe.RefractionRecipe.Serializer::new);
    public static final RegistryObject<Item> RIPENED_PHASE = ITEMS.register("ripened_phase", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> PHASE_FERMENTER = BLOCKS.register("phase_fermenter", PhaseFermenterBlock::new);
    public static final RegistryObject<Item> PHASE_FERMENTER_ITEM = ITEMS.register("phase_fermenter", () -> new BlockItem(PHASE_FERMENTER.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<PhaseFermenterBlockEntity>> PHASE_FERMENTER_ENTITY = ENTITIES.register("phase_fermenter",
        () -> BlockEntityType.Builder.of(PhaseFermenterBlockEntity::new, PHASE_FERMENTER.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<PhaseFermenterMenu>> PHASE_FERMENTER_MENU = MENUS.register("phase_fermenter",
        () -> new net.minecraft.world.inventory.MenuType<>(PhaseFermenterMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.FermentationRecipe>> FERMENTATION = TYPES.register("fermentation", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":fermentation"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.FermentationRecipe>> FERMENTATION_SERIALIZER = SERIALIZERS.register("fermentation", com.starmaylight.arcana_industry.recipe.FermentationRecipe.Serializer::new);
    public static final RegistryObject<Block> ORE_SLUICE = BLOCKS.register("ore_sluice", OreSluiceBlock::new);
    public static final RegistryObject<Item> ORE_SLUICE_ITEM = ITEMS.register("ore_sluice", () -> new BlockItem(ORE_SLUICE.get(), new Item.Properties()));
    public static final RegistryObject<Block> BLAST_COLUMN = BLOCKS.register("blast_column", BlastColumnBlock::new);
    public static final RegistryObject<Item> BLAST_COLUMN_ITEM = ITEMS.register("blast_column", () -> new BlockItem(BLAST_COLUMN.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.ColumnBlastingRecipe>> COLUMN_BLASTING = TYPES.register("column_blasting", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":column_blasting"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.ColumnBlastingRecipe>> COLUMN_BLASTING_SERIALIZER = SERIALIZERS.register("column_blasting", com.starmaylight.arcana_industry.recipe.ColumnBlastingRecipe.Serializer::new);
    public static final RegistryObject<Item> VITRIFIED_AMETHYST = ITEMS.register("vitrified_amethyst", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> RESONANCE_BASIN = BLOCKS.register("resonance_basin", ResonanceBasinBlock::new);
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.ResonanceCuttingRecipe>> RESONANCE_CUTTING = TYPES.register("resonance_cutting", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":resonance_cutting"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.ResonanceCuttingRecipe>> RESONANCE_CUTTING_SERIALIZER = SERIALIZERS.register("resonance_cutting", com.starmaylight.arcana_industry.recipe.ResonanceCuttingRecipe.Serializer::new);
    public static final RegistryObject<Item> RESONANCE_BASIN_ITEM = ITEMS.register("resonance_basin", () -> new BlockItem(RESONANCE_BASIN.get(), new Item.Properties()));
    public static final RegistryObject<Block> PROVISION_HEARTH = BLOCKS.register("provision_hearth", ProvisionHearthBlock::new);
    public static final RegistryObject<Item> PROVISION_HEARTH_ITEM = ITEMS.register("provision_hearth", () -> new BlockItem(PROVISION_HEARTH.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.HearthSmokingRecipe>> HEARTH_SMOKING = TYPES.register("hearth_smoking", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":hearth_smoking"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.HearthSmokingRecipe>> HEARTH_SMOKING_SERIALIZER = SERIALIZERS.register("hearth_smoking", com.starmaylight.arcana_industry.recipe.HearthSmokingRecipe.Serializer::new);
    public static final RegistryObject<Block> FOUNDRY_RING = BLOCKS.register("foundry_ring", FoundryRingBlock::new);
    public static final RegistryObject<Item> FOUNDRY_RING_ITEM = ITEMS.register("foundry_ring", () -> new BlockItem(FOUNDRY_RING.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.FoundrySmeltingRecipe>> FOUNDRY_SMELTING = TYPES.register("foundry_smelting", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":foundry_smelting"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.FoundrySmeltingRecipe>> FOUNDRY_SMELTING_SERIALIZER = SERIALIZERS.register("foundry_smelting", com.starmaylight.arcana_industry.recipe.FoundrySmeltingRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.RackSmokingRecipe>> RACK_SMOKING = TYPES.register("rack_smoking", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":rack_smoking"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.RackSmokingRecipe>> RACK_SMOKING_SERIALIZER = SERIALIZERS.register("rack_smoking", com.starmaylight.arcana_industry.recipe.RackSmokingRecipe.Serializer::new);
    public static final RegistryObject<Block> SMOKE_RACK = BLOCKS.register("smoke_rack", SmokeRackBlock::new);
    public static final RegistryObject<Item> SMOKE_RACK_ITEM = ITEMS.register("smoke_rack", () -> new BlockItem(SMOKE_RACK.get(), new Item.Properties()));
    public static final RegistryObject<Block> MILLING_GANTRY = BLOCKS.register("milling_gantry", MillingGantryBlock::new);
    public static final RegistryObject<Item> MILLING_GANTRY_ITEM = ITEMS.register("milling_gantry", () -> new BlockItem(MILLING_GANTRY.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.MillingRecipe>> MILLING = TYPES.register("milling", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":milling"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.MillingRecipe>> MILLING_SERIALIZER = SERIALIZERS.register("milling", com.starmaylight.arcana_industry.recipe.MillingRecipe.Serializer::new);
    public static final RegistryObject<Item> OPTIC_POWDER = ITEMS.register("optic_powder", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> CLAMPING_PRESS = BLOCKS.register("clamping_press", ClampingPressBlock::new);
    public static final RegistryObject<Item> CLAMPING_PRESS_ITEM = ITEMS.register("clamping_press", () -> new BlockItem(CLAMPING_PRESS.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.ClampingRecipe>> CLAMPING = TYPES.register("clamping", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":clamping"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.ClampingRecipe>> CLAMPING_SERIALIZER = SERIALIZERS.register("clamping", com.starmaylight.arcana_industry.recipe.ClampingRecipe.Serializer::new);
    public static final RegistryObject<Item> PRESSURE_GASKET = ITEMS.register("pressure_gasket", () -> new Item(new Item.Properties()));
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> CLAMPING_PRESS_ENTITY = ENTITIES.register("clamping_press",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("clamping_press", pos, state), CLAMPING_PRESS.get()).build(null));
    public static final RegistryObject<Block> SEAM_PRESS = BLOCKS.register("seam_press", SeamPressBlock::new);
    public static final RegistryObject<Item> SEAM_PRESS_ITEM = ITEMS.register("seam_press", () -> new BlockItem(SEAM_PRESS.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.SeamingRecipe>> SEAMING = TYPES.register("seaming", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":seaming"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.SeamingRecipe>> SEAMING_SERIALIZER = SERIALIZERS.register("seaming", com.starmaylight.arcana_industry.recipe.SeamingRecipe.Serializer::new);
    public static final RegistryObject<Item> CLAMPED_LINER = ITEMS.register("clamped_liner", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> EMBOSSED_WEAR_LINER = ITEMS.register("embossed_wear_liner", () -> new Item(new Item.Properties()));
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> SEAM_PRESS_ENTITY = ENTITIES.register("seam_press",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("seam_press", pos, state), SEAM_PRESS.get()).build(null));
    public static final RegistryObject<Block> SOAKING_HEARTH = BLOCKS.register("soaking_hearth", SoakingHearthBlock::new);
    public static final RegistryObject<Item> SOAKING_HEARTH_ITEM = ITEMS.register("soaking_hearth", () -> new BlockItem(SOAKING_HEARTH.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.SoakingRecipe>> SOAKING = TYPES.register("soaking", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":soaking"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.SoakingRecipe>> SOAKING_SERIALIZER = SERIALIZERS.register("soaking", com.starmaylight.arcana_industry.recipe.SoakingRecipe.Serializer::new);
    public static final RegistryObject<Item> SOAKED_BILLET = ITEMS.register("soaked_billet", () -> new Item(new Item.Properties()));
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> SOAKING_HEARTH_ENTITY = ENTITIES.register("soaking_hearth",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("soaking_hearth", pos, state), SOAKING_HEARTH.get()).build(null));
    public static final RegistryObject<Block> COMPACTION_SPIRE = BLOCKS.register("compaction_spire", CompactionSpireBlock::new);
    public static final RegistryObject<Item> COMPACTION_SPIRE_ITEM = ITEMS.register("compaction_spire", () -> new BlockItem(COMPACTION_SPIRE.get(), new Item.Properties()));
    public static final RegistryObject<Block> TWIN_CHANNEL_WASHER = BLOCKS.register("twin_channel_washer", TwinChannelWasherBlock::new);
    public static final RegistryObject<Item> TWIN_CHANNEL_WASHER_ITEM = ITEMS.register("twin_channel_washer", () -> new BlockItem(TWIN_CHANNEL_WASHER.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.ChannelWashingRecipe>> CHANNEL_WASHING = TYPES.register("channel_washing", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":channel_washing"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.ChannelWashingRecipe>> CHANNEL_WASHING_SERIALIZER = SERIALIZERS.register("channel_washing", com.starmaylight.arcana_industry.recipe.ChannelWashingRecipe.Serializer::new);
    public static final RegistryObject<Item> PANNED_CONCENTRATE = ITEMS.register("panned_concentrate", () -> new Item(new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.ScreeningRecipe>> SCREENING = TYPES.register("screening", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":screening"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.ScreeningRecipe>> SCREENING_SERIALIZER = SERIALIZERS.register("screening", com.starmaylight.arcana_industry.recipe.ScreeningRecipe.Serializer::new);
    public static final RegistryObject<Item> SCREENED_SILICA = ITEMS.register("screened_silica", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> FOUNDRY_LINER = ITEMS.register("foundry_liner", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CURED_LASHING = ITEMS.register("cured_lashing", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CREOSOTE_LAGGING = ITEMS.register("creosote_lagging", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MINERAL_FIBRE_LAGGING = ITEMS.register("mineral_fibre_lagging", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> ROTARY_SCREEN = BLOCKS.register("rotary_screen", RotaryScreenBlock::new);
    public static final RegistryObject<Item> ROTARY_SCREEN_ITEM = ITEMS.register("rotary_screen", () -> new BlockItem(ROTARY_SCREEN.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> ORE_SLUICE_ENTITY = ENTITIES.register("ore_sluice",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("ore_sluice", pos, state), ORE_SLUICE.get()).build(null));
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> BLAST_COLUMN_ENTITY = ENTITIES.register("blast_column",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("blast_column", pos, state), BLAST_COLUMN.get()).build(null));
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> RESONANCE_BASIN_ENTITY = ENTITIES.register("resonance_basin",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("resonance_basin", pos, state), RESONANCE_BASIN.get()).build(null));
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> PROVISION_HEARTH_ENTITY = ENTITIES.register("provision_hearth",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("provision_hearth", pos, state), PROVISION_HEARTH.get()).build(null));
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> FOUNDRY_RING_ENTITY = ENTITIES.register("foundry_ring",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("foundry_ring", pos, state), FOUNDRY_RING.get()).build(null));
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> SMOKE_RACK_ENTITY = ENTITIES.register("smoke_rack",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("smoke_rack", pos, state), SMOKE_RACK.get()).build(null));
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> MILLING_GANTRY_ENTITY = ENTITIES.register("milling_gantry",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("milling_gantry", pos, state), MILLING_GANTRY.get()).build(null));
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> COMPACTION_SPIRE_ENTITY = ENTITIES.register("compaction_spire",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("compaction_spire", pos, state), COMPACTION_SPIRE.get()).build(null));
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> TWIN_CHANNEL_WASHER_ENTITY = ENTITIES.register("twin_channel_washer",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("twin_channel_washer", pos, state), TWIN_CHANNEL_WASHER.get()).build(null));
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> ROTARY_SCREEN_ENTITY = ENTITIES.register("rotary_screen",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("rotary_screen", pos, state), ROTARY_SCREEN.get()).build(null));
    private static HandFedControllerBlockEntity controllerEntity(String name, BlockPos pos, BlockState state) {
        return new HandFedControllerBlockEntity(ForgeRegistries.BLOCK_ENTITY_TYPES.getValue(new ResourceLocation(MOD, name)), pos, state);
    }
    public static final RegistryObject<RecipeType<SluicingRecipe>> SLUICING = TYPES.register("sluicing", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":sluicing"; }
    });
    public static final RegistryObject<RecipeSerializer<SluicingRecipe>> SLUICING_SERIALIZER = SERIALIZERS.register("sluicing", SluicingRecipe.Serializer::new);
    public static final RegistryObject<Item> REFRACTORY_BINDER = ITEMS.register("refractory_binder", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> REFRACTORY_MIXER = BLOCKS.register("refractory_mixer", RefractoryMixerBlock::new);
    public static final RegistryObject<Item> REFRACTORY_MIXER_ITEM = ITEMS.register("refractory_mixer", () -> new BlockItem(REFRACTORY_MIXER.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<RefractoryMixerBlockEntity>> REFRACTORY_MIXER_ENTITY = ENTITIES.register("refractory_mixer",
        () -> BlockEntityType.Builder.of(RefractoryMixerBlockEntity::new, REFRACTORY_MIXER.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<RefractoryMixerMenu>> REFRACTORY_MIXER_MENU = MENUS.register("refractory_mixer",
        () -> new net.minecraft.world.inventory.MenuType<>(RefractoryMixerMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.MixingRecipe>> MIXING = TYPES.register("mixing", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":mixing"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.MixingRecipe>> MIXING_SERIALIZER = SERIALIZERS.register("mixing", com.starmaylight.arcana_industry.recipe.MixingRecipe.Serializer::new);
    public static final RegistryObject<Item> TEMPERED_STEEL = ITEMS.register("tempered_steel", () -> new Item(new Item.Properties()));
    // Press drive subassembly: sintered bronze bushings carry a quenched-and-tempered steel shaft.
    public static final RegistryObject<Item> BRONZE_POWDER_BLEND = ITEMS.register("bronze_powder_blend", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SINTERED_BRONZE_BUSHING = ITEMS.register("sintered_bronze_bushing", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> HYDRODYNAMIC_JOURNAL_BEARING = ITEMS.register("hydrodynamic_journal_bearing", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DRIVE_SHAFT = ITEMS.register("drive_shaft", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PRESS_DRIVE_TRAIN = ITEMS.register("press_drive_train", () -> new Item(new Item.Properties()));
    // Milling spindle reduction: die-compacted then sintered bronze gears on the same shaft and bushings, in a sealed plate housing.
    public static final RegistryObject<Item> GREEN_GEAR_COMPACT = ITEMS.register("green_gear_compact", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SINTERED_BRONZE_GEAR = ITEMS.register("sintered_bronze_gear", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPINDLE_GEARBOX = ITEMS.register("spindle_gearbox", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> WOUND_MOTOR = ITEMS.register("wound_motor", () -> new Item(new Item.Properties()));
    // Wire drawing die: a hot-pressed tungsten carbide/cobalt nib shrink-fit into a tempered steel casing.
    public static final RegistryObject<Item> CARBIDE_POWDER_BLEND = ITEMS.register("carbide_powder_blend", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CEMENTED_CARBIDE_NIB = ITEMS.register("cemented_carbide_nib", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CARBIDE_INSERT_BLANK = ITEMS.register("carbide_insert_blank", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CEMENTED_CARBIDE_INSERT = ITEMS.register("cemented_carbide_insert", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> WIRE_DRAW_DIE = ITEMS.register("wire_draw_die", () -> new Item(new Item.Properties()));
    // Nickel/chromium and nickel/aluminum legs are joined into a sheathed kiln temperature probe.
    public static final RegistryObject<Item> THERMOCOUPLE_PAIR = ITEMS.register("thermocouple_pair", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> THERMOCOUPLE_PROBE = ITEMS.register("thermocouple_probe", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BALL_SCREW = ITEMS.register("ball_screw", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MONEL_PUMP_BLANK = ITEMS.register("monel_pump_blank", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COMPRESSION_PACKING = ITEMS.register("compression_packing", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ROTARY_LIP_SEAL = ITEMS.register("rotary_lip_seal", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BOURDON_PRESSURE_GAUGE = ITEMS.register("bourdon_pressure_gauge", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LABYRINTH_SEAL = ITEMS.register("labyrinth_seal", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RENDERED_TALLOW = ITEMS.register("rendered_tallow", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BEARING_GREASE = ITEMS.register("bearing_grease", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LUBRICATING_OIL = ITEMS.register("lubricating_oil", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> METERED_GREASE_UNIT = ITEMS.register("metered_grease_unit", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> HYDRAULIC_CYLINDER = ITEMS.register("hydraulic_cylinder", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PNEUMATIC_CYLINDER = ITEMS.register("pneumatic_cylinder", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DRY_LUBE_COMPOUND = ITEMS.register("dry_lube_compound", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> FILLED_DRY_LUBE_BEARING = ITEMS.register("filled_dry_lube_bearing", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DRY_LUBE_BEARING = ITEMS.register("dry_lube_bearing", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ENCODER_RING_BLANK = ITEMS.register("encoder_ring_blank", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GRADUATED_ENCODER_RING = ITEMS.register("graduated_encoder_ring", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ENCODER_READHEAD = ITEMS.register("encoder_readhead", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ROTARY_ENCODER = ITEMS.register("rotary_encoder", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GRADUATED_LINEAR_SCALE = ITEMS.register("graduated_linear_scale", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LINEAR_ENCODER = ITEMS.register("linear_encoder", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> THRUST_WASHER_BLANK = ITEMS.register("thrust_washer_blank", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> HARDENED_THRUST_WASHER = ITEMS.register("hardened_thrust_washer", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> THRUST_BALL_BEARING = ITEMS.register("thrust_ball_bearing", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CARBON_FACE_BLANK = ITEMS.register("carbon_face_blank", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CARBON_SEAL_FACE = ITEMS.register("carbon_seal_face", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LAPPED_CARBIDE_SEAT = ITEMS.register("lapped_carbide_seat", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MECHANICAL_FACE_SEAL = ITEMS.register("mechanical_face_seal", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GUIDE_RAIL_BLANK = ITEMS.register("guide_rail_blank", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> HARDENED_GUIDE_RAIL = ITEMS.register("hardened_guide_rail", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LINEAR_GUIDE = ITEMS.register("linear_guide", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TAPERED_ROLLER_BLANK = ITEMS.register("tapered_roller_blank", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TAPERED_ROLLER = ITEMS.register("tapered_roller", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GROUND_TAPERED_RACE = ITEMS.register("ground_tapered_race", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TAPERED_ROLLER_BEARING = ITEMS.register("tapered_roller_bearing", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CYLINDRICAL_ROLLER = ITEMS.register("cylindrical_roller", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CYLINDRICAL_ROLLER_BEARING = ITEMS.register("cylindrical_roller_bearing", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SHAFT_KEY = ITEMS.register("shaft_key", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SERVO_MOTOR = ITEMS.register("servo_motor", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LIMIT_SWITCH = ITEMS.register("limit_switch", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> POWER_CONTACTOR = ITEMS.register("power_contactor", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GROUND_WORM = ITEMS.register("ground_worm", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> WORM_GEAR_SET = ITEMS.register("worm_gear_set", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BEVEL_GEAR_PAIR = ITEMS.register("bevel_gear_pair", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> JAW_SHAFT_COUPLING = ITEMS.register("jaw_shaft_coupling", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BELT_PULLEY_DRIVE = ITEMS.register("belt_pulley_drive", () -> new Item(new Item.Properties()));
      public static final RegistryObject<Item> ROLLER_CHAIN_DRIVE = ITEMS.register("roller_chain_drive", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PRESSED_FRICTION_LINING = ITEMS.register("pressed_friction_lining", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CLUTCH_BRAKE_PACK = ITEMS.register("clutch_brake_pack", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PROFILE_GROUND_CAM = ITEMS.register("profile_ground_cam", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CAM_BLANK = ITEMS.register("cam_blank", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CAM_FOLLOWER_ASSEMBLY = ITEMS.register("cam_follower_assembly", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GOVERNOR_FLYWEIGHT = ITEMS.register("governor_flyweight", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> FLYWEIGHT_GOVERNOR = ITEMS.register("flyweight_governor", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PATTERN_STOCK = ITEMS.register("pattern_stock", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CASTING_PATTERN = ITEMS.register("casting_pattern", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COPPER_BUSBAR = ITEMS.register("copper_busbar", () -> new Item(new Item.Properties()));
    // Flywheel (RP-T11): hot-pressed iron rim segments around a cast hub, shrink-fitted in the Quench Vat.
    public static final RegistryObject<Item> FLYWHEEL_RIM_BLANK = ITEMS.register("flywheel_rim_blank", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SHRINK_FIT_FLYWHEEL = ITEMS.register("shrink_fit_flywheel", () -> new Item(new Item.Properties()));
    // Semiconductor rectifier (RP-E10): sawn germanium wafer, copper whisker contact, electrically formed in the Flux Rectifier.
    public static final RegistryObject<Item> GERMANIUM_WAFER = ITEMS.register("germanium_wafer", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RECTIFIER_DIODE_BLANK = ITEMS.register("rectifier_diode_blank", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GERMANIUM_RECTIFIER = ITEMS.register("germanium_rectifier", () -> new Item(new Item.Properties()));
    // Ruled diffraction grating (RP-O04): aluminium film on a glass substrate, grooves ruled by the Prism Scorer.
    public static final RegistryObject<Item> ALUMINIZED_GRATING_BLANK = ITEMS.register("aluminized_grating_blank", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RULED_DIFFRACTION_GRATING = ITEMS.register("ruled_diffraction_grating", () -> new Item(new Item.Properties()));
    // Coated interference filter (RP-O05): quartz-laminated glass window, hafnium dielectric film from the Silvering Bath.
    public static final RegistryObject<Item> FILTER_WINDOW_BLANK = ITEMS.register("filter_window_blank", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COATED_INTERFERENCE_FILTER = ITEMS.register("coated_interference_filter", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PHOTODIODE_BLANK = ITEMS.register("photodiode_blank", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PHOTODETECTOR_CELL = ITEMS.register("photodetector_cell", () -> new Item(new Item.Properties()));
    // Rated pressure shell (RP-S04): stainless barrel between two flanged heads, seam-welded by the Lattice Welder.
    public static final RegistryObject<Item> PRESSURE_SHELL_BLANK = ITEMS.register("pressure_shell_blank", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RATED_PRESSURE_SHELL = ITEMS.register("rated_pressure_shell", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LOGIC_BOARD_BLANK = ITEMS.register("logic_board_blank", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PROGRAMMED_LOGIC_CONTROLLER = ITEMS.register("programmed_logic_controller", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PLATINUM_SENSOR_BLANK = ITEMS.register("platinum_sensor_blank", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PLATINUM_RESISTANCE_PROBE = ITEMS.register("platinum_resistance_probe", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COIL_SPRING = ITEMS.register("coil_spring", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RETAINING_RING = ITEMS.register("retaining_ring", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> HEX_BOLT_SET = ITEMS.register("hex_bolt_set", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TAB_LOCK_WASHER = ITEMS.register("tab_lock_washer", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RIBBED_MACHINE_BED = ITEMS.register("ribbed_machine_bed", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STAMPED_CONTACTOR_ENCLOSURE = ITEMS.register("stamped_contactor_enclosure", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GREEN_GRINDING_WHEEL = ITEMS.register("green_grinding_wheel", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> VITRIFIED_GRINDING_WHEEL = ITEMS.register("vitrified_grinding_wheel", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GREEN_ASSAY_CRUCIBLE = ITEMS.register("green_assay_crucible", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ASSAY_CRUCIBLE = ITEMS.register("assay_crucible", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GREEN_PORCELAIN_INSULATOR =ITEMS.register("green_porcelain_insulator", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PORCELAIN_INSULATOR = ITEMS.register("porcelain_insulator", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BATH_CIRCULATION_PUMP = ITEMS.register("bath_circulation_pump", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> QUENCH_VAT = BLOCKS.register("quench_vat", QuenchVatBlock::new);
    public static final RegistryObject<Item> QUENCH_VAT_ITEM = ITEMS.register("quench_vat", () -> new BlockItem(QUENCH_VAT.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<QuenchVatBlockEntity>> QUENCH_VAT_ENTITY = ENTITIES.register("quench_vat",
        () -> BlockEntityType.Builder.of(QuenchVatBlockEntity::new, QUENCH_VAT.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<QuenchVatMenu>> QUENCH_VAT_MENU = MENUS.register("quench_vat",
        () -> new net.minecraft.world.inventory.MenuType<>(QuenchVatMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.QuenchingRecipe>> QUENCHING = TYPES.register("quenching", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":quenching"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.QuenchingRecipe>> QUENCHING_SERIALIZER = SERIALIZERS.register("quenching", com.starmaylight.arcana_industry.recipe.QuenchingRecipe.Serializer::new);
    public static final RegistryObject<Item> WOUND_COIL = ITEMS.register("wound_coil", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> INDUCTION_COILER = BLOCKS.register("induction_coiler", InductionCoilerBlock::new);
    public static final RegistryObject<Item> INDUCTION_COILER_ITEM = ITEMS.register("induction_coiler", () -> new BlockItem(INDUCTION_COILER.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.InductionWindingRecipe>> INDUCTION_WINDING = TYPES.register("induction_winding", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":induction_winding"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.InductionWindingRecipe>> INDUCTION_WINDING_SERIALIZER = SERIALIZERS.register("induction_winding", com.starmaylight.arcana_industry.recipe.InductionWindingRecipe.Serializer::new);
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> INDUCTION_COILER_ENTITY = ENTITIES.register("induction_coiler",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("induction_coiler", pos, state), INDUCTION_COILER.get()).build(null));
    public static final RegistryObject<Item> CHARGED_CAPACITOR = ITEMS.register("charged_capacitor", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> CAPACITOR_ARRAY = BLOCKS.register("capacitor_array", CapacitorArrayBlock::new);
    public static final RegistryObject<Item> CAPACITOR_ARRAY_ITEM = ITEMS.register("capacitor_array", () -> new BlockItem(CAPACITOR_ARRAY.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.CapacitorChargingRecipe>> CAPACITOR_CHARGING = TYPES.register("capacitor_charging", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":capacitor_charging"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.CapacitorChargingRecipe>> CAPACITOR_CHARGING_SERIALIZER = SERIALIZERS.register("capacitor_charging", com.starmaylight.arcana_industry.recipe.CapacitorChargingRecipe.Serializer::new);
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> CAPACITOR_ARRAY_ENTITY = ENTITIES.register("capacitor_array",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("capacitor_array", pos, state), CAPACITOR_ARRAY.get()).build(null));
    public static final RegistryObject<Block> STOCK_HATCH = BLOCKS.register("stock_hatch", StockHatchBlock::new);
    public static final RegistryObject<Item> STOCK_HATCH_ITEM = ITEMS.register("stock_hatch", () -> new BlockItem(STOCK_HATCH.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<StockHatchBlockEntity>> STOCK_HATCH_ENTITY = ENTITIES.register("stock_hatch",
        () -> BlockEntityType.Builder.of(StockHatchBlockEntity::new, STOCK_HATCH.get()).build(null));
    public static final RegistryObject<Block> STRATA_EXCAVATOR = BLOCKS.register("strata_excavator", StrataExcavatorBlock::new);
    public static final RegistryObject<Item> STRATA_EXCAVATOR_ITEM = ITEMS.register("strata_excavator", () -> new BlockItem(STRATA_EXCAVATOR.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<StrataExcavatorBlockEntity>> STRATA_EXCAVATOR_ENTITY = ENTITIES.register("strata_excavator",
        () -> BlockEntityType.Builder.of(StrataExcavatorBlockEntity::new, STRATA_EXCAVATOR.get()).build(null));
    public static final RegistryObject<Item> MAGNETIZED_CORE = ITEMS.register("magnetized_core", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> MAGNETIZER = BLOCKS.register("magnetizer", MagnetizerBlock::new);
    public static final RegistryObject<Item> MAGNETIZER_ITEM = ITEMS.register("magnetizer", () -> new BlockItem(MAGNETIZER.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.MagnetizationRecipe>> MAGNETIZATION = TYPES.register("magnetization", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":magnetization"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.MagnetizationRecipe>> MAGNETIZATION_SERIALIZER = SERIALIZERS.register("magnetization", com.starmaylight.arcana_industry.recipe.MagnetizationRecipe.Serializer::new);
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> MAGNETIZER_ENTITY = ENTITIES.register("magnetizer",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("magnetizer", pos, state), MAGNETIZER.get()).build(null));
    public static final RegistryObject<Item> FUSED_STEEL_PLATE = ITEMS.register("fused_steel_plate", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> ARC_PRESS = BLOCKS.register("arc_press", ArcPressBlock::new);
    public static final RegistryObject<Item> ARC_PRESS_ITEM = ITEMS.register("arc_press", () -> new BlockItem(ARC_PRESS.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.ArcDischargeRecipe>> ARC_DISCHARGE = TYPES.register("arc_discharge", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":arc_discharge"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.ArcDischargeRecipe>> ARC_DISCHARGE_SERIALIZER = SERIALIZERS.register("arc_discharge", com.starmaylight.arcana_industry.recipe.ArcDischargeRecipe.Serializer::new);
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> ARC_PRESS_ENTITY = ENTITIES.register("arc_press",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("arc_press", pos, state), ARC_PRESS.get()).build(null));
    public static final RegistryObject<Item> RECTIFIED_FILAMENT = ITEMS.register("rectified_filament", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> FLUX_RECTIFIER = BLOCKS.register("flux_rectifier", FluxRectifierBlock::new);
    public static final RegistryObject<Item> FLUX_RECTIFIER_ITEM = ITEMS.register("flux_rectifier", () -> new BlockItem(FLUX_RECTIFIER.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.FluxRectificationRecipe>> FLUX_RECTIFICATION = TYPES.register("flux_rectification", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":flux_rectification"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.FluxRectificationRecipe>> FLUX_RECTIFICATION_SERIALIZER = SERIALIZERS.register("flux_rectification", com.starmaylight.arcana_industry.recipe.FluxRectificationRecipe.Serializer::new);
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> FLUX_RECTIFIER_ENTITY = ENTITIES.register("flux_rectifier",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("flux_rectifier", pos, state), FLUX_RECTIFIER.get()).build(null));
    public static final RegistryObject<Item> STATOR_COIL = ITEMS.register("stator_coil", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> POLARITY_INVERTER = BLOCKS.register("polarity_inverter", PolarityInverterBlock::new);
    public static final RegistryObject<Item> POLARITY_INVERTER_ITEM = ITEMS.register("polarity_inverter", () -> new BlockItem(POLARITY_INVERTER.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.PolarityInversionRecipe>> POLARITY_INVERSION = TYPES.register("polarity_inversion", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":polarity_inversion"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.PolarityInversionRecipe>> POLARITY_INVERSION_SERIALIZER = SERIALIZERS.register("polarity_inversion", com.starmaylight.arcana_industry.recipe.PolarityInversionRecipe.Serializer::new);
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> POLARITY_INVERTER_ENTITY = ENTITIES.register("polarity_inverter",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("polarity_inverter", pos, state), POLARITY_INVERTER.get()).build(null));
    public static final RegistryObject<Item> DEGAUSSED_ARMATURE = ITEMS.register("degaussed_armature", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> DEGAUSS_COIL = BLOCKS.register("degauss_coil", DegaussCoilBlock::new);
    public static final RegistryObject<Item> DEGAUSS_COIL_ITEM = ITEMS.register("degauss_coil", () -> new BlockItem(DEGAUSS_COIL.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.DegaussingRecipe>> DEGAUSSING = TYPES.register("degaussing", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":degaussing"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.DegaussingRecipe>> DEGAUSSING_SERIALIZER = SERIALIZERS.register("degaussing", com.starmaylight.arcana_industry.recipe.DegaussingRecipe.Serializer::new);
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> DEGAUSS_COIL_ENTITY = ENTITIES.register("degauss_coil",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("degauss_coil", pos, state), DEGAUSS_COIL.get()).build(null));
    public static final RegistryObject<Item> LAMINATED_SHELL = ITEMS.register("laminated_shell", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TRANSFORMER_CORE = ITEMS.register("transformer_core", () -> new Item(new Item.Properties()));
    // Short-stroke actuator (RP-A05): cold-drawn iron rod, induction-annealed into a soft plunger, inside a wound coil.
    public static final RegistryObject<Item> DRAWN_IRON_ROD = ITEMS.register("drawn_iron_rod", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SOFT_IRON_PLUNGER = ITEMS.register("soft_iron_plunger", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SOLENOID = ITEMS.register("solenoid", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> FORMING_DIE = ITEMS.register("forming_die", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GREEN_TUYERE = ITEMS.register("green_tuyere", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CERAMIC_TUYERE = ITEMS.register("ceramic_tuyere", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GAUGE_BLOCK = ITEMS.register("gauge_block", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SILVER_CONTACT_BLANK = ITEMS.register("silver_contact_blank", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PULSE_CONTACT_ASSEMBLY = ITEMS.register("pulse_contact_assembly", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> ARMOR_DELAMINATOR = BLOCKS.register("armor_delaminator", ArmorDelaminatorBlock::new);
    public static final RegistryObject<Item> ARMOR_DELAMINATOR_ITEM = ITEMS.register("armor_delaminator", () -> new BlockItem(ARMOR_DELAMINATOR.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.ArmorDelaminationRecipe>> ARMOR_DELAMINATION = TYPES.register("armor_delamination", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":armor_delamination"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.ArmorDelaminationRecipe>> ARMOR_DELAMINATION_SERIALIZER = SERIALIZERS.register("armor_delamination", com.starmaylight.arcana_industry.recipe.ArmorDelaminationRecipe.Serializer::new);
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> ARMOR_DELAMINATOR_ENTITY = ENTITIES.register("armor_delaminator",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("armor_delaminator", pos, state), ARMOR_DELAMINATOR.get()).build(null));
    public static final RegistryObject<Item> SIGNAL_MATRIX = ITEMS.register("signal_matrix", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> SIGNAL_IMPRINTER = BLOCKS.register("signal_imprinter", SignalImprinterBlock::new);
    public static final RegistryObject<Item> SIGNAL_IMPRINTER_ITEM = ITEMS.register("signal_imprinter", () -> new BlockItem(SIGNAL_IMPRINTER.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.SignalImprintingRecipe>> SIGNAL_IMPRINTING = TYPES.register("signal_imprinting", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":signal_imprinting"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.SignalImprintingRecipe>> SIGNAL_IMPRINTING_SERIALIZER = SERIALIZERS.register("signal_imprinting", com.starmaylight.arcana_industry.recipe.SignalImprintingRecipe.Serializer::new);
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> SIGNAL_IMPRINTER_ENTITY = ENTITIES.register("signal_imprinter",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("signal_imprinter", pos, state), SIGNAL_IMPRINTER.get()).build(null));
    public static final RegistryObject<Item> VERDANT_RESONATOR = ITEMS.register("verdant_resonator", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> RESONANT_CULTIVATOR = BLOCKS.register("resonant_cultivator", ResonantCultivatorBlock::new);
    public static final RegistryObject<Item> RESONANT_CULTIVATOR_ITEM = ITEMS.register("resonant_cultivator", () -> new BlockItem(RESONANT_CULTIVATOR.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.ResonantCultivationRecipe>> RESONANT_CULTIVATION = TYPES.register("resonant_cultivation", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":resonant_cultivation"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.ResonantCultivationRecipe>> RESONANT_CULTIVATION_SERIALIZER = SERIALIZERS.register("resonant_cultivation", com.starmaylight.arcana_industry.recipe.ResonantCultivationRecipe.Serializer::new);
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> RESONANT_CULTIVATOR_ENTITY = ENTITIES.register("resonant_cultivator",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("resonant_cultivator", pos, state), RESONANT_CULTIVATOR.get()).build(null));
    public static final RegistryObject<Item> CHROMATIC_ESSENCE = ITEMS.register("chromatic_essence", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> VERDANT_DISTILLER = BLOCKS.register("verdant_distiller", VerdantDistillerBlock::new);
    public static final RegistryObject<Item> VERDANT_DISTILLER_ITEM = ITEMS.register("verdant_distiller", () -> new BlockItem(VERDANT_DISTILLER.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.VerdantDistillationRecipe>> VERDANT_DISTILLATION = TYPES.register("verdant_distillation", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":verdant_distillation"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.VerdantDistillationRecipe>> VERDANT_DISTILLATION_SERIALIZER = SERIALIZERS.register("verdant_distillation", com.starmaylight.arcana_industry.recipe.VerdantDistillationRecipe.Serializer::new);
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> VERDANT_DISTILLER_ENTITY = ENTITIES.register("verdant_distiller",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("verdant_distiller", pos, state), VERDANT_DISTILLER.get()).build(null));
    public static final RegistryObject<Item> PRISMATIC_GLAZE = ITEMS.register("prismatic_glaze", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> CHROMATIC_KILN = BLOCKS.register("chromatic_kiln", ChromaticKilnBlock::new);
    public static final RegistryObject<Item> CHROMATIC_KILN_ITEM = ITEMS.register("chromatic_kiln", () -> new BlockItem(CHROMATIC_KILN.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.ChromaticGlazingRecipe>> CHROMATIC_GLAZING = TYPES.register("chromatic_glazing", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":chromatic_glazing"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.ChromaticGlazingRecipe>> CHROMATIC_GLAZING_SERIALIZER = SERIALIZERS.register("chromatic_glazing", com.starmaylight.arcana_industry.recipe.ChromaticGlazingRecipe.Serializer::new);
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> CHROMATIC_KILN_ENTITY = ENTITIES.register("chromatic_kiln",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("chromatic_kiln", pos, state), CHROMATIC_KILN.get()).build(null));
    public static final RegistryObject<Item> REFRACTED_MATRIX = ITEMS.register("refracted_matrix", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> PRISMATIC_PRESS = BLOCKS.register("prismatic_press", PrismaticPressBlock::new);
    public static final RegistryObject<Item> PRISMATIC_PRESS_ITEM = ITEMS.register("prismatic_press", () -> new BlockItem(PRISMATIC_PRESS.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.PrismaticReclamationRecipe>> PRISMATIC_RECLAMATION = TYPES.register("prismatic_reclamation", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":prismatic_reclamation"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.PrismaticReclamationRecipe>> PRISMATIC_RECLAMATION_SERIALIZER = SERIALIZERS.register("prismatic_reclamation", com.starmaylight.arcana_industry.recipe.PrismaticReclamationRecipe.Serializer::new);
    public static final RegistryObject<BlockEntityType<PrismaticPressBlockEntity>> PRISMATIC_PRESS_ENTITY = ENTITIES.register("prismatic_press",
        () -> BlockEntityType.Builder.of(PrismaticPressBlockEntity::new, PRISMATIC_PRESS.get()).build(null));
    public static final RegistryObject<Item> LUMEN_FILAMENT = ITEMS.register("lumen_filament", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> SPECTRAL_LOOM = BLOCKS.register("spectral_loom", SpectralLoomBlock::new);
    public static final RegistryObject<Item> SPECTRAL_LOOM_ITEM = ITEMS.register("spectral_loom", () -> new BlockItem(SPECTRAL_LOOM.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.TextileWeavingRecipe>> TEXTILE_WEAVING = TYPES.register("textile_weaving", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":textile_weaving"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.TextileWeavingRecipe>> TEXTILE_WEAVING_SERIALIZER = SERIALIZERS.register("textile_weaving", com.starmaylight.arcana_industry.recipe.TextileWeavingRecipe.Serializer::new);
    public static final RegistryObject<BlockEntityType<SpectralLoomBlockEntity>> SPECTRAL_LOOM_ENTITY = ENTITIES.register("spectral_loom",
        () -> BlockEntityType.Builder.of(SpectralLoomBlockEntity::new, SPECTRAL_LOOM.get()).build(null));
    public static final RegistryObject<Item> WOVEN_SIGIL = ITEMS.register("woven_sigil", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> HERALDIC_FRAME = BLOCKS.register("heraldic_frame", HeraldicFrameBlock::new);
    public static final RegistryObject<Item> HERALDIC_FRAME_ITEM = ITEMS.register("heraldic_frame", () -> new BlockItem(HERALDIC_FRAME.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.HeraldicWeavingRecipe>> HERALDIC_WEAVING = TYPES.register("heraldic_weaving", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":heraldic_weaving"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.HeraldicWeavingRecipe>> HERALDIC_WEAVING_SERIALIZER = SERIALIZERS.register("heraldic_weaving", com.starmaylight.arcana_industry.recipe.HeraldicWeavingRecipe.Serializer::new);
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> HERALDIC_FRAME_ENTITY = ENTITIES.register("heraldic_frame",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("heraldic_frame", pos, state), HERALDIC_FRAME.get()).build(null));
    public static final RegistryObject<Item> RELIQUARY_CORE = ITEMS.register("reliquary_core", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> RELIQUARY_STAND = BLOCKS.register("reliquary_stand", ReliquaryStandBlock::new);
    public static final RegistryObject<Item> RELIQUARY_STAND_ITEM = ITEMS.register("reliquary_stand", () -> new BlockItem(RELIQUARY_STAND.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.ReliquaryBindingRecipe>> RELIQUARY_BINDING = TYPES.register("reliquary_binding", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":reliquary_binding"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.ReliquaryBindingRecipe>> RELIQUARY_BINDING_SERIALIZER = SERIALIZERS.register("reliquary_binding", com.starmaylight.arcana_industry.recipe.ReliquaryBindingRecipe.Serializer::new);
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> RELIQUARY_STAND_ENTITY = ENTITIES.register("reliquary_stand",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("reliquary_stand", pos, state), RELIQUARY_STAND.get()).build(null));
    public static final RegistryObject<Item> CONCORDANCE_SEAL = ITEMS.register("concordance_seal", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> CONCORDANCE_ALTAR = BLOCKS.register("concordance_altar", ConcordanceAltarBlock::new);
    public static final RegistryObject<Item> CONCORDANCE_ALTAR_ITEM = ITEMS.register("concordance_altar", () -> new BlockItem(CONCORDANCE_ALTAR.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.ConcordanceRecipe>> CONCORDANCE = TYPES.register("concordance", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":concordance"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.ConcordanceRecipe>> CONCORDANCE_SERIALIZER = SERIALIZERS.register("concordance", com.starmaylight.arcana_industry.recipe.ConcordanceRecipe.Serializer::new);
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> CONCORDANCE_ALTAR_ENTITY = ENTITIES.register("concordance_altar",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("concordance_altar", pos, state), CONCORDANCE_ALTAR.get()).build(null));
    public static final RegistryObject<Item> IRIDESCENT_KEY = ITEMS.register("iridescent_key", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> IRIDESCENT_BASIN = BLOCKS.register("iridescent_basin", IridescentBasinBlock::new);
    public static final RegistryObject<Item> IRIDESCENT_BASIN_ITEM = ITEMS.register("iridescent_basin", () -> new BlockItem(IRIDESCENT_BASIN.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.IridescentRecipe>> IRIDESCENT = TYPES.register("iridescent", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":iridescent"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.IridescentRecipe>> IRIDESCENT_SERIALIZER = SERIALIZERS.register("iridescent", com.starmaylight.arcana_industry.recipe.IridescentRecipe.Serializer::new);
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> IRIDESCENT_BASIN_ENTITY = ENTITIES.register("iridescent_basin",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("iridescent_basin", pos, state), IRIDESCENT_BASIN.get()).build(null));
    public static final RegistryObject<Item> LUNAR_ESSENCE = ITEMS.register("lunar_essence", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> NOCTILUCENT_CONDENSER = BLOCKS.register("noctilucent_condenser", NoctilucentCondenserBlock::new);
    public static final RegistryObject<Item> NOCTILUCENT_CONDENSER_ITEM = ITEMS.register("noctilucent_condenser", () -> new BlockItem(NOCTILUCENT_CONDENSER.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<NoctilucentCondenserBlockEntity>> NOCTILUCENT_CONDENSER_ENTITY = ENTITIES.register("noctilucent_condenser",
        () -> BlockEntityType.Builder.of(NoctilucentCondenserBlockEntity::new, NOCTILUCENT_CONDENSER.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<NoctilucentCondenserMenu>> NOCTILUCENT_CONDENSER_MENU = MENUS.register("noctilucent_condenser",
        () -> new net.minecraft.world.inventory.MenuType<>(NoctilucentCondenserMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.CondensingRecipe>> CONDENSING = TYPES.register("condensing", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":condensing"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.CondensingRecipe>> CONDENSING_SERIALIZER = SERIALIZERS.register("condensing", com.starmaylight.arcana_industry.recipe.CondensingRecipe.Serializer::new);
    public static final RegistryObject<Item> ASTRAL_CORE = ITEMS.register("astral_core", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> ASTRAL_RELAY = BLOCKS.register("astral_relay", AstralRelayBlock::new);
    public static final RegistryObject<Item> ASTRAL_RELAY_ITEM = ITEMS.register("astral_relay", () -> new BlockItem(ASTRAL_RELAY.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<AstralRelayBlockEntity>> ASTRAL_RELAY_ENTITY = ENTITIES.register("astral_relay",
        () -> BlockEntityType.Builder.of(AstralRelayBlockEntity::new, ASTRAL_RELAY.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<AstralRelayMenu>> ASTRAL_RELAY_MENU = MENUS.register("astral_relay",
        () -> new net.minecraft.world.inventory.MenuType<>(AstralRelayMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.RelayingRecipe>> RELAYING = TYPES.register("relaying", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":relaying"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.RelayingRecipe>> RELAYING_SERIALIZER = SERIALIZERS.register("relaying", com.starmaylight.arcana_industry.recipe.RelayingRecipe.Serializer::new);
    public static final RegistryObject<Block> STELLAR_CRUCIBLE = BLOCKS.register("stellar_crucible", StellarCrucibleBlock::new);
    public static final RegistryObject<Item> STELLAR_CRUCIBLE_ITEM = ITEMS.register("stellar_crucible", () -> new BlockItem(STELLAR_CRUCIBLE.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<StellarCrucibleBlockEntity>> STELLAR_CRUCIBLE_ENTITY = ENTITIES.register("stellar_crucible",
        () -> BlockEntityType.Builder.of(StellarCrucibleBlockEntity::new, STELLAR_CRUCIBLE.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<StellarCrucibleMenu>> STELLAR_CRUCIBLE_MENU = MENUS.register("stellar_crucible",
        () -> new net.minecraft.world.inventory.MenuType<>(StellarCrucibleMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.FusingRecipe>> FUSING = TYPES.register("fusing", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":fusing"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.FusingRecipe>> FUSING_SERIALIZER = SERIALIZERS.register("fusing", com.starmaylight.arcana_industry.recipe.FusingRecipe.Serializer::new);
    public static final RegistryObject<Item> GROUND_LENS = ITEMS.register("ground_lens", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> LENS_GRINDER = BLOCKS.register("lens_grinder", LensGrinderBlock::new);
    public static final RegistryObject<Item> LENS_GRINDER_ITEM = ITEMS.register("lens_grinder", () -> new BlockItem(LENS_GRINDER.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<LensGrinderBlockEntity>> LENS_GRINDER_ENTITY = ENTITIES.register("lens_grinder",
        () -> BlockEntityType.Builder.of(LensGrinderBlockEntity::new, LENS_GRINDER.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<LensGrinderMenu>> LENS_GRINDER_MENU = MENUS.register("lens_grinder",
        () -> new net.minecraft.world.inventory.MenuType<>(LensGrinderMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.GrindingRecipe>> GRINDING = TYPES.register("grinding", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":grinding"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.GrindingRecipe>> GRINDING_SERIALIZER = SERIALIZERS.register("grinding", com.starmaylight.arcana_industry.recipe.GrindingRecipe.Serializer::new);
    public static final RegistryObject<Item> CORONA_FRAGMENT = ITEMS.register("corona_fragment", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CORONA_EMBER = ITEMS.register("corona_ember", () -> new Item(new Item.Properties()));
    // Player-kill trophies unique to each hostile mob; the crusher renders them into their mob's material.
    public static final RegistryObject<Item> CORRODED_MANDIBLE = ITEMS.register("corroded_mandible", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CINDER_CARAPACE = ITEMS.register("cinder_carapace", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> FLUX_FLETCHING = ITEMS.register("flux_fletching", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STATIC_GLAND = ITEMS.register("static_gland", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MITE_CHITIN = ITEMS.register("mite_chitin", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> CORONA_COLLECTOR = BLOCKS.register("corona_collector", CoronaCollectorBlock::new);
    public static final RegistryObject<Item> CORONA_COLLECTOR_ITEM = ITEMS.register("corona_collector", () -> new BlockItem(CORONA_COLLECTOR.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<CoronaCollectorBlockEntity>> CORONA_COLLECTOR_ENTITY = ENTITIES.register("corona_collector",
        () -> BlockEntityType.Builder.of(CoronaCollectorBlockEntity::new, CORONA_COLLECTOR.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<CoronaCollectorMenu>> CORONA_COLLECTOR_MENU = MENUS.register("corona_collector",
        () -> new net.minecraft.world.inventory.MenuType<>(CoronaCollectorMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.CollectionRecipe>> COLLECTION = TYPES.register("collection", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":collection"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.CollectionRecipe>> COLLECTION_SERIALIZER = SERIALIZERS.register("collection", com.starmaylight.arcana_industry.recipe.CollectionRecipe.Serializer::new);
    public static final RegistryObject<Item> ATTUNED_STARSHARD = ITEMS.register("attuned_starshard", () -> new com.starmaylight.arcana_industry.magic.AttunedStarshardItem(new Item.Properties()));
    public static final RegistryObject<Block> SANCTUM_PYLON = BLOCKS.register("sanctum_pylon", SanctumPylonBlock::new);
    public static final RegistryObject<Item> SANCTUM_PYLON_ITEM = ITEMS.register("sanctum_pylon", () -> new BlockItem(SANCTUM_PYLON.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.SanctumAttunementRecipe>> SANCTUM_ATTUNEMENT = TYPES.register("sanctum_attunement", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":sanctum_attunement"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.SanctumAttunementRecipe>> SANCTUM_ATTUNEMENT_SERIALIZER = SERIALIZERS.register("sanctum_attunement", com.starmaylight.arcana_industry.recipe.SanctumAttunementRecipe.Serializer::new);
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> SANCTUM_PYLON_ENTITY = ENTITIES.register("sanctum_pylon",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("sanctum_pylon", pos, state), SANCTUM_PYLON.get()).build(null));
    public static final RegistryObject<Block> SWAGE_PRESS = BLOCKS.register("swage_press", SwagePressBlock::new);
    public static final RegistryObject<Item> SWAGE_PRESS_ITEM = ITEMS.register("swage_press", () -> new BlockItem(SWAGE_PRESS.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.SwagingRecipe>> SWAGING = TYPES.register("swaging", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":swaging"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.SwagingRecipe>> SWAGING_SERIALIZER = SERIALIZERS.register("swaging", com.starmaylight.arcana_industry.recipe.SwagingRecipe.Serializer::new);
    public static final RegistryObject<Item> SWAGED_FITTING = ITEMS.register("swaged_fitting", () -> new Item(new Item.Properties()));
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> SWAGE_PRESS_ENTITY = ENTITIES.register("swage_press",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("swage_press", pos, state), SWAGE_PRESS.get()).build(null));
    public static final RegistryObject<Block> CRIMP_PRESS = BLOCKS.register("crimp_press", CrimpPressBlock::new);
    public static final RegistryObject<Item> CRIMP_PRESS_ITEM = ITEMS.register("crimp_press", () -> new BlockItem(CRIMP_PRESS.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.CrimpingRecipe>> CRIMPING = TYPES.register("crimping", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":crimping"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.CrimpingRecipe>> CRIMPING_SERIALIZER = SERIALIZERS.register("crimping", com.starmaylight.arcana_industry.recipe.CrimpingRecipe.Serializer::new);
    public static final RegistryObject<Item> CRIMPED_COLLAR = ITEMS.register("crimped_collar", () -> new Item(new Item.Properties()));
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> CRIMP_PRESS_ENTITY = ENTITIES.register("crimp_press",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("crimp_press", pos, state), CRIMP_PRESS.get()).build(null));
    public static final RegistryObject<Block> BROACH_PRESS = BLOCKS.register("broach_press", BroachPressBlock::new);
    public static final RegistryObject<Item> BROACH_PRESS_ITEM = ITEMS.register("broach_press", () -> new BlockItem(BROACH_PRESS.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.BroachingRecipe>> BROACHING = TYPES.register("broaching", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":broaching"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.BroachingRecipe>> BROACHING_SERIALIZER = SERIALIZERS.register("broaching", com.starmaylight.arcana_industry.recipe.BroachingRecipe.Serializer::new);
    public static final RegistryObject<Item> BROACHED_PIN = ITEMS.register("broached_pin", () -> new Item(new Item.Properties()));
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> BROACH_PRESS_ENTITY = ENTITIES.register("broach_press",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("broach_press", pos, state), BROACH_PRESS.get()).build(null));
    public static final RegistryObject<Block> HYDRAULIC_RIVETER = BLOCKS.register("hydraulic_riveter", HydraulicRiveterBlock::new);
    public static final RegistryObject<Item> HYDRAULIC_RIVETER_ITEM = ITEMS.register("hydraulic_riveter", () -> new BlockItem(HYDRAULIC_RIVETER.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.RivetingRecipe>> RIVETING = TYPES.register("riveting", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":riveting"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.RivetingRecipe>> RIVETING_SERIALIZER = SERIALIZERS.register("riveting", com.starmaylight.arcana_industry.recipe.RivetingRecipe.Serializer::new);
    public static final RegistryObject<Item> RIVETED_BRACE = ITEMS.register("riveted_brace", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ALUMINUM_RIVET = ITEMS.register("aluminum_rivet", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PREPARED_BRAZE_JOINT = ITEMS.register("prepared_braze_joint", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BRAZED_JOINT = ITEMS.register("brazed_joint", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DRAWN_COPPER_TUBE = ITEMS.register("drawn_copper_tube", () -> new Item(new Item.Properties()));
    // Electric heating element (RP-H02): 80/20 Ni-Cr alloy fired in the coiled Kiln, drawn to resistance wire, sheathed in stainless steel.
    public static final RegistryObject<Item> NICHROME_CHARGE = ITEMS.register("nichrome_charge", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> NICHROME_BILLET = ITEMS.register("nichrome_billet", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> NICHROME_RESISTANCE_WIRE = ITEMS.register("nichrome_resistance_wire", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> NICHROME_HEATING_ELEMENT = ITEMS.register("nichrome_heating_element", () -> new Item(new Item.Properties()));
    // Shell-and-tube heat exchanger (RP-H03): drawn copper tubes in sealed-steel tubesheets, roller-expanded, then shelled in stainless.
    public static final RegistryObject<Item> HEAT_EXCHANGER_TUBE_BUNDLE = ITEMS.register("heat_exchanger_tube_bundle", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> EXPANDED_TUBE_BUNDLE = ITEMS.register("expanded_tube_bundle", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> HEAT_EXCHANGER = ITEMS.register("heat_exchanger", () -> new Item(new Item.Properties()));
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> HYDRAULIC_RIVETER_ENTITY = ENTITIES.register("hydraulic_riveter",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("hydraulic_riveter", pos, state), HYDRAULIC_RIVETER.get()).build(null));
    public static final RegistryObject<Block> FLANGE_PRESS = BLOCKS.register("flange_press", FlangePressBlock::new);
    public static final RegistryObject<Item> FLANGE_PRESS_ITEM = ITEMS.register("flange_press", () -> new BlockItem(FLANGE_PRESS.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.FlangingRecipe>> FLANGING = TYPES.register("flanging", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":flanging"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.FlangingRecipe>> FLANGING_SERIALIZER = SERIALIZERS.register("flanging", com.starmaylight.arcana_industry.recipe.FlangingRecipe.Serializer::new);
    public static final RegistryObject<Item> FLANGED_RING = ITEMS.register("flanged_ring", () -> new Item(new Item.Properties()));
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> FLANGE_PRESS_ENTITY = ENTITIES.register("flange_press",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("flange_press", pos, state), FLANGE_PRESS.get()).build(null));
    public static final RegistryObject<Block> SILVERING_BATH = BLOCKS.register("silvering_bath", SilveringBathBlock::new);
    public static final RegistryObject<Item> SILVERING_BATH_ITEM = ITEMS.register("silvering_bath", () -> new BlockItem(SILVERING_BATH.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.SilveringRecipe>> SILVERING = TYPES.register("silvering", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":silvering"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.SilveringRecipe>> SILVERING_SERIALIZER = SERIALIZERS.register("silvering", com.starmaylight.arcana_industry.recipe.SilveringRecipe.Serializer::new);
    public static final RegistryObject<Item> SILVERED_MIRROR = ITEMS.register("silvered_mirror", () -> new Item(new Item.Properties()));
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> SILVERING_BATH_ENTITY = ENTITIES.register("silvering_bath",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("silvering_bath", pos, state), SILVERING_BATH.get()).build(null));
    public static final RegistryObject<Block> ENAMEL_KILN = BLOCKS.register("enamel_kiln", EnamelKilnBlock::new);
    public static final RegistryObject<Item> ENAMEL_KILN_ITEM = ITEMS.register("enamel_kiln", () -> new BlockItem(ENAMEL_KILN.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.EnamelingRecipe>> ENAMELING = TYPES.register("enameling", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":enameling"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.EnamelingRecipe>> ENAMELING_SERIALIZER = SERIALIZERS.register("enameling", com.starmaylight.arcana_industry.recipe.EnamelingRecipe.Serializer::new);
    public static final RegistryObject<Item> HELIOGRAPH_PLATE = ITEMS.register("heliograph_plate", () -> new Item(new Item.Properties()));
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> ENAMEL_KILN_ENTITY = ENTITIES.register("enamel_kiln",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("enamel_kiln", pos, state), ENAMEL_KILN.get()).build(null));
    public static final RegistryObject<Block> DRAWING_BENCH = BLOCKS.register("drawing_bench", DrawingBenchBlock::new);
    public static final RegistryObject<Item> DRAWING_BENCH_ITEM = ITEMS.register("drawing_bench", () -> new BlockItem(DRAWING_BENCH.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.WireDrawingRecipe>> WIRE_DRAWING = TYPES.register("wire_drawing", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":wire_drawing"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.WireDrawingRecipe>> WIRE_DRAWING_SERIALIZER = SERIALIZERS.register("wire_drawing", com.starmaylight.arcana_industry.recipe.WireDrawingRecipe.Serializer::new);
    public static final RegistryObject<Item> RHENIUM_FILAMENT = ITEMS.register("rhenium_filament", () -> new Item(new Item.Properties()));
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> DRAWING_BENCH_ENTITY = ENTITIES.register("drawing_bench",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("drawing_bench", pos, state), DRAWING_BENCH.get()).build(null));
    public static final RegistryObject<Block> ASSAY_HEARTH = BLOCKS.register("assay_hearth", AssayHearthBlock::new);
    public static final RegistryObject<Item> ASSAY_HEARTH_ITEM = ITEMS.register("assay_hearth", () -> new BlockItem(ASSAY_HEARTH.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.CupellationRecipe>> CUPELLATION = TYPES.register("cupellation", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":cupellation"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.CupellationRecipe>> CUPELLATION_SERIALIZER = SERIALIZERS.register("cupellation", com.starmaylight.arcana_industry.recipe.CupellationRecipe.Serializer::new);
    public static final RegistryObject<Item> ASSAY_BEAD = ITEMS.register("assay_bead", () -> new Item(new Item.Properties()));
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> ASSAY_HEARTH_ENTITY = ENTITIES.register("assay_hearth",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("assay_hearth", pos, state), ASSAY_HEARTH.get()).build(null));
    public static final RegistryObject<Block> COAL_FORGE = BLOCKS.register("coal_forge", CoalForgeBlock::new);
    public static final RegistryObject<Item> COAL_FORGE_ITEM = ITEMS.register("coal_forge", () -> new BlockItem(COAL_FORGE.get(), new Item.Properties()));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.ForgingRecipe>> FORGING = TYPES.register("forging", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":forging"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.ForgingRecipe>> FORGING_SERIALIZER = SERIALIZERS.register("forging", com.starmaylight.arcana_industry.recipe.ForgingRecipe.Serializer::new);
    public static final RegistryObject<Item> CUPEL_TONGS = ITEMS.register("cupel_tongs", () -> new Item(new Item.Properties()));
    public static final RegistryObject<BlockEntityType<HandFedControllerBlockEntity>> COAL_FORGE_ENTITY = ENTITIES.register("coal_forge",
        () -> BlockEntityType.Builder.of((pos, state) -> controllerEntity("coal_forge", pos, state), COAL_FORGE.get()).build(null));
    public static final RegistryObject<Item> FILTERED_PRISM = ITEMS.register("filtered_prism", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> SPECTRAL_FILTER = BLOCKS.register("spectral_filter", SpectralFilterBlock::new);
    public static final RegistryObject<Item> SPECTRAL_FILTER_ITEM = ITEMS.register("spectral_filter", () -> new BlockItem(SPECTRAL_FILTER.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<SpectralFilterBlockEntity>> SPECTRAL_FILTER_ENTITY = ENTITIES.register("spectral_filter",
        () -> BlockEntityType.Builder.of(SpectralFilterBlockEntity::new, SPECTRAL_FILTER.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<SpectralFilterMenu>> SPECTRAL_FILTER_MENU = MENUS.register("spectral_filter",
        () -> new net.minecraft.world.inventory.MenuType<>(SpectralFilterMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.FiltrationRecipe>> FILTRATION = TYPES.register("filtration", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":filtration"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.FiltrationRecipe>> FILTRATION_SERIALIZER = SERIALIZERS.register("filtration", com.starmaylight.arcana_industry.recipe.FiltrationRecipe.Serializer::new);
    public static final RegistryObject<Item> BURNISHED_PRISM_SHEET = ITEMS.register("burnished_prism_sheet", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> PRISM_BURNISHER = BLOCKS.register("prism_burnisher", PrismBurnisherBlock::new);
    public static final RegistryObject<Item> PRISM_BURNISHER_ITEM = ITEMS.register("prism_burnisher", () -> new BlockItem(PRISM_BURNISHER.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<PrismBurnisherBlockEntity>> PRISM_BURNISHER_ENTITY = ENTITIES.register("prism_burnisher",
        () -> BlockEntityType.Builder.of(PrismBurnisherBlockEntity::new, PRISM_BURNISHER.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<PrismBurnisherMenu>> PRISM_BURNISHER_MENU = MENUS.register("prism_burnisher",
        () -> new net.minecraft.world.inventory.MenuType<>(PrismBurnisherMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.BurnishingRecipe>> BURNISHING = TYPES.register("burnishing", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":burnishing"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.BurnishingRecipe>> BURNISHING_SERIALIZER = SERIALIZERS.register("burnishing", com.starmaylight.arcana_industry.recipe.BurnishingRecipe.Serializer::new);
    public static final RegistryObject<Item> SCORED_PRISM_BLANK = ITEMS.register("scored_prism_blank", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> PRISM_SCORER = BLOCKS.register("prism_scorer", PrismScorerBlock::new);
    public static final RegistryObject<Item> PRISM_SCORER_ITEM = ITEMS.register("prism_scorer", () -> new BlockItem(PRISM_SCORER.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<PrismScorerBlockEntity>> PRISM_SCORER_ENTITY = ENTITIES.register("prism_scorer",
        () -> BlockEntityType.Builder.of(PrismScorerBlockEntity::new, PRISM_SCORER.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<PrismScorerMenu>> PRISM_SCORER_MENU = MENUS.register("prism_scorer",
        () -> new net.minecraft.world.inventory.MenuType<>(PrismScorerMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.ScoringRecipe>> SCORING = TYPES.register("scoring", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":scoring"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.ScoringRecipe>> SCORING_SERIALIZER = SERIALIZERS.register("scoring", com.starmaylight.arcana_industry.recipe.ScoringRecipe.Serializer::new);
    public static final RegistryObject<Item> DIFFRACTION_CORE = ITEMS.register("diffraction_core", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> DIFFRACTION_GRATE = BLOCKS.register("diffraction_grate", DiffractionGrateBlock::new);
    public static final RegistryObject<Item> DIFFRACTION_GRATE_ITEM = ITEMS.register("diffraction_grate", () -> new BlockItem(DIFFRACTION_GRATE.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<DiffractionGrateBlockEntity>> DIFFRACTION_GRATE_ENTITY = ENTITIES.register("diffraction_grate",
        () -> BlockEntityType.Builder.of(DiffractionGrateBlockEntity::new, DIFFRACTION_GRATE.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<DiffractionGrateMenu>> DIFFRACTION_GRATE_MENU = MENUS.register("diffraction_grate",
        () -> new net.minecraft.world.inventory.MenuType<>(DiffractionGrateMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.DiffractionRecipe>> DIFFRACTION = TYPES.register("diffraction", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":diffraction"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.DiffractionRecipe>> DIFFRACTION_SERIALIZER = SERIALIZERS.register("diffraction", com.starmaylight.arcana_industry.recipe.DiffractionRecipe.Serializer::new);
    public static final RegistryObject<Item> FACETED_PRISM_SEAL = ITEMS.register("faceted_prism_seal", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> PRISM_LAPIDARY = BLOCKS.register("prism_lapidary", PrismLapidaryBlock::new);
    public static final RegistryObject<Item> PRISM_LAPIDARY_ITEM = ITEMS.register("prism_lapidary", () -> new BlockItem(PRISM_LAPIDARY.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<PrismLapidaryBlockEntity>> PRISM_LAPIDARY_ENTITY = ENTITIES.register("prism_lapidary",
        () -> BlockEntityType.Builder.of(PrismLapidaryBlockEntity::new, PRISM_LAPIDARY.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<PrismLapidaryMenu>> PRISM_LAPIDARY_MENU = MENUS.register("prism_lapidary",
        () -> new net.minecraft.world.inventory.MenuType<>(PrismLapidaryMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.FacetingRecipe>> FACETING = TYPES.register("faceting", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":faceting"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.FacetingRecipe>> FACETING_SERIALIZER = SERIALIZERS.register("faceting", com.starmaylight.arcana_industry.recipe.FacetingRecipe.Serializer::new);
    public static final RegistryObject<Item> POLISHED_PRISM_FACET = ITEMS.register("polished_prism_facet", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> PRISM_POLISHER = BLOCKS.register("prism_polisher", PrismPolisherBlock::new);
    public static final RegistryObject<Item> PRISM_POLISHER_ITEM = ITEMS.register("prism_polisher", () -> new BlockItem(PRISM_POLISHER.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<PrismPolisherBlockEntity>> PRISM_POLISHER_ENTITY = ENTITIES.register("prism_polisher",
        () -> BlockEntityType.Builder.of(PrismPolisherBlockEntity::new, PRISM_POLISHER.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<PrismPolisherMenu>> PRISM_POLISHER_MENU = MENUS.register("prism_polisher",
        () -> new net.minecraft.world.inventory.MenuType<>(PrismPolisherMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.PolishingRecipe>> POLISHING = TYPES.register("polishing", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":polishing"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.PolishingRecipe>> POLISHING_SERIALIZER = SERIALIZERS.register("polishing", com.starmaylight.arcana_industry.recipe.PolishingRecipe.Serializer::new);
    public static final RegistryObject<Item> TUMBLED_PRISM_BEAD = ITEMS.register("tumbled_prism_bead", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> PRISM_TUMBLER = BLOCKS.register("prism_tumbler", PrismTumblerBlock::new);
    public static final RegistryObject<Item> PRISM_TUMBLER_ITEM = ITEMS.register("prism_tumbler", () -> new BlockItem(PRISM_TUMBLER.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<PrismTumblerBlockEntity>> PRISM_TUMBLER_ENTITY = ENTITIES.register("prism_tumbler",
        () -> BlockEntityType.Builder.of(PrismTumblerBlockEntity::new, PRISM_TUMBLER.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<PrismTumblerMenu>> PRISM_TUMBLER_MENU = MENUS.register("prism_tumbler",
        () -> new net.minecraft.world.inventory.MenuType<>(PrismTumblerMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.TumblingRecipe>> TUMBLING = TYPES.register("tumbling", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":tumbling"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.TumblingRecipe>> TUMBLING_SERIALIZER = SERIALIZERS.register("tumbling", com.starmaylight.arcana_industry.recipe.TumblingRecipe.Serializer::new);


    public static final RegistryObject<Item> FORMED_FIREBRICK = ITEMS.register("formed_firebrick", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> CLAY_EXTRUDER = BLOCKS.register("clay_extruder", ClayExtruderBlock::new);
    public static final RegistryObject<Item> CLAY_EXTRUDER_ITEM = ITEMS.register("clay_extruder", () -> new BlockItem(CLAY_EXTRUDER.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<ClayExtruderBlockEntity>> CLAY_EXTRUDER_ENTITY = ENTITIES.register("clay_extruder",
        () -> BlockEntityType.Builder.of(ClayExtruderBlockEntity::new, CLAY_EXTRUDER.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<ClayExtruderMenu>> CLAY_EXTRUDER_MENU = MENUS.register("clay_extruder",
        () -> new net.minecraft.world.inventory.MenuType<>(ClayExtruderMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.ExtrudingRecipe>> EXTRUDING = TYPES.register("extruding", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":extruding"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.ExtrudingRecipe>> EXTRUDING_SERIALIZER = SERIALIZERS.register("extruding", com.starmaylight.arcana_industry.recipe.ExtrudingRecipe.Serializer::new);
    public static final RegistryObject<Item> TEMPERED_COPPER_SHEET = ITEMS.register("tempered_copper_sheet", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> COPPER_HOT_PRESS = BLOCKS.register("copper_hot_press", CopperHotPressBlock::new);
    public static final RegistryObject<Item> COPPER_HOT_PRESS_ITEM = ITEMS.register("copper_hot_press", () -> new BlockItem(COPPER_HOT_PRESS.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<CopperHotPressBlockEntity>> COPPER_HOT_PRESS_ENTITY = ENTITIES.register("copper_hot_press",
        () -> BlockEntityType.Builder.of(CopperHotPressBlockEntity::new, COPPER_HOT_PRESS.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<CopperHotPressMenu>> COPPER_HOT_PRESS_MENU = MENUS.register("copper_hot_press",
        () -> new net.minecraft.world.inventory.MenuType<>(CopperHotPressMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.HotPressingRecipe>> HOT_PRESSING = TYPES.register("hot_pressing", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":hot_pressing"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.HotPressingRecipe>> HOT_PRESSING_SERIALIZER = SERIALIZERS.register("hot_pressing", com.starmaylight.arcana_industry.recipe.HotPressingRecipe.Serializer::new);
    public static final RegistryObject<Item> TEMPERED_BLACKSTONE = ITEMS.register("tempered_blackstone", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> BASALT_TEMPERER = BLOCKS.register("basalt_temperer", BasaltTempererBlock::new);
    public static final RegistryObject<Item> BASALT_TEMPERER_ITEM = ITEMS.register("basalt_temperer", () -> new BlockItem(BASALT_TEMPERER.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<BasaltTempererBlockEntity>> BASALT_TEMPERER_ENTITY = ENTITIES.register("basalt_temperer",
        () -> BlockEntityType.Builder.of(BasaltTempererBlockEntity::new, BASALT_TEMPERER.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<BasaltTempererMenu>> BASALT_TEMPERER_MENU = MENUS.register("basalt_temperer",
        () -> new net.minecraft.world.inventory.MenuType<>(BasaltTempererMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.StoneTemperingRecipe>> STONE_TEMPERING = TYPES.register("stone_tempering", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":stone_tempering"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.StoneTemperingRecipe>> STONE_TEMPERING_SERIALIZER = SERIALIZERS.register("stone_tempering", com.starmaylight.arcana_industry.recipe.StoneTemperingRecipe.Serializer::new);
    public static final RegistryObject<Item> SCRIBED_PLATE = ITEMS.register("scribed_plate", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> LATTICE_SCRIBER = BLOCKS.register("lattice_scriber", LatticeScriberBlock::new);
    public static final RegistryObject<Item> LATTICE_SCRIBER_ITEM = ITEMS.register("lattice_scriber", () -> new BlockItem(LATTICE_SCRIBER.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<LatticeScriberBlockEntity>> LATTICE_SCRIBER_ENTITY = ENTITIES.register("lattice_scriber",
        () -> BlockEntityType.Builder.of(LatticeScriberBlockEntity::new, LATTICE_SCRIBER.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<LatticeScriberMenu>> LATTICE_SCRIBER_MENU = MENUS.register("lattice_scriber",
        () -> new net.minecraft.world.inventory.MenuType<>(LatticeScriberMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.ScribingRecipe>> SCRIBING = TYPES.register("scribing", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":scribing"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.ScribingRecipe>> SCRIBING_SERIALIZER = SERIALIZERS.register("scribing", com.starmaylight.arcana_industry.recipe.ScribingRecipe.Serializer::new);
    public static final RegistryObject<Item> WELDED_PLATE = ITEMS.register("welded_plate", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> LATTICE_WELDER = BLOCKS.register("lattice_welder", LatticeWelderBlock::new);
    public static final RegistryObject<Item> LATTICE_WELDER_ITEM = ITEMS.register("lattice_welder", () -> new BlockItem(LATTICE_WELDER.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<LatticeWelderBlockEntity>> LATTICE_WELDER_ENTITY = ENTITIES.register("lattice_welder",
        () -> BlockEntityType.Builder.of(LatticeWelderBlockEntity::new, LATTICE_WELDER.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<LatticeWelderMenu>> LATTICE_WELDER_MENU = MENUS.register("lattice_welder",
        () -> new net.minecraft.world.inventory.MenuType<>(LatticeWelderMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.WeldingRecipe>> WELDING = TYPES.register("welding", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":welding"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.WeldingRecipe>> WELDING_SERIALIZER = SERIALIZERS.register("welding", com.starmaylight.arcana_industry.recipe.WeldingRecipe.Serializer::new);
    public static final RegistryObject<Item> REINFORCED_SEAL = ITEMS.register("reinforced_seal", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STATIC_O_RING = ITEMS.register("static_o_ring", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> LATTICE_RECLAIMER = BLOCKS.register("lattice_reclaimer", LatticeReclaimerBlock::new);
    public static final RegistryObject<Item> LATTICE_RECLAIMER_ITEM = ITEMS.register("lattice_reclaimer", () -> new BlockItem(LATTICE_RECLAIMER.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<LatticeReclaimerBlockEntity>> LATTICE_RECLAIMER_ENTITY = ENTITIES.register("lattice_reclaimer",
        () -> BlockEntityType.Builder.of(LatticeReclaimerBlockEntity::new, LATTICE_RECLAIMER.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<LatticeReclaimerMenu>> LATTICE_RECLAIMER_MENU = MENUS.register("lattice_reclaimer",
        () -> new net.minecraft.world.inventory.MenuType<>(LatticeReclaimerMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.SalvagingRecipe>> SALVAGING = TYPES.register("salvaging", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":salvaging"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.SalvagingRecipe>> SALVAGING_SERIALIZER = SERIALIZERS.register("salvaging", com.starmaylight.arcana_industry.recipe.SalvagingRecipe.Serializer::new);
    public static final RegistryObject<Item> SOLAR_FOIL = ITEMS.register("solar_foil", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> SOLAR_PHOTOLYZER = BLOCKS.register("solar_photolyzer", SolarPhotolyzerBlock::new);
    public static final RegistryObject<Item> SOLAR_PHOTOLYZER_ITEM = ITEMS.register("solar_photolyzer", () -> new BlockItem(SOLAR_PHOTOLYZER.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<SolarPhotolyzerBlockEntity>> SOLAR_PHOTOLYZER_ENTITY = ENTITIES.register("solar_photolyzer",
        () -> BlockEntityType.Builder.of(SolarPhotolyzerBlockEntity::new, SOLAR_PHOTOLYZER.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<SolarPhotolyzerMenu>> SOLAR_PHOTOLYZER_MENU = MENUS.register("solar_photolyzer",
        () -> new net.minecraft.world.inventory.MenuType<>(SolarPhotolyzerMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.PhotolysisRecipe>> PHOTOLYSIS = TYPES.register("photolysis", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":photolysis"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.PhotolysisRecipe>> PHOTOLYSIS_SERIALIZER = SERIALIZERS.register("photolysis", com.starmaylight.arcana_industry.recipe.PhotolysisRecipe.Serializer::new);
    public static final RegistryObject<Item> SOLAR_TEMPERED_PLATE = ITEMS.register("solar_tempered_plate", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> SOLAR_SMELTER = BLOCKS.register("solar_smelter", SolarSmelterBlock::new);
    public static final RegistryObject<Item> SOLAR_SMELTER_ITEM = ITEMS.register("solar_smelter", () -> new BlockItem(SOLAR_SMELTER.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<SolarSmelterBlockEntity>> SOLAR_SMELTER_ENTITY = ENTITIES.register("solar_smelter",
        () -> BlockEntityType.Builder.of(SolarSmelterBlockEntity::new, SOLAR_SMELTER.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<SolarSmelterMenu>> SOLAR_SMELTER_MENU = MENUS.register("solar_smelter",
        () -> new net.minecraft.world.inventory.MenuType<>(SolarSmelterMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.SolarSmeltingRecipe>> SOLAR_SMELTING = TYPES.register("solar_smelting", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":solar_smelting"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.SolarSmeltingRecipe>> SOLAR_SMELTING_SERIALIZER = SERIALIZERS.register("solar_smelting", com.starmaylight.arcana_industry.recipe.SolarSmeltingRecipe.Serializer::new);
    public static final RegistryObject<Item> INFUSED_LATTICE_CORE = ITEMS.register("infused_lattice_core", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Block> LATTICE_INFUSER = BLOCKS.register("lattice_infuser", LatticeInfuserBlock::new);
    public static final RegistryObject<Item> LATTICE_INFUSER_ITEM = ITEMS.register("lattice_infuser", () -> new BlockItem(LATTICE_INFUSER.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<LatticeInfuserBlockEntity>> LATTICE_INFUSER_ENTITY = ENTITIES.register("lattice_infuser",
        () -> BlockEntityType.Builder.of(LatticeInfuserBlockEntity::new, LATTICE_INFUSER.get()).build(null));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<LatticeInfuserMenu>> LATTICE_INFUSER_MENU = MENUS.register("lattice_infuser",
        () -> new net.minecraft.world.inventory.MenuType<>(LatticeInfuserMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.InfusionRecipe>> INFUSION = TYPES.register("infusion", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":infusion"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.InfusionRecipe>> INFUSION_SERIALIZER = SERIALIZERS.register("infusion", com.starmaylight.arcana_industry.recipe.InfusionRecipe.Serializer::new);
    /** One conductor block and item per built-in {@link com.starmaylight.arcana_industry.energy.ConductorSpec}. */
    public static final java.util.Map<String, RegistryObject<Block>> CABLES = registerCables();
    private static java.util.Map<String, RegistryObject<Block>> registerCables() {
        java.util.Map<String, RegistryObject<Block>> cables = new java.util.LinkedHashMap<>();
        for (var spec : com.starmaylight.arcana_industry.energy.ConductorSpec.BUILT_IN) {
            RegistryObject<Block> block = BLOCKS.register(spec.id(), () -> new com.starmaylight.arcana_industry.energy.CableBlock(spec));
            ITEMS.register(spec.id(), () -> new BlockItem(block.get(), new Item.Properties()));
            cables.put(spec.id(), block);
        }
        return java.util.Collections.unmodifiableMap(cables);
    }
    /** One stateless transformer per adjacent voltage-class pair, keyed {@code transformer_<low>_<high>}. */
    public static final java.util.Map<String, RegistryObject<Block>> TRANSFORMERS = registerTransformers();
    private static java.util.Map<String, RegistryObject<Block>> registerTransformers() {
        java.util.Map<String, RegistryObject<Block>> transformers = new java.util.LinkedHashMap<>();
        var classes = com.starmaylight.arcana_industry.energy.VoltageTier.values();
        for (int i = 0; i + 1 < classes.length; i++) {
            var low = classes[i];
            String id = "transformer_" + low.name().toLowerCase(java.util.Locale.ROOT) + "_" + classes[i + 1].name().toLowerCase(java.util.Locale.ROOT);
            RegistryObject<Block> block = BLOCKS.register(id, () -> new com.starmaylight.arcana_industry.energy.TransformerBlock(low));
            ITEMS.register(id, () -> new BlockItem(block.get(), new Item.Properties()));
            transformers.put(id, block);
        }
        return java.util.Collections.unmodifiableMap(transformers);
    }
    public static final RegistryObject<Block> FIREBOX_DYNAMO = BLOCKS.register("firebox_dynamo", com.starmaylight.arcana_industry.energy.FireboxDynamoBlock::new);
    public static final RegistryObject<Item> FIREBOX_DYNAMO_ITEM = ITEMS.register("firebox_dynamo", () -> new BlockItem(FIREBOX_DYNAMO.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<com.starmaylight.arcana_industry.energy.FireboxDynamoBlockEntity>> FIREBOX_DYNAMO_ENTITY = ENTITIES.register("firebox_dynamo",
        () -> BlockEntityType.Builder.of(com.starmaylight.arcana_industry.energy.FireboxDynamoBlockEntity::new, FIREBOX_DYNAMO.get()).build(null));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.CombustionRecipe>> COMBUSTION = TYPES.register("combustion", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":combustion"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.CombustionRecipe>> COMBUSTION_SERIALIZER = SERIALIZERS.register("combustion", com.starmaylight.arcana_industry.recipe.CombustionRecipe.Serializer::new);
    public static final RegistryObject<Block> BOILER_TURBINE = BLOCKS.register("boiler_turbine", com.starmaylight.arcana_industry.energy.BoilerTurbineBlock::new);
    public static final RegistryObject<Item> BOILER_TURBINE_ITEM = ITEMS.register("boiler_turbine", () -> new BlockItem(BOILER_TURBINE.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<com.starmaylight.arcana_industry.energy.BoilerTurbineBlockEntity>> BOILER_TURBINE_ENTITY = ENTITIES.register("boiler_turbine",
        () -> BlockEntityType.Builder.of(com.starmaylight.arcana_industry.energy.BoilerTurbineBlockEntity::new, BOILER_TURBINE.get()).build(null));
    public static final RegistryObject<RecipeType<com.starmaylight.arcana_industry.recipe.SteamRaisingRecipe>> STEAM_RAISING = TYPES.register("steam_raising", () -> new RecipeType<>() {
        @Override public String toString() { return MOD + ":steam_raising"; }
    });
    public static final RegistryObject<RecipeSerializer<com.starmaylight.arcana_industry.recipe.SteamRaisingRecipe>> STEAM_RAISING_SERIALIZER = SERIALIZERS.register("steam_raising", com.starmaylight.arcana_industry.recipe.SteamRaisingRecipe.Serializer::new);
    public static final RegistryObject<CreativeModeTab> INDUSTRY_TAB = TABS.register("industry", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup.arcana_industry")).icon(() -> new ItemStack(CRUSHER_ITEM.get()))
        .displayItems((parameters, output) -> {
            ITEMS.getEntries().forEach(item -> output.accept(item.get()));
            com.starmaylight.arcana_industry.ore.OreRegistry.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
        }).build());
    public static void register(IEventBus bus) {
        BLOCKS.register(bus); ITEMS.register(bus); ENTITIES.register(bus);
        SERIALIZERS.register(bus); TYPES.register(bus); TABS.register(bus); MENUS.register(bus);
    }
    private MachineRegistry() {}
}
