package com.starcases.prime.base.impl;

import com.starcases.prime.base.api.BaseTypesIntfc;

/**
 *
 * Defines the configuration of the bases for the primes.
 *
 */
public enum BaseTypes implements BaseTypesIntfc
{
	/**
	 * Defining as the set or a super set of the primes from:
	 * 	https://primes.utm.edu/lists/small/millions/
	 *
	 * This is a direct mapping from a prime to itself - consider it "identity".
	 *
	 * Examples:
	 *  2, 3, 5, 7, 11, 13, 17, 19, 23, etc
	 */
	DEFAULT

}