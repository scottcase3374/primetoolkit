package com.starcases.prime;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.base.PrimeBaseWithLists;
import com.starcases.prime.impl.PrimeRef;
import com.starcases.prime.intfc.FactoryIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.NonNull;

@ExtendWith(MockitoExtension.class)
class PrimeSourceUnitTest
{
	@NonNull
	private PrimeSourceIntfc ps;

	@BeforeEach
	void init()
	{
		PTKFactory.setMaxCount(100);
		PTKFactory.setConfidenceLevel(100);

		PTKFactory.setActiveBaseId(BaseTypes.DEFAULT);
		PTKFactory.setBaseSetPrimeSource(PrimeBaseWithLists::setPrimeSource);
		PTKFactory.setPrimeRefSetPrimeSource(PrimeRef::setPrimeSource);

		PTKFactory.setPrimeBaseCtor(PrimeBaseWithLists::new);
		PTKFactory.setPrimeRefCtor( (i, base) -> (new PrimeRef(i)).init( base, PTKFactory.getPrimeBaseCtor() ));

		FactoryIntfc factory = PTKFactory.getFactory();
		ps = factory.getPrimeSource();
		ps.init();
	}

//	@Test
//	void testGetPrimeIdx() {
//		Assertions.assertEquals(1, ps.getPrimeIdx(BigInteger.TWO));
//
//		var bi101 = BigInteger.valueOf(101L);
//		Assertions.assertEquals(26, ps.getPrimeIdx(bi101));
//	}

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
}
