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
	int port = 8690;
	PrimeSourceIntfc primeSrc;

	public CmdServer(final PrimeSourceIntfc primeSrc, final int port)
	{
		this.primeSrc = primeSrc;
		this.port = port;
	}

	public void run() throws InterruptedException
	{
		EventLoopGroup listenGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try
		{
			ServerBootstrap bs = new ServerBootstrap();
			bs
				.group(listenGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new CmdChannelInit(primeSrc))
				;

			ChannelFuture future = bs.bind(port).sync();
			future.channel().closeFuture().sync();
		}
		finally
		{
			workerGroup.shutdownGracefully();
			listenGroup.shutdownGracefully();
		}
	}
}
