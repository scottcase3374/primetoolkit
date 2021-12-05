package com.starcases.prime.impl;

import java.util.logging.Logger;

import com.starcases.prime.intfc.PrimeBaseIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

public abstract class AbstractPrimeBase implements PrimeBaseIntfc 
{
	protected Logger log;
	protected PrimeSourceIntfc ps;
	protected boolean doLog;
	
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
