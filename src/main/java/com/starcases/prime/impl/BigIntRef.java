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
	 * @param bi
	 * @param refQ
	 */
	public BigIntRef(final BigInteger bi, final ReferenceQueue<BigInteger> refQ)
	{
		super(bi, refQ);
	}
}
