package com.starmaylight.arcana_industry.magic;

import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

/** Endgame item that keeps FE in stack NBT and is charged by sneak-using it on an extractable FE face. */
public abstract class FluxStoreItem extends Item {
    private static final String CHARGE = "FluxCharge";
    private final int capacity;
    private final int chargeStep;
    private final int barColor;

    protected FluxStoreItem(Properties properties, int capacity, int chargeStep, int barColor) {
        super(properties);
        this.capacity = capacity;
        this.chargeStep = chargeStep;
        this.barColor = barColor;
    }

    public int capacity() { return capacity; }

    public int charge(ItemStack stack) {
        if (stack.getItem() != this || stack.getTag() == null) return 0;
        return Mth.clamp(stack.getTag().getInt(CHARGE), 0, capacity);
    }

    public void setCharge(ItemStack stack, int amount) {
        if (stack.getItem() == this) stack.getOrCreateTag().putInt(CHARGE, Mth.clamp(amount, 0, capacity));
    }

    /** Sneak-use on an FE machine's output face to draw up to one charge step per click. */
    @Override public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() == null || !context.getPlayer().isShiftKeyDown()) return InteractionResult.PASS;
        var entity = context.getLevel().getBlockEntity(context.getClickedPos());
        if (entity == null) return InteractionResult.PASS;
        var optional = entity.getCapability(ForgeCapabilities.ENERGY, context.getClickedFace());
        if (!optional.isPresent()) return InteractionResult.PASS;
        if (context.getLevel().isClientSide) return InteractionResult.SUCCESS;
        ItemStack stack = context.getItemInHand();
        optional.ifPresent(energy -> {
            int wanted = Math.min(chargeStep, capacity - charge(stack));
            if (wanted > 0) setCharge(stack, charge(stack) + energy.extractEnergy(wanted, false));
        });
        return InteractionResult.CONSUME;
    }

    @Override public boolean isBarVisible(ItemStack stack) { return true; }
    @Override public int getBarWidth(ItemStack stack) { return Math.round(13.0F * charge(stack) / capacity); }
    @Override public int getBarColor(ItemStack stack) { return barColor; }
}
