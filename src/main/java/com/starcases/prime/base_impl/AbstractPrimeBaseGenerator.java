package com.starcases.prime.base_impl;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.starcases.prime.base_api.PrimeBaseGenerateIntfc;
import com.starcases.prime.core_api.PrimeSourceIntfc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 *
 * Abstract class for common Prime base functionality.
 *
 */
public abstract class AbstractPrimeBaseGenerator implements PrimeBaseGenerateIntfc
{
	/**
	 * Private logger
	 */
	private static final Logger ABSTRACT_LOG = Logger.getLogger(AbstractPrimeBaseGenerator.class.getName());

	/**
	 * Access to lookup of prime/primerefs and the init of base information.
	 */
	@Getter(AccessLevel.PROTECTED)
	@Setter(AccessLevel.PRIVATE)
	protected PrimeSourceIntfc primeSrc;

	/**
	 * Flag determining whether to output info about the construction of base info.
	 */
	@Getter(AccessLevel.PROTECTED)
	@Setter
	protected boolean baseGenerationOutput;

	/**
	 * Indicator for whether a sum is over/under/equal desired value or must-undo change
	 */
	protected enum State { OVER, UNDER, EQUAL, REVERT }

	/**
	 * Flag indicating whether base construction can use multiple CPU cores
	 */
	@Getter(AccessLevel.PROTECTED)
	protected boolean preferParallel;

	/**
	 * flag for whether to track the start/end time of base construction.
	 */
	@Setter
	@Getter(AccessLevel.PRIVATE)
	private boolean trackTime;

	/**
	 * Saved start time if tracking base construction time.
	 */
	@Setter(AccessLevel.PRIVATE)
	@Getter(AccessLevel.PRIVATE)
	private Instant start;

	/**
	 * constructor for primary base.
	 *
	 * subclass is responsible for setting PrimeSourceIntfc
	 */
	protected AbstractPrimeBaseGenerator()
	{
		// subclass is responsible for setting PrimeSourceIntfc
		//  The reason is that the subclass should implements the interface.
	}

	/**
	 * Constructor for secondary bases.
	 * @param primeSrc
	 */
	protected AbstractPrimeBaseGenerator(@NonNull final PrimeSourceIntfc primeSrc)
	{
		this.primeSrc = primeSrc;
	}

	/**
	 * fluent style method for setting flag for whether base construction can use multiple CPU cores.
	 * @param preferParallel
	 * @return
	 */
	public PrimeBaseGenerateIntfc doPreferParallel(final boolean preferParallel)
	{
		this.preferParallel = preferParallel;
		return this;
	}

	/**
	 * Initiate base construction - template method
	 *
	 * subclasses define method genBasesImpl() which performs the real base construction.
	 * This method is responsible for handling base construction time tracking if enabled.
	 */
	@Override
	public void genBases()
	{
		if (trackTime)
		{
			event(true);
		}

		genBasesImpl();

		if (trackTime)
		{
			event(false);
		}
	}

	/**
	 * Method defined by subclasses that perform actual base construction.
	 */
	protected abstract void genBasesImpl();

	/** set start instance if startTime=true; display diff from start
	 *  to now in milli-seconds if startTime=false
	 *
	 * @param startTime
	 */
	@SuppressWarnings({ "PMD.GuardLogStatement"})
	protected void event(final boolean startTime)
	{
		if (ABSTRACT_LOG.isLoggable(Level.INFO))
		{
			if (startTime)
			{
				this.start = Instant.now();

					ABSTRACT_LOG.info("Base generation start time: " + start);
			}
			else
			{

					final var end = Instant.now();
					ABSTRACT_LOG.info("Base generation end time: " + end);
					final var diff = ChronoUnit.MILLIS.between(start, end);
					final var milliRemain = diff % 1_000;
					final var secondRemain = diff / 1_000 % 60;
					final var minuteRemain = diff / 60_000 % 60;

					final var timeDisplayFmt = new DecimalFormat("###,###");
					ABSTRACT_LOG.info(String.format("Base generation: %s min %s sec %s milli",
							timeDisplayFmt.format(minuteRemain),
							timeDisplayFmt.format(secondRemain),
							timeDisplayFmt.format(milliRemain)
							));
			}
		}
	}
}
