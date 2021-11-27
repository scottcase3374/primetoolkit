package com.starcases.prime.graph.log;

import com.starcases.prime.intfc.LogGraphIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import java.util.logging.Logger;

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
