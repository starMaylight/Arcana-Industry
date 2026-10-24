package com.starmaylight.arcana_industry.machine;

import com.starmaylight.arcana_industry.recipe.ForgingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import java.util.Comparator;
import java.util.List;

/**
 * Forges metal stock in a smithy forge; every heat is stoked with fuel. Players hold stock in the main hand and
 * fuel in the off hand; automation fills the north face's stock and fuel slots and pulls work from the south face.
 * The 3x3, six-course structure is a blacksmith's hearth: a stone-brick plinth around a magma bed,
 * east/west coal walls with brick corners at controller height (north and south faces open for ports),
 * four brick pillars leaving open working gaps, a brick hood ring and a two-course brick chimney around
 * the open flue. A stone-brick work apron projects two blocks south with an anvil flanked by two coal bins.
 */
public class CoalForgeBlock extends StockFedControllerBlock {
    private static final int[][] SIDES = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    private static final int[][] CORNERS = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
    private static final List<StructureMatcher.Part> FRAME = frame();

    private static StructureMatcher.Part part(int right, int up, int forward, Block block) {
        return new StructureMatcher.Part(right, up, forward, (level, p) -> level.getBlockState(p).is(block));
    }

    /** Parts are listed course by course, bottom to top, matching tools/generate_multiblock_patterns.py. */
    private static List<StructureMatcher.Part> frame() {
        var parts = new java.util.ArrayList<StructureMatcher.Part>();
        parts.add(part(0, -1, 0, Blocks.MAGMA_BLOCK));
        for (int[] s : SIDES) parts.add(part(s[0], -1, s[1], Blocks.STONE_BRICKS));
        for (int[] c : CORNERS) parts.add(part(c[0], -1, c[1], Blocks.STONE_BRICKS));
        for (int right : new int[]{1, -1}) parts.add(part(right, 0, 0, Blocks.COAL_BLOCK));
        for (int[] c : CORNERS) parts.add(part(c[0], 0, c[1], Blocks.BRICKS));
        for (int[] c : CORNERS) parts.add(part(c[0], 1, c[1], Blocks.BRICKS));
        for (int[] s : SIDES) parts.add(part(s[0], 2, s[1], Blocks.BRICKS));
        for (int[] c : CORNERS) parts.add(part(c[0], 2, c[1], Blocks.BRICKS));
        for (int up = 3; up <= 4; up++) for (int[] s : SIDES) parts.add(part(s[0], up, s[1], Blocks.BRICKS));
        for (int right = -1; right <= 1; right++) parts.add(part(right, -1, -2, Blocks.STONE_BRICKS));
        parts.add(part(0, 0, -2, Blocks.ANVIL));
        for (int right : new int[]{1, -1}) parts.add(part(right, 0, -2, Blocks.COAL_BLOCK));
        return List.copyOf(parts);
    }
    public CoalForgeBlock() { super(Properties.of().mapColor(MapColor.COLOR_BLACK).strength(3.5F).requiresCorrectToolForDrops()); }
    @Override protected net.minecraft.world.item.crafting.RecipeType<?> stockRecipeType() { return MachineRegistry.FORGING.get(); }
    @Override protected boolean stockReagent() { return true; }
    @Override protected boolean stockFormed(Level level, BlockPos pos) { return formed(level, pos); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HandFedControllerBlockEntity(MachineRegistry.COAL_FORGE_ENTITY.get(), pos, state);
    }
    /** Forges sharing a coal block resolve it to the lower-position controller, like every other hand-fed frame. */
    public static boolean formed(Level level, BlockPos pos) {
        return StructureMatcher.ownsParts(level, pos, Direction.NORTH, FRAME, MachineRegistry.COAL_FORGE.get());
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        ItemStack held = player.getMainHandItem();
        if (held.isEmpty()) return useEmptyHand(level, pos, player, hit);
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!formed(level, pos)) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.coal_forge.incomplete"), true);
            return InteractionResult.CONSUME;
        }
        ItemStack fuel = player.getOffhandItem();
        var view = new SimpleContainer(held, fuel);
        List<ForgingRecipe> recipes = level.getRecipeManager().getAllRecipesFor(MachineRegistry.FORGING.get()).stream()
            .sorted(Comparator.comparing(r -> r.id().toString())).toList();
        ForgingRecipe recipe = recipes.stream().filter(r -> r.matches(view, level)).findFirst().orElse(null);
        if (recipe == null) {
            ForgingRecipe starved = recipes.stream().filter(r -> r.matchesStock(held)).findFirst().orElse(null);
            if (starved == null) return InteractionResult.PASS;
            player.displayClientMessage(Component.translatable("message.arcana_industry.coal_forge.fuel", starved.fuelCount()), true);
            return InteractionResult.CONSUME;
        }
        if (!(level.getBlockEntity(pos) instanceof HandFedControllerBlockEntity controller)) return InteractionResult.PASS;
        // One hand load is one heat: stock and its fuel enter the buffers and the ticker forges them like hopper feed.
        ItemStack stockBatch = held.copyWithCount(recipe.count());
        ItemStack fuelBatch = fuel.copyWithCount(recipe.fuelCount());
        if (!controller.insertStock(stockBatch, true).isEmpty() || !controller.insertReagent(fuelBatch, true).isEmpty()) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.stock_fed.busy"), true);
            return InteractionResult.CONSUME;
        }
        InteractionResult loaded = loadHeldStock(level, pos, player, stockBatch);
        if (loaded != InteractionResult.CONSUME) return loaded;
        controller.insertReagent(fuelBatch, false);
        if (!player.getAbilities().instabuild) {
            held.shrink(recipe.count());
            fuel.shrink(recipe.fuelCount());
        }
        com.starmaylight.arcana_industry.sound.ModSounds.playMultiblock(level, pos, "coal_forge");
        return InteractionResult.CONSUME;
    }
}
