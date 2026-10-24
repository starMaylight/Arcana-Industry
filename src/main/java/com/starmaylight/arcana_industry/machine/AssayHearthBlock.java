package com.starmaylight.arcana_industry.machine;

import com.starmaylight.arcana_industry.recipe.CupellationRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import java.util.Comparator;
import java.util.List;

/** Cupels a held raw-metal batch on bone-ash over a blast furnace; the assayer's skill is paid in experience levels. */
public class AssayHearthBlock extends StockFedControllerBlock {
    private static final List<StructureMatcher.Part> FRAME = List.of(
        new StructureMatcher.Part(0, -1, 0, (level, p) -> level.getBlockState(p).is(Blocks.BLAST_FURNACE)),
        new StructureMatcher.Part(1, 0, 1, (level, p) -> level.getBlockState(p).is(Blocks.BONE_BLOCK)),
        new StructureMatcher.Part(1, 0, -1, (level, p) -> level.getBlockState(p).is(Blocks.BONE_BLOCK)),
        new StructureMatcher.Part(-1, 0, 1, (level, p) -> level.getBlockState(p).is(Blocks.BONE_BLOCK)),
        new StructureMatcher.Part(-1, 0, -1, (level, p) -> level.getBlockState(p).is(Blocks.BONE_BLOCK)),
        new StructureMatcher.Part(1, 1, 1, (level, p) -> level.getBlockState(p).is(Blocks.IRON_BARS)),
        new StructureMatcher.Part(1, 1, -1, (level, p) -> level.getBlockState(p).is(Blocks.IRON_BARS)),
        new StructureMatcher.Part(-1, 1, 1, (level, p) -> level.getBlockState(p).is(Blocks.IRON_BARS)),
        new StructureMatcher.Part(-1, 1, -1, (level, p) -> level.getBlockState(p).is(Blocks.IRON_BARS))
    );
    public AssayHearthBlock() { super(Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(3.5F).requiresCorrectToolForDrops()); }
    @Override protected net.minecraft.world.item.crafting.RecipeType<?> stockRecipeType() { return MachineRegistry.CUPELLATION.get(); }
    @Override protected boolean stockReagent() { return true; }
    @Override protected boolean stockFormed(Level level, BlockPos pos) { return formed(level, pos); }
    @Override public ItemStack stockByproduct(com.starmaylight.arcana_industry.recipe.SingleStockRecipe recipe) {
        return recipe instanceof CupellationRecipe cupellation ? cupellation.byproduct() : ItemStack.EMPTY;
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HandFedControllerBlockEntity(MachineRegistry.ASSAY_HEARTH_ENTITY.get(), pos, state);
    }
    /** Hearths sharing a bone block resolve it to the lower-position controller, like every other hand-fed frame. */
    public static boolean formed(Level level, BlockPos pos) {
        return StructureMatcher.ownsParts(level, pos, Direction.NORTH, FRAME, MachineRegistry.ASSAY_HEARTH.get());
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        ItemStack held = player.getMainHandItem();
        if (held.isEmpty()) return useEmptyHand(level, pos, player, hit);
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!formed(level, pos)) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.assay_hearth.incomplete"), true);
            return InteractionResult.CONSUME;
        }
        var view = new SimpleContainer(held);
        CupellationRecipe recipe = level.getRecipeManager().getAllRecipesFor(MachineRegistry.CUPELLATION.get()).stream()
            .filter(r -> r.matches(view, level))
            .min(Comparator.comparing(r -> r.id().toString())).orElse(null);
        if (recipe == null) return InteractionResult.PASS;
        boolean creative = player.getAbilities().instabuild;
        if (!creative && player.experienceLevel < recipe.levels()) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.assay_hearth.levels", recipe.levels()), true);
            return InteractionResult.CONSUME;
        }
        if (!(level.getBlockEntity(pos) instanceof HandFedControllerBlockEntity controller)) return InteractionResult.PASS;
        // The levels paid are condensed into the same bottle charge automation supplies, then the batch runs on the ticker.
        ItemStack batch = held.copyWithCount(recipe.count());
        ItemStack charge = new ItemStack(Items.EXPERIENCE_BOTTLE, recipe.levels());
        if (!controller.insertStock(batch, true).isEmpty() || !controller.insertReagent(charge, true).isEmpty()) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.stock_fed.busy"), true);
            return InteractionResult.CONSUME;
        }
        controller.insertStock(batch, false);
        controller.insertReagent(charge, false);
        if (!creative) {
            held.shrink(recipe.count());
            player.giveExperienceLevels(-recipe.levels());
        }
        player.displayClientMessage(Component.translatable("message.arcana_industry.stock_fed.loaded",
            controller.inputStack().getCount(), controller.energyStored(), controller.energyCapacity()), true);
        return InteractionResult.CONSUME;
    }
}
