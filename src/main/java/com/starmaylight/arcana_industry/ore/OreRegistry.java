package com.starmaylight.arcana_industry.ore;

import com.starmaylight.arcana_industry.Arcana_industry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * New modded ore blocks and their raw/ingot materials. Distinct package from
 * {@link com.starmaylight.arcana_industry.machine.MachineRegistry} because ore veins are a separate
 * concern from crafted apparatus; this is the template for the remaining SPEC-required ore set.
 */
public final class OreRegistry {
    private static final String MOD = Arcana_industry.MODID;
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD);

    public static final RegistryObject<Block> TIN_ORE = BLOCKS.register("tin_ore",
        () -> new Block(Properties.of().mapColor(MapColor.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> TIN_ORE_ITEM = ITEMS.register("tin_ore",
        () -> new BlockItem(TIN_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Block> DEEPSLATE_TIN_ORE = BLOCKS.register("deepslate_tin_ore",
        () -> new Block(Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> DEEPSLATE_TIN_ORE_ITEM = ITEMS.register("deepslate_tin_ore",
        () -> new BlockItem(DEEPSLATE_TIN_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RAW_TIN = ITEMS.register("raw_tin", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TIN_INGOT = ITEMS.register("tin_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Block> LEAD_ORE = BLOCKS.register("lead_ore",
        () -> new Block(Properties.of().mapColor(MapColor.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> LEAD_ORE_ITEM = ITEMS.register("lead_ore",
        () -> new BlockItem(LEAD_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Block> DEEPSLATE_LEAD_ORE = BLOCKS.register("deepslate_lead_ore",
        () -> new Block(Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> DEEPSLATE_LEAD_ORE_ITEM = ITEMS.register("deepslate_lead_ore",
        () -> new BlockItem(DEEPSLATE_LEAD_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RAW_LEAD = ITEMS.register("raw_lead", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LEAD_INGOT = ITEMS.register("lead_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Block> ZINC_ORE = BLOCKS.register("zinc_ore",
        () -> new Block(Properties.of().mapColor(MapColor.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> ZINC_ORE_ITEM = ITEMS.register("zinc_ore",
        () -> new BlockItem(ZINC_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Block> DEEPSLATE_ZINC_ORE = BLOCKS.register("deepslate_zinc_ore",
        () -> new Block(Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> DEEPSLATE_ZINC_ORE_ITEM = ITEMS.register("deepslate_zinc_ore",
        () -> new BlockItem(DEEPSLATE_ZINC_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RAW_ZINC = ITEMS.register("raw_zinc", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ZINC_INGOT = ITEMS.register("zinc_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Block> NICKEL_ORE = BLOCKS.register("nickel_ore",
        () -> new Block(Properties.of().mapColor(MapColor.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> NICKEL_ORE_ITEM = ITEMS.register("nickel_ore",
        () -> new BlockItem(NICKEL_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Block> DEEPSLATE_NICKEL_ORE = BLOCKS.register("deepslate_nickel_ore",
        () -> new Block(Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> DEEPSLATE_NICKEL_ORE_ITEM = ITEMS.register("deepslate_nickel_ore",
        () -> new BlockItem(DEEPSLATE_NICKEL_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RAW_NICKEL = ITEMS.register("raw_nickel", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> NICKEL_INGOT = ITEMS.register("nickel_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Block> SILVER_ORE = BLOCKS.register("silver_ore",
        () -> new Block(Properties.of().mapColor(MapColor.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> SILVER_ORE_ITEM = ITEMS.register("silver_ore",
        () -> new BlockItem(SILVER_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Block> DEEPSLATE_SILVER_ORE = BLOCKS.register("deepslate_silver_ore",
        () -> new Block(Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> DEEPSLATE_SILVER_ORE_ITEM = ITEMS.register("deepslate_silver_ore",
        () -> new BlockItem(DEEPSLATE_SILVER_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RAW_SILVER = ITEMS.register("raw_silver", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SILVER_INGOT = ITEMS.register("silver_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Block> COBALT_ORE = BLOCKS.register("cobalt_ore",
        () -> new Block(Properties.of().mapColor(MapColor.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> COBALT_ORE_ITEM = ITEMS.register("cobalt_ore",
        () -> new BlockItem(COBALT_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Block> DEEPSLATE_COBALT_ORE = BLOCKS.register("deepslate_cobalt_ore",
        () -> new Block(Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> DEEPSLATE_COBALT_ORE_ITEM = ITEMS.register("deepslate_cobalt_ore",
        () -> new BlockItem(DEEPSLATE_COBALT_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RAW_COBALT = ITEMS.register("raw_cobalt", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COBALT_INGOT = ITEMS.register("cobalt_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Block> PLATINUM_ORE = BLOCKS.register("platinum_ore",
        () -> new Block(Properties.of().mapColor(MapColor.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> PLATINUM_ORE_ITEM = ITEMS.register("platinum_ore",
        () -> new BlockItem(PLATINUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Block> DEEPSLATE_PLATINUM_ORE = BLOCKS.register("deepslate_platinum_ore",
        () -> new Block(Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> DEEPSLATE_PLATINUM_ORE_ITEM = ITEMS.register("deepslate_platinum_ore",
        () -> new BlockItem(DEEPSLATE_PLATINUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RAW_PLATINUM = ITEMS.register("raw_platinum", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PLATINUM_INGOT = ITEMS.register("platinum_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Block> IRIDIUM_ORE = BLOCKS.register("iridium_ore",
        () -> new Block(Properties.of().mapColor(MapColor.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> IRIDIUM_ORE_ITEM = ITEMS.register("iridium_ore",
        () -> new BlockItem(IRIDIUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Block> DEEPSLATE_IRIDIUM_ORE = BLOCKS.register("deepslate_iridium_ore",
        () -> new Block(Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> DEEPSLATE_IRIDIUM_ORE_ITEM = ITEMS.register("deepslate_iridium_ore",
        () -> new BlockItem(DEEPSLATE_IRIDIUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RAW_IRIDIUM = ITEMS.register("raw_iridium", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> IRIDIUM_INGOT = ITEMS.register("iridium_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Block> ALUMINUM_ORE = BLOCKS.register("aluminum_ore",
        () -> new Block(Properties.of().mapColor(MapColor.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> ALUMINUM_ORE_ITEM = ITEMS.register("aluminum_ore",
        () -> new BlockItem(ALUMINUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Block> DEEPSLATE_ALUMINUM_ORE = BLOCKS.register("deepslate_aluminum_ore",
        () -> new Block(Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> DEEPSLATE_ALUMINUM_ORE_ITEM = ITEMS.register("deepslate_aluminum_ore",
        () -> new BlockItem(DEEPSLATE_ALUMINUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RAW_ALUMINUM = ITEMS.register("raw_aluminum", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ALUMINUM_INGOT = ITEMS.register("aluminum_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Block> TUNGSTEN_ORE = BLOCKS.register("tungsten_ore",
        () -> new Block(Properties.of().mapColor(MapColor.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> TUNGSTEN_ORE_ITEM = ITEMS.register("tungsten_ore",
        () -> new BlockItem(TUNGSTEN_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Block> DEEPSLATE_TUNGSTEN_ORE = BLOCKS.register("deepslate_tungsten_ore",
        () -> new Block(Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> DEEPSLATE_TUNGSTEN_ORE_ITEM = ITEMS.register("deepslate_tungsten_ore",
        () -> new BlockItem(DEEPSLATE_TUNGSTEN_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RAW_TUNGSTEN = ITEMS.register("raw_tungsten", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TUNGSTEN_INGOT = ITEMS.register("tungsten_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Block> BISMUTH_ORE = BLOCKS.register("bismuth_ore",
        () -> new Block(Properties.of().mapColor(MapColor.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> BISMUTH_ORE_ITEM = ITEMS.register("bismuth_ore",
        () -> new BlockItem(BISMUTH_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Block> DEEPSLATE_BISMUTH_ORE = BLOCKS.register("deepslate_bismuth_ore",
        () -> new Block(Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> DEEPSLATE_BISMUTH_ORE_ITEM = ITEMS.register("deepslate_bismuth_ore",
        () -> new BlockItem(DEEPSLATE_BISMUTH_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RAW_BISMUTH = ITEMS.register("raw_bismuth", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BISMUTH_INGOT = ITEMS.register("bismuth_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Block> MANGANESE_ORE = BLOCKS.register("manganese_ore",
        () -> new Block(Properties.of().mapColor(MapColor.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> MANGANESE_ORE_ITEM = ITEMS.register("manganese_ore",
        () -> new BlockItem(MANGANESE_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Block> DEEPSLATE_MANGANESE_ORE = BLOCKS.register("deepslate_manganese_ore",
        () -> new Block(Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> DEEPSLATE_MANGANESE_ORE_ITEM = ITEMS.register("deepslate_manganese_ore",
        () -> new BlockItem(DEEPSLATE_MANGANESE_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RAW_MANGANESE = ITEMS.register("raw_manganese", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MANGANESE_INGOT = ITEMS.register("manganese_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Block> ANTIMONY_ORE = BLOCKS.register("antimony_ore",
        () -> new Block(Properties.of().mapColor(MapColor.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> ANTIMONY_ORE_ITEM = ITEMS.register("antimony_ore",
        () -> new BlockItem(ANTIMONY_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Block> DEEPSLATE_ANTIMONY_ORE = BLOCKS.register("deepslate_antimony_ore",
        () -> new Block(Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> DEEPSLATE_ANTIMONY_ORE_ITEM = ITEMS.register("deepslate_antimony_ore",
        () -> new BlockItem(DEEPSLATE_ANTIMONY_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RAW_ANTIMONY = ITEMS.register("raw_antimony", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ANTIMONY_INGOT = ITEMS.register("antimony_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Block> CHROMIUM_ORE = BLOCKS.register("chromium_ore",
        () -> new Block(Properties.of().mapColor(MapColor.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> CHROMIUM_ORE_ITEM = ITEMS.register("chromium_ore",
        () -> new BlockItem(CHROMIUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Block> DEEPSLATE_CHROMIUM_ORE = BLOCKS.register("deepslate_chromium_ore",
        () -> new Block(Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> DEEPSLATE_CHROMIUM_ORE_ITEM = ITEMS.register("deepslate_chromium_ore",
        () -> new BlockItem(DEEPSLATE_CHROMIUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RAW_CHROMIUM = ITEMS.register("raw_chromium", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CHROMIUM_INGOT = ITEMS.register("chromium_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Block> TITANIUM_ORE = BLOCKS.register("titanium_ore",
        () -> new Block(Properties.of().mapColor(MapColor.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> TITANIUM_ORE_ITEM = ITEMS.register("titanium_ore",
        () -> new BlockItem(TITANIUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Block> DEEPSLATE_TITANIUM_ORE = BLOCKS.register("deepslate_titanium_ore",
        () -> new Block(Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> DEEPSLATE_TITANIUM_ORE_ITEM = ITEMS.register("deepslate_titanium_ore",
        () -> new BlockItem(DEEPSLATE_TITANIUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RAW_TITANIUM = ITEMS.register("raw_titanium", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TITANIUM_INGOT = ITEMS.register("titanium_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Block> MOLYBDENUM_ORE = BLOCKS.register("molybdenum_ore",
        () -> new Block(Properties.of().mapColor(MapColor.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> MOLYBDENUM_ORE_ITEM = ITEMS.register("molybdenum_ore",
        () -> new BlockItem(MOLYBDENUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Block> DEEPSLATE_MOLYBDENUM_ORE = BLOCKS.register("deepslate_molybdenum_ore",
        () -> new Block(Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> DEEPSLATE_MOLYBDENUM_ORE_ITEM = ITEMS.register("deepslate_molybdenum_ore",
        () -> new BlockItem(DEEPSLATE_MOLYBDENUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RAW_MOLYBDENUM = ITEMS.register("raw_molybdenum", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MOLYBDENUM_INGOT = ITEMS.register("molybdenum_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Block> VANADIUM_ORE = BLOCKS.register("vanadium_ore",
        () -> new Block(Properties.of().mapColor(MapColor.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> VANADIUM_ORE_ITEM = ITEMS.register("vanadium_ore",
        () -> new BlockItem(VANADIUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Block> DEEPSLATE_VANADIUM_ORE = BLOCKS.register("deepslate_vanadium_ore",
        () -> new Block(Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> DEEPSLATE_VANADIUM_ORE_ITEM = ITEMS.register("deepslate_vanadium_ore",
        () -> new BlockItem(DEEPSLATE_VANADIUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RAW_VANADIUM = ITEMS.register("raw_vanadium", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> VANADIUM_INGOT = ITEMS.register("vanadium_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Block> NIOBIUM_ORE = BLOCKS.register("niobium_ore",
        () -> new Block(Properties.of().mapColor(MapColor.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> NIOBIUM_ORE_ITEM = ITEMS.register("niobium_ore",
        () -> new BlockItem(NIOBIUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Block> DEEPSLATE_NIOBIUM_ORE = BLOCKS.register("deepslate_niobium_ore",
        () -> new Block(Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> DEEPSLATE_NIOBIUM_ORE_ITEM = ITEMS.register("deepslate_niobium_ore",
        () -> new BlockItem(DEEPSLATE_NIOBIUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RAW_NIOBIUM = ITEMS.register("raw_niobium", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> NIOBIUM_INGOT = ITEMS.register("niobium_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Block> TANTALUM_ORE = BLOCKS.register("tantalum_ore",
        () -> new Block(Properties.of().mapColor(MapColor.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> TANTALUM_ORE_ITEM = ITEMS.register("tantalum_ore",
        () -> new BlockItem(TANTALUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Block> DEEPSLATE_TANTALUM_ORE = BLOCKS.register("deepslate_tantalum_ore",
        () -> new Block(Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> DEEPSLATE_TANTALUM_ORE_ITEM = ITEMS.register("deepslate_tantalum_ore",
        () -> new BlockItem(DEEPSLATE_TANTALUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RAW_TANTALUM = ITEMS.register("raw_tantalum", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TANTALUM_INGOT = ITEMS.register("tantalum_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Block> ZIRCONIUM_ORE = BLOCKS.register("zirconium_ore",
        () -> new Block(Properties.of().mapColor(MapColor.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> ZIRCONIUM_ORE_ITEM = ITEMS.register("zirconium_ore",
        () -> new BlockItem(ZIRCONIUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Block> DEEPSLATE_ZIRCONIUM_ORE = BLOCKS.register("deepslate_zirconium_ore",
        () -> new Block(Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> DEEPSLATE_ZIRCONIUM_ORE_ITEM = ITEMS.register("deepslate_zirconium_ore",
        () -> new BlockItem(DEEPSLATE_ZIRCONIUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RAW_ZIRCONIUM = ITEMS.register("raw_zirconium", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ZIRCONIUM_INGOT = ITEMS.register("zirconium_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Block> OSMIUM_ORE = BLOCKS.register("osmium_ore",
        () -> new Block(Properties.of().mapColor(MapColor.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> OSMIUM_ORE_ITEM = ITEMS.register("osmium_ore",
        () -> new BlockItem(OSMIUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Block> DEEPSLATE_OSMIUM_ORE = BLOCKS.register("deepslate_osmium_ore",
        () -> new Block(Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> DEEPSLATE_OSMIUM_ORE_ITEM = ITEMS.register("deepslate_osmium_ore",
        () -> new BlockItem(DEEPSLATE_OSMIUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RAW_OSMIUM = ITEMS.register("raw_osmium", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> OSMIUM_INGOT = ITEMS.register("osmium_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Block> PALLADIUM_ORE = BLOCKS.register("palladium_ore",
        () -> new Block(Properties.of().mapColor(MapColor.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> PALLADIUM_ORE_ITEM = ITEMS.register("palladium_ore",
        () -> new BlockItem(PALLADIUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Block> DEEPSLATE_PALLADIUM_ORE = BLOCKS.register("deepslate_palladium_ore",
        () -> new Block(Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> DEEPSLATE_PALLADIUM_ORE_ITEM = ITEMS.register("deepslate_palladium_ore",
        () -> new BlockItem(DEEPSLATE_PALLADIUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RAW_PALLADIUM = ITEMS.register("raw_palladium", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PALLADIUM_INGOT = ITEMS.register("palladium_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Block> RUTHENIUM_ORE = BLOCKS.register("ruthenium_ore",
        () -> new Block(Properties.of().mapColor(MapColor.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> RUTHENIUM_ORE_ITEM = ITEMS.register("ruthenium_ore",
        () -> new BlockItem(RUTHENIUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Block> DEEPSLATE_RUTHENIUM_ORE = BLOCKS.register("deepslate_ruthenium_ore",
        () -> new Block(Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> DEEPSLATE_RUTHENIUM_ORE_ITEM = ITEMS.register("deepslate_ruthenium_ore",
        () -> new BlockItem(DEEPSLATE_RUTHENIUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RAW_RUTHENIUM = ITEMS.register("raw_ruthenium", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RUTHENIUM_INGOT = ITEMS.register("ruthenium_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Block> RHENIUM_ORE = BLOCKS.register("rhenium_ore",
        () -> new Block(Properties.of().mapColor(MapColor.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> RHENIUM_ORE_ITEM = ITEMS.register("rhenium_ore",
        () -> new BlockItem(RHENIUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Block> DEEPSLATE_RHENIUM_ORE = BLOCKS.register("deepslate_rhenium_ore",
        () -> new Block(Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> DEEPSLATE_RHENIUM_ORE_ITEM = ITEMS.register("deepslate_rhenium_ore",
        () -> new BlockItem(DEEPSLATE_RHENIUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RAW_RHENIUM = ITEMS.register("raw_rhenium", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RHENIUM_INGOT = ITEMS.register("rhenium_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Block> RHODIUM_ORE = BLOCKS.register("rhodium_ore",
        () -> new Block(Properties.of().mapColor(MapColor.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> RHODIUM_ORE_ITEM = ITEMS.register("rhodium_ore",
        () -> new BlockItem(RHODIUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Block> DEEPSLATE_RHODIUM_ORE = BLOCKS.register("deepslate_rhodium_ore",
        () -> new Block(Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> DEEPSLATE_RHODIUM_ORE_ITEM = ITEMS.register("deepslate_rhodium_ore",
        () -> new BlockItem(DEEPSLATE_RHODIUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RAW_RHODIUM = ITEMS.register("raw_rhodium", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RHODIUM_INGOT = ITEMS.register("rhodium_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Block> HAFNIUM_ORE = BLOCKS.register("hafnium_ore",
        () -> new Block(Properties.of().mapColor(MapColor.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> HAFNIUM_ORE_ITEM = ITEMS.register("hafnium_ore",
        () -> new BlockItem(HAFNIUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Block> DEEPSLATE_HAFNIUM_ORE = BLOCKS.register("deepslate_hafnium_ore",
        () -> new Block(Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> DEEPSLATE_HAFNIUM_ORE_ITEM = ITEMS.register("deepslate_hafnium_ore",
        () -> new BlockItem(DEEPSLATE_HAFNIUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RAW_HAFNIUM = ITEMS.register("raw_hafnium", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> HAFNIUM_INGOT = ITEMS.register("hafnium_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Block> INDIUM_ORE = BLOCKS.register("indium_ore",
        () -> new Block(Properties.of().mapColor(MapColor.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> INDIUM_ORE_ITEM = ITEMS.register("indium_ore",
        () -> new BlockItem(INDIUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Block> DEEPSLATE_INDIUM_ORE = BLOCKS.register("deepslate_indium_ore",
        () -> new Block(Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> DEEPSLATE_INDIUM_ORE_ITEM = ITEMS.register("deepslate_indium_ore",
        () -> new BlockItem(DEEPSLATE_INDIUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RAW_INDIUM = ITEMS.register("raw_indium", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> INDIUM_INGOT = ITEMS.register("indium_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Block> GALLIUM_ORE = BLOCKS.register("gallium_ore",
        () -> new Block(Properties.of().mapColor(MapColor.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> GALLIUM_ORE_ITEM = ITEMS.register("gallium_ore",
        () -> new BlockItem(GALLIUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Block> DEEPSLATE_GALLIUM_ORE = BLOCKS.register("deepslate_gallium_ore",
        () -> new Block(Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> DEEPSLATE_GALLIUM_ORE_ITEM = ITEMS.register("deepslate_gallium_ore",
        () -> new BlockItem(DEEPSLATE_GALLIUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RAW_GALLIUM = ITEMS.register("raw_gallium", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GALLIUM_INGOT = ITEMS.register("gallium_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Block> THALLIUM_ORE = BLOCKS.register("thallium_ore",
        () -> new Block(Properties.of().mapColor(MapColor.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> THALLIUM_ORE_ITEM = ITEMS.register("thallium_ore",
        () -> new BlockItem(THALLIUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Block> DEEPSLATE_THALLIUM_ORE = BLOCKS.register("deepslate_thallium_ore",
        () -> new Block(Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> DEEPSLATE_THALLIUM_ORE_ITEM = ITEMS.register("deepslate_thallium_ore",
        () -> new BlockItem(DEEPSLATE_THALLIUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RAW_THALLIUM = ITEMS.register("raw_thallium", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> THALLIUM_INGOT = ITEMS.register("thallium_ingot", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Block> GERMANIUM_ORE = BLOCKS.register("germanium_ore",
        () -> new Block(Properties.of().mapColor(MapColor.STONE).strength(3.0F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> GERMANIUM_ORE_ITEM = ITEMS.register("germanium_ore",
        () -> new BlockItem(GERMANIUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Block> DEEPSLATE_GERMANIUM_ORE = BLOCKS.register("deepslate_germanium_ore",
        () -> new Block(Properties.of().mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).requiresCorrectToolForDrops()));
    public static final RegistryObject<Item> DEEPSLATE_GERMANIUM_ORE_ITEM = ITEMS.register("deepslate_germanium_ore",
        () -> new BlockItem(DEEPSLATE_GERMANIUM_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RAW_GERMANIUM = ITEMS.register("raw_germanium", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GERMANIUM_INGOT = ITEMS.register("germanium_ingot", () -> new Item(new Item.Properties()));

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }
    private OreRegistry() {}
}
