package com.starmaylight.arcana_industry.magic.spell;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Ordered list of implemented spells; the wand cycles through this list. */
public final class SpellRegistry {
    private static final Map<String, Spell> SPELLS = new LinkedHashMap<>();

    static {
        register(new LumenBoltSpell());
        register(new ResonancePulseSpell());
        register(new AstralLanceSpell());
        register(new StarfallSpell());
        register(new ArcChainSpell());
        register(new DuskFanSpell());
        register(new GravityWellSpell());
        register(new RepulsionBurstSpell());
        register(new SkywardSurgeSpell());
        register(new WitheringSigilSpell());
        register(new SiphonMarkSpell());
        register(new NullifyingBrandSpell());
        register(new BindingSnareSpell());
        register(new FinishingGleamSpell());
        register(new AegisWardSpell());
        register(new HarmonicReversalSpell());
        register(new BlinkStrikeSpell());
        register(new DiscordantCallSpell());
        register(new RiftExchangeSpell());
        register(new ArcaneFurySpell());
    }

    private static void register(Spell spell) { SPELLS.put(spell.id(), spell); }

    public static Spell first() { return SPELLS.values().iterator().next(); }

    public static Spell byId(String id) { return SPELLS.getOrDefault(id, first()); }

    public static String next(String currentId) {
        List<String> ids = List.copyOf(SPELLS.keySet());
        int index = ids.indexOf(currentId);
        return ids.get((index + 1) % ids.size());
    }

    public static List<String> ids() { return List.copyOf(SPELLS.keySet()); }

    private SpellRegistry() {}
}
