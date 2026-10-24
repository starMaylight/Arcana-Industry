package com.starmaylight.arcana_industry.machine;

import com.starmaylight.arcana_industry.recipe.TextileWeavingRecipe;
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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import java.util.Comparator;
import java.util.List;

/** A loom with a scaffolding frame, opposed wool/cobweb arms and a stripped-log warp beam weaves dyes into textiles. */
public class SpectralLoomBlock extends Block implements EntityBlock {
    private static final List<StructureMatcher.Part> FRAME = List.of(
        new StructureMatcher.Part(0, -1, 0, (level, p) -> level.getBlockState(p).is(Blocks.LOOM)),
        new StructureMatcher.Part(0, 1, 0, (level, p) -> level.getBlockState(p).is(Blocks.SCAFFOLDING)),
        new StructureMatcher.Part(0, 0, 1, (level, p) -> level.getBlockState(p).is(Blocks.WHITE_WOOL)),
        new StructureMatcher.Part(0, 0, -1, (level, p) -> level.getBlockState(p).is(Blocks.WHITE_WOOL)),
        new StructureMatcher.Part(1, 0, 0, (level, p) -> level.getBlockState(p).is(Blocks.COBWEB)),
        new StructureMatcher.Part(-1, 0, 0, (level, p) -> level.getBlockState(p).is(Blocks.COBWEB)),
        new StructureMatcher.Part(1, 1, 0, (level, p) -> level.getBlockState(p).is(Blocks.STRIPPED_OAK_LOG)),
        new StructureMatcher.Part(-1, 1, 0, (level, p) -> level.getBlockState(p).is(Blocks.STRIPPED_OAK_LOG))
    );
    public SpectralLoomBlock() { super(Properties.of().mapColor(MapColor.WOOL).strength(2.5F)); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SpectralLoomBlockEntity(pos, state);
    }
    @Override @Nullable public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide || type != MachineRegistry.SPECTRAL_LOOM_ENTITY.get()) return null;
        return (world, pos, block, entity) -> ((SpectralLoomBlockEntity) entity).tickServer();
    }
    @Override public void onRemove(BlockState state, Level level, BlockPos pos, BlockState next, boolean moving) {
        if (!state.is(next.getBlock()) && level.getBlockEntity(pos) instanceof SpectralLoomBlockEntity machine) {
            popResource(level, pos, machine.takeInput()); popResource(level, pos, machine.takeOutput());
        }
        super.onRemove(state, level, pos, next, moving);
    }
    public static boolean formed(Level level, BlockPos pos) {
        return StructureMatcher.ownsParts(level, pos, Direction.NORTH, FRAME, MachineRegistry.SPECTRAL_LOOM.get());
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!formed(level, pos)) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.spectral_loom.incomplete"), true);
            return InteractionResult.CONSUME;
        }
        var view = new SimpleContainer(held);
        TextileWeavingRecipe recipe = level.getRecipeManager().getAllRecipesFor(MachineRegistry.TEXTILE_WEAVING.get()).stream()
            .filter(r -> r.matches(view, level))
            .min(Comparator.comparing(r -> r.id().toString())).orElse(null);
        if (recipe == null) return InteractionResult.PASS;
        ItemStack output = recipe.output();
        if (output.isEmpty()) return InteractionResult.PASS;
        if (!player.getAbilities().instabuild) held.shrink(recipe.count());
        if (!player.getInventory().add(output)) player.drop(output, false);
        com.starmaylight.arcana_industry.sound.ModSounds.playMultiblock(level, pos, "spectral_loom");
        return InteractionResult.CONSUME;
    }
}
