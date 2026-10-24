package com.starmaylight.arcana_industry.guide;

import net.minecraft.resources.ResourceLocation;
import java.util.HashSet;
import java.util.List;
import java.nio.charset.StandardCharsets;

/** One schema for datapack data and network snapshots; lengths are UTF-16 code units. */
public final class GuideLimits {
    public static final int ENTRIES = 256, PAGES = 32, PAGE_LENGTH = 2048, LABEL_LENGTH = 128, ID_LENGTH = 256;
    public static final int SOURCE_CHARACTERS = 400_000, SNAPSHOT_CHARACTERS = 400_000;
    // Count actual UTF-8 bytes before sending; text length alone cannot bound a mixed-language guide.
    public static final int PACKET_BYTES = 1_000_000;

    private static int varIntBytes(int value) {
        int bytes = 1;
        while ((value & ~0x7f) != 0) { bytes++; value >>>= 7; }
        return bytes;
    }
    private static int wireTextBytes(String value) {
        int bytes = value.getBytes(StandardCharsets.UTF_8).length;
        return varIntBytes(bytes) + bytes;
    }
    public static int wireEntryBytes(GuideData.Entry entry) {
        int size = wireTextBytes(entry.id()) + wireTextBytes(entry.title()) + wireTextBytes(entry.category())
            + wireTextBytes(entry.chapter()) + wireTextBytes(entry.group()) + varIntBytes(entry.pages().size());
        for (String page : entry.pages()) size += wireTextBytes(page);
        return size;
    }
    public static int wireHeaderBytes(int entries) { return 1 + varIntBytes(entries); }

    public static void text(String value, int maximum) {
        if (value == null || value.isBlank() || value.length() > maximum)
            throw new IllegalArgumentException("Expected nonblank text of at most " + maximum + " characters");
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (Character.isHighSurrogate(c)) {
                if (++i >= value.length() || !Character.isLowSurrogate(value.charAt(i)))
                    throw new IllegalArgumentException("Unpaired Unicode surrogate");
            } else if (Character.isLowSurrogate(c)) throw new IllegalArgumentException("Unpaired Unicode surrogate");
        }
    }

    public static int characters(GuideData.Entry entry) {
        text(entry.id(), ID_LENGTH);
        if (ResourceLocation.tryParse(entry.id()) == null || !entry.id().contains(":"))
            throw new IllegalArgumentException("Expected namespaced guide ID");
        text(entry.title(), LABEL_LENGTH); text(entry.category(), LABEL_LENGTH); text(entry.chapter(), LABEL_LENGTH); text(entry.group(), LABEL_LENGTH);
        if (entry.pages().isEmpty() || entry.pages().size() > PAGES) throw new IllegalArgumentException("Expected 1..32 pages");
        int size = entry.id().length() + entry.title().length() + entry.category().length() + entry.chapter().length() + entry.group().length();
        for (String page : entry.pages()) { text(page, PAGE_LENGTH); size += page.length(); }
        return size;
    }

    public static void snapshot(List<GuideData.Entry> entries) {
        if (entries.size() > ENTRIES) throw new IllegalArgumentException("Too many guide entries");
        var ids = new HashSet<String>();
        int size = 0, bytes = wireHeaderBytes(entries.size());
        for (var entry : entries) {
            if (!ids.add(entry.id())) throw new IllegalArgumentException("Duplicate guide ID " + entry.id());
            size += characters(entry);
            if (size > SNAPSHOT_CHARACTERS) throw new IllegalArgumentException("Guide snapshot text budget exceeded");
            bytes += wireEntryBytes(entry);
            if (bytes > PACKET_BYTES) throw new IllegalArgumentException("Guide packet byte budget exceeded");
        }
    }
    private GuideLimits() {}
}
