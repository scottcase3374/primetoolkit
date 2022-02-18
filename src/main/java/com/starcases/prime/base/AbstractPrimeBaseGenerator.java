package com.starcases.prime.base;

import java.util.logging.Logger;

import com.starcases.prime.intfc.PrimeBaseGenerateIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import lombok.NonNull;

/**
 *
 * Abstract class for common Prime base functionality.
 *
 */
public abstract class AbstractPrimeBaseGenerator implements PrimeBaseGenerateIntfc
{
	@NonNull
	protected final Logger log;

	@NonNull
	protected final PrimeSourceIntfc ps;

	protected boolean doLog;

	protected enum State { OVER, UNDER, EQUAL, REVERT }

	protected boolean preferParallel;

	public PrimeBaseGenerateIntfc doPreferParallel(boolean preferParallel)
	{
		this.preferParallel = preferParallel;
		return this;
	}

	protected AbstractPrimeBaseGenerator(@NonNull PrimeSourceIntfc ps, @NonNull Logger log)
	{
		this.log = log;
		this.ps = ps;
		this.ps.init();
	}

	@Override
	public void setLogBaseGeneration(boolean doLog)
	{
		this.doLog = doLog;
	}
}
