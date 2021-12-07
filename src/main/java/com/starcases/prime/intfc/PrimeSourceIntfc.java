package com.starcases.prime.intfc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

public interface PrimeSourceIntfc
{
	Optional<PrimeRefIntfc> getPrimeRef(int primeIdx);
	BigInteger getPrime(int primeIdx);
	int getPrimeIdx(BigInteger val);
	Optional<PrimeRefIntfc> getPrime(BigInteger val);
	
	int getNextLowPrimeIdx(BigInteger val);
	int getNextHighPrimeIdx(BigInteger val);
	
	int getNextLowPrimeIdx(BigDecimal val);
	int getNextHighPrimeIdx(BigDecimal val);
	
	BigInteger getDistToNextPrime(int curIdx);
	BaseTypes getActiveBaseId();
	void setActiveBaseId(BaseTypes activeBaseId);
	
	int getMaxIdx();
	void init();
}
