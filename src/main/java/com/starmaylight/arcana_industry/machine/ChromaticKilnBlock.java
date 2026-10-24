package com.starmaylight.arcana_industry.machine;

import com.starmaylight.arcana_industry.recipe.ChromaticGlazingRecipe;
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

/** A furnace-fired terracotta kiln with a glass crown, two-brick flue and offset lava arms glazes dyes into color blocks. */
public class ChromaticKilnBlock extends StockFedControllerBlock {
    private static final List<StructureMatcher.Part> FRAME = hatchable(List.of(
        new StructureMatcher.Part(0, -1, 0, (level, p) -> level.getBlockState(p).is(Blocks.FURNACE)),
        new StructureMatcher.Part(0, 1, 0, (level, p) -> level.getBlockState(p).is(Blocks.GLASS)),
        new StructureMatcher.Part(0, 0, 1, (level, p) -> level.getBlockState(p).is(Blocks.TERRACOTTA)),
        new StructureMatcher.Part(0, 0, -1, (level, p) -> level.getBlockState(p).is(Blocks.TERRACOTTA)),
        new StructureMatcher.Part(1, 0, 0, (level, p) -> level.getBlockState(p).is(Blocks.LAVA)),
        new StructureMatcher.Part(-1, 0, 0, (level, p) -> level.getBlockState(p).is(Blocks.LAVA)),
        new StructureMatcher.Part(0, 2, 0, (level, p) -> level.getBlockState(p).is(Blocks.BRICKS)),
        new StructureMatcher.Part(0, 3, 0, (level, p) -> level.getBlockState(p).is(Blocks.BRICKS))
    ));
    public ChromaticKilnBlock() { super(Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(5.0F).requiresCorrectToolForDrops()); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HandFedControllerBlockEntity(MachineRegistry.CHROMATIC_KILN_ENTITY.get(), pos, state);
    }
    @Override protected net.minecraft.world.item.crafting.RecipeType<?> stockRecipeType() { return MachineRegistry.CHROMATIC_GLAZING.get(); }
    @Override protected boolean stockFormed(Level level, BlockPos pos) { return formed(level, pos); }
    public static boolean formed(Level level, BlockPos pos) {
        return StructureMatcher.ownsParts(level, pos, Direction.NORTH, FRAME, MachineRegistry.CHROMATIC_KILN.get())
            && hatchesWithinLimit(level, pos);
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) return useEmptyHand(level, pos, player, hit);
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!formed(level, pos)) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.chromatic_kiln.incomplete"), true);
            return InteractionResult.CONSUME;
        }
        var view = new SimpleContainer(held);
        ChromaticGlazingRecipe recipe = level.getRecipeManager().getAllRecipesFor(MachineRegistry.CHROMATIC_GLAZING.get()).stream()
            .filter(r -> r.matches(view, level))
            .min(Comparator.comparing(r -> r.id().toString())).orElse(null);
        if (recipe == null) return InteractionResult.PASS;
        ItemStack output = recipe.output();
        if (output.isEmpty()) return InteractionResult.PASS;
        if (!(level.getBlockEntity(pos) instanceof HandFedControllerBlockEntity controller)) return InteractionResult.PASS;
        ItemStack batch = held.copyWithCount(recipe.count());
        if (!controller.insertStock(batch, true).isEmpty()) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.stock_fed.busy"), true);
            return InteractionResult.CONSUME;
        }
        InteractionResult loaded = loadHeldStock(level, pos, player, batch);
        if (loaded != InteractionResult.CONSUME) return loaded;
        if (!player.getAbilities().instabuild) held.shrink(recipe.count());
        com.starmaylight.arcana_industry.sound.ModSounds.playMultiblock(level, pos, "chromatic_kiln");
        return InteractionResult.CONSUME;
    }
}
