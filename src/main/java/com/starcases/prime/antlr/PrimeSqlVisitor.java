package com.starcases.prime.antlr;

import com.starcases.prime.antlrimpl.PrimeSqlBaseVisitor;
import com.starcases.prime.antlrimpl.PrimeSqlParser.RootContext;
import com.starcases.prime.intfc.PrimeSourceIntfc;

public class PrimeSqlVisitor extends PrimeSqlBaseVisitor<PrimeSqlResult>
{
	final private PrimeSourceIntfc primeSrc;

	public PrimeSqlVisitor(final PrimeSourceIntfc primeSrc)
	{
		this.primeSrc = primeSrc;
	}

	@Override
	public PrimeSqlResult visitRoot(final RootContext ctx)
	{
		return new PrimeSqlResult();
	}
}
