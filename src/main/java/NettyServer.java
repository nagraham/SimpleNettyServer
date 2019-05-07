import handlers.DiscardServerHandler;
import handlers.EchoServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

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
                    .childHandler(channelInitializer)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // start server
            System.out.printf("Starting server on port %d . . . \n\n", port);
            ChannelFuture serverFuture = bootstrap.bind(port).sync();

            // handle shutdown when server socket is closed
            serverFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    static NettyServer discardServer(int port) {
        return new NettyServer(port, new DiscardServerChannelInitializer());
    }

    private static class DiscardServerChannelInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        public void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast(new DiscardServerHandler());
        }
    }

    static NettyServer echoServer(int port) {
        return new NettyServer(port, new EchoServerChannelInitializer());
    }

    private static class EchoServerChannelInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        public void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast(new EchoServerHandler());
        }
    }

    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        try {
            NettyServer.echoServer(port).run();
        } catch (Exception e) {
            System.out.println("ERROR: something goofed");
            e.printStackTrace();
        }
    }
}
