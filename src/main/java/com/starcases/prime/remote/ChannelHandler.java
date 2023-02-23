package com.starcases.prime.remote;

import java.nio.charset.Charset;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import com.starcases.prime.antlr.PrimeSqlResult;
import com.starcases.prime.antlr.PrimeSqlVisitor;
import com.starcases.prime.antlrimpl.PrimeSqlLexer;
import com.starcases.prime.antlrimpl.PrimeSqlParser;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;


public class ChannelHandler extends ChannelInboundHandlerAdapter
{
	final private PrimeSourceIntfc primeSrc;

	public ChannelHandler(final PrimeSourceIntfc primeSrc)
	{
		this.primeSrc = primeSrc;
	}

	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause)
	{
		// Close the connection when an exception is raised.
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void channelRead(final ChannelHandlerContext ctx, final Object msg)
	{
	    final ByteBuf in = (ByteBuf) msg;
	    try
	    {
	        while (in.isReadable())
	        {
	        	final CharSequence cmd = in.getCharSequence(0, in.readableBytes(), Charset.defaultCharset());
	        	in.clear(); // Otherwise we end up retrieving the same data repeatedly
        		final PrimeSqlLexer psl = new PrimeSqlLexer(CharStreams.fromString(String.valueOf(cmd)));
        		final CommonTokenStream tokenStream = new CommonTokenStream(psl);
        		final PrimeSqlParser psp = new PrimeSqlParser(tokenStream);

        		final ParseTree parseTree = psp.root();
        		final PrimeSqlVisitor visitor = new PrimeSqlVisitor(primeSrc);
        		final PrimeSqlResult result = visitor.visit(parseTree);
        		ctx.writeAndFlush(result);
	        }
	    }
	    finally
	    {
	        ReferenceCountUtil.release(msg);
	    }
	}
}
