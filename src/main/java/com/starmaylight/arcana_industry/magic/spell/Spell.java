package com.starmaylight.arcana_industry.magic.spell;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

/** One customizable Lumen-powered attack; the wand selects among registered spells. */
public interface Spell {
    String id();
    int lumenCost();
    void cast(ServerLevel level, Player caster);
}
