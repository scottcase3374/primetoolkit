package com.starcases.prime.log;

import com.starcases.prime.intfc.LogGraphIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.NonNull;

import java.util.logging.Logger;

/**
 * Base for algorithms that just log info (which it maybe just calculated).
 *
 */
public abstract class AbstractLogBase implements LogGraphIntfc
{
	@NonNull
	PrimeSourceIntfc ps;
	
	@NonNull
	Logger log;
	
	protected AbstractLogBase(@NonNull PrimeSourceIntfc ps, @NonNull Logger log)
	{
		this.ps = ps;
		this.log = log;
	}
}
