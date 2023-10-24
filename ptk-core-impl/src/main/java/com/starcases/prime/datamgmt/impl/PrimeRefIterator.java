package com.starcases.prime.datamgmt.impl;

import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.datamgmt.api.PrimeRefIteratorIntfc;

public class PrimeRefIterator implements PrimeRefIteratorIntfc
{
	private PrimeRefIntfc primeRef;

	public PrimeRefIterator(final PrimeRefIntfc primeRef)
	{
		this.primeRef = primeRef;
	}

	@Override
	public boolean hasNext()
	{
		return primeRef.hasNext();
	}

	@Override
	public PrimeRefIntfc next()
	{
		final var ret = primeRef.getNextPrimeRef().orElse(null);
		primeRef = ret;
		return ret;
	}

	@Override
	public boolean hasPrevious()
	{
		return primeRef.hasPrev();
	}

	@Override
	public PrimeRefIntfc previous()
	{
		final var ret = primeRef.getPrevPrimeRef().orElseThrow();
		primeRef = ret;
		return ret;
	}

	@Override
	public int nextIndex()
	{
		final var ret = (int)primeRef.getNextPrimeRef().orElseThrow().getPrimeRefIdx();
		return ret;
	}

	@Override
	public int previousIndex()
	{
		final var ret = (int)primeRef.getPrevPrimeRef().orElseThrow().getPrimeRefIdx();
		return ret;
	}

	@Override
	public void remove()
	{
		// NO-OP
	}

	@Override
	public void set(PrimeRefIntfc e)
	{
		// NO-OP
	}

	@Override
	public void add(PrimeRefIntfc e)
	{
		// NO-OP
	}
}
