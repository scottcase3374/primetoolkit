package com.starcases.prime;

import java.math.BigInteger;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.starcases.prime.base.PrimeBaseWithLists;
import com.starcases.prime.impl.PrimeRef;
import com.starcases.prime.impl.PrimeSource;

import lombok.NonNull;

@ExtendWith(MockitoExtension.class)
class PrimeRefUnitTest
{
	@NonNull
	private PrimeSource ps;

	@BeforeEach
	void init()
	{
		ps = new PrimeSource(120, null, PrimeRef::setPrimeSource, PrimeBaseWithLists::setPrimeSource, 100);
		ps.init();
	}

	@Test
	void testPrimeIdxDiff() {
		Assertions.assertEquals(1, ps.getPrimeIdx(BigInteger.TWO));

		var bi5 = BigInteger.valueOf(5L);
		var bi3 = BigInteger.valueOf(3L);
		Assertions.assertEquals(1, ps.getPrimeRef(bi5).get().getPrimeRefIdx()-ps.getPrimeRef(bi3).get().getPrimeRefIdx());
	}


	@Test
	void testPrimeDist() {
		Assertions.assertEquals(Optional.of(BigInteger.ONE), ps.getPrimeRef(BigInteger.TWO).get().getDistToNextPrime());
		Assertions.assertEquals(Optional.of(BigInteger.ONE.negate()), ps.getPrimeRef(BigInteger.TWO).get().getDistToPrevPrime());

		var bi7 = BigInteger.valueOf(7L);
		Assertions.assertEquals(Optional.of(BigInteger.valueOf(4L)), ps.getPrimeRef(bi7).get().getDistToNextPrime());
		Assertions.assertEquals(Optional.of(BigInteger.valueOf(2L).negate()), ps.getPrimeRef(bi7).get().getDistToPrevPrime());
	}

}
