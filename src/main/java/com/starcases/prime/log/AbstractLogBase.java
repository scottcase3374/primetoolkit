package com.starcases.prime.log;

import com.starcases.prime.intfc.LogGraphIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.NonNull;


/**
 * Base for algorithms that just log info (which it may just calculate).
 *
 */
public abstract class AbstractLogBase implements LogGraphIntfc
{
	@NonNull
	protected final PrimeSourceIntfc ps;

	protected boolean preferParallel;

	public LogGraphIntfc doPreferParallel(boolean preferParallel)
	{
		this.preferParallel = preferParallel;
		return this;
	}

	protected AbstractLogBase(@NonNull PrimeSourceIntfc ps)
	{
		this.ps = ps;
	}
}
