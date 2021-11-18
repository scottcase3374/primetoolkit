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
	void testGetDistToNextPrime() {
		
		Assertions.assertEquals(2, ps.getDistToNextPrime(2));
	}

	// @ Test
	void testGetNextLowPrimeBigIntegerIntInt() {
		fail("Not yet implemented");
	}

	// @ Test
	void testGetNextHighPrimeBigIntegerIntInt() {
		fail("Not yet implemented");
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
