package handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object input) {
        ByteBuf inputBuf = (ByteBuf) input;
        System.out.println(inputBuf.toString(CharsetUtil.UTF_8));
        ctx.write(input);
        ctx.flush();
    }
}
