package com.starcases.prime.remote;

import com.starcases.prime.intfc.PrimeSourceIntfc;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Receive commands and process via an antlr parser.
 * @author scott
 *
 */
public class CmdServer
{
	private final int port;
	private final PrimeSourceIntfc primeSrc;

	public CmdServer(final PrimeSourceIntfc primeSrc, final int port)
	{
		this.primeSrc = primeSrc;
		this.port = port;
	}

	public void run() throws InterruptedException
	{
		final EventLoopGroup listenGroup = new NioEventLoopGroup();
		final EventLoopGroup workerGroup = new NioEventLoopGroup();

		try
		{
			final ServerBootstrap bootStrap = new ServerBootstrap();
			bootStrap
				.group(listenGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new CmdChannelInit(primeSrc))
				;

			final ChannelFuture future = bootStrap.bind(port).sync();
			future.channel().closeFuture().sync();
		}
		finally
		{
			workerGroup.shutdownGracefully();
			listenGroup.shutdownGracefully();
		}
	}
}
