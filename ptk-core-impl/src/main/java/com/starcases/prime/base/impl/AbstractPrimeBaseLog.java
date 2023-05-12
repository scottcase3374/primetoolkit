package com.starcases.prime.base.impl;

import com.starcases.prime.core.api.LogPrimeDataIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;


/**
 * Base for algorithms that just outputs info (which it may just calculate).
 *
 */
public abstract class AbstractPrimeBaseLog implements LogPrimeDataIntfc
{
	/**
	 * Prime source instance - provides access to most operations needed for working with the prime references.
	 */
	@Getter(AccessLevel.PROTECTED)
	@NonNull
	protected final PrimeSourceIntfc primeSrc;

	/**
	 * flag indicating that parallel/concurrent execution is preferred.
	 */
	@Getter(AccessLevel.PROTECTED)
	@Setter(AccessLevel.PRIVATE)
	protected boolean preferParallel;

	/**
	 * Set flag indicating ok to use multiple CPU cores
	 */
	@Override
	public LogPrimeDataIntfc doPreferParallel(final boolean preferParallel)
	{
		this.preferParallel = preferParallel;
		return this;
	}

	/**
	 * constructor for base of logging
	 * @param primeSrc
	 */
	protected AbstractPrimeBaseLog(@NonNull final PrimeSourceIntfc primeSrc)
	{
		this.primeSrc = primeSrc;
	}
}
