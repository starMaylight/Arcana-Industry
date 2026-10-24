package com.starmaylight.arcana_industry.machine;

import com.starmaylight.arcana_industry.recipe.VerdantDistillationRecipe;
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

/** A heated copper still with glass crown, north condenser and offset water arms extracts concentrated botanical pigments. */
public class VerdantDistillerBlock extends StockFedControllerBlock {
    private static final List<StructureMatcher.Part> FRAME = List.of(
        new StructureMatcher.Part(0, -1, 0, (level, p) -> level.getBlockState(p).is(Blocks.CAMPFIRE)),
        new StructureMatcher.Part(0, 1, 0, (level, p) -> level.getBlockState(p).is(Blocks.GLASS)),
        new StructureMatcher.Part(0, 0, 2, (level, p) -> level.getBlockState(p).is(Blocks.COPPER_BLOCK)),
        new StructureMatcher.Part(0, 0, -2, (level, p) -> level.getBlockState(p).is(Blocks.COPPER_BLOCK)),
        new StructureMatcher.Part(1, 0, 0, (level, p) -> level.getBlockState(p).is(Blocks.WATER)),
        new StructureMatcher.Part(-1, 0, 0, (level, p) -> level.getBlockState(p).is(Blocks.WATER)),
        new StructureMatcher.Part(0, 1, 2, (level, p) -> level.getBlockState(p).is(Blocks.WAXED_CUT_COPPER))
    );
    public VerdantDistillerBlock() { super(Properties.of().mapColor(MapColor.METAL).strength(5.0F).requiresCorrectToolForDrops()); }
    @Override protected net.minecraft.world.item.crafting.RecipeType<?> stockRecipeType() { return MachineRegistry.VERDANT_DISTILLATION.get(); }
    @Override protected boolean stockFormed(Level level, BlockPos pos) { return formed(level, pos); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HandFedControllerBlockEntity(MachineRegistry.VERDANT_DISTILLER_ENTITY.get(), pos, state);
    }
    public static boolean formed(Level level, BlockPos pos) {
        return StructureMatcher.ownsParts(level, pos, Direction.NORTH, FRAME, MachineRegistry.VERDANT_DISTILLER.get());
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) return useEmptyHand(level, pos, player, hit);
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!formed(level, pos)) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.verdant_distiller.incomplete"), true);
            return InteractionResult.CONSUME;
        }
        var view = new SimpleContainer(held);
        VerdantDistillationRecipe recipe = level.getRecipeManager().getAllRecipesFor(MachineRegistry.VERDANT_DISTILLATION.get()).stream()
            .filter(r -> r.matches(view, level))
            .min(Comparator.comparing(r -> r.id().toString())).orElse(null);
        if (recipe == null || recipe.output().isEmpty()) return InteractionResult.PASS;
        if (!(level.getBlockEntity(pos) instanceof HandFedControllerBlockEntity controller)) return InteractionResult.PASS;
        ItemStack batch = held.copyWithCount(recipe.count());
        if (!controller.inputStack().isEmpty() || !controller.insertStock(batch, true).isEmpty()) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.stock_fed.busy"), true);
            return InteractionResult.CONSUME;
        }
        InteractionResult loaded = loadHeldStock(level, pos, player, batch);
        if (loaded != InteractionResult.CONSUME) return loaded;
        if (!player.getAbilities().instabuild) held.shrink(recipe.count());
        com.starmaylight.arcana_industry.sound.ModSounds.playMultiblock(level, pos, "verdant_distiller");
        return InteractionResult.CONSUME;
    }
}
