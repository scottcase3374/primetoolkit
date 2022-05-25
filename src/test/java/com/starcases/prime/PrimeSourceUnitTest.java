package com.starcases.prime;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;
import java.util.Optional;

import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.starcases.prime.base.PrimeBaseContainer;
import com.starcases.prime.impl.PrimeRef;
import com.starcases.prime.intfc.FactoryIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Prime source unit tests
 * Lookup prime / prime refs.
 */
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.CommentDefaultAccessModifier"})
@ExtendWith(MockitoExtension.class)
class PrimeSourceUnitTest
{
	/**
	 * Prime source ref - lookup prime/prime refs.
	 */
	@Setter
	@Getter
	@NonNull
	private PrimeSourceIntfc primeSrc;

	/**
	 * unit test pre-init
	 */
	@BeforeEach
	public void init()
	{
		PTKFactory.setMaxCount(100);
		PTKFactory.setConfidenceLevel(100);

		PTKFactory.setBaseSetPrimeSource(PrimeBaseContainer::setPrimeSource);
		PTKFactory.setPrimeRefSetPrimeSource(PrimeRef::setPrimeSource);

		PTKFactory.setPrimeBaseCtor(PrimeBaseContainer::new);
		PTKFactory.setPrimeRefRawCtor( (i, base) -> new PrimeRef(i).init(PTKFactory.getPrimeBaseCtor(), base ));

		final FactoryIntfc factory = PTKFactory.getFactory();
		primeSrc = factory.getPrimeSource();
		primeSrc.init();
	}

	/**
	 * Unit test - unused
	 */
	@Ignore
	@Test
	void checkGetNextLowPrimeBigInteger()
	{
		fail("Not yet implemented");
	}

	/**
	 * Unit test - unused
	 */
	@Ignore
	@Test
	void testGetNextHighPrimeBigInteger()
	{
		fail("Not yet implemented");
	}

	/**
	 * Unit test - get prime
	 */
	@Test
	void canGetPrime()
	{
		final var bi101 = Optional.of(BigInteger.valueOf(101L));
		assertEquals(bi101, primeSrc.getPrime(26));
	}

	/**
	 * unit test - unused
	 */
	@Ignore
	@Test
	void testGetPrimeRef()
	{
		fail("Not yet implemented");
	}
}
