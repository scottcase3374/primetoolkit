package com.starcases.prime.intfc;

import java.math.BigInteger;

public interface PrimeSourceIntfc
{
	PrimeRefIntfc getPrimeRef(int primeIdx);
	BigInteger getPrime(int primeIdx);
	int getPrimeIdx(BigInteger val);
	
	int getNextLowPrimeIdx(BigInteger val);
	int getNextHighPrimeIdx(BigInteger val);
	
	BigInteger getDistToNextPrime(int curIdx);
	int getActiveBaseId();
	void setActiveBaseId(int activeBaseId);
	
	int getMaxIdx();
	void init();
}
