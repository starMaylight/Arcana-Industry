package com.starmaylight.arcana_industry.machine;

import com.starmaylight.arcana_industry.recipe.SanctumAttunementRecipe;
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
 * A wide sea-lantern ring at the controller's own height, four amethyst pillars one block above,
 * a beacon capstone two above and a lodestone anchor below: a large-footprint (5x5x4) multiblock
 * spanning four vertical tiers, distinct from every compact single-layer controller in the mod.
 */
public class SanctumPylonBlock extends StockFedControllerBlock {
    private static final List<StructureMatcher.Part> PARTS = List.of(
        new StructureMatcher.Part(0, -1, 0, (level, p) -> level.getBlockState(p).is(Blocks.LODESTONE)),
        new StructureMatcher.Part(2, 0, 0, (level, p) -> level.getBlockState(p).is(Blocks.SEA_LANTERN)),
        new StructureMatcher.Part(-2, 0, 0, (level, p) -> level.getBlockState(p).is(Blocks.SEA_LANTERN)),
        new StructureMatcher.Part(0, 0, 2, (level, p) -> level.getBlockState(p).is(Blocks.SEA_LANTERN)),
        new StructureMatcher.Part(0, 0, -2, (level, p) -> level.getBlockState(p).is(Blocks.SEA_LANTERN)),
        new StructureMatcher.Part(2, 0, 2, (level, p) -> level.getBlockState(p).is(Blocks.SEA_LANTERN)),
        new StructureMatcher.Part(2, 0, -2, (level, p) -> level.getBlockState(p).is(Blocks.SEA_LANTERN)),
        new StructureMatcher.Part(-2, 0, 2, (level, p) -> level.getBlockState(p).is(Blocks.SEA_LANTERN)),
        new StructureMatcher.Part(-2, 0, -2, (level, p) -> level.getBlockState(p).is(Blocks.SEA_LANTERN)),
        new StructureMatcher.Part(1, 1, 0, (level, p) -> level.getBlockState(p).is(Blocks.AMETHYST_BLOCK)),
        new StructureMatcher.Part(-1, 1, 0, (level, p) -> level.getBlockState(p).is(Blocks.AMETHYST_BLOCK)),
        new StructureMatcher.Part(0, 1, 1, (level, p) -> level.getBlockState(p).is(Blocks.AMETHYST_BLOCK)),
        new StructureMatcher.Part(0, 1, -1, (level, p) -> level.getBlockState(p).is(Blocks.AMETHYST_BLOCK)),
        new StructureMatcher.Part(0, 2, 0, (level, p) -> level.getBlockState(p).is(Blocks.BEACON)));

    public SanctumPylonBlock() { super(Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).strength(4.0F).requiresCorrectToolForDrops()); }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HandFedControllerBlockEntity(MachineRegistry.SANCTUM_PYLON_ENTITY.get(), pos, state);
    }

    @Override protected net.minecraft.world.item.crafting.RecipeType<?> stockRecipeType() { return MachineRegistry.SANCTUM_ATTUNEMENT.get(); }
    @Override protected boolean stockFormed(Level level, BlockPos pos) { return formed(level, pos); }
    /** Buffered gems are imprinted like hand-fed ones; traitless stock waits instead of producing a blank focus. */
    @Override public ItemStack stockResult(com.starmaylight.arcana_industry.recipe.SingleStockRecipe recipe, ItemStack stock) {
        return attune((SanctumAttunementRecipe) recipe, stock);
    }

    public static boolean formed(Level level, BlockPos pos) {
        return StructureMatcher.ownsParts(level, pos, Direction.NORTH, PARTS, MachineRegistry.SANCTUM_PYLON.get());
    }

    /** The recipe output imprinted with the batch's dominant trait; empty when the input carries no trait. */
    public static ItemStack attune(SanctumAttunementRecipe recipe, ItemStack input) {
        return recipe.attune(input, com.starmaylight.arcana_industry.magic.trait.MaterialTraits.INSTANCE.snapshot());
    }

    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) return useEmptyHand(level, pos, player, hit);
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!formed(level, pos)) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.sanctum_pylon.incomplete"), true);
            return InteractionResult.CONSUME;
        }
        var view = new SimpleContainer(held);
        SanctumAttunementRecipe recipe = level.getRecipeManager().getAllRecipesFor(MachineRegistry.SANCTUM_ATTUNEMENT.get()).stream()
            .filter(r -> r.matches(view, level))
            .min(Comparator.comparing(r -> r.id().toString())).orElse(null);
        if (recipe == null) return InteractionResult.PASS;
        // Refuse traitless gems up front: buffered stock without a trait would otherwise wait in the frame forever.
        if (attune(recipe, held).isEmpty()) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.sanctum_pylon.no_trait"), true);
            return InteractionResult.CONSUME;
        }
        if (!(level.getBlockEntity(pos) instanceof HandFedControllerBlockEntity controller)) return InteractionResult.PASS;
        ItemStack batch = held.copyWithCount(recipe.count());
        if (!controller.inputStack().isEmpty() || !controller.insertStock(batch, true).isEmpty()) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.stock_fed.busy"), true);
            return InteractionResult.CONSUME;
        }
        InteractionResult loaded = loadHeldStock(level, pos, player, batch);
        if (loaded != InteractionResult.CONSUME) return loaded;
        if (!player.getAbilities().instabuild) held.shrink(recipe.count());
        com.starmaylight.arcana_industry.sound.ModSounds.playMultiblock(level, pos, "sanctum_pylon");
        return InteractionResult.CONSUME;
    }
}
