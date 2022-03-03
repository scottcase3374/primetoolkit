package com.starcases.prime.base;

import java.io.Serializable;

import org.infinispan.protostream.annotations.ProtoEnumValue;

/**
 *
 * Defines the configuration of the bases for the primes.
 *
 */
public enum BaseTypes implements Serializable
{
	/**
	 * default is case where bases of Prime Pn consist of:  [Pn-1,  subset of other primes < Pn-1 which in total sum to Pn]
	 */
	@ProtoEnumValue(1)
	DEFAULT,

	@ProtoEnumValue(2)
	PREFIX,

	@ProtoEnumValue(3)
	PREFIX_TREE,

	/**
	 * Reduce Prime bases to some N non-unique initial primes.  sum count of duplicates; more useful as input for
	 * logging of interesting info.
	 */
	@ProtoEnumValue(4)
	NPRIME,

	/**
	 * Limit bases to a total of 3 values and try to avoid the small values(1,2,3) when possible.
	 *
	 * List of 3 primes makes 1 base.  There are X bases (combinations) per prime.
	 */
	@ProtoEnumValue(5)
	THREETRIPLE ;
}