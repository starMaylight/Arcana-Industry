package com.starmaylight.arcana_industry.machine;

import com.starmaylight.arcana_industry.recipe.SingleStockRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.EnumMap;

/**
 * Block entity for a hand-fed multiblock controller. Hand processing remains in its block;
 * controllers extending {@link StockFedControllerBlock} additionally buffer automated stock here.
 * Slots: 0 stock, 1 product, 2 reagent, 3 optional by-product.
 */
public final class HandFedControllerBlockEntity extends BlockEntity {
    /** Default ticks per automated batch for frames whose structure is the whole catalyst and draws no power. */
    public static final int BATCH_TICKS = 40;
    /** Upper bound on a saved batch progress, matching the longest recipe duration a frame recipe may declare. */
    private static final int MAX_TICKS = 72000;
    private static final int STOCK = 0, PRODUCT = 1, REAGENT = 2, BYPRODUCT = 3;
    private final ItemStackHandler stock = new ItemStackHandler(4) {
        @Override protected void onContentsChanged(int slot) { setChanged(); }
        @Override public boolean isItemValid(int slot, ItemStack stack) {
            return slot == STOCK ? acceptsStock(stack) : slot == REAGENT && acceptsReagent(stack);
        }
    };
    private final EnumMap<Direction, ItemPortMode> sideModes = new EnumMap<>(Direction.class);
    private final EnumMap<Direction, LazyOptional<IItemHandler>> ports = new EnumMap<>(Direction.class);
    private LazyOptional<IItemHandler> unsided = LazyOptional.of(() -> new Port(null));
    /** Receive-only FE buffer for powered frames (see StockFedControllerBlock#stockEnergyCapacity); null when unpowered. */
    @Nullable private final MachineEnergy energy;
    private LazyOptional<IEnergyStorage> power;
    private int progress;
    /** Fingerprint of the running recipe definition (see {@link #definitionKey}); progress only resumes on an exact match. */
    private String activeRecipe = "";
    /** The recipe instance {@link #activeRecipe} was computed from; a datapack reload replaces it and forces a recheck. */
    @Nullable private SingleStockRecipe activeInstance;
    private String status = "idle";
    /** Session-only record of the last finished cycle (not saved): which recipe ran and how many cycles ran so far. */
    @Nullable private net.minecraft.resources.ResourceLocation lastCompleted;
    private int completedCycles;

    public HandFedControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        int capacity = state.getBlock() instanceof StockFedControllerBlock block ? block.energyCapacityForStock() : 0;
        energy = capacity > 0 ? new MachineEnergy(capacity, this::setChanged) : null;
        power = energy == null ? LazyOptional.empty() : LazyOptional.of(() -> energy);
        for (Direction side : Direction.values()) {
            sideModes.put(side, defaultMode(side));
            ports.put(side, LazyOptional.of(() -> new Port(side)));
        }
    }

    private static ItemPortMode defaultMode(Direction side) { return side == Direction.DOWN ? ItemPortMode.OUTPUT : ItemPortMode.INPUT; }
    @Nullable private StockFedControllerBlock stockBlock() {
        return getBlockState().getBlock() instanceof StockFedControllerBlock block ? block : null;
    }
    public boolean automated() { return stockBlock() != null; }
    private boolean usesReagent() { StockFedControllerBlock block = stockBlock(); return block != null && block.reagentForStock(); }
    public ItemPortMode sideMode(Direction side) { return sideModes.get(side); }
    public ItemPortMode cycleSide(Direction side) {
        ItemPortMode next = sideMode(side).next();
        sideModes.put(side, next); setChanged();
        return next;
    }
    public ItemStack inputStack() { return stock.getStackInSlot(0).copy(); }
    public ItemStack outputStack() { return stock.getStackInSlot(1).copy(); }
    public ItemStack byproductStack() { return stock.getStackInSlot(BYPRODUCT).copy(); }
    public ItemStack reagentStack() { return stock.getStackInSlot(REAGENT).copy(); }
    public int progress() { return progress; }
    public String status() { return status; }
    @Nullable public net.minecraft.resources.ResourceLocation lastCompletedRecipe() { return lastCompleted; }
    public int completedCycles() { return completedCycles; }
    public int energyStored() { return energy == null ? 0 : energy.getEnergyStored(); }
    public int energyCapacity() { return energy == null ? 0 : energy.getMaxEnergyStored(); }
    /**
     * Loads hand-held feedstock into the stock buffer as if a hopper inserted it, so manual use runs the same timed,
     * powered batch as automation. Returns what did not fit.
     */
    public ItemStack insertStock(ItemStack stack, boolean simulate) { return stock.insertItem(STOCK, stack, simulate); }
    /** Loads a paid second ingredient into the reagent slot (e.g. levels condensed into bottles); returns what did not fit. */
    public ItemStack insertReagent(ItemStack stack, boolean simulate) { return stock.insertItem(REAGENT, stack, simulate); }
    /** Whether any loaded recipe of this frame takes {@code stack} as its stock ingredient. */
    public boolean acceptsStockItem(ItemStack stack) { return acceptsStock(stack); }
    public ItemStack takeOutput() {
        return stock.extractItem(stock.getStackInSlot(PRODUCT).isEmpty() ? BYPRODUCT : PRODUCT, 64, false);
    }

    private boolean acceptsStock(ItemStack stack) { return anyRecipe(stack, false); }
    private boolean acceptsReagent(ItemStack stack) { return usesReagent() && anyRecipe(stack, true); }

    private boolean anyRecipe(ItemStack stack, boolean reagent) {
        StockFedControllerBlock block = stockBlock();
        if (block == null || level == null || stack.isEmpty()) return false;
        if (!reagent && !block.acceptsForStock(stack)) return false;
        return level.getRecipeManager().getRecipes().stream()
            .filter(r -> r.getType() == block.recipeTypeForStock() && r instanceof SingleStockRecipe)
            .map(r -> (SingleStockRecipe) r)
            .anyMatch(r -> reagent ? r.reagentCount() > 0 && r.reagent().test(stack) : r.input().test(stack));
    }

    @Nullable private SingleStockRecipe findRecipe(StockFedControllerBlock block) {
        if (level == null) return null;
        var view = block.reagentForStock() ? new SimpleContainer(stock.getStackInSlot(STOCK), stock.getStackInSlot(REAGENT))
            : new SimpleContainer(stock.getStackInSlot(STOCK));
        int reagentHeld = stock.getStackInSlot(REAGENT).getCount();
        return level.getRecipeManager().getRecipes().stream()
            .filter(r -> r.getType() == block.recipeTypeForStock() && r instanceof SingleStockRecipe)
            .filter(r -> matches(r, view))
            .map(r -> (SingleStockRecipe) r)
            .filter(r -> view.getItem(0).getCount() >= block.batchesForStock() * r.count())
            .filter(r -> reagentHeld >= block.batchesForStock() * r.reagentCount())
            .min(Comparator.comparing(r -> r.id().toString())).orElse(null);
    }

    @SuppressWarnings("unchecked")
    private boolean matches(net.minecraft.world.item.crafting.Recipe<?> recipe, SimpleContainer view) {
        return ((net.minecraft.world.item.crafting.Recipe<net.minecraft.world.Container>) recipe).matches(view, level);
    }

    /** One server tick of automated work; called by the controller block's ticker. */
    void tickStock(StockFedControllerBlock block) {
        if (level == null || level.isClientSide) return;
        if (stock.getStackInSlot(0).isEmpty()) { idle("idle"); return; }
        if (block.reagentForStock() && stock.getStackInSlot(REAGENT).isEmpty()) { idle("no_reagent"); return; }
        if (!block.formedForStock(level, worldPosition)) { idle("incomplete"); return; }
        SingleStockRecipe recipe = findRecipe(block);
        if (recipe == null) { idle("no_recipe"); return; }
        if (recipe != activeInstance) {
            String key = definitionKey(recipe);
            if (!key.equals(activeRecipe)) { progress = 0; activeRecipe = key; }
            activeInstance = recipe;
        }
        int batches = block.batchesForStock();
        ItemStack result = block.stockResult(recipe, stock.getStackInSlot(0), stock.getStackInSlot(REAGENT));
        ItemStack byproduct = block.stockByproduct(recipe, stock.getStackInSlot(0));
        if (result.isEmpty()) { idle("no_result"); return; }
        String hold = block.holdForStock(level, worldPosition, recipe, stock.getStackInSlot(0), stock.getStackInSlot(REAGENT));
        if (hold != null) { idle(hold); return; }
        result.setCount(result.getCount() * batches);
        if (!byproduct.isEmpty()) byproduct.setCount(byproduct.getCount() * batches);
        ItemStack existing = stock.getStackInSlot(1);
        ItemStack existingByproduct = stock.getStackInSlot(BYPRODUCT);
        if (!fits(existing, result) || !fits(existingByproduct, byproduct)) {
            status = "output_full"; return;
        }
        int perTick = recipe.stockEnergyPerTick();
        if (perTick > 0 && (energy == null || !energy.consume(perTick))) { status = "no_power"; return; }
        status = "working";
        if (++progress >= recipe.stockTicks()) {
            block.completedForStock(level, worldPosition, recipe, stock.getStackInSlot(0), stock.getStackInSlot(REAGENT));
            stock.extractItem(STOCK, batches * recipe.count(), false);
            if (recipe.reagentCount() > 0) stock.extractItem(REAGENT, batches * recipe.reagentCount(), false);
            if (!existing.isEmpty()) result.grow(existing.getCount());
            stock.setStackInSlot(1, result);
            if (!byproduct.isEmpty()) {
                if (!existingByproduct.isEmpty()) byproduct.grow(existingByproduct.getCount());
                stock.setStackInSlot(BYPRODUCT, byproduct);
            }
            progress = 0;
            lastCompleted = recipe.id();
            completedCycles++;
            com.starmaylight.arcana_industry.sound.ModSounds.playMultiblock(level, worldPosition,
                BuiltInRegistries.BLOCK.getKey(block).getPath());
        }
        setChanged();
    }

    /**
     * Identifies a recipe by ID plus everything that shapes a batch, so a datapack reload that keeps the ID but changes
     * ingredients, product, duration or power restarts the batch instead of finishing it under the old terms.
     */
    static String definitionKey(SingleStockRecipe recipe) {
        ItemStack out = recipe.output();
        int hash = java.util.Objects.hash(recipe.input().toJson().toString(), recipe.count(),
            recipe.reagentCount() > 0 ? recipe.reagent().toJson().toString() : "", recipe.reagentCount(),
            BuiltInRegistries.ITEM.getKey(out.getItem()).toString(), out.getCount(), String.valueOf(out.getTag()),
            recipe.stockTicks(), recipe.stockEnergyPerTick());
        return recipe.id() + "#" + Integer.toHexString(hash);
    }

    private static boolean fits(ItemStack existing, ItemStack result) {
        return result.isEmpty() || result.getCount() <= result.getMaxStackSize()
            && (existing.isEmpty() || ItemStack.isSameItemSameTags(existing, result)
                && existing.getCount() + result.getCount() <= existing.getMaxStackSize());
    }

    private void idle(String reason) {
        status = reason;
        activeInstance = null;
        if (progress != 0 || !activeRecipe.isEmpty()) { progress = 0; activeRecipe = ""; setChanged(); }
    }

    void dropStock() {
        if (level == null) return;
        for (int slot = 0; slot < stock.getSlots(); slot++) {
            ItemStack stack = stock.getStackInSlot(slot);
            if (!stack.isEmpty()) Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack);
            stock.setStackInSlot(slot, ItemStack.EMPTY);
        }
    }

    @Override protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!automated()) return;
        tag.put("Stock", stock.serializeNBT());
        tag.putInt("Progress", progress); tag.putString("Recipe", activeRecipe);
        if (energy != null) tag.putInt("Energy", energy.getEnergyStored());
        CompoundTag sides = new CompoundTag();
        for (Direction side : Direction.values()) sides.putInt(side.getName(), sideMode(side).ordinal());
        tag.put("ItemSides", sides);
    }

    @Override public void load(CompoundTag tag) {
        super.load(tag);
        FixedInventory.load(stock, tag.getCompound("Stock"));
        progress = Math.max(0, Math.min(MAX_TICKS, tag.getInt("Progress")));
        if (energy != null) energy.restore(tag.getInt("Energy"));
        activeRecipe = tag.getString("Recipe"); activeInstance = null; status = "idle";
        CompoundTag sides = tag.getCompound("ItemSides");
        for (Direction side : Direction.values()) sideModes.put(side, sides.contains(side.getName(), Tag.TAG_INT)
            ? ItemPortMode.fromSaved(sides.getInt(side.getName())) : defaultMode(side));
    }

    @Override public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
        if (capability == ForgeCapabilities.ITEM_HANDLER && automated()) return (side == null ? unsided : ports.get(side)).cast();
        if (capability == ForgeCapabilities.ENERGY && energy != null) return power.cast();
        return super.getCapability(capability, side);
    }
    @Override public void invalidateCaps() {
        super.invalidateCaps(); unsided.invalidate(); power.invalidate(); ports.values().forEach(LazyOptional::invalidate);
    }
    @Override public void reviveCaps() {
        super.reviveCaps(); unsided = LazyOptional.of(() -> new Port(null));
        if (energy != null) power = LazyOptional.of(() -> energy);
        for (Direction side : Direction.values()) ports.put(side, LazyOptional.of(() -> new Port(side)));
    }

    /**
     * World view of the buffer: output faces see the product and optional by-product slots; input faces see the stock slot,
     * then the reagent slot on two-input controllers, so a hopper fills whichever slot accepts the item.
     */
    private class Port implements IItemHandler {
        private final Direction side;
        private Port(@Nullable Direction side) { this.side = side; }
        private ItemPortMode mode() { return side == null ? ItemPortMode.INPUT : sideMode(side); }
        private int slot(int slot) {
            if (slot < 0 || slot >= getSlots()) throw new IndexOutOfBoundsException(slot);
            return mode() == ItemPortMode.OUTPUT ? (slot == 0 ? PRODUCT : BYPRODUCT) : slot == 0 ? STOCK : REAGENT;
        }
        @Override public int getSlots() {
            if (mode() == ItemPortMode.OUTPUT) {
                StockFedControllerBlock block = stockBlock();
                return block != null && level != null && level.getRecipeManager().getRecipes().stream()
                    .filter(r -> r.getType() == block.recipeTypeForStock() && r instanceof SingleStockRecipe)
                    .map(r -> (SingleStockRecipe) r).anyMatch(r -> java.util.Arrays.stream(r.input().getItems()).anyMatch(s -> !block.stockByproduct(r, s).isEmpty())) ? 2 : 1;
            }
            return usesReagent() ? 2 : 1;
        }
        @Override public ItemStack getStackInSlot(int slot) {
            int mapped = slot(slot);
            return mode() == ItemPortMode.DISABLED ? ItemStack.EMPTY : stock.getStackInSlot(mapped).copy();
        }
        @Override public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            int mapped = slot(slot); return mode() == ItemPortMode.INPUT ? stock.insertItem(mapped, stack, simulate) : stack;
        }
        @Override public ItemStack extractItem(int slot, int amount, boolean simulate) {
            int mapped = slot(slot); return mode() == ItemPortMode.OUTPUT && amount > 0 ? stock.extractItem(mapped, amount, simulate) : ItemStack.EMPTY;
        }
        @Override public int getSlotLimit(int slot) { return stock.getSlotLimit(slot(slot)); }
        @Override public boolean isItemValid(int slot, ItemStack stack) {
            int mapped = slot(slot); return mode() == ItemPortMode.INPUT && stock.isItemValid(mapped, stack);
        }
    }
}
