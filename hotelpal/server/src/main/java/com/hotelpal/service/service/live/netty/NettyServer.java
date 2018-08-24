package com.hotelpal.service.service.live.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;


/**
 * netty websocket server 的基类， 具体实现再扩展此类
 */
public class NettyServer {
	private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
	private final EventLoopGroup bossGroup = new NioEventLoopGroup();
	private final EventLoopGroup workerGroup = new NioEventLoopGroup();
	private Channel channel;

	public static ServerBootstrap server;

	public void initServer() {
		InetSocketAddress addr = new InetSocketAddress(8081);
		startServer(addr);
	}

	public ChannelFuture startServer (SocketAddress address) {
		ChannelFuture f = null;
		try {
			server = new ServerBootstrap();
			server.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 128)
					.childOption(ChannelOption.SO_KEEPALIVE, true)
					.childHandler(new NettyServiceHandler());

			f = server.bind(address).syncUninterruptibly();
			channel = f.channel();
		} catch (Exception e) {
			logger.error("Netty start error:", e);
		} finally {
			if (f != null && f.isSuccess()) {
				logger.info("Netty server listening and ready for connections...");
			} else {
				logger.error("Netty server start up Error!");
			}
		}
		return f;
	}

	public void destroy() {
		logger.info("Shutting down Netty Server...");
		if(channel != null) { channel.close();}
		Future workerFuture = workerGroup.shutdownGracefully(2, 3, TimeUnit.SECONDS);
		try {
			workerFuture.await();
		} catch (InterruptedException e) {
			logger.error("Worker await Interrupted", e);
		}
		Future bossFuture = bossGroup.shutdownGracefully(2, 3, TimeUnit.SECONDS);
		try {
			bossFuture.await();
		} catch (InterruptedException e) {
			logger.error("Boss await Interrupted", e);
		}
		logger.info("Shutdown Netty Server Success!");
	}
}