package com.hotelpal.service.service.live.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

public class NettyServiceHandler extends ChannelInitializer<SocketChannel> {
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		// HttpServerCodec：将请求和应答消息解码为HTTP消息
		ch.pipeline().addLast(new HttpServerCodec());
		// HttpObjectAggregator：将HTTP消息的多个部分合成一条完整的HTTP消息
		ch.pipeline().addLast(new HttpObjectAggregator(65536));
		// 在管道中添加我们自己的接收数据实现方法
		ch.pipeline().addLast(new WebSocketServerProtocolHandler("/live/chat","",true));
		ch.pipeline().addLast(new CourseServiceHandler());
	}
}