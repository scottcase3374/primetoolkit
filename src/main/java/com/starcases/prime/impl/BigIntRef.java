package com.starcases.prime.impl;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.math.BigInteger;

/**
 * Intended for performance / scalability improvement.
 * Re-evaluating this need.
 */
public class BigIntRef extends WeakReference<BigInteger>
{
	/**
	 * Constructor
	 *
	 * @param bigInt
	 * @param refQ
	 */
	public BigIntRef(final BigInteger bigInt, final ReferenceQueue<BigInteger> refQ)
	{
		super(bigInt, refQ);
	}
}
