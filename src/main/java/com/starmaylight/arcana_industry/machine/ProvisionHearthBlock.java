package com.starmaylight.arcana_industry.machine;

import com.starmaylight.arcana_industry.recipe.HearthSmokingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Comparator;
import java.util.List;

/** Four diagonal smoker chimneys concentrate a lit campfire beneath the controller; stock-fed in batches of four. */
public class ProvisionHearthBlock extends StockFedControllerBlock {
    private static final List<StructureMatcher.Part> PARTS = List.of(
        new StructureMatcher.Part(0, -1, 0, (level, at) -> {
            BlockState fire = level.getBlockState(at);
            return fire.is(Blocks.CAMPFIRE) && fire.getValue(CampfireBlock.LIT);
        }),
        new StructureMatcher.Part(-1, 0, 1, (level, at) -> level.getBlockState(at).is(Blocks.SMOKER)),
        new StructureMatcher.Part(-1, 0, -1, (level, at) -> level.getBlockState(at).is(Blocks.SMOKER)),
        new StructureMatcher.Part(1, 0, 1, (level, at) -> level.getBlockState(at).is(Blocks.SMOKER)),
        new StructureMatcher.Part(1, 0, -1, (level, at) -> level.getBlockState(at).is(Blocks.SMOKER)));
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HandFedControllerBlockEntity(MachineRegistry.PROVISION_HEARTH_ENTITY.get(), pos, state);
    }
    public ProvisionHearthBlock() { super(Properties.of().mapColor(MapColor.COLOR_BROWN).strength(3.5F).requiresCorrectToolForDrops()); }

    @Override protected net.minecraft.world.item.crafting.RecipeType<?> stockRecipeType() { return MachineRegistry.HEARTH_SMOKING.get(); }
    /** Four batches per cycle, one per smoker chimney, exactly like a manual quadrupled batch. */
    @Override protected int stockBatches() { return 4; }
    @Override protected boolean stockFormed(Level level, BlockPos pos) { return formed(level, pos); }
    public static boolean formed(Level level, BlockPos pos) {
        return StructureMatcher.ownsParts(level, pos, Direction.NORTH, PARTS, MachineRegistry.PROVISION_HEARTH.get());
    }

    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) return useEmptyHand(level, pos, player, hit);
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!formed(level, pos)) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.provision_hearth.incomplete"), true);
            return InteractionResult.CONSUME;
        }
        var view = new SimpleContainer(held);
        HearthSmokingRecipe recipe = level.getRecipeManager().getAllRecipesFor(MachineRegistry.HEARTH_SMOKING.get()).stream()
            .filter(r -> r.matches(view, level) && held.getCount() >= 4 * r.count())
            .sorted(Comparator.comparing(r -> r.id().toString()))
            .findFirst().orElse(null);
        if (recipe == null || recipe.output().isEmpty()) return InteractionResult.PASS;
        if (!(level.getBlockEntity(pos) instanceof HandFedControllerBlockEntity controller)) return InteractionResult.PASS;
        ItemStack batch = held.copyWithCount(4 * recipe.count());
        // A finished heat must be collected first so hand loads never stack food behind an uncollected batch.
        boolean outputWaiting = !controller.outputStack().isEmpty() || !controller.byproductStack().isEmpty();
        if (outputWaiting || !controller.insertStock(batch, true).isEmpty()) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.stock_fed.busy"), true);
            return InteractionResult.CONSUME;
        }
        InteractionResult loaded = loadHeldStock(level, pos, player, batch);
        if (loaded != InteractionResult.CONSUME) return loaded;
        if (!player.getAbilities().instabuild) held.shrink(4 * recipe.count());
        com.starmaylight.arcana_industry.sound.ModSounds.playMultiblock(level, pos, "provision_hearth");
        return InteractionResult.CONSUME;
    }
}
