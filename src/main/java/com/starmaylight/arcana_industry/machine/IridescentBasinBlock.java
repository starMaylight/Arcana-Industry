package com.starmaylight.arcana_industry.machine;

import com.starmaylight.arcana_industry.magic.trait.MaterialTraits;
import com.starmaylight.arcana_industry.magic.trait.TraitAssay;
import com.starmaylight.arcana_industry.recipe.IridescentRecipe;
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
import net.minecraft.resources.ResourceLocation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/** Paired dye batches around a sea-lit basin. */
public class IridescentBasinBlock extends StockFedControllerBlock {
    private static final List<StructureMatcher.Part> FRAME = List.of(
        new StructureMatcher.Part(0, -1, 0, (level, p) -> level.getBlockState(p).is(Blocks.SEA_LANTERN)),
        new StructureMatcher.Part(0, 1, 0, (level, p) -> level.getBlockState(p).is(Blocks.END_ROD)),
        new StructureMatcher.Part(0, 0, 2, (level, p) -> level.getBlockState(p).is(Blocks.PRISMARINE_BRICKS)),
        new StructureMatcher.Part(0, 0, -2, (level, p) -> level.getBlockState(p).is(Blocks.PRISMARINE_BRICKS)),
        new StructureMatcher.Part(1, 0, 0, (level, p) -> level.getBlockState(p).is(Blocks.OBSIDIAN)),
        new StructureMatcher.Part(-1, 0, 0, (level, p) -> level.getBlockState(p).is(Blocks.OBSIDIAN))
    );
    public IridescentBasinBlock() { super(Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(3.5F)); }
    @Override protected net.minecraft.world.item.crafting.RecipeType<?> stockRecipeType() { return MachineRegistry.IRIDESCENT.get(); }
    @Override protected boolean stockReagent() { return true; }
    @Override protected boolean stockFormed(Level level, BlockPos pos) { return formed(level, pos); }
    @Override public ItemStack stockResult(com.starmaylight.arcana_industry.recipe.SingleStockRecipe recipe,
                                           ItemStack stock, ItemStack reagent) {
        return recipe instanceof IridescentRecipe iridescent && unexplained(iridescent, stock, reagent).isEmpty()
            ? iridescent.output() : ItemStack.EMPTY;
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HandFedControllerBlockEntity(MachineRegistry.IRIDESCENT_BASIN_ENTITY.get(), pos, state);
    }
    public static boolean formed(Level level, BlockPos pos) {
        return StructureMatcher.ownsParts(level, pos, Direction.NORTH, FRAME, MachineRegistry.IRIDESCENT_BASIN.get());
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        ItemStack base = player.getMainHandItem();
        ItemStack reagent = player.getOffhandItem();
        if (base.isEmpty()) return useEmptyHand(level, pos, player, hit);
        if (reagent.isEmpty()) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!formed(level, pos)) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.iridescent_basin.incomplete"), true);
            return InteractionResult.CONSUME;
        }
        var view = new SimpleContainer(base, reagent);
        IridescentRecipe recipe = level.getRecipeManager().getAllRecipesFor(MachineRegistry.IRIDESCENT.get()).stream()
            .filter(r -> r.matches(view, level)).min(Comparator.comparing(r -> r.id().toString())).orElse(null);
        if (recipe == null) return InteractionResult.PASS;
        ItemStack output = recipe.output();
        if (output.isEmpty()) return InteractionResult.PASS;
        var unexplained = unexplained(recipe, base, reagent);
        if (!unexplained.isEmpty()) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.iridescent_basin.unexplained",
                TraitAssay.name(unexplained.iterator().next())), true);
            return InteractionResult.CONSUME;
        }
        if (!(level.getBlockEntity(pos) instanceof HandFedControllerBlockEntity controller)) return InteractionResult.PASS;
        // One hand load is one bath: base and dye enter the buffers and the ticker tints them like hopper feed.
        ItemStack baseBatch = base.copyWithCount(recipe.baseCount());
        ItemStack reagentBatch = reagent.copyWithCount(recipe.reagentCount());
        if (!controller.inputStack().isEmpty() || !controller.reagentStack().isEmpty()
            || !controller.insertStock(baseBatch, true).isEmpty() || !controller.insertReagent(reagentBatch, true).isEmpty()) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.stock_fed.busy"), true);
            return InteractionResult.CONSUME;
        }
        InteractionResult loaded = loadHeldStock(level, pos, player, baseBatch);
        if (loaded != InteractionResult.CONSUME) return loaded;
        controller.insertReagent(reagentBatch, false);
        if (!player.getAbilities().instabuild) {
            base.shrink(recipe.baseCount());
            reagent.shrink(recipe.reagentCount());
        }
        com.starmaylight.arcana_industry.sound.ModSounds.playMultiblock(level, pos, "iridescent_basin");
        return InteractionResult.CONSUME;
    }

    /** The bath adds no property: product traits must come from base_count bases plus reagent_count reagents. */
    public static Set<ResourceLocation> unexplained(IridescentRecipe recipe, ItemStack base, ItemStack reagent) {
        List<ItemStack> batch = new ArrayList<>(Collections.nCopies(recipe.baseCount(), base));
        batch.addAll(Collections.nCopies(recipe.reagentCount(), reagent));
        return TraitAssay.unexplained(recipe.output(), batch, MaterialTraits.INSTANCE.snapshot());
    }
}
