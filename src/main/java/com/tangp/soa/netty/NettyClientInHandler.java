package com.tangp.soa.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class NettyClientInHandler extends ChannelHandlerAdapter {

	// 服务端返回的消息
	private StringBuffer message;
	// 要发送的消息
	private String sendMsg;

	public NettyClientInHandler(StringBuffer message, String sendMsg) {
		this.message = message;
		this.sendMsg = sendMsg;
	}

	/**
	 * 当与netty服务端连接成功后会触发这个方法。 在这个方法里完成消息的发送
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("===========channelActive==========");
		ByteBuf encoded = ctx.alloc().buffer(4 * sendMsg.length());
		encoded.writeBytes(sendMsg.getBytes());
		ctx.write(encoded);
		ctx.flush();
	}

	/**
	 * 一旦服务端有消息过来，则触发该方法
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("==========channelRead=============");
		ByteBuf result = (ByteBuf) msg;
		byte[] result1 = new byte[result.readableBytes()];
		result.readBytes(result1);
		System.out.println("server response msg：" + new String(result1));
		message.append(new String(result1));
		result.release();
	}

}
