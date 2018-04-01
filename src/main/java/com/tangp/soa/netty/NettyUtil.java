package com.tangp.soa.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyUtil {

	public static void startServer(String port) throws NumberFormatException, InterruptedException {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel sc) throws Exception {
							sc.pipeline().addLast(new NettyServerInHandler());
						}
					}).option(ChannelOption.SO_BACKLOG, 128);

			// ChannelFuture 实现nio同步
			ChannelFuture f = b.bind(Integer.valueOf(port)).sync();
			// 服务端会在此阻塞，直到有客户端请求过来
			f.channel().closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
	}

	public static String sendMsg(String host, String port, final String sendMsg)
			throws Exception, InterruptedException {
		EventLoopGroup workGroup = new NioEventLoopGroup();
		final StringBuffer resultMsg = new StringBuffer();
		try {
			Bootstrap b = new Bootstrap();
			b.group(workGroup);
			b.channel(NioSocketChannel.class);
			b.handler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					// TODO Auto-generated method stub
					ch.pipeline().addLast(new NettyClientInHandler(resultMsg, sendMsg));
				}
			});
			// 这个是连接服务端，一直等待服务端返回消息，返回的信息封装到future，可以监控线程的返回
			ChannelFuture f = b.bind(host, Integer.valueOf(port)).channel().closeFuture().await();

			return resultMsg.toString();
		} finally {
			workGroup.shutdownGracefully();
		}
	}

}
