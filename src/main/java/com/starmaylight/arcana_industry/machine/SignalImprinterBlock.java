package com.starmaylight.arcana_industry.machine;

import com.starmaylight.arcana_industry.recipe.SignalImprintingRecipe;
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
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import java.util.Comparator;
import java.util.List;

/** A redstone base, lightning cap and lamp-braced iron/copper signal lattice imprint control components. */
public class SignalImprinterBlock extends StockFedControllerBlock {
    private static final List<StructureMatcher.Part> FRAME = hatchable(List.of(
        new StructureMatcher.Part(0, -1, 0, (level, p) -> level.getBlockState(p).is(Blocks.REDSTONE_BLOCK)),
        new StructureMatcher.Part(0, 1, 0, (level, p) -> level.getBlockState(p).is(Blocks.LIGHTNING_ROD)),
        new StructureMatcher.Part(0, 0, 1, (level, p) -> level.getBlockState(p).is(Blocks.IRON_BLOCK)),
        new StructureMatcher.Part(0, 0, -1, (level, p) -> level.getBlockState(p).is(Blocks.IRON_BLOCK)),
        new StructureMatcher.Part(1, 0, 0, (level, p) -> level.getBlockState(p).is(Blocks.COPPER_BLOCK)),
        new StructureMatcher.Part(-1, 0, 0, (level, p) -> level.getBlockState(p).is(Blocks.COPPER_BLOCK)),
        new StructureMatcher.Part(1, 0, 1, (level, p) -> level.getBlockState(p).is(Blocks.REDSTONE_LAMP)),
        new StructureMatcher.Part(1, 0, -1, (level, p) -> level.getBlockState(p).is(Blocks.REDSTONE_LAMP)),
        new StructureMatcher.Part(-1, 0, 1, (level, p) -> level.getBlockState(p).is(Blocks.REDSTONE_LAMP)),
        new StructureMatcher.Part(-1, 0, -1, (level, p) -> level.getBlockState(p).is(Blocks.REDSTONE_LAMP))
    ));
    public SignalImprinterBlock() { super(Properties.of().mapColor(MapColor.METAL).strength(5.0F).requiresCorrectToolForDrops()); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HandFedControllerBlockEntity(MachineRegistry.SIGNAL_IMPRINTER_ENTITY.get(), pos, state);
    }
    @Override protected net.minecraft.world.item.crafting.RecipeType<?> stockRecipeType() { return MachineRegistry.SIGNAL_IMPRINTING.get(); }
    @Override protected boolean stockFormed(Level level, BlockPos pos) { return formed(level, pos); }
    public static boolean formed(Level level, BlockPos pos) {
        return StructureMatcher.ownsParts(level, pos, Direction.NORTH, FRAME, MachineRegistry.SIGNAL_IMPRINTER.get())
            && hatchesWithinLimit(level, pos);
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) return useEmptyHand(level, pos, player, hit);
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!formed(level, pos)) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.signal_imprinter.incomplete"), true);
            return InteractionResult.CONSUME;
        }
        var view = new SimpleContainer(held);
        SignalImprintingRecipe recipe = level.getRecipeManager().getAllRecipesFor(MachineRegistry.SIGNAL_IMPRINTING.get()).stream()
            .filter(r -> r.matches(view, level))
            .min(Comparator.comparing(r -> r.id().toString())).orElse(null);
        if (recipe == null) return InteractionResult.PASS;
        return loadHeldStock(level, pos, player, held);
    }
}
