package com.starmaylight.arcana_industry.multiblock;

import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Placement aid: use on a multiblock controller to project its datapack blueprint. Use in the air to
 * step through layers (all, then bottom to top); sneak-use in the air to switch the projection off.
 * The client draws the parts from item NBT; this item never changes blocks in the world.
 */
public final class StructureProjectorItem extends Item {
    public StructureProjectorItem() { super(new Properties().stacksTo(1)); }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide) return InteractionResult.SUCCESS;
        Optional<Projection> bound = bind(level, context.getClickedPos(), context.getItemInHand());
        if (context.getPlayer() != null) {
            context.getPlayer().displayClientMessage(bound.map(projection -> status(level, projection))
                .orElse(Component.translatable("item.arcana_industry.structure_projector.no_blueprint")
                    .withStyle(ChatFormatting.GRAY)), true);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Optional<Projection> current = Projection.read(stack);
        if (current.isEmpty()) return InteractionResultHolder.pass(stack);
        if (!level.isClientSide) {
            Projection projection = current.get();
            boolean brokenController = projection.dimension().equals(level.dimension().location())
                && level.hasChunkAt(projection.origin()) && !projection.controllerPresent(level);
            if (player.isShiftKeyDown() || brokenController) {
                Projection.clear(stack);
                player.displayClientMessage(Component.translatable("item.arcana_industry.structure_projector.cleared"), true);
            } else {
                Projection next = projection.nextLayer();
                next.write(stack);
                player.displayClientMessage(status(level, next), true);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    /** Binds the stack to the blueprint of the controller at {@code pos}; empty when it has none. */
    public static Optional<Projection> bind(Level level, BlockPos pos, ItemStack stack) {
        var key = ForgeRegistries.BLOCKS.getKey(level.getBlockState(pos).getBlock());
        var blueprint = key == null ? Optional.<MultiblockBlueprints.Blueprint>empty() : MultiblockBlueprints.INSTANCE.get(key);
        if (blueprint.isEmpty()) {
            Projection.clear(stack);
            return Optional.empty();
        }
        return blueprint.map(found -> {
            Projection projection = new Projection(key, level.dimension().location(), pos.immutable(), found.parts(), null);
            projection.write(stack);
            return projection;
        });
    }

    private static Component status(Level level, Projection projection) {
        Component layer = projection.layer() == null
            ? Component.translatable("item.arcana_industry.structure_projector.all_layers")
            : Component.translatable("item.arcana_industry.structure_projector.layer", projection.layer());
        int missing = projection.missing(level).size();
        return Component.translatable("item.arcana_industry.structure_projector.status",
            Component.translatable(ForgeRegistries.BLOCKS.getValue(projection.controller()).getDescriptionId()), layer,
            missing, projection.visibleParts().size()).withStyle(missing == 0 ? ChatFormatting.GREEN : ChatFormatting.AQUA);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        Projection.read(stack).ifPresentOrElse(
            projection -> tooltip.add(Component.translatable("item.arcana_industry.structure_projector.bound",
                Component.translatable(ForgeRegistries.BLOCKS.getValue(projection.controller()).getDescriptionId()),
                projection.origin().toShortString()).withStyle(ChatFormatting.GRAY)),
            () -> tooltip.add(Component.translatable("item.arcana_industry.structure_projector.hint").withStyle(ChatFormatting.GRAY)));
    }
}
