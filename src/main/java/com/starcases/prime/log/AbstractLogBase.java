package com.starcases.prime.log;

import com.starcases.prime.intfc.LogGraphIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.NonNull;


/**
 * Base for algorithms that just outputs info (which it may just calculate).
 *
 */
@SuppressWarnings("PMD.CommentSize")
public abstract class AbstractLogBase implements LogGraphIntfc
{
	/**
	 * Prime source instance - provides access to most operations needed for working with the prime references.
	 */
	@NonNull
	protected transient final PrimeSourceIntfc ps;

	/**
	 * flag indicating that parallel/concurrent execution is preferred.
	 */
	protected transient boolean preferParallel;

	@Override
	public LogGraphIntfc doPreferParallel(final boolean preferParallel)
	{
		this.preferParallel = preferParallel;
		return this;
	}

	protected AbstractLogBase(@NonNull final PrimeSourceIntfc ps)
	{
		this.ps = ps;
	}
}
