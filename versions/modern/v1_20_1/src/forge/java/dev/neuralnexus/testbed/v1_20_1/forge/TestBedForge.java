package dev.neuralnexus.testbed.v1_20_1.forge;

import dev.neuralnexus.bedapi.CustomPacketPayload;
import dev.neuralnexus.bedapi.event.NetworkEvents;
import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.meta.MetaAPI;

import io.netty.buffer.Unpooled;

import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("testbed")
public class TestBedForge {
    private static final Logger logger = Logger.create("testbed");

    public TestBedForge() {
        MetaAPI api = MetaAPI.instance();

        if (api.side().isServer()) {
            MinecraftForge.EVENT_BUS.<ServerChatEvent>addListener(
                    serverChatEvent -> {
                        String message = serverChatEvent.getMessage().getString();
                        if (message.equals("ping")) {
                            logger.info("Sending packet to player");
                            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                            buf.writeUtf("Ping");
                            ResourceLocation sendChannel =
                                    ResourceLocation.fromNamespaceAndPath("testmod", "ping");
                            serverChatEvent
                                    .getPlayer()
                                    .connection
                                    .send(new ClientboundCustomPayloadPacket(sendChannel, buf));
                        }
                    });

            NetworkEvents.C2S_CUSTOM_PACKET.register(
                    event -> {
                        CustomPacketPayload packet = event.payload();
                        ResourceLocation channel = (ResourceLocation) packet.channel();
                        if (channel.equals(
                                ResourceLocation.fromNamespaceAndPath("testmod", "pong"))) {
                            FriendlyByteBuf data = (FriendlyByteBuf) packet.data();
                            String message = data.readUtf();
                            logger.info(
                                    "Received packet on channel "
                                            + channel
                                            + " with message: "
                                            + message);
                            ((ServerPlayer) event.player())
                                    .sendSystemMessage(
                                            Component.literal(
                                                    "Received packet with message: " + message));
                        }
                    });
        }

        if (api.side().isClient()) {
            NetworkEvents.S2C_CUSTOM_PACKET.register(
                    event -> {
                        try {
                            CustomPacketPayload packet = event.payload();
                            ResourceLocation channel = (ResourceLocation) packet.channel();
                            if (channel.equals(
                                    ResourceLocation.fromNamespaceAndPath("testmod", "ping"))) {
                                FriendlyByteBuf data = (FriendlyByteBuf) packet.data();
                                String message = data.readUtf();
                                // logger.info(Arrays.toString(data));
                                logger.info(
                                        "Received packet on channel "
                                                + channel
                                                + " with message: "
                                                + message);

                                logger.info("Sending packet to server");
                                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                                buf.writeUtf("Pong");
                                ResourceLocation sendChannel =
                                        ResourceLocation.fromNamespaceAndPath("testmod", "pong");
                                ((Connection) event.connection())
                                        .send(new ServerboundCustomPayloadPacket(sendChannel, buf));
                            }
                        } catch (Exception e) {
                            logger.warn("Failed to send packet to server", e);
                        }
                    });
        }
    }
}
