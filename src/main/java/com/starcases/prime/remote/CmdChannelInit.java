package com.starcases.prime.remote;

import com.starcases.prime.intfc.PrimeSourceIntfc;

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
public class CmdChannelInit extends ChannelInitializer<SocketChannel>
{
	private final PrimeSourceIntfc primeSrc;

	public CmdChannelInit(final PrimeSourceIntfc primeSrc)
	{
		this.primeSrc = primeSrc;
	}

	@Override
    public void initChannel(SocketChannel ch) throws Exception
	{
        ch
        	.pipeline()
        	.addLast(new HttpServerCodec())
        	.addLast(new HttpServerExpectContinueHandler())
        	.addLast(new PrimeSQLChannelHandler(primeSrc));
    }
}
