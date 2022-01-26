package com.starcases.prime.base.def;

import com.starcases.prime.intfc.PrimeSourceIntfc;
import com.starcases.prime.log.AbstractLogBase;

import lombok.NonNull;
import lombok.extern.java.Log;
import picocli.CommandLine.Command;

@Log
public class LogDefaultBasePrefixes extends AbstractLogBase
{
	public LogDefaultBasePrefixes(@NonNull PrimeSourceIntfc ps)
	{
		super(ps, log);
	}

	@Override
	@Command
	public void log()
	{

	}
}
