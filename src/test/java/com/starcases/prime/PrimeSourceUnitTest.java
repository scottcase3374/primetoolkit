package com.starcases.prime;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.starcases.prime.impl.PrimeSource;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class PrimeSourceUnitTest 
{
	private PrimeSource ps;
	
	@BeforeEach
	void init()
	{
		ps = new PrimeSource(120,100);
		ps.init();
	}
	
	@Test
	void testGetPrimeIdx() {
		Assertions.assertEquals(1, ps.getPrimeIdx(BigInteger.TWO));
		
		BigInteger bi101 = BigInteger.valueOf(101L);
		Assertions.assertEquals(26, ps.getPrimeIdx(bi101));
	}

	// @ Test
	void testGetDistToNextPrime() 
	{	
		Assertions.assertEquals(2, ps.getDistToNextPrime(2));
	}

	@Test
	void testGetNextLowPrimeBigIntegerIntInt() 
	{
		Assertions.assertEquals(3, ps.getNextLowPrimeIdx(BigInteger.valueOf(6L))); // Prime 5 @ idx 3 for BigInteger 6
		Assertions.assertEquals(5, ps.getNextLowPrimeIdx(BigInteger.valueOf(13L)));   // Prime 11 @ idx 5 for BigInteger 13
	}

	@Test
	void testGetNextHighPrimeBigIntegerIntInt() 
	{
		Assertions.assertEquals(4, ps.getNextHighPrimeIdx(BigInteger.valueOf(6L)));    // Prime 7 @ idx 4 for BigInteger 6
		Assertions.assertEquals(7, ps.getNextHighPrimeIdx(BigInteger.valueOf(13L)));   // Prime 17 @ idx 7 for BigInteger 13	}
	}
	
	// @ Test
	void testGetNextLowPrimeBigInteger() {
		fail("Not yet implemented");
	}

	// @ Test
	void testGetNextHighPrimeBigInteger() {
		fail("Not yet implemented");
	}

	@Test
	void testGetPrime() {
		
		BigInteger bi101 = BigInteger.valueOf(101L);
		Assertions.assertEquals(bi101, ps.getPrime(26));
	}

	// @ Test
	void testGetPrimeRef() {
		fail("Not yet implemented");
	}

}
