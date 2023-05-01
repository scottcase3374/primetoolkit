package com.starcases.prime.base.api;

/**
 *
 * Main interface for generating bases. This is implemented for
 * any alternative base types.
 *
 */
public interface PrimeBaseGenerateIntfc
{
	/**
	 * Generate the base information for some specific base type.
	 */
	void genBases();

	/**
	 * Set flag indicating whether base generation
	 * should output any logging.
	 * @param doLog
	 */
	void setBaseGenerationOutput(boolean doLog);
}
