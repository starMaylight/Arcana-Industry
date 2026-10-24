package com.starmaylight.arcana_industry.machine;

import com.starmaylight.arcana_industry.magic.AstralFontBlockEntity;
import com.starmaylight.arcana_industry.magic.trait.MaterialTraits;
import com.starmaylight.arcana_industry.magic.trait.TraitAssay;
import com.starmaylight.arcana_industry.magic.trait.TraitInstability;
import com.starmaylight.arcana_industry.recipe.ConcordanceRecipe;
import com.starmaylight.arcana_industry.recipe.SingleStockRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.ChatFormatting;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Paired hand/offhand transmutation around an enchanting-table plinth; unmatched held items are assayed for traits.
 * Bookshelves stand two blocks north and south so those faces serve as stock ports: automated rites pause rather
 * than backlash when stability is short, and pay corner-font lumen like manual ones.
 */
public class ConcordanceAltarBlock extends StockFedControllerBlock {
    private static final List<StructureMatcher.Part> FRAME = List.of(
        new StructureMatcher.Part(0, -1, 0, (level, p) -> level.getBlockState(p).is(Blocks.ENCHANTING_TABLE)),
        new StructureMatcher.Part(0, 1, 0, (level, p) -> level.getBlockState(p).is(Blocks.AMETHYST_CLUSTER)),
        new StructureMatcher.Part(0, 0, 2, (level, p) -> level.getBlockState(p).is(Blocks.BOOKSHELF)),
        new StructureMatcher.Part(0, 0, -2, (level, p) -> level.getBlockState(p).is(Blocks.BOOKSHELF)),
        new StructureMatcher.Part(1, 0, 0, (level, p) -> level.getBlockState(p).is(Blocks.COPPER_BLOCK)),
        new StructureMatcher.Part(-1, 0, 0, (level, p) -> level.getBlockState(p).is(Blocks.COPPER_BLOCK))
    );
    /** Optional stabilisers: amethyst blocks on the four diagonal corners at altar height. */
    private static final List<BlockPos> STABILISERS = List.of(
        new BlockPos(1, 0, 1), new BlockPos(1, 0, -1), new BlockPos(-1, 0, 1), new BlockPos(-1, 0, -1));
    public static final int BASE_STABILITY = 1;
    /** A corner Astral Font holding at least {@link #LUMEN_PER_RITE} lumen adds this much stability. */
    public static final int FONT_STABILITY = 2;
    /** Lumen a font pays each time a fusion leans on it. */
    public static final int LUMEN_PER_RITE = 100;
    public ConcordanceAltarBlock() { super(Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(3.5F)); }
    @Override protected net.minecraft.world.item.crafting.RecipeType<?> stockRecipeType() { return MachineRegistry.CONCORDANCE.get(); }
    @Override protected boolean stockReagent() { return true; }
    @Override protected boolean stockFormed(Level level, BlockPos pos) { return formed(level, pos); }
    /** Containers such as the water bucket's bucket stay behind in the by-product slot. */
    @Override public ItemStack stockByproduct(SingleStockRecipe recipe, ItemStack stock) {
        ItemStack remainder = stock.getCraftingRemainingItem();
        if (!remainder.isEmpty()) remainder.setCount(recipe.count());
        return remainder;
    }
    @Override protected String stockHold(Level level, BlockPos pos, SingleStockRecipe recipe, ItemStack stock, ItemStack reagent) {
        if (!(recipe instanceof ConcordanceRecipe rite)) return "no_recipe";
        var traits = MaterialTraits.INSTANCE.snapshot();
        if (!TraitAssay.unexplained(rite.output(), List.of(stock, reagent), traits).isEmpty()) return "unexplained";
        return TraitInstability.excess(requiredStability(rite, stock, reagent, traits), stability(level, pos)) > 0 ? "unstable" : null;
    }
    @Override protected void stockCompleted(Level level, BlockPos pos, SingleStockRecipe recipe, ItemStack stock, ItemStack reagent) {
        if (recipe instanceof ConcordanceRecipe rite)
            drawLumen(level, pos, requiredStability(rite, stock, reagent, MaterialTraits.INSTANCE.snapshot()));
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HandFedControllerBlockEntity(MachineRegistry.CONCORDANCE_ALTAR_ENTITY.get(), pos, state);
    }
    public static boolean formed(Level level, BlockPos pos) {
        return StructureMatcher.ownsParts(level, pos, Direction.NORTH, FRAME, MachineRegistry.CONCORDANCE_ALTAR.get());
    }
    /**
     * Opposed-trait instability the altar can hold: one, plus one per amethyst block on a diagonal corner,
     * plus {@link #FONT_STABILITY} per charged Astral Font on a diagonal corner.
     */
    public static int stability(Level level, BlockPos pos) {
        return passiveStability(level, pos) + FONT_STABILITY * chargedFonts(level, pos).size();
    }
    /** Stability that costs nothing to use: the base plus corner amethyst blocks. */
    public static int passiveStability(Level level, BlockPos pos) {
        int capacity = BASE_STABILITY;
        for (BlockPos offset : STABILISERS) if (level.getBlockState(pos.offset(offset)).is(Blocks.AMETHYST_BLOCK)) capacity++;
        return capacity;
    }
    /** Corner Astral Fonts that can pay one rite, in fixed corner order. */
    private static List<AstralFontBlockEntity> chargedFonts(Level level, BlockPos pos) {
        List<AstralFontBlockEntity> fonts = new ArrayList<>();
        for (BlockPos offset : STABILISERS)
            if (level.getBlockEntity(pos.offset(offset)) instanceof AstralFontBlockEntity font && font.lumen() >= LUMEN_PER_RITE)
                fonts.add(font);
        return fonts;
    }
    /** Pays lumen for the instability above the passive capacity: one rite's lumen per font needed. */
    private static void drawLumen(Level level, BlockPos pos, int score) {
        int needed = Math.max(0, score - passiveStability(level, pos));
        for (AstralFontBlockEntity font : chargedFonts(level, pos)) {
            if (needed <= 0) return;
            font.spend(LUMEN_PER_RITE);
            needed -= FONT_STABILITY;
        }
    }
    /** Stability a rite needs: its inputs' trait instability, raised to the recipe's declared (unreality) floor. */
    public static int requiredStability(ConcordanceRecipe recipe, ItemStack base, ItemStack reagent, MaterialTraits.Snapshot traits) {
        return Math.max(recipe.stability(), TraitInstability.score(TraitAssay.read(List.of(base, reagent), traits).pool(), traits));
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        ItemStack base = player.getMainHandItem();
        ItemStack reagent = player.getOffhandItem();
        if (base.isEmpty() && reagent.isEmpty()) return useEmptyHand(level, pos, player, hit);
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!formed(level, pos)) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.concordance_altar.incomplete"), true);
            return InteractionResult.CONSUME;
        }
        var traits = MaterialTraits.INSTANCE.snapshot();
        var view = new SimpleContainer(base, reagent);
        ConcordanceRecipe recipe = base.isEmpty() || reagent.isEmpty() ? null
            : level.getRecipeManager().getAllRecipesFor(MachineRegistry.CONCORDANCE.get()).stream()
                .filter(r -> r.matches(view, level)).min(Comparator.comparing(r -> r.id().toString())).orElse(null);
        ItemStack output = recipe == null ? ItemStack.EMPTY : recipe.output();
        if (output.isEmpty()) {
            // No fusion for this pair: the altar reads the held materials instead (nothing is consumed).
            TraitAssay.describe(List.of(base, reagent), traits).forEach(player::sendSystemMessage);
            int score = TraitInstability.score(TraitAssay.read(List.of(base, reagent), traits).pool(), traits);
            player.sendSystemMessage(Component.translatable("message.arcana_industry.concordance_altar.stability",
                score, stability(level, pos)).withStyle(score > stability(level, pos) ? ChatFormatting.RED : ChatFormatting.AQUA));
            return InteractionResult.CONSUME;
        }
        var unexplained = TraitAssay.unexplained(output, List.of(base, reagent), traits);
        if (!unexplained.isEmpty()) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.concordance_altar.unexplained",
                TraitAssay.name(unexplained.iterator().next())), true);
            return InteractionResult.CONSUME;
        }
        int score = requiredStability(recipe, base, reagent, traits);
        int capacity = stability(level, pos);
        int excess = TraitInstability.excess(score, capacity);
        if (excess > 0) {
            // Backlash: the opposed pool tears apart. The volatile reagent is spent; the base survives.
            if (!player.getAbilities().instabuild) reagent.shrink(recipe.reagentCount());
            player.hurt(level.damageSources().magic(), 2.0F * excess);
            player.displayClientMessage(Component.translatable("message.arcana_industry.concordance_altar.backlash",
                score, capacity).withStyle(ChatFormatting.RED), true);
            return InteractionResult.CONSUME;
        }
        drawLumen(level, pos, score);
        ItemStack remainder = ItemStack.EMPTY;
        if (!player.getAbilities().instabuild) {
            // Containers such as a water bucket's bucket come back, after the product.
            remainder = base.getCraftingRemainingItem();
            if (!remainder.isEmpty()) remainder.setCount(recipe.baseCount());
            base.shrink(recipe.baseCount());
            reagent.shrink(recipe.reagentCount());
        }
        if (!player.getInventory().add(output)) player.drop(output, false);
        if (!remainder.isEmpty() && !player.getInventory().add(remainder)) player.drop(remainder, false);
        com.starmaylight.arcana_industry.sound.ModSounds.playMultiblock(level, pos, "concordance_altar");
        return InteractionResult.CONSUME;
    }
}
