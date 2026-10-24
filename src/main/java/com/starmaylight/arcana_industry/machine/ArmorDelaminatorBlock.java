package com.starmaylight.arcana_industry.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import java.util.List;

/** An obsidian press bed and piston cap with four braced iron jaws separate worn armor layers. */
public class ArmorDelaminatorBlock extends StockFedControllerBlock {
    private static final List<StructureMatcher.Part> FRAME = hatchable(List.of(
        new StructureMatcher.Part(0, -1, 0, (level, p) -> level.getBlockState(p).is(Blocks.OBSIDIAN)),
        new StructureMatcher.Part(0, 1, 0, (level, p) -> level.getBlockState(p).is(Blocks.PISTON)),
        new StructureMatcher.Part(1, 0, 0, (level, p) -> level.getBlockState(p).is(Blocks.IRON_BLOCK)),
        new StructureMatcher.Part(-1, 0, 0, (level, p) -> level.getBlockState(p).is(Blocks.IRON_BLOCK)),
        new StructureMatcher.Part(0, 0, 1, (level, p) -> level.getBlockState(p).is(Blocks.IRON_BLOCK)),
        new StructureMatcher.Part(0, 0, -1, (level, p) -> level.getBlockState(p).is(Blocks.IRON_BLOCK)),
        new StructureMatcher.Part(1, -1, 0, (level, p) -> level.getBlockState(p).is(Blocks.IRON_BARS)),
        new StructureMatcher.Part(-1, -1, 0, (level, p) -> level.getBlockState(p).is(Blocks.IRON_BARS)),
        new StructureMatcher.Part(0, -1, 1, (level, p) -> level.getBlockState(p).is(Blocks.IRON_BARS)),
        new StructureMatcher.Part(0, -1, -1, (level, p) -> level.getBlockState(p).is(Blocks.IRON_BARS))
    ));
    public ArmorDelaminatorBlock() { super(Properties.of().mapColor(MapColor.METAL).strength(5.0F).requiresCorrectToolForDrops()); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HandFedControllerBlockEntity(MachineRegistry.ARMOR_DELAMINATOR_ENTITY.get(), pos, state);
    }
    @Override protected net.minecraft.world.item.crafting.RecipeType<?> stockRecipeType() { return MachineRegistry.ARMOR_DELAMINATION.get(); }
    /** Worn armor is refused at the hatch so a damaged piece cannot jam the stock slot. */
    @Override protected boolean stockAccepts(ItemStack stack) { return !stack.isDamaged(); }
    @Override protected boolean stockFormed(Level level, BlockPos pos) { return formed(level, pos); }
    public static boolean formed(Level level, BlockPos pos) {
        return StructureMatcher.ownsParts(level, pos, Direction.NORTH, FRAME, MachineRegistry.ARMOR_DELAMINATOR.get())
            && hatchesWithinLimit(level, pos);
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) return useEmptyHand(level, pos, player, hit);
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!formed(level, pos)) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.armor_delaminator.incomplete"), true);
            return InteractionResult.CONSUME;
        }
        return loadHeldStock(level, pos, player, held);
    }
}
