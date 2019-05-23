import handlers.DiscardServerHandler;
import handlers.EchoServerHandler;
import handlers.SimpleHttpServerHandler;
import handlers.TimeEncoderHandler;
import handlers.TimeServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyServer {
    private static int DEFAULT_PORT = 8080;
    private int port;
    private ChannelInitializer<SocketChannel> channelInitializer;

    private NettyServer(int port, ChannelInitializer<SocketChannel> channelInitializer) {
        this.port = port;
        this.channelInitializer = channelInitializer;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(channelInitializer)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // start server
            System.out.printf("Starting server at http://127.0.0.1:%d . . . \n\n", port);
            ChannelFuture serverFuture = bootstrap.bind(port).sync();

            // handle shutdown when server socket is closed
            serverFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    static NettyServer discardServer(int port) {
        return new NettyServer(port, createChannelInitializer(new DiscardServerHandler()));
    }

    static NettyServer echoServer(int port) {
        return new NettyServer(port, createChannelInitializer(new EchoServerHandler()));
    }

    static NettyServer httpServer(int port) {
        // TODO: Handle SSL
        return new NettyServer(port, new HttpServerInitializer());
    }


    static NettyServer timeServer(int port) {
        return new NettyServer(port, createChannelInitializer(
                new TimeEncoderHandler(),
                new TimeServerHandler()));
    }

    /*
     * this is a bad way to set up the pipeline b/c it's using the same handler instances whenever initChannel() is called
     * this causes the "not a @Sharable handler" exception to get thrown on subsequent requests (unless you make your
     * handlers @Sharable, which means they have to be completely stateless -- no instance variables).q
     */
    private static ChannelInitializer<SocketChannel> createChannelInitializer(ChannelHandler... handlers) {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(handlers);
            }
        };
    }

    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        try {
            NettyServer.httpServer(port).run();
        } catch (Exception e) {
            System.out.println("ERROR: something goofed");
            e.printStackTrace();
        }
    }
}
