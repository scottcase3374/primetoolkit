package com.starcases.prime.intfc;

import java.math.BigInteger;

public interface PrimeSourceIntfc
{
	PrimeRefIntfc nextPrimeRef();
	PrimeRefIntfc getPrimeRef(int primeIdx);
	BigInteger getPrime(int primeIdx);
}
