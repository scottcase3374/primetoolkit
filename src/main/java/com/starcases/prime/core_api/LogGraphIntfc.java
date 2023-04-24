package com.starcases.prime.core_api;

/**
 * Interface for output oriented processing.
 *
 */
public interface LogGraphIntfc
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
	LogGraphIntfc doPreferParallel(boolean preferParallel);
}
