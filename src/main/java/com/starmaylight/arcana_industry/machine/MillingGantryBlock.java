package com.starmaylight.arcana_industry.machine;

import com.starmaylight.arcana_industry.recipe.MillingRecipe;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import java.util.Comparator;

/** A piston cross presses feedstock against an overhead anvil for loaded crushing recipes. */
public class MillingGantryBlock extends StockFedControllerBlock {
    public MillingGantryBlock() { super(Properties.of().mapColor(MapColor.METAL).strength(4.0F).requiresCorrectToolForDrops()); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HandFedControllerBlockEntity(MachineRegistry.MILLING_GANTRY_ENTITY.get(), pos, state);
    }
    @Override protected net.minecraft.world.item.crafting.RecipeType<?> stockRecipeType() { return MachineRegistry.MILLING.get(); }
    @Override protected boolean stockFormed(Level level, BlockPos pos) { return formed(level, pos); }
    public static boolean formed(Level level, BlockPos pos) {
        if (!level.hasChunkAt(pos) || !level.getBlockState(pos).is(MachineRegistry.MILLING_GANTRY.get())) return false;
        if (!level.hasChunkAt(pos.below()) || !level.hasChunkAt(pos.above())) return false;
        if (!(level.getBlockState(pos.below()).is(Blocks.REDSTONE_BLOCK) || isHatch(level, pos.below()))
            || !(level.getBlockState(pos.above()).is(Blocks.ANVIL) || isHatch(level, pos.above()))) return false;
        for (Direction side : Direction.Plane.HORIZONTAL) {
            if (!level.hasChunkAt(pos.relative(side))) return false;
            if (isHatch(level, pos.relative(side))) continue;
            BlockState piston = level.getBlockState(pos.relative(side));
            if (!piston.is(Blocks.PISTON) || piston.getValue(BlockStateProperties.FACING) != side.getOpposite()) return false;
        }
        return hatchesWithinLimit(level, pos);
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) return useEmptyHand(level, pos, player, hit);
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!formed(level, pos)) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.milling_gantry.incomplete"), true);
            return InteractionResult.CONSUME;
        }
        var view = new SimpleContainer(held);
        MillingRecipe recipe = level.getRecipeManager().getAllRecipesFor(MachineRegistry.MILLING.get()).stream()
            .filter(r -> r.matches(view, level))
            .min(Comparator.comparing(r -> r.id().toString())).orElse(null);
        if (recipe == null || recipe.output().isEmpty()) return InteractionResult.PASS;
        if (!(level.getBlockEntity(pos) instanceof HandFedControllerBlockEntity controller)) return InteractionResult.PASS;
        ItemStack batch = held.copyWithCount(recipe.count());
        if (!controller.insertStock(batch, true).isEmpty()) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.stock_fed.busy"), true);
            return InteractionResult.CONSUME;
        }
        InteractionResult loaded = loadHeldStock(level, pos, player, batch);
        if (loaded != InteractionResult.CONSUME) return loaded;
        if (!player.getAbilities().instabuild) held.shrink(recipe.count());
        com.starmaylight.arcana_industry.sound.ModSounds.playMultiblock(level, pos, "milling_gantry");
        return InteractionResult.CONSUME;
    }
}
