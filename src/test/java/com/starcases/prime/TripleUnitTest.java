package com.starcases.prime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.starcases.prime.base.PrimeBaseContainer;
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

		PTKFactory.setBaseSetPrimeSource(PrimeRef::setPrimeSource);
		PTKFactory.setPrimeRefSetPrimeSource(PrimeRef::setPrimeSource);

		PTKFactory.setPrimeBaseCtor(PrimeBaseContainer::new);
		PTKFactory.setPrimeRefRawCtor( (i, base) -> (new PrimeRef(i)).init(PTKFactory.getPrimeBaseCtor(), base) );

		FactoryIntfc factory = PTKFactory.getFactory();
		ps = factory.getPrimeSource();
		ps.init();

	}

//	@Test
//	void testFindMatchOffset() {
//		Assertions.assertEquals(1, primeSrc.getPrimeIdx(BigInteger.TWO));
//
//		var bi37 = BigInteger.valueOf(37L);
//		var bi41 = BigInteger.valueOf(41L);
//		Assertions.assertEquals(1, primeSrc.getPrimeRef(bi41).get().getPrimeRefIdx()-primeSrc.getPrimeRef(bi37).get().getPrimeRefIdx());
//	}
}
