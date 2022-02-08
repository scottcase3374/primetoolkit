package com.starcases.prime.intfc;

/**
 * Interface for output oriented processing.
 *
 */
public interface LogGraphIntfc
{
	void log();
	LogGraphIntfc doPreferParallel(boolean preferParallel);
}
