package com.starcases.prime.core.api;

/**
 * Interface for output oriented processing.
 *
 */
public interface LogPrimeDataIntfc
{
	/**
	 * Output log info
	 */
	void outputLogs();

	/**
	 * set flag to allow use of multiple CPU cores
	 * @param preferParallel
	 * @return
	 */
	LogPrimeDataIntfc doPreferParallel(boolean preferParallel);
}
