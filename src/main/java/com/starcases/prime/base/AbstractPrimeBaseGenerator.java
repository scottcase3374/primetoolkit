package com.starcases.prime.base;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.starcases.prime.intfc.PrimeBaseGenerateIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import lombok.NonNull;
import lombok.Setter;

/**
 *
 * Abstract class for common Prime base functionality.
 *
 */
public abstract class AbstractPrimeBaseGenerator implements PrimeBaseGenerateIntfc
{
	static final Logger log = Logger.getLogger(AbstractPrimeBaseGenerator.class.getName());
	static final  DecimalFormat timeDisplayFmt = new DecimalFormat("###,###");

	protected PrimeSourceIntfc ps;

	protected boolean doLog;

	protected enum State { OVER, UNDER, EQUAL, REVERT }

	protected boolean preferParallel;

	@Setter
	protected boolean trackTime;
	protected Instant start;

	public PrimeBaseGenerateIntfc doPreferParallel(boolean preferParallel)
	{
		this.preferParallel = preferParallel;
		return this;
	}

	protected AbstractPrimeBaseGenerator()
	{
		// subclass responsible for setting PrimeSourceIntfc
		//  The reason is that the subclass should implements the interface.
	}

	protected AbstractPrimeBaseGenerator(@NonNull PrimeSourceIntfc ps)
	{
		this.ps = ps;
	}

	@Override
	public void setLogBaseGeneration(boolean doLog)
	{
		this.doLog = doLog;
	}

	@Override
	public void genBases()
	{
		if (trackTime)
			event(true);

		genBasesImpl();

		if (trackTime)
			event(false);
	}

	protected abstract void genBasesImpl();

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

			if (log.isLoggable(Level.INFO))
			{
				log.info(String.format("Base generation: %s min %s sec %s milli",
						timeDisplayFmt.format(mim),
						timeDisplayFmt.format(s),
						timeDisplayFmt.format(m)));
			}
		}

	}
}
