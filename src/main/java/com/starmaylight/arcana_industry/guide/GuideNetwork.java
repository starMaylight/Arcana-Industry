package com.starmaylight.arcana_industry.guide;

import com.starmaylight.arcana_industry.Arcana_industry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.*;
import net.minecraftforge.network.simple.SimpleChannel;
import java.util.*;

public final class GuideNetwork {
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(Arcana_industry.MODID, "guide"),
        () -> "1", "1"::equals, "1"::equals);
    public record Snapshot(boolean open, List<GuideData.Entry> entries) {
        public Snapshot { entries = List.copyOf(entries); GuideLimits.snapshot(entries); }
    }
    public static void register() {
        CHANNEL.messageBuilder(Snapshot.class, 0, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(GuideSnapshotCodec::encode).decoder(GuideSnapshotCodec::decode)
            .consumerMainThread((packet, context) -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> com.starmaylight.arcana_industry.client.GuideScreen.receive(packet))).add();
    }
    public static void send(ServerPlayer player, boolean open) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new Snapshot(open, GuideData.INSTANCE.entries()));
    }
    private GuideNetwork() {}
}
