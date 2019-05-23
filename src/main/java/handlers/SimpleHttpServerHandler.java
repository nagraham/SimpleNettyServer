package handlers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;

/**
 * Inspired by code found here:
 * https://netty.io/4.1/xref/io/netty/example/http/snoop/HttpSnoopServerHandler.html
 */
public class SimpleHttpServerHandler extends ChannelInboundHandlerAdapter {

    private HttpRequest httpRequest;
    private StringBuilder responseContent = new StringBuilder();

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpRequest) {
            httpRequest = (HttpRequest) msg;
            responseContent.append("URI: ").append(httpRequest.uri()).append("\n");
            responseContent.append("METHOD: ").append(httpRequest.method()).append("\n");
            responseContent.append("HOST: ").append(httpRequest.headers().get(HttpHeaderNames.HOST, "unknown")).append("\n");
        }

        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;
            ByteBuf contentBuf = httpContent.content();
            if (contentBuf.isReadable()) {
                responseContent.append("CONTENT: ").append(contentBuf.toString(CharsetUtil.UTF_8)).append("\n");
            }

            if (msg instanceof LastHttpContent) {
                responseContent.append("END OF CONTENT\n");
                LastHttpContent lastHttpContent = (LastHttpContent) msg;
                writeResponse(lastHttpContent, ctx);

                // if keep alive off, close connection
                if (!isKeepAlive()) {
                    ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private void writeResponse(HttpObject currentObj, ChannelHandlerContext ctx) {
        HttpResponseStatus status = currentObj.decoderResult().isSuccess() ?
                HttpResponseStatus.OK :
                HttpResponseStatus.BAD_REQUEST;
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                status,
                Unpooled.copiedBuffer(responseContent.toString(), CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

        if (isKeepAlive()) {
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        // TODO: Handle cookies
        ctx.write(response);
    }

    private boolean isKeepAlive() {
        return HttpUtil.isKeepAlive(httpRequest);
    }
}
