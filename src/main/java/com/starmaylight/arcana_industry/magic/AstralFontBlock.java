package com.starmaylight.arcana_industry.magic;

import com.starmaylight.arcana_industry.machine.MachineRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import javax.annotation.Nullable;

public class AstralFontBlock extends BaseEntityBlock {
    public AstralFontBlock() { super(Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(3).requiresCorrectToolForDrops()); }
    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new AstralFontBlockEntity(pos, state); }
    @Override @Nullable public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, MachineRegistry.ASTRAL_FONT_ENTITY.get(), (world, pos, block, font) -> font.tickServer());
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof AstralFontBlockEntity font)) return InteractionResult.PASS;
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) {
            if (level.isClientSide) return InteractionResult.SUCCESS;
            if (font.spend(100)) player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 600, 0));
            player.displayClientMessage(Component.translatable("message.arcana_industry.astral_font", font.lumen()), true);
            return InteractionResult.CONSUME;
        }
        var recipe = AstralFontBlockEntity.riteFor(level, held);
        if (recipe.isEmpty()) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        var rite = recipe.get();
        // Lumen powers a rite but adds no property: the product must inherit its traits from count x the offering.
        var traits = com.starmaylight.arcana_industry.magic.trait.MaterialTraits.INSTANCE.snapshot();
        var unexplained = com.starmaylight.arcana_industry.magic.trait.TraitAssay.unexplained(
            rite.output(), java.util.Collections.nCopies(rite.count(), held), traits);
        if (!unexplained.isEmpty()) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.astral_font.unexplained",
                com.starmaylight.arcana_industry.magic.trait.TraitAssay.name(unexplained.iterator().next())), true);
            return InteractionResult.CONSUME;
        }
        if (!font.spend(rite.lumenCost())) {
            player.displayClientMessage(Component.translatable("message.arcana_industry.astral_font", font.lumen()), true);
            return InteractionResult.CONSUME;
        }
        held.shrink(rite.count());
        ItemStack output = rite.output();
        if (!player.getInventory().add(output)) player.drop(output, false);
        level.playSound(null, pos, com.starmaylight.arcana_industry.sound.ModSounds.ASTRAL_RITE.get(),
            net.minecraft.sounds.SoundSource.BLOCKS, 0.8F, 1.0F);
        return InteractionResult.CONSUME;
    }
}
