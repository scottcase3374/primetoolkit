package com.starcases.prime;

import java.math.BigInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.starcases.prime.impl.PrimeSource;

@ExtendWith(MockitoExtension.class)
class TripleUnitTest 
{
	private PrimeSource ps;
	
	@BeforeEach
	void init()
	{
		ps = new PrimeSource(120,100);
		ps.init();
	}
	
	@Test
	void testFindMatchOffset() {
		Assertions.assertEquals(1, ps.getPrimeIdx(BigInteger.TWO));
		
		var bi37 = BigInteger.valueOf(37L);
		var bi41 = BigInteger.valueOf(41L);
		Assertions.assertEquals(1, ps.getPrimeRef(bi41).get().getPrimeRefIdx()-ps.getPrimeRef(bi37).get().getPrimeRefIdx());
	}
	
}
