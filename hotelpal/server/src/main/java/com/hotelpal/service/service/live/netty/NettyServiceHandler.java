package com.hotelpal.service.service.live.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

public class NettyServiceHandler extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		// 设置30秒没有读到数据，则触发一个READER_IDLE事件。
		// pipeline.addLast(new IdleStateHandler(30, 0, 0));
//		ch.pipeline().addLast(new StringEncoder(Charset.forName("UTF-8")));
//		ch.pipeline().addLast(new StringDecoder(Charset.forName("UTF-8")));
		// HttpServerCodec：将请求和应答消息解码为HTTP消息
		ch.pipeline().addLast(new HttpServerCodec());
		// HttpObjectAggregator：将HTTP消息的多个部分合成一条完整的HTTP消息
		ch.pipeline().addLast(new HttpObjectAggregator(65536));
		// ChunkedWriteHandler：向客户端发送HTML5文件
//		ch.pipeline().addLast("http-chunked",new ChunkedWriteHandler());
		// 在管道中添加我们自己的接收数据实现方法
		ch.pipeline().addLast(new WebSocketServerProtocolHandler("/live/chat","",true));
		ch.pipeline().addLast(new CourseServiceHandler());
	}
}