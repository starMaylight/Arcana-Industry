package com.starmaylight.arcana_industry.energy;

import net.minecraft.core.Direction;

/**
 * A block entity whose energy leaves at a fixed voltage class. A cable run only carries power from such a source when
 * the classes match: a higher source burns the cable it touches, a lower one moves nothing. Sources without this
 * interface (other mods' generators and batteries) have no declared class and feed any run.
 */
public interface VoltageSource {
    VoltageTier outputClass(Direction face);
}
