package com.starmaylight.arcana_industry.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.starmaylight.arcana_industry.recipe.RackSmokingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import java.util.Comparator;
import java.util.List;

/** A lit campfire dries four items beneath a rack braced with fences and iron bars. */
public class SmokeRackBlock extends StockFedControllerBlock {
    private static final List<StructureMatcher.Part> PARTS = List.of(
        new StructureMatcher.Part(0, -1, 0, (level, at) -> {
            BlockState fire = level.getBlockState(at);
            return fire.is(Blocks.CAMPFIRE) && fire.getValue(CampfireBlock.LIT);
        }),
        new StructureMatcher.Part(1, 0, 0, (level, at) -> level.getBlockState(at).is(Blocks.OAK_FENCE)),
        new StructureMatcher.Part(-1, 0, 0, (level, at) -> level.getBlockState(at).is(Blocks.OAK_FENCE)),
        new StructureMatcher.Part(0, 0, 1, (level, at) -> level.getBlockState(at).is(Blocks.OAK_FENCE)),
        new StructureMatcher.Part(0, 1, -1, (level, at) -> level.getBlockState(at).is(Blocks.OAK_FENCE)),
        new StructureMatcher.Part(1, 0, 1, (level, at) -> level.getBlockState(at).is(Blocks.IRON_BARS)),
        new StructureMatcher.Part(1, 0, -1, (level, at) -> level.getBlockState(at).is(Blocks.IRON_BARS)),
        new StructureMatcher.Part(-1, 0, 1, (level, at) -> level.getBlockState(at).is(Blocks.IRON_BARS)),
        new StructureMatcher.Part(-1, 0, -1, (level, at) -> level.getBlockState(at).is(Blocks.IRON_BARS)));
    public SmokeRackBlock() { super(Properties.of().mapColor(MapColor.WOOD).strength(3.0F).requiresCorrectToolForDrops()); }

    @Override protected net.minecraft.world.item.crafting.RecipeType<?> stockRecipeType() { return MachineRegistry.RACK_SMOKING.get(); }
    @Override protected int stockBatches() { return 4; }
    @Override protected boolean stockFormed(Level level, BlockPos pos) { return formed(level, pos); }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HandFedControllerBlockEntity(MachineRegistry.SMOKE_RACK_ENTITY.get(), pos, state);
    }

    public static boolean formed(Level level, BlockPos pos) {
        return StructureMatcher.ownsParts(level, pos, Direction.NORTH, PARTS, MachineRegistry.SMOKE_RACK.get());
    }

    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) return useEmptyHand(level, pos, player, hit);
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!formed(level, pos)) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.smoke_rack.incomplete"), true);
            return InteractionResult.CONSUME;
        }
        if (held.getCount() < 4) return InteractionResult.PASS;
        var view = new SimpleContainer(held);
        RackSmokingRecipe recipe = level.getRecipeManager().getAllRecipesFor(MachineRegistry.RACK_SMOKING.get()).stream()
            .filter(r -> r.matches(view, level) && held.getCount() >= 4 * r.count())
            .min(Comparator.comparing(r -> r.id().toString())).orElse(null);
        if (recipe == null) return InteractionResult.PASS;
        if (!(level.getBlockEntity(pos) instanceof HandFedControllerBlockEntity controller)) return InteractionResult.PASS;
        ItemStack batch = held.copyWithCount(4 * recipe.count());
        if (!controller.insertStock(batch, true).isEmpty()) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.stock_fed.busy"), true);
            return InteractionResult.CONSUME;
        }
        InteractionResult loaded = loadHeldStock(level, pos, player, batch);
        if (loaded != InteractionResult.CONSUME) return loaded;
        if (!player.getAbilities().instabuild) held.shrink(4 * recipe.count());
        return InteractionResult.CONSUME;
    }
}
