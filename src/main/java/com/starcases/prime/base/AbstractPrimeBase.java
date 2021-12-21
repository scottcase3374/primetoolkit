package com.starcases.prime.base;

import java.util.logging.Logger;
import com.starcases.prime.intfc.PrimeBaseIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

/**
 * 
 * Abstract class for any common prime base functionality.
 *
 */
public abstract class AbstractPrimeBase implements PrimeBaseIntfc 
{
	protected Logger log;
	protected PrimeSourceIntfc ps;
	protected boolean doLog;
	
	protected enum State { OVER, UNDER, EQUAL, REVERT }
	
	protected AbstractPrimeBase(PrimeSourceIntfc ps, Logger log)
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