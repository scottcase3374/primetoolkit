package com.starcases.prime.log;

import com.starcases.prime.intfc.LogGraphIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;


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
	public LogGraphIntfc doPreferParallel(final boolean preferParallel)
	{
		this.preferParallel = preferParallel;
		return this;
	}

	/**
	 * constructor for base of logging
	 * @param primeSrc
	 */
	protected AbstractLogBase(@NonNull final PrimeSourceIntfc primeSrc)
	{
		this.primeSrc = primeSrc;
	}
}
