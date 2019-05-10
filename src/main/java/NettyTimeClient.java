import handlers.TimeClientHandler;
import handlers.TimeDecoderHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lib.FromRFC868TimeStampConverter;

import java.util.LinkedList;
import java.util.List;

public class NettyTimeClient {

    private String host;
    private int port;
    List<ChannelInboundHandlerAdapter> handlerList;

    public NettyTimeClient() {
        handlerList = new LinkedList<>();
    }

    public NettyTimeClient host(String host) {
        this.host = host;
        return this;
    }

    public NettyTimeClient port(int port) {
        this.port = port;
        return this;
    }

    public NettyTimeClient withHandler(ChannelInboundHandlerAdapter timeHandler) {
        handlerList.add(timeHandler);
        return this;
    }

    public int getTime() {
        return 0;
    }

    public void connect() throws Exception {
        connectToServer();
    }

    private void connectToServer() throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(allHandlers());
                        }
                    });

            ChannelFuture f = bootstrap.connect(host, port).sync();

            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    private ChannelInboundHandlerAdapter[] allHandlers() {
        return handlerList.toArray(new ChannelInboundHandlerAdapter[0]);
    }

    public static void main(String[] args) throws Exception {
        new NettyTimeClient()
                .host("localhost")
                .port(8080)
                .withHandler(new TimeDecoderHandler())
                .withHandler(new TimeClientHandler(new FromRFC868TimeStampConverter()))
                .connect();
    }
}
