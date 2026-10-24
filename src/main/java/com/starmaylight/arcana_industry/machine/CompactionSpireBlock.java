package com.starmaylight.arcana_industry.machine;

import com.starmaylight.arcana_industry.recipe.CompactionRecipe;
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
 * Vertical frame compacts two loaded compaction batches at a time. The north and south copper
 * buttresses stand two blocks out, leaving those faces open for automated stock and products.
 */
public class CompactionSpireBlock extends StockFedControllerBlock {
    private static final List<StructureMatcher.Part> PARTS = List.of(
        new StructureMatcher.Part(0, -1, 0, (level, at) -> level.getBlockState(at).is(Blocks.OBSIDIAN)),
        new StructureMatcher.Part(0, 1, 0, (level, at) -> level.getBlockState(at).is(Blocks.REDSTONE_BLOCK)),
        new StructureMatcher.Part(1, 0, 0, (level, at) -> level.getBlockState(at).is(Blocks.COPPER_BLOCK)),
        new StructureMatcher.Part(-1, 0, 0, (level, at) -> level.getBlockState(at).is(Blocks.COPPER_BLOCK)),
        new StructureMatcher.Part(0, 0, 2, (level, at) -> level.getBlockState(at).is(Blocks.COPPER_BLOCK)),
        new StructureMatcher.Part(0, 0, -2, (level, at) -> level.getBlockState(at).is(Blocks.COPPER_BLOCK)),
        new StructureMatcher.Part(1, 1, -1, (level, at) -> level.getBlockState(at).is(Blocks.IRON_BLOCK)),
        new StructureMatcher.Part(1, 1, 1, (level, at) -> level.getBlockState(at).is(Blocks.IRON_BLOCK)),
        new StructureMatcher.Part(-1, 1, -1, (level, at) -> level.getBlockState(at).is(Blocks.IRON_BLOCK)),
        new StructureMatcher.Part(-1, 1, 1, (level, at) -> level.getBlockState(at).is(Blocks.IRON_BLOCK)),
        new StructureMatcher.Part(1, 2, 0, (level, at) -> level.getBlockState(at).is(Blocks.PISTON)),
        new StructureMatcher.Part(-1, 2, 0, (level, at) -> level.getBlockState(at).is(Blocks.PISTON)),
        new StructureMatcher.Part(0, 2, 1, (level, at) -> level.getBlockState(at).is(Blocks.PISTON)),
        new StructureMatcher.Part(0, 2, -1, (level, at) -> level.getBlockState(at).is(Blocks.PISTON)),
        new StructureMatcher.Part(0, 3, 0, (level, at) -> level.getBlockState(at).is(Blocks.IRON_BLOCK)));
    public CompactionSpireBlock() { super(Properties.of().mapColor(MapColor.METAL).strength(4.0F).requiresCorrectToolForDrops()); }
    @Override protected net.minecraft.world.item.crafting.RecipeType<?> stockRecipeType() { return MachineRegistry.COMPACTION.get(); }
    @Override protected int stockBatches() { return 2; }
    @Override protected boolean stockFormed(Level level, BlockPos pos) { return formed(level, pos); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HandFedControllerBlockEntity(MachineRegistry.COMPACTION_SPIRE_ENTITY.get(), pos, state);
    }
    public static boolean formed(Level level, BlockPos pos) {
        return StructureMatcher.ownsParts(level, pos, Direction.NORTH, PARTS, MachineRegistry.COMPACTION_SPIRE.get());
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) return useEmptyHand(level, pos, player, hit);
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!formed(level, pos)) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.compaction_spire.incomplete"), true);
            return InteractionResult.CONSUME;
        }
        var view = new SimpleContainer(held);
        CompactionRecipe recipe = level.getRecipeManager().getAllRecipesFor(MachineRegistry.COMPACTION.get()).stream()
            .filter(r -> r.matches(view, level) && r.count() <= 32 && held.getCount() >= 2 * r.count())
            .min(Comparator.comparing(r -> r.id().toString())).orElse(null);
        if (recipe == null || recipe.output().isEmpty()) return InteractionResult.PASS;
        if (!(level.getBlockEntity(pos) instanceof HandFedControllerBlockEntity controller)) return InteractionResult.PASS;
        ItemStack batch = held.copyWithCount(2 * recipe.count());
        if (!controller.insertStock(batch, true).isEmpty()) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.stock_fed.busy"), true);
            return InteractionResult.CONSUME;
        }
        InteractionResult loaded = loadHeldStock(level, pos, player, batch);
        if (loaded != InteractionResult.CONSUME) return loaded;
        if (!player.getAbilities().instabuild) held.shrink(2 * recipe.count());
        com.starmaylight.arcana_industry.sound.ModSounds.playMultiblock(level, pos, "compaction_spire");
        return InteractionResult.CONSUME;
    }
}
