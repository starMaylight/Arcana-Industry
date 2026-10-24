package com.starmaylight.arcana_industry.mob;

import com.starmaylight.arcana_industry.Arcana_industry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class MobRegistry {
    private static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Arcana_industry.MODID);
    public static final RegistryObject<EntityType<RustCrawler>> RUST_CRAWLER = TYPES.register("rust_crawler",
        () -> EntityType.Builder.of(RustCrawler::new, MobCategory.MONSTER).sized(1.25F, 0.8F)
            .clientTrackingRange(8).build(Arcana_industry.MODID + ":rust_crawler"));
    public static final RegistryObject<EntityType<CinderHusk>> CINDER_HUSK = TYPES.register("cinder_husk",
        () -> EntityType.Builder.of(CinderHusk::new, MobCategory.MONSTER).sized(0.6F, 1.95F)
            .fireImmune().clientTrackingRange(8).build(Arcana_industry.MODID + ":cinder_husk"));
    public static final RegistryObject<EntityType<FluxArcher>> FLUX_ARCHER = TYPES.register("flux_archer",
        () -> EntityType.Builder.of(FluxArcher::new, MobCategory.MONSTER).sized(0.6F, 1.99F)
            .clientTrackingRange(8).build(Arcana_industry.MODID + ":flux_archer"));
    public static final RegistryObject<EntityType<StaticCreeper>> STATIC_CREEPER = TYPES.register("static_creeper",
        () -> EntityType.Builder.of(StaticCreeper::new, MobCategory.MONSTER).sized(0.6F, 1.7F)
            .clientTrackingRange(8).build(Arcana_industry.MODID + ":static_creeper"));
    public static final RegistryObject<EntityType<OreMite>> ORE_MITE = TYPES.register("ore_mite",
        () -> EntityType.Builder.of(OreMite::new, MobCategory.MONSTER).sized(0.4F, 0.3F)
            .clientTrackingRange(8).build(Arcana_industry.MODID + ":ore_mite"));

    public static void register(IEventBus bus) {
        TYPES.register(bus);
        bus.addListener(MobRegistry::attributes);
        bus.addListener(MobRegistry::spawnPlacement);
    }
    private static void attributes(EntityAttributeCreationEvent event) {
        event.put(RUST_CRAWLER.get(), net.minecraft.world.entity.monster.Spider.createAttributes().build());
        event.put(CINDER_HUSK.get(), net.minecraft.world.entity.monster.Zombie.createAttributes().build());
        event.put(FLUX_ARCHER.get(), net.minecraft.world.entity.monster.Skeleton.createAttributes().build());
        event.put(STATIC_CREEPER.get(), net.minecraft.world.entity.monster.Creeper.createAttributes().build());
        event.put(ORE_MITE.get(), net.minecraft.world.entity.monster.Silverfish.createAttributes().build());
    }
    private static void spawnPlacement(SpawnPlacementRegisterEvent event) {
        event.register(RUST_CRAWLER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            Monster::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(CINDER_HUSK.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            Monster::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(FLUX_ARCHER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            Monster::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(STATIC_CREEPER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            Monster::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ORE_MITE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            Monster::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
    }
    private MobRegistry() {}
}
