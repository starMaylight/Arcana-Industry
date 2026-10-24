package com.starmaylight.arcana_industry.energy;

import net.minecraft.util.StringRepresentable;

/** State of one cable face: nothing to join, joined to a cable or energy holder, or switched off by a player. */
public enum CableSide implements StringRepresentable {
    NONE("none"),
    LINK("link"),
    OFF("off");

    private final String name;

    CableSide(String name) { this.name = name; }

    @Override public String getSerializedName() { return name; }
}
