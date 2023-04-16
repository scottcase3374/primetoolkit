package com.starcases.prime.remote;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import com.starcases.prime.antlr.PrimeSqlResult;
import com.starcases.prime.antlr.PrimeSqlVisitor;
import com.starcases.prime.antlrimpl.PrimeSqlLexer;
import com.starcases.prime.antlrimpl.PrimeSqlParser;
import com.starcases.prime.intfc.OutputableIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import com.starcases.prime.metrics.MetricMonitor;

import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.LongTaskTimer.Sample;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

import java.nio.charset.Charset;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderValues.CLOSE;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;

/**
 * Handles processing the raw networking aspects and HTTP protocol. This also
 * takes the network message and provides it so the SQL parser / visitor
 * implementation which processes the message and provides the repsponse data.
 * @author scott
 *
 */
public class PrimeSQLChannelHandler extends SimpleChannelInboundHandler<Object>
{
	private static final Logger LOG = Logger.getLogger(PrimeSQLChannelHandler.class.getName());

	private final PrimeSourceIntfc primeSrc;

	private HttpRequest httpRequest;

	private enum MetricType implements OutputableIntfc
	{
		SQLCOMMAND;
	}

	public PrimeSQLChannelHandler(final PrimeSourceIntfc primeSrc)
	{
		super();
		this.primeSrc = primeSrc;
	}

	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause)
	{
		// Close the connection when an exception is raised.
		if (LOG.isLoggable(Level.SEVERE))
		{
			LOG.severe(cause.toString());
		}
		ctx.close();
	}

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx)
    {
        ctx.flush();
    }

	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, final Object msg) throws Exception
	{

		if (msg instanceof HttpRequest httpReq)
		{
			this.httpRequest = httpReq;
		}
		if (msg instanceof HttpContent httpContent)
		{
			final var content = httpContent.content().readCharSequence(httpContent.content().readableBytes(), Charset.defaultCharset());
			final StringBuilder respBuf = new StringBuilder(processRequest(String.valueOf(content)).getResult());

			if (!writeResponse(ctx, httpRequest, respBuf))
			{
				ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
			}
		}
	}

	private boolean writeResponse(final ChannelHandlerContext ctx, final HttpRequest req, final CharSequence respBuf)
	{
		final boolean keepAlive = HttpUtil.isKeepAlive(req);
		final FullHttpResponse response = new DefaultFullHttpResponse(
				req.protocolVersion(),
				req.decoderResult().isSuccess() ? OK : BAD_REQUEST,
				Unpooled.wrappedBuffer(String.valueOf(respBuf).getBytes()));

		if (keepAlive)
		{
			response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
			response.headers().set(CONNECTION, KEEP_ALIVE);
		}
		else
		{
			response.headers().set(CONNECTION, CLOSE);
		}
		ctx.writeAndFlush(response);

		return keepAlive;
	}


	private PrimeSqlResult processRequest(final String request)
	{

		final Optional<LongTaskTimer.Sample> timer = MetricMonitor.longTimer(MetricType.SQLCOMMAND);

		try
		{
			final PrimeSqlLexer psl = new PrimeSqlLexer(CharStreams.fromString(request));
			final CommonTokenStream tokenStream = new CommonTokenStream(psl);
			final PrimeSqlParser psp = new PrimeSqlParser(tokenStream);

			final ParseTree parseTree = psp.root();
			final PrimeSqlVisitor visitor = new PrimeSqlVisitor(primeSrc);
			return visitor.visit(parseTree);
		}
		finally
		{
			timer.ifPresent(Sample::stop);
		}
	}
}
