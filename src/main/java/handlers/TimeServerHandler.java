package handlers;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lib.TimeStamp;

@ChannelHandler.Sharable
public class TimeServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // I'd normally chain the following code; leaving variable for learning purposes
        ChannelFuture channelFuture = ctx.writeAndFlush(TimeStamp.rfc868Now());

        // the docs suggest an anonymous ChannelFutureListener or a static implementation;
        // but it's a single-method interface! So let's take a better option . . .
        channelFuture.addListener(future -> ctx.close());
    }
}
