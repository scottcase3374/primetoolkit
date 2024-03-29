package com.starcases.prime.sql.impl;

import java.lang.reflect.Proxy;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.sql.api.CmdServerIntfc;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Receive commands and process via an antlr parser (SQL-like processing
 * for primes/data).
 *
 * @author scott
 *
 */
class CmdServer implements CmdServerIntfc
{
	/**
	 * default logger
	 */
	private static final Logger LOG = Logger.getLogger(CmdServer.class.getName());

	private final int port;
	private final PrimeSourceIntfc primeSrc;

	/**
	 * Need interface to use with proxy which provides access to both
	 * the EventLoopGroup and Autocloseable interfaces.
	 *
	 */
	public interface CmdEventLoopGroup extends EventLoopGroup, AutoCloseable
	{}

	/**
	 * Constructor for the cmd server which provides access to a
	 * SQL-like processor for the primes and data.
	 *
	 * @param primeSrc
	 * @param port
	 */
	public CmdServer(final PrimeSourceIntfc primeSrc, final int port)
	{
		this.primeSrc = primeSrc;
		this.port = port;
	}

	/**
	 * run the listener for the SQL-like processor.
	 *
	 * @throws InterruptedException
	 */
	@Override
	public void run() throws InterruptedException
	{

		try (CmdEventLoopGroup listenGroup = createEventLoopGroup();
			 CmdEventLoopGroup workerGroup = createEventLoopGroup();)
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
		catch(final Exception e)
		{
			if (LOG.isLoggable(Level.SEVERE))
			{
				LOG.severe(e.toString());
			}
		}
	}

	/**
	 * code checking tools complain about needing to close the resource.  This method generates a
	 * proxy to handle the Autocloseable interface for the EventLoopGroup.
	 *
	 * Note that if you open up the interface hierarchy for NioEventLoopGroup, AutoCloseable
	 * interface is eventually found - not immediately clear why it isn't found when
	 * directly using NioEventLoopGroup
	 *
	 * @return
	 */
	private CmdEventLoopGroup createEventLoopGroup()
	{
		@SuppressWarnings({"PMD.LocalVariableNamingConventions"})
		final String CLOSE_METHOD = "close";
		final NioEventLoopGroup evtLoopGrp = new NioEventLoopGroup();

	    return (CmdEventLoopGroup) Proxy.newProxyInstance(
	    Thread.currentThread().getContextClassLoader(), new Class<?>[]
	    		{CmdEventLoopGroup.class },
	    		(proxy, method, methodArgs) ->
	    		{
	    			if (CLOSE_METHOD.equals(method.getName()))
	    			{
	    				return evtLoopGrp.awaitTermination(2, TimeUnit.SECONDS);
	    			}
	    			else
	    			{
	    				return method.invoke(evtLoopGrp, methodArgs);
	    			}
	    		} );
	}
}
