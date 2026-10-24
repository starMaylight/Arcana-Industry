package com.starmaylight.arcana_industry.guide;

import net.minecraft.network.FriendlyByteBuf;
import java.util.ArrayList;

/** Bounded, all-or-nothing snapshot decoding before the main-thread screen update. */
public final class GuideSnapshotCodec {
    public static void encode(GuideNetwork.Snapshot packet, FriendlyByteBuf buffer) {
        buffer.writeBoolean(packet.open()); buffer.writeVarInt(packet.entries().size());
        for (var entry : packet.entries()) {
            buffer.writeUtf(entry.id(), GuideLimits.ID_LENGTH);
            buffer.writeUtf(entry.title(), GuideLimits.LABEL_LENGTH);
            buffer.writeUtf(entry.category(), GuideLimits.LABEL_LENGTH);
            buffer.writeUtf(entry.chapter(), GuideLimits.LABEL_LENGTH);
            buffer.writeUtf(entry.group(), GuideLimits.LABEL_LENGTH);
            buffer.writeVarInt(entry.pages().size());
            for (String page : entry.pages()) buffer.writeUtf(page, GuideLimits.PAGE_LENGTH);
        }
    }
    public static GuideNetwork.Snapshot decode(FriendlyByteBuf buffer) {
        if (buffer.readableBytes() > GuideLimits.PACKET_BYTES) throw new IllegalArgumentException("Guide packet byte budget exceeded");
        boolean open = buffer.readBoolean();
        int count = buffer.readVarInt();
        if (count < 0 || count > GuideLimits.ENTRIES) throw new IllegalArgumentException("Invalid guide entry count");
        var entries = new ArrayList<GuideData.Entry>();
        int characters = 0;
        for (int i = 0; i < count; i++) {
            String id = buffer.readUtf(GuideLimits.ID_LENGTH), title = buffer.readUtf(GuideLimits.LABEL_LENGTH),
                category = buffer.readUtf(GuideLimits.LABEL_LENGTH), chapter = buffer.readUtf(GuideLimits.LABEL_LENGTH),
                group = buffer.readUtf(GuideLimits.LABEL_LENGTH);
            int pages = buffer.readVarInt();
            if (pages < 1 || pages > GuideLimits.PAGES) throw new IllegalArgumentException("Invalid guide page count");
            var content = new ArrayList<String>();
            for (int page = 0; page < pages; page++) content.add(buffer.readUtf(GuideLimits.PAGE_LENGTH));
            var entry = new GuideData.Entry(id, title, category, chapter, group, content);
            characters += GuideLimits.characters(entry);
            if (characters > GuideLimits.SNAPSHOT_CHARACTERS) throw new IllegalArgumentException("Guide snapshot text budget exceeded");
            entries.add(entry);
        }
        if (buffer.isReadable()) throw new IllegalArgumentException("Trailing guide packet data");
        return new GuideNetwork.Snapshot(open, entries);
    }
    private GuideSnapshotCodec() {}
}
