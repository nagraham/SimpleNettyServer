package handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ByteBuf timeBuf = ctx.alloc().buffer(4);
        timeBuf.writeInt((int) secondsFrom01Jan1900ToNow());

        // I'd normally chain the following code; leaving variable for learning purposes
        ChannelFuture channelFuture = ctx.writeAndFlush(timeBuf);

        // the docs suggest an anonymous ChannelFutureListener or a static implementation;
        // but it's a single-method interface! So let's take a better option . . .
        channelFuture.addListener(future -> ctx.close());
    }

    private long secondsFrom01Jan1900ToNow() {
        long secondsFrom01Jan1900ToUnixEpoch = 2208988800L;
        return System.currentTimeMillis() / 1000L + secondsFrom01Jan1900ToUnixEpoch;
    }
}
