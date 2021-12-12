package com.starcases.prime.log;

import com.starcases.prime.intfc.LogGraphIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import java.util.logging.Logger;

/**
 * Base for algorithms that just log info (which it maybe just calculated).
 *
 */
public abstract class AbstractLogBase implements LogGraphIntfc
{
	PrimeSourceIntfc ps;
	Logger log;
	
	protected AbstractLogBase(PrimeSourceIntfc ps, Logger log)
	{
		this.ps = ps;
		this.log = log;
	}
}
