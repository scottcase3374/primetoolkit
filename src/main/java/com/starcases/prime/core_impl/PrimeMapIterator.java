package com.starcases.prime.core_impl;

import java.util.Iterator;

import com.starcases.prime.core_api.PrimeRefIntfc;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Implementation of iterator returning prime refs.
 */
class PrimeMapIterator implements Iterator<PrimeRefIntfc>
{
	/**
	 * Entry for tracking the prime prefix / map info.
	 *
	 *  Add better description here..
	 */
	@Getter(AccessLevel.PRIVATE)
	private final Iterator<PrimeMapEntry> entry;

	/**
	 * Constructor for prime map iterator.
	 * Part of functionality for tracking prefixes
	 * @param entry
	 */
	public PrimeMapIterator(final Iterator<PrimeMapEntry> entry)
	{
		this.entry = entry;
	}

	/**
	 * override of hasnNext
	 */
	@Override
	public boolean hasNext()
	{
		return entry.hasNext();
	}

	/**
	 * Override of next
	 */
	@Override
	public PrimeRefIntfc next()
	{
		return entry.next().getPrimeRef();
	}
}
