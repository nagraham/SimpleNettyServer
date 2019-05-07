package handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

// The ChannelInboundHandlerAdapter implements the ChannelInboundHandler, but you can override them
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {

    // This is called with the received object (msg)
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf inputBuf = (ByteBuf) msg;
        try {
            System.out.println(inputBuf.toString(CharsetUtil.UTF_8));
        } finally {
            inputBuf.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // typically, you want to log the exception, and close the channel
        cause.printStackTrace();
        ctx.close();
    }
}
