package com.starcases.prime.base;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
	final static Logger log = Logger.getLogger(AbstractPrimeBaseGenerator.class.getName());
	final static DecimalFormat timeDisplayFmt = new DecimalFormat("###,###");

	@NonNull
	protected final PrimeSourceIntfc ps;

	protected boolean doLog;

	protected enum State { OVER, UNDER, EQUAL, REVERT }

	protected boolean preferParallel;

	protected Instant start;

	public PrimeBaseGenerateIntfc doPreferParallel(boolean preferParallel)
	{
		this.preferParallel = preferParallel;
		return this;
	}

	protected AbstractPrimeBaseGenerator(@NonNull PrimeSourceIntfc ps)
	{
		this.ps = ps;
		this.ps.init();
	}

	@Override
	public void setLogBaseGeneration(boolean doLog)
	{
		this.doLog = doLog;
	}

	/** set start instance if startTime=true; display diff from start to now in milli-seconds if startTime=false
	 *
	 * @param startTime
	 * @return
	 */
	protected void event(boolean startTime)
	{
		long diff = 0;
		if (startTime)
		{
			this.start = Instant.now();
		}
		else
		{
			diff = ChronoUnit.MILLIS.between(start, Instant.now());
			long m = diff % 1000;
			long s = diff / 1000 % 60;
			long mim = diff / 60000 % 60;

			log.info(String.format("Base generation: %s min %s sec %s milli",
					timeDisplayFmt.format(mim),
					timeDisplayFmt.format(s),
					timeDisplayFmt.format(m)));
		}

	}
}
