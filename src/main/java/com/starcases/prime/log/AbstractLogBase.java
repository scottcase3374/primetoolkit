package com.starcases.prime.log;

import com.starcases.prime.intfc.LogGraphIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.NonNull;

import java.util.logging.Logger;

/**
 * Base for algorithms that just log info (which it may just calculate).
 *
 */
public abstract class AbstractLogBase implements LogGraphIntfc
{
	@NonNull
	protected final PrimeSourceIntfc ps;

	@NonNull
	protected final Logger log;

	protected boolean preferParallel;

	public LogGraphIntfc doPreferParallel(boolean preferParallel)
	{
		this.preferParallel = preferParallel;
		return this;
	}

	protected AbstractLogBase(@NonNull PrimeSourceIntfc ps, @NonNull Logger log)
	{
		this.ps = ps;
		this.log = log;
	}
}
