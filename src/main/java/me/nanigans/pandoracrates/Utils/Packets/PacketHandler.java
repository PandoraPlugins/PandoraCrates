package me.nanigans.pandoracrates.Utils.Packets;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class PacketHandler extends ChannelDuplexHandler {
    private Player p;

    public PacketHandler(final Player p) {
        this.p = p;
    }
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
    }
    @Override
    public void channelRead(ChannelHandlerContext c, Object m) throws Exception {
        if (m.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInSteerVehicle")) {
            Field d = m.getClass().getDeclaredField("d");
            d.setAccessible(true);
            d.set(m, false);
            d.setAccessible(false);
        }
        super.channelRead(c, m);
    }
}