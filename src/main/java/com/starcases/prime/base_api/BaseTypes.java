package com.starcases.prime.base_api;

import com.starcases.prime.core_api.OutputableIntfc;

/**
 *
 * Defines the configuration of the bases for the primes.
 *
 */
public enum BaseTypes implements OutputableIntfc
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
	DEFAULT,

	/**
	 * Prefix is case where bases of Prime Pn consist of:
	 *  [Pn-1, subset of other primes < Pn-1 which in total sum to Pn]
	 */
	PREFIX,


	/**
	 * Similar to PREFIX.
	 */
	PRIME_TREE,

	/**
	 * Reduce Prime bases to some N non-unique initial primes.
	 * sum count of duplicates; more useful as input for
	 * logging of interesting info.
	 */
	NPRIME,

	/**
	 * Base entries are a total of exactly 3 prime values; handle ALL combinations by default.
	 *
	 * List of 3 primes makes 1 base.  There are X bases (combinations) per prime.
	 *
	 * Example for Prime 773; each triple below sums to 773.
	 * There are actually 796 bases with only 4 shown here.
	 *
	 * [241,263,269], [239,263,271], [233,269,271], [239,257,277]
	 */
	THREETRIPLE ;
}