package handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lib.TimeStampConverter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TimeClientHandler extends ChannelInboundHandlerAdapter {

    private TimeStampConverter converter;

    public TimeClientHandler(TimeStampConverter converter) {
        this.converter = converter;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        try {
            Instant i = Instant.ofEpochMilli(converter.convert(buf.readUnsignedInt()));
            System.out.println(i.toString());
            ctx.close();
        } finally {
            buf.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
