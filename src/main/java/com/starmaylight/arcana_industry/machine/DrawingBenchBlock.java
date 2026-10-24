package com.starmaylight.arcana_industry.machine;

import com.starmaylight.arcana_industry.recipe.WireDrawingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
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
 * Pulls a metal batch through a die braced on an anvil between four guarded grindstone capstans.
 * The north and south capstans stand two blocks out so those faces stay open for stock and product.
 * A hand pull loads one recipe batch into the timed stock buffer and charges the puller's hunger up front;
 * hopper-fed stock runs the same timer without any hunger cost.
 */
public class DrawingBenchBlock extends StockFedControllerBlock {
    /** Players at or below this food level (no sprinting) are too tired to draw wire. */
    public static final int MIN_FOOD_LEVEL = 7;
    private static final List<StructureMatcher.Part> FRAME = List.of(
        new StructureMatcher.Part(0, -1, 0, (level, p) -> level.getBlockState(p).is(BlockTags.ANVIL)),
        new StructureMatcher.Part(1, 0, 0, (level, p) -> level.getBlockState(p).is(Blocks.GRINDSTONE)),
        new StructureMatcher.Part(-1, 0, 0, (level, p) -> level.getBlockState(p).is(Blocks.GRINDSTONE)),
        new StructureMatcher.Part(0, 0, 2, (level, p) -> level.getBlockState(p).is(Blocks.GRINDSTONE)),
        new StructureMatcher.Part(0, 0, -2, (level, p) -> level.getBlockState(p).is(Blocks.GRINDSTONE)),
        new StructureMatcher.Part(1, 1, 0, (level, p) -> level.getBlockState(p).is(Blocks.IRON_BARS)),
        new StructureMatcher.Part(-1, 1, 0, (level, p) -> level.getBlockState(p).is(Blocks.IRON_BARS)),
        new StructureMatcher.Part(0, 1, 2, (level, p) -> level.getBlockState(p).is(Blocks.IRON_BARS)),
        new StructureMatcher.Part(0, 1, -2, (level, p) -> level.getBlockState(p).is(Blocks.IRON_BARS))
    );
    public DrawingBenchBlock() { super(Properties.of().mapColor(MapColor.METAL).strength(4.0F).requiresCorrectToolForDrops()); }
    @Override protected net.minecraft.world.item.crafting.RecipeType<?> stockRecipeType() { return MachineRegistry.WIRE_DRAWING.get(); }
    @Override protected boolean stockFormed(Level level, BlockPos pos) { return formed(level, pos); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HandFedControllerBlockEntity(MachineRegistry.DRAWING_BENCH_ENTITY.get(), pos, state);
    }
    /** Benches sharing a grindstone resolve it to the lower-position controller, like every other hand-fed frame. */
    public static boolean formed(Level level, BlockPos pos) {
        return StructureMatcher.ownsParts(level, pos, Direction.NORTH, FRAME, MachineRegistry.DRAWING_BENCH.get());
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        ItemStack held = player.getMainHandItem();
        if (held.isEmpty()) return useEmptyHand(level, pos, player, hit);
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!formed(level, pos)) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.drawing_bench.incomplete"), true);
            return InteractionResult.CONSUME;
        }
        var view = new SimpleContainer(held);
        WireDrawingRecipe recipe = level.getRecipeManager().getAllRecipesFor(MachineRegistry.WIRE_DRAWING.get()).stream()
            .filter(r -> r.matches(view, level))
            .min(Comparator.comparing(r -> r.id().toString())).orElse(null);
        if (recipe == null) return InteractionResult.PASS;
        if (!player.getAbilities().instabuild && player.getFoodData().getFoodLevel() < MIN_FOOD_LEVEL) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.drawing_bench.tired"), true);
            return InteractionResult.CONSUME;
        }
        if (!(level.getBlockEntity(pos) instanceof HandFedControllerBlockEntity controller)) return InteractionResult.PASS;
        ItemStack batch = held.copyWithCount(recipe.count());
        // One pull at a time: a loaded batch must be drawn before the player can pay for the next one.
        if (!controller.inputStack().isEmpty() || !controller.insertStock(batch, true).isEmpty()) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.stock_fed.busy"), true);
            return InteractionResult.CONSUME;
        }
        InteractionResult loaded = loadHeldStock(level, pos, player, batch);
        if (loaded != InteractionResult.CONSUME) return loaded;
        if (!player.getAbilities().instabuild) {
            held.shrink(recipe.count());
            player.causeFoodExhaustion(recipe.exhaustion());
        }
        com.starmaylight.arcana_industry.sound.ModSounds.playMultiblock(level, pos, "drawing_bench");
        return InteractionResult.CONSUME;
    }
}
