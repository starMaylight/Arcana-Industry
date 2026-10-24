package com.starmaylight.arcana_industry.magic;

import com.starmaylight.arcana_industry.sound.ModSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import java.util.List;

/**
 * A sealed container that carries lumen away from an Astral Font. It only fills from a font
 * (use on it) and can pour its charge back (sneak-use), so lumen stays unproducible by machines;
 * Astral Wands fall back to a carried vessel when no font within reach holds enough lumen.
 */
public class LumenVesselItem extends Item {
    private static final String TAG = "Lumen";
    private static final int BAR_COLOR = 0x8E7CF0;
    private final int capacity;

    public LumenVesselItem(int capacity, Rarity rarity) {
        super(new Item.Properties().stacksTo(1).rarity(rarity));
        this.capacity = capacity;
    }

    public int capacity() { return capacity; }

    /** Stored lumen, clamped so edited NBT can never exceed the vessel's tier capacity. */
    public static int stored(ItemStack stack) {
        if (!(stack.getItem() instanceof LumenVesselItem vessel) || stack.getTag() == null) return 0;
        return Mth.clamp(stack.getTag().getInt(TAG), 0, vessel.capacity);
    }

    public static void setStored(ItemStack stack, int lumen) {
        if (stack.getItem() instanceof LumenVesselItem vessel)
            stack.getOrCreateTag().putInt(TAG, Mth.clamp(lumen, 0, vessel.capacity));
    }

    /** The fullest carried vessel holding at least {@code cost} lumen, or empty when none does. */
    public static ItemStack findCharged(Player player, int cost) {
        ItemStack best = ItemStack.EMPTY;
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (stored(stack) >= cost && stored(stack) > stored(best)) best = stack;
        }
        return best;
    }

    public static boolean spend(ItemStack stack, int cost) {
        int lumen = stored(stack);
        if (cost <= 0 || lumen < cost) return false;
        setStored(stack, lumen - cost);
        return true;
    }

    @Override public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level.getBlockEntity(context.getClickedPos()) instanceof AstralFontBlockEntity font)) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();
        int lumen = stored(stack);
        boolean pour = player != null && player.isShiftKeyDown();
        int moved = pour ? font.accept(lumen) : Math.min(font.lumen(), capacity - lumen);
        if (!pour && moved > 0) font.spend(moved);
        setStored(stack, pour ? lumen - moved : lumen + moved);
        if (player != null) player.displayClientMessage(Component.translatable("message.arcana_industry.lumen_vessel_level",
            stored(stack), capacity), true);
        if (moved > 0) level.playSound(null, context.getClickedPos(), ModSounds.ASTRAL_RITE.get(), SoundSource.PLAYERS,
            0.5F, pour ? 0.8F : 1.4F);
        return InteractionResult.CONSUME;
    }

    @Override public boolean isBarVisible(ItemStack stack) { return true; }

    @Override public int getBarWidth(ItemStack stack) { return Math.round(13.0F * stored(stack) / capacity); }

    @Override public int getBarColor(ItemStack stack) { return BAR_COLOR; }

    @Override public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("message.arcana_industry.lumen_vessel_tooltip", stored(stack), capacity));
    }
}
