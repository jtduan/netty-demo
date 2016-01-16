package code.djt.demo.server;

import io.netty.bootstrap.ServerBootstrap;

import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
* Usage: `telnet localhost 8080` to test it
*/
public class HelloWorldServer {

    private int port;

    public HelloWorldServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        final HelloWorldServerHandler handler=new HelloWorldServerHandler();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(handler);
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync(); // (7)

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
        new HelloWorldServer(port).run();
    }
}

//@ChannelHandler.Sharable  //同一实例可以被不同channel共享,不同实例不算共享
class HelloWorldServerHandler implements ChannelInboundHandler{//extends ChannelInboundHandlerAdapter {
    private int count; //this value will be sharable,NOTE HERE!!!
    public void channelRegistered(ChannelHandlerContext channelHandlerContext) throws Exception {
        System.out.println("Channel Registered!");
    }

    public void channelUnregistered(ChannelHandlerContext channelHandlerContext) throws Exception {
        System.out.println("Channel Unregistered!");
    }

//    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Channel Actived!");
//        ctx.fireChannelActive();
    }

    public void channelInactive(ChannelHandlerContext channelHandlerContext) throws Exception {
        System.out.println("Channel channelInactive!");
    }

//    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("count="+count);
        count++;
        ctx.writeAndFlush(msg);//.addListener(ChannelFutureListener.CLOSE);
    }

    public void channelReadComplete(ChannelHandlerContext channelHandlerContext) throws Exception {
        System.out.println("Channel channelReadComplete!");
    }

    public void userEventTriggered(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        System.out.println("Channel userEventTriggered!");
    }

    public void channelWritabilityChanged(ChannelHandlerContext channelHandlerContext) throws Exception {
        System.out.println("Channel channelWritabilityChanged!");
    }

    public void handlerAdded(ChannelHandlerContext channelHandlerContext) throws Exception {
        System.out.println("Channel handlerAdded!");
    }

    public void handlerRemoved(ChannelHandlerContext channelHandlerContext) throws Exception {
        System.out.println("Channel handlerRemoved!");
    }

//    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}