package com.starcases.prime.impl;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.math.BigInteger;

public class BigIntRef extends WeakReference<BigInteger>
{
	public BigIntRef(BigInteger bi, ReferenceQueue<BigInteger> refQ)
	{
		super(bi, refQ);
	}
}
