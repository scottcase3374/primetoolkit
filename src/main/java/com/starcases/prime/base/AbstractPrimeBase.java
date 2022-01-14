package com.starcases.prime.base;

import java.util.logging.Logger;
import com.starcases.prime.intfc.PrimeBaseIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import lombok.NonNull;

/**
 *
 * Abstract class for common prime base functionality.
 *
 */
public abstract class AbstractPrimeBase implements PrimeBaseIntfc
{
	@NonNull
	protected Logger log;

	@NonNull
	protected PrimeSourceIntfc ps;

	protected boolean doLog;

	protected enum State { OVER, UNDER, EQUAL, REVERT }

	protected AbstractPrimeBase(@NonNull PrimeSourceIntfc ps, @NonNull Logger log)
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
