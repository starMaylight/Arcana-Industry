package com.starmaylight.arcana_industry.machine;

import com.starmaylight.arcana_industry.recipe.ChannelWashingRecipe;
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
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import java.util.Comparator;
import java.util.List;

/** Two water channels rinse two sluicing batches through a braced stone bed. */
public class TwinChannelWasherBlock extends StockFedControllerBlock {
    private static final List<StructureMatcher.Part> FRAME = List.of(
        new StructureMatcher.Part(0, -1, 0, (level, p) -> level.getBlockState(p).is(Blocks.STONE_BRICKS)),
        new StructureMatcher.Part(0, 1, 1, TwinChannelWasherBlock::isWaterSource),
        new StructureMatcher.Part(0, 1, -1, TwinChannelWasherBlock::isWaterSource),
        new StructureMatcher.Part(1, 0, 0, (level, p) -> level.getBlockState(p).is(Blocks.COPPER_BLOCK)),
        new StructureMatcher.Part(-1, 0, 0, (level, p) -> level.getBlockState(p).is(Blocks.COPPER_BLOCK)),
        new StructureMatcher.Part(1, 0, -1, (level, p) -> level.getBlockState(p).is(Blocks.IRON_BARS)),
        new StructureMatcher.Part(1, 0, 1, (level, p) -> level.getBlockState(p).is(Blocks.IRON_BARS)),
        new StructureMatcher.Part(-1, 0, -1, (level, p) -> level.getBlockState(p).is(Blocks.IRON_BARS)),
        new StructureMatcher.Part(-1, 0, 1, (level, p) -> level.getBlockState(p).is(Blocks.IRON_BARS)),
        new StructureMatcher.Part(2, -1, 0, (level, p) -> level.getBlockState(p).is(Blocks.STONE_BRICKS)),
        new StructureMatcher.Part(-2, -1, 0, (level, p) -> level.getBlockState(p).is(Blocks.STONE_BRICKS)),
        new StructureMatcher.Part(2, 0, 0, (level, p) -> level.getBlockState(p).is(Blocks.HOPPER)),
        new StructureMatcher.Part(-2, 0, 0, (level, p) -> level.getBlockState(p).is(Blocks.HOPPER))
    );
    public TwinChannelWasherBlock() { super(Properties.of().mapColor(MapColor.METAL).strength(3.5F).requiresCorrectToolForDrops()); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HandFedControllerBlockEntity(MachineRegistry.TWIN_CHANNEL_WASHER_ENTITY.get(), pos, state);
    }
    private static boolean isWaterSource(Level level, BlockPos pos) {
        var fluid = level.getFluidState(pos);
        return fluid.getType() == Fluids.WATER && fluid.isSource();
    }
    @Override protected net.minecraft.world.item.crafting.RecipeType<?> stockRecipeType() { return MachineRegistry.CHANNEL_WASHING.get(); }
    @Override protected boolean stockFormed(Level level, BlockPos pos) { return formed(level, pos); }
    public static boolean formed(Level level, BlockPos pos) {
        return StructureMatcher.ownsParts(level, pos, Direction.NORTH, FRAME, MachineRegistry.TWIN_CHANNEL_WASHER.get());
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) return useEmptyHand(level, pos, player, hit);
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!formed(level, pos)) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.twin_channel_washer.incomplete"), true);
            return InteractionResult.CONSUME;
        }
        var view = new SimpleContainer(held);
        ChannelWashingRecipe recipe = level.getRecipeManager().getAllRecipesFor(MachineRegistry.CHANNEL_WASHING.get()).stream()
            .filter(r -> r.matches(view, level)).min(Comparator.comparing(r -> r.getId().toString())).orElse(null);
        if (recipe == null) return InteractionResult.PASS;
        if (!(level.getBlockEntity(pos) instanceof HandFedControllerBlockEntity controller)) return InteractionResult.PASS;
        ItemStack batch = held.copyWithCount(recipe.count());
        if (!controller.insertStock(batch, true).isEmpty()) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.stock_fed.busy"), true);
            return InteractionResult.CONSUME;
        }
        InteractionResult loaded = loadHeldStock(level, pos, player, batch);
        if (loaded != InteractionResult.CONSUME) return loaded;
        if (!player.getAbilities().instabuild) held.shrink(recipe.count());
        com.starmaylight.arcana_industry.sound.ModSounds.playMultiblock(level, pos, "twin_channel_washer");
        return InteractionResult.CONSUME;
    }
}
