package com.starcases.prime.impl;

import java.util.Iterator;

import com.starcases.prime.intfc.PrimeRefIntfc;

/**
 * Implementation of iterator returning prime refs.
 */
class PrimeMapIterator implements Iterator<PrimeRefIntfc>
{
	private final transient Iterator<PrimeMapEntry> entry;

	public PrimeMapIterator(final Iterator<PrimeMapEntry> entry)
	{
		this.entry = entry;
	}

	@Override
	public boolean hasNext()
	{
		return entry.hasNext();
	}

	@SuppressWarnings("PMD.LawOfDemeter")
	@Override
	public PrimeRefIntfc next()
	{
		return entry.next().getPrimeRef();
	}
}
