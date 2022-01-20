package com.starcases.prime;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;
import java.util.Optional;
import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.starcases.prime.base.PrimeBaseWithLists;
import com.starcases.prime.impl.PrimeRef;
import com.starcases.prime.impl.PrimeSource;
import com.starcases.prime.intfc.PrimeRefIntfc;

import lombok.NonNull;

@ExtendWith(MockitoExtension.class)
class PrimeSourceUnitTest
{
	@NonNull
	private PrimeSource ps;

	@BeforeEach
	void init()
	{
		ps = new PrimeSource(120,100, null, PrimeRef::setPrimeSource, PrimeBaseWithLists::setPrimeSource);
		ps.init();
	}

	@Test
	void testGetPrimeIdx() {
		Assertions.assertEquals(1, ps.getPrimeIdx(BigInteger.TWO));

		var bi101 = BigInteger.valueOf(101L);
		Assertions.assertEquals(26, ps.getPrimeIdx(bi101));
	}

	@Test
	void testGetDistToNextPrime()
	{
		Assertions.assertEquals(BigInteger.valueOf(2L), ps.getDistToNextPrime(2));
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

		var bi101 = Optional.of(BigInteger.valueOf(101L));
		Assertions.assertEquals(bi101, ps.getPrime(26));
	}

	// @ Test
	void testGetPrimeRef() {
		fail("Not yet implemented");
	}

	@Test
	void testGetNearPrimeRef()
	{
		Assertions.assertEquals(BigInteger.valueOf(7L), ps.getNearPrimeRef(new BigDecimal("6.5"))         .get().getPrime());
		Assertions.assertEquals(BigInteger.valueOf(5L), ps.getNearPrimeRef(new BigDecimal("6.5").negate()).get().getPrime());

		Assertions.assertEquals(BigInteger.valueOf(11L), ps.getNearPrimeRef(new BigDecimal("10.5")         ).get().getPrime());
		Assertions.assertEquals(BigInteger.valueOf(7L),  ps.getNearPrimeRef(new BigDecimal("10.5").negate()).get().getPrime());
	}

	@Test
	void testGetPrimeRefWithinOffset()
	{
		Assertions.assertEquals(BigInteger.valueOf(11L), ps.getPrimeRefWithinOffset(4, BigInteger.valueOf(2)).get().getPrime());
		Assertions.assertEquals(BigInteger.valueOf(5L), ps.getPrimeRefWithinOffset(4, BigInteger.valueOf(-2)).get().getPrime());
	}

	@Test
	void testDistinct()
	{
		PrimeRefIntfc [] vals = {ps.getPrimeRef(BigInteger.ONE).get(), ps.getPrimeRef(BigInteger.TWO).get(), null};
		Assertions.assertTrue(ps.distinct(vals));

		PrimeRefIntfc [] vals1 = {ps.getPrimeRef(BigInteger.ONE).get(), null, null};
		Assertions.assertFalse(ps.distinct(vals1));

		PrimeRefIntfc [] vals2 = {ps.getPrimeRef(BigInteger.ONE).get(), ps.getPrimeRef(BigInteger.TWO).get(),ps.getPrimeRef(BigInteger.ONE).get()};
		Assertions.assertFalse(ps.distinct(vals2));
	}

}
