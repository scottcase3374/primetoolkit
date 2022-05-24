package com.starcases.prime.intfc;

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
