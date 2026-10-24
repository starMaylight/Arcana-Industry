package com.starmaylight.arcana_industry.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.starmaylight.arcana_industry.recipe.FoundrySmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import java.util.Comparator;
import java.util.List;

/** Four furnace stations surround the controller above a lined lava hearth and vented hood. */
public class FoundryRingBlock extends StockFedControllerBlock {
    private static final List<StructureMatcher.Part> PARTS = List.of(
        new StructureMatcher.Part(0, -1, 0, (level, at) -> level.getFluidState(at).getType() == Fluids.LAVA && level.getFluidState(at).isSource()),
        new StructureMatcher.Part(1, -1, 0, (level, at) -> level.getBlockState(at).is(Blocks.STONE_BRICKS)),
        new StructureMatcher.Part(-1, -1, 0, (level, at) -> level.getBlockState(at).is(Blocks.STONE_BRICKS)),
        new StructureMatcher.Part(0, -1, 1, (level, at) -> level.getBlockState(at).is(Blocks.STONE_BRICKS)),
        new StructureMatcher.Part(0, -1, -1, (level, at) -> level.getBlockState(at).is(Blocks.STONE_BRICKS)),
        new StructureMatcher.Part(1, -1, 1, (level, at) -> level.getBlockState(at).is(Blocks.IRON_BLOCK)),
        new StructureMatcher.Part(1, -1, -1, (level, at) -> level.getBlockState(at).is(Blocks.IRON_BLOCK)),
        new StructureMatcher.Part(-1, -1, 1, (level, at) -> level.getBlockState(at).is(Blocks.IRON_BLOCK)),
        new StructureMatcher.Part(-1, -1, -1, (level, at) -> level.getBlockState(at).is(Blocks.IRON_BLOCK)),
        new StructureMatcher.Part(1, 0, 0, (level, at) -> level.getBlockState(at).is(Blocks.FURNACE)),
        new StructureMatcher.Part(-1, 0, 0, (level, at) -> level.getBlockState(at).is(Blocks.FURNACE)),
        new StructureMatcher.Part(0, 0, 1, (level, at) -> level.getBlockState(at).is(Blocks.FURNACE)),
        new StructureMatcher.Part(0, 0, -2, (level, at) -> level.getBlockState(at).is(Blocks.FURNACE)),
        new StructureMatcher.Part(1, 0, 1, (level, at) -> level.getBlockState(at).is(Blocks.BRICKS)),
        new StructureMatcher.Part(1, 0, -1, (level, at) -> level.getBlockState(at).is(Blocks.BRICKS)),
        new StructureMatcher.Part(-1, 0, 1, (level, at) -> level.getBlockState(at).is(Blocks.BRICKS)),
        new StructureMatcher.Part(-1, 0, -1, (level, at) -> level.getBlockState(at).is(Blocks.BRICKS)),
        new StructureMatcher.Part(1, 1, 0, (level, at) -> level.getBlockState(at).is(Blocks.IRON_BARS)),
        new StructureMatcher.Part(-1, 1, 0, (level, at) -> level.getBlockState(at).is(Blocks.IRON_BARS)),
        new StructureMatcher.Part(0, 1, 1, (level, at) -> level.getBlockState(at).is(Blocks.IRON_BARS)),
        new StructureMatcher.Part(0, 1, -1, (level, at) -> level.getBlockState(at).is(Blocks.IRON_BARS)),
        new StructureMatcher.Part(1, 1, 1, (level, at) -> level.getBlockState(at).is(Blocks.COPPER_BLOCK)),
        new StructureMatcher.Part(1, 1, -1, (level, at) -> level.getBlockState(at).is(Blocks.CAULDRON)),
        new StructureMatcher.Part(-1, 1, 1, (level, at) -> level.getBlockState(at).is(Blocks.COPPER_BLOCK)),
        new StructureMatcher.Part(-1, 1, -1, (level, at) -> level.getBlockState(at).is(Blocks.CAULDRON)));
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HandFedControllerBlockEntity(MachineRegistry.FOUNDRY_RING_ENTITY.get(), pos, state);
    }
    public FoundryRingBlock() { super(Properties.of().mapColor(MapColor.METAL).strength(3.5F).requiresCorrectToolForDrops()); }

    @Override protected net.minecraft.world.item.crafting.RecipeType<?> stockRecipeType() { return MachineRegistry.FOUNDRY_SMELTING.get(); }
    @Override protected int stockBatches() { return 8; }
    @Override protected boolean stockFormed(Level level, BlockPos pos) { return formed(level, pos); }

    public static boolean formed(Level level, BlockPos pos) {
        return StructureMatcher.ownsParts(level, pos, Direction.NORTH, PARTS, MachineRegistry.FOUNDRY_RING.get());
    }

    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) return useEmptyHand(level, pos, player, hit);
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!formed(level, pos)) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.foundry_ring.incomplete"), true);
            return InteractionResult.CONSUME;
        }
        if (held.getCount() < 8) return InteractionResult.PASS;
        var view = new SimpleContainer(held);
        FoundrySmeltingRecipe recipe = level.getRecipeManager().getAllRecipesFor(MachineRegistry.FOUNDRY_SMELTING.get()).stream()
            .filter(r -> r.matches(view, level) && held.getCount() >= 8 * r.count())
            .min(Comparator.comparing(r -> r.id().toString())).orElse(null);
        if (recipe == null) return InteractionResult.PASS;
        if (recipe.output().isEmpty()) return InteractionResult.PASS;
        if (!(level.getBlockEntity(pos) instanceof HandFedControllerBlockEntity controller)) return InteractionResult.PASS;
        // One hand load is the ring's full eight-station cycle; the ticker smelts it like hopper-fed stock.
        ItemStack batch = held.copyWithCount(8 * recipe.count());
        if (!controller.insertStock(batch, true).isEmpty()) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.stock_fed.busy"), true);
            return InteractionResult.CONSUME;
        }
        InteractionResult loaded = loadHeldStock(level, pos, player, batch);
        if (loaded != InteractionResult.CONSUME) return loaded;
        if (!player.getAbilities().instabuild) held.shrink(8 * recipe.count());
        com.starmaylight.arcana_industry.sound.ModSounds.playMultiblock(level, pos, "foundry_ring");
        return InteractionResult.CONSUME;
    }
}
