package com.starcases.prime.intfc;

/**
 *
 * Main interface for generating bases. This is implemented for
 * any alternative base types.
 *
 */
public interface PrimeBaseGenerateIntfc
{
	void genBases(boolean trackGenTime);
	void setLogBaseGeneration(boolean doLog);
}
