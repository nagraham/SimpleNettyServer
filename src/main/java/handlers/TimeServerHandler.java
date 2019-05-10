package handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lib.TimeStampConverter;

@ChannelHandler.Sharable
public class TimeServerHandler extends ChannelInboundHandlerAdapter {

    private TimeStampConverter converter;

    public TimeServerHandler(TimeStampConverter converter) {
        this.converter = converter;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ByteBuf timeBuf = ctx.alloc().buffer(4);
        timeBuf.writeInt(converter.convert(System.currentTimeMillis()).intValue());

        // I'd normally chain the following code; leaving variable for learning purposes
        ChannelFuture channelFuture = ctx.writeAndFlush(timeBuf);

        // the docs suggest an anonymous ChannelFutureListener or a static implementation;
        // but it's a single-method interface! So let's take a better option . . .
        channelFuture.addListener(future -> ctx.close());
    }
}
