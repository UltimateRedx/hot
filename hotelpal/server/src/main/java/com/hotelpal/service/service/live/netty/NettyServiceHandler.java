package com.hotelpal.service.service.live.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

public class NettyServiceHandler extends ChannelInitializer<SocketChannel> {
	private static final Logger logger = LoggerFactory.getLogger(NettyServiceHandler.class);
	//private static final String pw = "214886541640622";
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
//		try {
//			KeyStore ks = KeyStore.getInstance("JKS"); /// "JKS"
//			InputStream ksInputStream = new FileInputStream("C:\\Users\\Redx\\Desktop\\ssl-tomcat\\214886541640622.pfx"); /// 证书存放地址
//			ks.load(ksInputStream, pw.toCharArray());
//			//KeyManagerFactory充当基于密钥内容源的密钥管理器的工厂。
//			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());//getDefaultAlgorithm:获取默认的 KeyManagerFactory 算法名称。
//			kmf.init(ks, pw.toCharArray());
//			//SSLContext的实例表示安全套接字协议的实现，它充当用于安全套接字工厂或 SSLEngine 的工厂。
//			SSLContext sslContext = SSLContext.getInstance("TLS");
//			sslContext.init(kmf.getKeyManagers(), null, null);
//
//			SSLEngine sslEngine = SSLContext.getDefault().createSSLEngine();
//			sslEngine.setUseClientMode(false);
//			ch.pipeline().addLast(new SslHandler(sslEngine));
//		} catch (Exception e) {
//			logger.error("启动wss失败", e);
//		}
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