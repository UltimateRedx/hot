package com.hotelpal.service.service.live.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;


/**
 * netty websocket server 的基类， 具体实现再扩展此类
 */
public class NettyServer {
	private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
	private static final boolean OS_LINUX = System.getProperty("os.name").toUpperCase().contains("LINUX");

	private final EventLoopGroup bossGroup = OS_LINUX ? new EpollEventLoopGroup() : new NioEventLoopGroup();
	private final EventLoopGroup workerGroup = OS_LINUX ? new EpollEventLoopGroup() : new NioEventLoopGroup();
	private Channel channel;

	public void initServer() {
		InetSocketAddress addr = new InetSocketAddress(8081);
		startServer(addr);
	}

	private void startServer (SocketAddress address) {
		if (OS_LINUX) {
			logger.info("Using epoll on Linux platform...");
		} else {
			logger.info("Using nio on non-Linux platform...");
		}

		ChannelFuture f = null;
		try {
			ServerBootstrap server = new ServerBootstrap();
			server.group(bossGroup, workerGroup)
					.channel(OS_LINUX ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
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
	}

	public void destroy() {
		logger.info("Shutting down Netty Server...");
		if(channel != null) { channel.close();}
		workerGroup.shutdownGracefully().syncUninterruptibly();
		bossGroup.shutdownGracefully().syncUninterruptibly();
		logger.info("Shutdown Netty Server Success!");
	}
}