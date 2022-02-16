package com.starcases.prime;

import java.math.BigInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.base.PrimeBaseWithBitsets;
import com.starcases.prime.impl.PrimeRef;
import com.starcases.prime.intfc.FactoryIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import lombok.NonNull;

@ExtendWith(MockitoExtension.class)
class TripleUnitTest
{
	@NonNull
	private PrimeSourceIntfc ps;

	@BeforeEach
	void init()
	{
		PTKFactory.setMaxCount(100);
		PTKFactory.setConfidenceLevel(100);

		PTKFactory.setActiveBaseId(BaseTypes.DEFAULT);
		PTKFactory.setBaseSetPrimeSource(PrimeRef::setPrimeSource);
		PTKFactory.setPrimeRefSetPrimeSource(PrimeRef::setPrimeSource);

		PTKFactory.setPrimeBaseCtor(PrimeBaseWithBitsets::new);
		PTKFactory.setPrimeRefCtor( (i, base) -> new PrimeRef(i, base, PTKFactory.getPrimeBaseCtor()) );

		FactoryIntfc factory = PTKFactory.getFactory();
		ps = factory.getPrimeSource();
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
