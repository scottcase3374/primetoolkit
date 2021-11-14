package com.starcases.prime.intfc;

import java.math.BigInteger;

public interface PrimeSourceIntfc
{
	PrimeRefIntfc getPrimeRef(int primeIdx);
	BigInteger getPrime(int primeIdx);
	int getPrimeIdx(BigInteger val);
	int getNextLowPrime(BigInteger val);
	int getMaxIdx();
	void init();
}
