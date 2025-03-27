package com.starcases.prime.sql.impl;

import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.sql.api.CmdServerIntfc;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;

/**
 * Handle channel init. If system gets more complex, additional
 * handles might be added to the pipeline.
 *
 * @author scott
 *
 */
class CmdChannelInit extends ChannelInitializer<SocketChannel>
{
	private final PrimeSourceIntfc primeSrc;
	private final CmdServerIntfc cmdServer;

	/**
	 * Constructor
	 *
	 * @param primeSrc Prime source ref
	 */
	public CmdChannelInit(final PrimeSourceIntfc primeSrc, final CmdServerIntfc cmdServer)
	{
		super();
		this.primeSrc = primeSrc;
		this.cmdServer = cmdServer;
	}

	/**
	 * Initialize - setup http and prime sql handler
	 */
	@Override
    public void initChannel(final SocketChannel channel) throws Exception
	{
        channel
        	.pipeline()
        	.addLast(new HttpServerCodec())
        	.addLast(new HttpServerExpectContinueHandler())
        	.addLast(new PrimeSQLChannelHandler(primeSrc, cmdServer));
    }
}
