package com.starmaylight.arcana_industry.machine;

import com.starmaylight.arcana_industry.recipe.EnamelingRecipe;
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

/** Fuses a base batch with a metal colorant in a raised brick flue over a lava cauldron. */
public class EnamelKilnBlock extends StockFedControllerBlock {
    private static final List<StructureMatcher.Part> FRAME = List.of(
        new StructureMatcher.Part(0, -1, 0, (level, p) -> level.getBlockState(p).is(Blocks.LAVA_CAULDRON)),
        new StructureMatcher.Part(1, 0, 0, (level, p) -> level.getBlockState(p).is(Blocks.BRICKS)),
        new StructureMatcher.Part(-1, 0, 0, (level, p) -> level.getBlockState(p).is(Blocks.BRICKS)),
        new StructureMatcher.Part(0, 0, 2, (level, p) -> level.getBlockState(p).is(Blocks.BRICKS)),
        new StructureMatcher.Part(0, 0, -2, (level, p) -> level.getBlockState(p).is(Blocks.BRICKS)),
        new StructureMatcher.Part(1, 1, 0, (level, p) -> level.getBlockState(p).is(Blocks.BRICKS)),
        new StructureMatcher.Part(-1, 1, 0, (level, p) -> level.getBlockState(p).is(Blocks.BRICKS)),
        new StructureMatcher.Part(0, 1, 2, (level, p) -> level.getBlockState(p).is(Blocks.BRICKS)),
        new StructureMatcher.Part(0, 1, -2, (level, p) -> level.getBlockState(p).is(Blocks.BRICKS)),
        new StructureMatcher.Part(0, 2, 2, (level, p) -> level.getBlockState(p).is(Blocks.IRON_BARS)),
        new StructureMatcher.Part(0, 2, -2, (level, p) -> level.getBlockState(p).is(Blocks.IRON_BARS))
    );
    public EnamelKilnBlock() { super(Properties.of().mapColor(MapColor.COLOR_RED).strength(4.0F).requiresCorrectToolForDrops().lightLevel(s -> 7)); }
    @Override protected net.minecraft.world.item.crafting.RecipeType<?> stockRecipeType() { return MachineRegistry.ENAMELING.get(); }
    @Override protected boolean stockReagent() { return true; }
    @Override protected boolean stockFormed(Level level, BlockPos pos) { return formed(level, pos); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HandFedControllerBlockEntity(MachineRegistry.ENAMEL_KILN_ENTITY.get(), pos, state);
    }
    /** Kilns sharing a brick resolve it to the lower-position controller, like every other hand-fed frame. */
    public static boolean formed(Level level, BlockPos pos) {
        return StructureMatcher.ownsParts(level, pos, Direction.NORTH, FRAME, MachineRegistry.ENAMEL_KILN.get());
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        ItemStack held = player.getMainHandItem();
        ItemStack colorant = player.getOffhandItem();
        if (held.isEmpty()) return useEmptyHand(level, pos, player, hit);
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!formed(level, pos)) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.enamel_kiln.incomplete"), true);
            return InteractionResult.CONSUME;
        }
        var view = new SimpleContainer(held, colorant);
        EnamelingRecipe recipe = level.getRecipeManager().getAllRecipesFor(MachineRegistry.ENAMELING.get()).stream()
            .filter(r -> r.matches(view, level))
            .min(Comparator.comparing(r -> r.id().toString())).orElse(null);
        if (recipe == null) {
            if (colorant.isEmpty())
                player.displayClientMessage(Component.translatable("message.arcana_industry.enamel_kiln.no_colorant"), true);
            return InteractionResult.PASS;
        }
        if (!(level.getBlockEntity(pos) instanceof HandFedControllerBlockEntity controller)) return InteractionResult.PASS;
        ItemStack baseBatch = held.copyWithCount(recipe.count());
        ItemStack colorantBatch = colorant.copyWithCount(1);
        if (!controller.insertStock(baseBatch, true).isEmpty()
            || !controller.insertReagent(colorantBatch, true).isEmpty()) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.stock_fed.busy"), true);
            return InteractionResult.CONSUME;
        }
        InteractionResult loaded = loadHeldStock(level, pos, player, baseBatch);
        if (loaded != InteractionResult.CONSUME) return loaded;
        controller.insertReagent(colorantBatch, false);
        if (!player.getAbilities().instabuild) {
            held.shrink(recipe.count());
            colorant.shrink(1);
        }
        return InteractionResult.CONSUME;
    }
}
