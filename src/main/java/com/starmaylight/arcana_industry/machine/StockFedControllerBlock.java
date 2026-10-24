package com.starmaylight.arcana_industry.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * A hand-fed controller that can also be automated: hoppers or pipes fill a buffered stock slot,
 * the formed structure runs one batch every {@link HandFedControllerBlockEntity#BATCH_TICKS} ticks,
 * and products wait in an output buffer extracted from the bottom face by default.
 * Sneak-using an empty hand cycles the clicked face between input, output and disabled;
 * an empty hand otherwise collects the output buffer.
 */
public abstract class StockFedControllerBlock extends Block implements EntityBlock {
    /** Stock Hatches one frame may use in place of its controller-adjacent parts. */
    public static final int MAX_HATCHES = 2;

    protected StockFedControllerBlock(Properties properties) { super(properties); }

    /** Lets a Stock Hatch stand in for every part that touches the controller face-to-face. */
    protected static java.util.List<StructureMatcher.Part> hatchable(java.util.List<StructureMatcher.Part> frame) {
        return frame.stream().map(part -> Math.abs(part.right()) + Math.abs(part.up()) + Math.abs(part.forward()) != 1 ? part
            : new StructureMatcher.Part(part.right(), part.up(), part.forward(), (level, at) -> isHatch(level, at) || part.condition().test(level, at)))
            .toList();
    }
    protected static boolean isHatch(Level level, BlockPos at) { return level.getBlockState(at).is(MachineRegistry.STOCK_HATCH.get()); }
    /** True while no more than {@link #MAX_HATCHES} Stock Hatches touch the controller; unloaded neighbours count as absent. */
    public static boolean hatchesWithinLimit(Level level, BlockPos pos) {
        int hatches = 0;
        for (net.minecraft.core.Direction side : net.minecraft.core.Direction.values())
            if (level.hasChunkAt(pos.relative(side)) && isHatch(level, pos.relative(side))) hatches++;
        return hatches <= MAX_HATCHES;
    }

    /** The exclusive recipe type the buffered stock runs; recipes must implement SingleStockRecipe. */
    protected abstract RecipeType<?> stockRecipeType();
    /** Recipe batches consumed and produced together per cycle; Rotary Screen runs two like its manual use. */
    protected int stockBatches() { return 1; }
    /** Whether batches also draw a second ingredient from the reagent slot (see SingleStockRecipe#reagent). */
    protected boolean stockReagent() { return false; }
    /**
     * FE buffer size for frames whose recipes draw power (SingleStockRecipe#stockEnergyPerTick); zero keeps the frame
     * unpowered and exposes no energy capability. Cables and generators fill the buffer through any face of the controller.
     */
    protected int stockEnergyCapacity() { return 0; }
    /** Extra per-stack stock filter, e.g. rejecting damaged armor no recipe will ever accept. */
    protected boolean stockAccepts(ItemStack stack) { return true; }
    /** Whether this controller currently owns a complete structure. */
    protected abstract boolean stockFormed(Level level, BlockPos pos);
    /** The product of one batch cut from {@code stock}; an empty result pauses the controller. */
    public ItemStack stockResult(com.starmaylight.arcana_industry.recipe.SingleStockRecipe recipe, ItemStack stock) { return recipe.output(); }
    /** Reagent-aware result hook for paired batches; legacy one-input overrides remain valid. */
    public ItemStack stockResult(com.starmaylight.arcana_industry.recipe.SingleStockRecipe recipe, ItemStack stock, ItemStack reagent) {
        return stockResult(recipe, stock);
    }
    /** Optional second output, kept in its own extractable slot. */
    public ItemStack stockByproduct(com.starmaylight.arcana_industry.recipe.SingleStockRecipe recipe) { return ItemStack.EMPTY; }
    /** Stock-aware by-product hook, e.g. a container left behind by the consumed stock. */
    public ItemStack stockByproduct(com.starmaylight.arcana_industry.recipe.SingleStockRecipe recipe, ItemStack stock) {
        return stockByproduct(recipe);
    }
    /** A status key that holds the batch without consuming anything, or null when the batch may run. */
    @org.jetbrains.annotations.Nullable
    protected String stockHold(Level level, BlockPos pos, com.starmaylight.arcana_industry.recipe.SingleStockRecipe recipe,
                               ItemStack stock, ItemStack reagent) { return null; }
    /** Called once per finished cycle, before its inputs are consumed, to pay structure-side costs. */
    protected void stockCompleted(Level level, BlockPos pos, com.starmaylight.arcana_industry.recipe.SingleStockRecipe recipe,
                                  ItemStack stock, ItemStack reagent) {}

    /**
     * Manual feeding for timed frames: loads the held recipe feedstock into the stock buffer, where the formed structure
     * processes it exactly like hopper-fed stock. Returns PASS when no loaded recipe of this frame accepts the item.
     */
    protected InteractionResult loadHeldStock(Level level, BlockPos pos, Player player, ItemStack held) {
        if (!(level.getBlockEntity(pos) instanceof HandFedControllerBlockEntity controller)) return InteractionResult.PASS;
        ItemStack offered = held.copy();
        ItemStack left = controller.insertStock(offered, true);
        if (left.getCount() == offered.getCount()) {
            if (!controller.acceptsStockItem(offered)) return InteractionResult.PASS;
            player.displayClientMessage(Component.translatable("message.arcana_industry.stock_fed.busy"), true);
            return InteractionResult.CONSUME;
        }
        left = controller.insertStock(offered, false);
        if (!player.getAbilities().instabuild) held.setCount(left.getCount());
        player.displayClientMessage(Component.translatable("message.arcana_industry.stock_fed.loaded",
            controller.inputStack().getCount(), controller.energyStored(), controller.energyCapacity()), true);
        return InteractionResult.CONSUME;
    }

    protected InteractionResult useEmptyHand(Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof HandFedControllerBlockEntity controller)) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (player.isShiftKeyDown()) {
            ItemPortMode mode = controller.cycleSide(hit.getDirection());
            player.displayClientMessage(Component.translatable("message.arcana_industry.stock_fed.side",
                Component.translatable("direction.arcana_industry." + hit.getDirection().getName()),
                Component.translatable("port.arcana_industry." + mode.name().toLowerCase(java.util.Locale.ROOT))), true);
            return InteractionResult.CONSUME;
        }
        ItemStack taken = controller.takeOutput();
        if (taken.isEmpty()) return InteractionResult.PASS;
        if (!player.getInventory().add(taken)) player.drop(taken, false);
        return InteractionResult.CONSUME;
    }

    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : (lvl, pos, st, be) -> {
            if (be instanceof HandFedControllerBlockEntity controller) controller.tickStock(this);
        };
    }

    @Override public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof HandFedControllerBlockEntity controller)
            controller.dropStock();
        super.onRemove(state, level, pos, newState, moved);
    }

    boolean acceptsForStock(ItemStack stack) { return stockAccepts(stack); }
    boolean formedForStock(Level level, BlockPos pos) { return stockFormed(level, pos); }
    RecipeType<?> recipeTypeForStock() { return stockRecipeType(); }
    public int batchesForStock() { return stockBatches(); }
    int energyCapacityForStock() { return stockEnergyCapacity(); }
    boolean reagentForStock() { return stockReagent(); }
    String holdForStock(Level level, BlockPos pos, com.starmaylight.arcana_industry.recipe.SingleStockRecipe recipe,
                        ItemStack stock, ItemStack reagent) { return stockHold(level, pos, recipe, stock, reagent); }
    void completedForStock(Level level, BlockPos pos, com.starmaylight.arcana_industry.recipe.SingleStockRecipe recipe,
                           ItemStack stock, ItemStack reagent) { stockCompleted(level, pos, recipe, stock, reagent); }
}
