package com.nukkitx.network.synapse.session;

import com.nukkitx.network.NetworkSession;
import com.nukkitx.network.SessionConnection;
import com.nukkitx.network.synapse.SynapsePacket;
import com.nukkitx.network.synapse.SynapsePacketHandler;
import com.nukkitx.network.util.Preconditions;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.util.Optional;

@RequiredArgsConstructor
public class SynapseSession implements NetworkSession<SynapseSession>, SessionConnection<SynapsePacket> {
    private final Channel channel;
    private final InetSocketAddress remoteAddress;
    private final JSONObject loginData;
    private boolean closed = false;
    private SynapsePacketHandler handler = null;

    @Override
    public void disconnect() {
        close();
    }

    @Override
    public void onTimeout() {
        close();
    }

    @Override
    public Optional<InetSocketAddress> getRemoteAddress() {
        return Optional.ofNullable(remoteAddress);
    }

    @Override
    public SynapseSession getConnection() {
        return this;
    }

    @Override
    public void close() {
        checkForClosed();
        closed = true;

        channel.close();
    }

    void checkForClosed() {
        Preconditions.checkState(!closed, "Session already closed");
    }

    @Override
    public void sendPacket(@Nonnull SynapsePacket packet) {
        Preconditions.checkNotNull(packet, "packet");
        channel.writeAndFlush(packet);
    }

    public void onPacket(@Nonnull SynapsePacket packet) {
        Preconditions.checkNotNull(packet, "packet");
        if (handler != null) {
            packet.handle(handler);
        }
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void onTick() {
    }

    public Channel getChannel() {
        return channel;
    }

    public JSONObject getLoginData() {
        return loginData;
    }
}
