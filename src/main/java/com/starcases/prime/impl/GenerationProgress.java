package com.starcases.prime.impl;

import com.starcases.prime.PrimeToolKit;

/**
 * Class that manages progress info for user.
 */
public final class GenerationProgress
{
	/**
	 * Display/update progress info
	 *
	 * @param newPrimeIndex
	 */
	public void dispProgress(final long newPrimeIndex)
	{
		final String [] units = { "%n[%s x 1024k] ", "%n [%s x 128k] ", "%n [%s x 16k] ", "K"};
		final long [] mask = {(1L << 20) -1, (1L << 17) -1 , (1L << 14) -1, (1L << 10) -1};

		// display some progress info
		final var primesProcessed = newPrimeIndex;
		int unitIdx = -1;

		while ((primesProcessed & mask[++unitIdx]) != 0 && unitIdx <  mask.length-1);
			// loop expression does the work

		final var num = (int)(primesProcessed / mask[unitIdx]);
		if (unitIdx != units.length-1 || num <= 2)
		{
			PrimeToolKit.output(String.format(units[unitIdx], num));
		}
	}
}
