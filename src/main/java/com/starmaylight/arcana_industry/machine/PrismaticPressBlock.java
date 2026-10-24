package com.starmaylight.arcana_industry.machine;

import com.starmaylight.arcana_industry.recipe.PrismaticReclamationRecipe;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;

/** Opposed pistons compress colored panes and slabs around a quartz anvil. */
public class PrismaticPressBlock extends Block implements EntityBlock {
    private static final List<StructureMatcher.Part> FRAME = List.of(
        new StructureMatcher.Part(0, -1, 0, (level, p) -> level.getBlockState(p).is(Blocks.QUARTZ_BLOCK)),
        new StructureMatcher.Part(0, 1, 0, (level, p) -> level.getBlockState(p).is(Blocks.IRON_BLOCK)),
        new StructureMatcher.Part(0, 0, 1, (level, p) -> level.getBlockState(p).is(Blocks.PISTON)),
        new StructureMatcher.Part(0, 0, -1, (level, p) -> level.getBlockState(p).is(Blocks.PISTON)),
        new StructureMatcher.Part(1, 0, 0, (level, p) -> level.getBlockState(p).is(Blocks.AMETHYST_BLOCK)),
        new StructureMatcher.Part(-1, 0, 0, (level, p) -> level.getBlockState(p).is(Blocks.AMETHYST_BLOCK))
    );
    public PrismaticPressBlock() { super(Properties.of().mapColor(MapColor.METAL).strength(5.0F).requiresCorrectToolForDrops()); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PrismaticPressBlockEntity(pos, state);
    }
    @Override @Nullable public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide || type != MachineRegistry.PRISMATIC_PRESS_ENTITY.get()) return null;
        return (world, pos, block, entity) -> ((PrismaticPressBlockEntity) entity).tickServer();
    }
    @Override public void onRemove(BlockState state, Level level, BlockPos pos, BlockState next, boolean moving) {
        if (!state.is(next.getBlock()) && level.getBlockEntity(pos) instanceof PrismaticPressBlockEntity machine) {
            popResource(level, pos, machine.takeInput()); popResource(level, pos, machine.takeOutput());
        }
        super.onRemove(state, level, pos, next, moving);
    }
    public static boolean formed(Level level, BlockPos pos) {
        return StructureMatcher.ownsParts(level, pos, Direction.NORTH, FRAME, MachineRegistry.PRISMATIC_PRESS.get());
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!formed(level, pos)) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.prismatic_press.incomplete"), true);
            return InteractionResult.CONSUME;
        }
        var view = new SimpleContainer(held);
        PrismaticReclamationRecipe recipe = level.getRecipeManager().getAllRecipesFor(MachineRegistry.PRISMATIC_RECLAMATION.get()).stream()
            .filter(r -> r.matches(view, level))
            .min(Comparator.comparing(r -> r.id().toString())).orElse(null);
        if (recipe == null) return InteractionResult.PASS;
        ItemStack output = recipe.output();
        if (output.isEmpty()) return InteractionResult.PASS;
        if (!player.getAbilities().instabuild) held.shrink(recipe.count());
        if (!player.getInventory().add(output)) player.drop(output, false);
        com.starmaylight.arcana_industry.sound.ModSounds.playMultiblock(level, pos, "prismatic_press");
        return InteractionResult.CONSUME;
    }
}

