package com.starcases.prime.core_impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.starcases.prime.PTKFactory;
import com.starcases.prime.base.impl.PrimeMultiBaseContainer;
import com.starcases.prime.core.api.FactoryIntfc;
import com.starcases.prime.core.api.PrimeSourceFactoryIntfc;
import com.starcases.prime.core.impl.PrimeRef;
import com.starcases.prime.core.impl.PrimeSource;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Prime source unit tests
 * Lookup prime / prime refs.
 */
@SuppressWarnings({"PMD.CommentDefaultAccessModifier"})
@ExtendWith(MockitoExtension.class)
class PrimeSourceUnitTest
{
	/**
	 * Prime source ref - lookup prime/prime refs.
	 */
	@Setter
	@Getter
	@NonNull
	private PrimeSourceFactoryIntfc primeSrc;

	/**
	 * unit test pre-init
	 */
	@BeforeEach
	public void init()
	{
		PTKFactory.setMaxCount(100);
		PTKFactory.setConfidenceLevel(100);

		PTKFactory.setBaseSetPrimeSource(PrimeMultiBaseContainer::setPrimeSource);
		PTKFactory.setPrimeRefSetPrimeSource(PrimeRef::setPrimeSource);

		PTKFactory.setPrimeBaseCtor(PrimeMultiBaseContainer::new);
		PTKFactory.setPrimeRefRawCtor( (i, base) -> new PrimeRef(i).init(PTKFactory.getPrimeBaseCtor(), base ));

		final FactoryIntfc factory = PTKFactory.getFactory();
		primeSrc = factory.getPrimeSource();
		primeSrc.init();
	}

	@Test
	void checkGetBitsRequired()
	{
		assertEquals(13, Math.log(0x01000) / Math.log(2) + 1, "Math - base2 + 1 should be 13");
		assertEquals(13, ((PrimeSource)primeSrc).getBitsRequired(0x01000), "Value should require 13 bits.");
	}


	/**
	 * Unit test - get prime
	 */
	@Test
	void canGetPrime()
	{
		final var bi101 = 101L;
		assertEquals(bi101, primeSrc.getPrimeForIdx(26).getAsLong(), "Prime at index 26 should be 101.");
	}

	/**
	 * unit test - unused
	 */
	@Disabled
	@Test
	void testGetPrimeRef()
	{
		fail("Not yet implemented");
	}
}
