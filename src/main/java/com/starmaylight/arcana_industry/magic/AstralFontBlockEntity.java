package com.starmaylight.arcana_industry.magic;

import com.starmaylight.arcana_industry.machine.MachineRegistry;
import com.starmaylight.arcana_industry.recipe.LumenRiteRecipe;
import com.starmaylight.arcana_industry.recipe.PedestalRiteRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Lumen is gathered from the night sky and deliberately has no Forge FE capability. */
public class AstralFontBlockEntity extends BlockEntity {
    public static final int CAPACITY = 1000;
    public static final int OFFERING_LUMEN = 160;
    public static final int OFFERING_INTERVAL = 80;
    private int lumen;

    public AstralFontBlockEntity(BlockPos pos, BlockState state) {
        super(MachineRegistry.ASTRAL_FONT_ENTITY.get(), pos, state);
    }

    public int lumen() { return lumen; }

    public boolean spend(int amount) {
        if (amount <= 0 || lumen < amount) return false;
        lumen -= amount;
        setChanged();
        return true;
    }

    /** Takes back up to {@code amount} lumen poured from a carried vessel; returns the amount accepted. */
    public int accept(int amount) {
        int accepted = Math.max(0, Math.min(amount, CAPACITY - lumen));
        if (accepted == 0) return 0;
        lumen += accepted;
        setChanged();
        return accepted;
    }

    public void tickServer() {
        if (level == null || level.isClientSide) return;
        if (level.getGameTime() % 20 == 0 && level.dimensionType().hasSkyLight()
            && level.isNight() && level.canSeeSky(worldPosition.above()) && lumen < CAPACITY) {
            lumen = Math.min(CAPACITY, lumen + 5);
            setChanged();
            if (Boolean.getBoolean("arcana.debugAstralFont")) System.err.println("ARCANA_DEBUG gather pos=" + worldPosition + " time=" + level.getGameTime() + " lumen=" + lumen);
        }
        if (level.getGameTime() % OFFERING_INTERVAL == 0) {
            if (offeringRingFormed(level, worldPosition)) {
                performPedestalRite();
                consumeRingOffering();
            } else {
                performBasinRite();
            }
        }
        // A strict gradient and fixed face order prevent backflow or energy duplication.
        if (level.getGameTime() % 5 != 0 || lumen < 2) return;
        for (Direction face : Direction.values()) {
            BlockPos otherPos = worldPosition.relative(face);
            if (!level.hasChunkAt(otherPos) || !(level.getBlockEntity(otherPos) instanceof AstralFontBlockEntity other)) continue;
            int moved = Math.min(4, Math.min((lumen - other.lumen) / 2, CAPACITY - other.lumen));
            if (moved <= 0) continue;
            lumen -= moved;
            other.lumen += moved;
            setChanged();
            other.setChanged();
            if (Boolean.getBoolean("arcana.debugAstralFont")) System.err.println("ARCANA_DEBUG transfer from=" + worldPosition + " to=" + otherPos + " time=" + level.getGameTime() + " moved=" + moved + " selfLumen=" + lumen + " otherLumen=" + other.lumen);
        }
    }

    /** Four gold cardinal blocks and four amethyst diagonal blocks form the offering ring. */
    public static boolean offeringRingFormed(Level level, BlockPos center) {
        for (Direction face : Direction.Plane.HORIZONTAL) {
            BlockPos part = center.relative(face);
            if (!level.hasChunkAt(part) || !level.getBlockState(part).is(Blocks.GOLD_BLOCK)) return false;
        }
        for (int x : new int[] {-1, 1}) for (int z : new int[] {-1, 1}) {
            BlockPos part = center.offset(x, 0, z);
            if (!level.hasChunkAt(part) || !level.getBlockState(part).is(Blocks.AMETHYST_BLOCK)) return false;
        }
        return true;
    }

    private void consumeRingOffering() {
        if (level == null || lumen > CAPACITY - OFFERING_LUMEN || !level.hasNeighborSignal(worldPosition)
            || !offeringRingFormed(level, worldPosition)) return;
        // A dropper can automate offerings without giving the Font an item or FE capability.
        for (ItemEntity offering : level.getEntitiesOfClass(ItemEntity.class,
            new AABB(worldPosition.above()).inflate(0.25, 0.5, 0.25))) {
            if (!offering.isAlive() || !offering.getItem().is(Items.AMETHYST_SHARD)) continue;
            offering.getItem().shrink(1);
            if (offering.getItem().isEmpty()) offering.discard();
            lumen += OFFERING_LUMEN;
            setChanged();
            return;
        }
    }

    /** The lumen rite for an offered stack; datapack rites sharing an input resolve to the lowest recipe ID. */
    public static Optional<LumenRiteRecipe> riteFor(Level level, ItemStack offered) {
        return level.getRecipeManager().getAllRecipesFor(MachineRegistry.LUMEN_RITE.get()).stream()
            .filter(candidate -> candidate.matchesHeld(offered))
            .min(Comparator.comparing(candidate -> candidate.getId().toString()));
    }

    /** True when the rite's product holds no material trait that count x the offering lacks. */
    public static boolean traitsExplained(LumenRiteRecipe rite, ItemStack offered) {
        return com.starmaylight.arcana_industry.magic.trait.TraitAssay.unexplained(rite.output(),
            java.util.Collections.nCopies(rite.count(), offered),
            com.starmaylight.arcana_industry.magic.trait.MaterialTraits.INSTANCE.snapshot()).isEmpty();
    }

    /**
     * Rite basin: a powered font without the offering ring performs one lumen rite from dropped offerings
     * and inserts the product into the item handler below it. Nothing is spent unless the whole product fits.
     */
    private void performBasinRite() {
        if (level == null || !level.hasNeighborSignal(worldPosition)) return;
        BlockPos below = worldPosition.below();
        if (!level.hasChunkAt(below)) return;
        BlockEntity receptacle = level.getBlockEntity(below);
        if (receptacle == null) return;
        IItemHandler output = receptacle.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP).orElse(null);
        if (output == null) return;
        List<ItemEntity> offered = level.getEntitiesOfClass(ItemEntity.class,
            new AABB(worldPosition.above()).inflate(0.25, 0.5, 0.25), item -> item.isAlive() && !item.getItem().isEmpty());
        offered.sort(Comparator.comparingInt(ItemEntity::getId));
        for (ItemEntity first : offered) {
            List<ItemEntity> same = offered.stream()
                .filter(item -> ItemStack.isSameItemSameTags(item.getItem(), first.getItem())).toList();
            int total = same.stream().mapToInt(item -> item.getItem().getCount()).sum();
            ItemStack pooled = first.getItem().copyWithCount(Math.min(total, first.getItem().getMaxStackSize()));
            var found = riteFor(level, pooled);
            if (found.isEmpty()) continue;
            LumenRiteRecipe rite = found.get();
            if (lumen < rite.lumenCost() || !traitsExplained(rite, pooled)) return;
            if (!ItemHandlerHelper.insertItemStacked(output, rite.output(), true).isEmpty()) return;
            int remaining = rite.count();
            for (ItemEntity item : same) {
                int taken = Math.min(remaining, item.getItem().getCount());
                item.getItem().shrink(taken);
                if (item.getItem().isEmpty()) item.discard();
                remaining -= taken;
                if (remaining == 0) break;
            }
            spend(rite.lumenCost());
            ItemHandlerHelper.insertItemStacked(output, rite.output(), false);
            level.playSound(null, worldPosition, com.starmaylight.arcana_industry.sound.ModSounds.ASTRAL_RITE.get(),
                net.minecraft.sounds.SoundSource.BLOCKS, 0.8F, 1.0F);
            return;
        }
    }

    private ItemEntity offeringAt(BlockPos pedestal) {
        for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class,
            new AABB(pedestal.above()).inflate(0.25, 0.5, 0.25))) {
            if (item.isAlive() && !item.getItem().isEmpty()) return item;
        }
        return null;
    }

    private void performPedestalRite() {
        if (level == null || !level.hasNeighborSignal(worldPosition) || !offeringRingFormed(level, worldPosition)) return;
        List<ItemEntity> offerings = new ArrayList<>();
        ItemEntity center = offeringAt(worldPosition);
        if (center == null) return;
        offerings.add(center);
        for (int x : new int[] {-1, 1}) for (int z : new int[] {-1, 1}) {
            ItemEntity corner = offeringAt(worldPosition.offset(x, 0, z));
            if (corner == null) return;
            offerings.add(corner);
        }
        SimpleContainer inventory = new SimpleContainer(5);
        for (int i = 0; i < 5; i++) inventory.setItem(i, offerings.get(i).getItem().copyWithCount(1));
        var rite = level.getRecipeManager().getAllRecipesFor(MachineRegistry.PEDESTAL_RITE.get()).stream()
            .filter(recipe -> recipe.matches(inventory, level) && (recipe.sacrifice() == null || sacrificeFor(recipe) != null))
            .min(Comparator.comparing(recipe -> recipe.getId().toString()));
        if (rite.isEmpty()) return;
        PedestalRiteRecipe recipe = rite.get();
        if (lumen < recipe.lumenCost()) return;
        List<net.minecraft.world.item.ItemStack> inputs = new ArrayList<>();
        for (int i = 0; i < 5; i++) inputs.add(inventory.getItem(i));
        if (!com.starmaylight.arcana_industry.magic.trait.TraitAssay.unexplained(recipe.output(), inputs,
            com.starmaylight.arcana_industry.magic.trait.MaterialTraits.INSTANCE.snapshot()).isEmpty()) return;
        LivingEntity sacrifice = recipe.sacrifice() == null ? null : sacrificeFor(recipe);
        if (recipe.sacrifice() != null && sacrifice == null) return;
        for (ItemEntity item : offerings) {
            item.getItem().shrink(1);
            if (item.getItem().isEmpty()) item.discard();
        }
        spend(recipe.lumenCost());
        if (sacrifice != null) sacrifice.discard();
        ItemEntity result = new ItemEntity(level, worldPosition.getX() + .5, worldPosition.getY() + 1.2,
            worldPosition.getZ() + .5, recipe.output());
        result.setDeltaMovement(0, 0.15, 0);
        level.addFreshEntity(result);
        level.playSound(null, worldPosition, com.starmaylight.arcana_industry.sound.ModSounds.ASTRAL_RITE.get(),
            net.minecraft.sounds.SoundSource.BLOCKS, 0.8F, 0.9F);
    }

    private LivingEntity sacrificeFor(PedestalRiteRecipe recipe) {
        return level.getEntitiesOfClass(LivingEntity.class, new AABB(worldPosition.above()).inflate(1.5, 0.5, 1.5),
            entity -> entity.isAlive() && BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).equals(recipe.sacrifice()))
            .stream().min(Comparator.comparingInt(LivingEntity::getId)).orElse(null);
    }

    @Override protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Lumen", lumen);
    }

    @Override public void load(CompoundTag tag) {
        super.load(tag);
        lumen = Math.max(0, Math.min(CAPACITY, tag.getInt("Lumen")));
    }
}
