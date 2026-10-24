package com.starmaylight.arcana_industry.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

/**
 * A frame part that may stand in for any part touching a stock-fed controller face-to-face (at most
 * {@link StockFedControllerBlock#MAX_HATCHES} per frame) so hoppers and pipes reach an enclosed controller.
 * Sneak-using an empty hand cycles the covered controller face; an empty hand collects the output buffer.
 */
public class StockHatchBlock extends Block implements EntityBlock {
    public StockHatchBlock() { super(Properties.of().mapColor(MapColor.METAL).strength(3.5F).requiresCorrectToolForDrops()); }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new StockHatchBlockEntity(pos, state); }

    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        if (!player.getItemInHand(hand).isEmpty() || !(level.getBlockEntity(pos) instanceof StockHatchBlockEntity hatch))
            return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        var link = hatch.link();
        if (link == null) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.stock_hatch.unlinked"), true);
            return InteractionResult.CONSUME;
        }
        if (player.isShiftKeyDown()) {
            ItemPortMode mode = link.controller().cycleSide(link.face());
            player.displayClientMessage(Component.translatable("message.arcana_industry.stock_fed.side",
                Component.translatable("direction.arcana_industry." + link.face().getName()),
                Component.translatable("port.arcana_industry." + mode.name().toLowerCase(java.util.Locale.ROOT))), true);
            return InteractionResult.CONSUME;
        }
        ItemStack taken = link.controller().takeOutput();
        if (taken.isEmpty()) return InteractionResult.PASS;
        if (!player.getInventory().add(taken)) player.drop(taken, false);
        return InteractionResult.CONSUME;
    }
}
