package com.starcases.prime.antlr;

import com.starcases.prime.antlrimpl.PrimeSqlBaseVisitor;
import com.starcases.prime.antlrimpl.PrimeSqlParser.GreaterThanContext;
import com.starcases.prime.antlrimpl.PrimeSqlParser.LessThanContext;
import com.starcases.prime.antlrimpl.PrimeSqlParser.RootContext;
import com.starcases.prime.intfc.PrimeSourceIntfc;

public class PrimeSqlVisitor extends PrimeSqlBaseVisitor<PrimeSqlResult>
{
    private final  PrimeSourceIntfc primeSrc;
	private final PrimeSqlResult result = new PrimeSqlResult();

	public PrimeSqlVisitor(final PrimeSourceIntfc primeSrc)
	{
		System.out.println("PrimeSqlVisitor - ctor");
		this.primeSrc = primeSrc;
	}

	@Override
	public PrimeSqlResult visitRoot(RootContext ctx)
	{
		System.out.println("PrimeSqlVisitor - visitRoot");
		this.visitChildren(ctx);
		return result;
	}

	@Override
	public PrimeSqlResult visitGreaterThan(GreaterThanContext ctx)
	{


		System.out.println("primesqlvisitor - visitGreaterThan  " );
		return this.visitChildren(ctx);
	}

	@Override
	public PrimeSqlResult visitLessThan(LessThanContext ctx)
	{
		//final var id = ctx.children.get(0);
		//final var val = ctx.children.get(1);

		System.out.println("primesqlvisitor - visitLessThan: "); // id[" + id.getText() + "] val: [" + val.getText() + "]");
		return this.visitChildren(ctx);
	}
}
