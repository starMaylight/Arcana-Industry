package com.starmaylight.arcana_industry.machine;

import com.starmaylight.arcana_industry.recipe.SilveringRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import java.util.Comparator;
import java.util.List;

/**
 * Coats a held substrate batch with an off-hand metal ingot over a water-cauldron rinse, braced by iron bars.
 * The north and south bars stand two blocks out so those faces stay open; automated feeds buffer the
 * substrate and coating ingot in separate stock and reagent slots.
 */
public class SilveringBathBlock extends StockFedControllerBlock {
    private static final List<StructureMatcher.Part> FRAME = List.of(
        new StructureMatcher.Part(0, -1, 0, (level, p) -> level.getBlockState(p).is(Blocks.WATER_CAULDRON)),
        new StructureMatcher.Part(1, 0, 0, (level, p) -> level.getBlockState(p).is(Blocks.IRON_BARS)),
        new StructureMatcher.Part(-1, 0, 0, (level, p) -> level.getBlockState(p).is(Blocks.IRON_BARS)),
        new StructureMatcher.Part(0, 0, 2, (level, p) -> level.getBlockState(p).is(Blocks.IRON_BARS)),
        new StructureMatcher.Part(0, 0, -2, (level, p) -> level.getBlockState(p).is(Blocks.IRON_BARS))
    );
    public SilveringBathBlock() { super(Properties.of().mapColor(MapColor.METAL).strength(4.0F).requiresCorrectToolForDrops()); }
    @Override protected net.minecraft.world.item.crafting.RecipeType<?> stockRecipeType() { return MachineRegistry.SILVERING.get(); }
    @Override protected boolean stockReagent() { return true; }
    @Override protected boolean stockFormed(Level level, BlockPos pos) { return formed(level, pos); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HandFedControllerBlockEntity(MachineRegistry.SILVERING_BATH_ENTITY.get(), pos, state);
    }
    /** Two baths sharing a bar resolve it to the lower-position controller, like every other hand-fed frame. */
    public static boolean formed(Level level, BlockPos pos) {
        return StructureMatcher.ownsParts(level, pos, Direction.NORTH, FRAME, MachineRegistry.SILVERING_BATH.get());
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        ItemStack held = player.getMainHandItem();
        ItemStack coating = player.getOffhandItem();
        if (held.isEmpty()) return useEmptyHand(level, pos, player, hit);
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!formed(level, pos)) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.silvering_bath.incomplete"), true);
            return InteractionResult.CONSUME;
        }
        var view = new SimpleContainer(held, coating);
        SilveringRecipe recipe = level.getRecipeManager().getAllRecipesFor(MachineRegistry.SILVERING.get()).stream()
            .filter(r -> r.matches(view, level))
            .min(Comparator.comparing(r -> r.id().toString())).orElse(null);
        if (recipe == null) {
            if (coating.isEmpty())
                player.displayClientMessage(Component.translatable("message.arcana_industry.silvering_bath.no_coating"), true);
            return InteractionResult.PASS;
        }
        if (!(level.getBlockEntity(pos) instanceof HandFedControllerBlockEntity controller)) return InteractionResult.PASS;
        ItemStack substrateBatch = held.copyWithCount(recipe.count());
        ItemStack coatingBatch = coating.copyWithCount(1);
        if (!controller.insertStock(substrateBatch, true).isEmpty()
            || !controller.insertReagent(coatingBatch, true).isEmpty()) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.stock_fed.busy"), true);
            return InteractionResult.CONSUME;
        }
        InteractionResult loaded = loadHeldStock(level, pos, player, substrateBatch);
        if (loaded != InteractionResult.CONSUME) return loaded;
        controller.insertReagent(coatingBatch, false);
        if (!player.getAbilities().instabuild) {
            held.shrink(recipe.count());
            coating.shrink(1);
        }
        return InteractionResult.CONSUME;
    }
}
