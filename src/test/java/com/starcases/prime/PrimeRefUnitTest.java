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
class PrimeRefUnitTest
{
	@NonNull
	private PrimeSourceIntfc ps;

	@BeforeEach
	void init()
	{
		PTKFactory.setMaxCount(100);
		PTKFactory.setConfidenceLevel(100);

		PTKFactory.setBaseSetPrimeSource(PrimeBaseContainer::setPrimeSource);
		PTKFactory.setPrimeRefSetPrimeSource(PrimeRef::setPrimeSource);

		PTKFactory.setPrimeBaseCtor(PrimeBaseContainer::new);
		PTKFactory.setPrimeRefRawCtor( (i, base) -> (new PrimeRef(i)).init(PTKFactory.getPrimeBaseCtor(), base) );

		FactoryIntfc factory = PTKFactory.getFactory();
		ps = factory.getPrimeSource();
		ps.init();
	}

//	@Test
//	void testPrimeIdxDiff() {
//		Assertions.assertEquals(1, primeSrc.getPrimeIdx(BigInteger.TWO));
//
//		var bi5 = BigInteger.valueOf(5L);
//		var bi3 = BigInteger.valueOf(3L);
//		Assertions.assertEquals(1, primeSrc.getPrimeRef(bi5).get().getPrimeRefIdx()-primeSrc.getPrimeRef(bi3).get().getPrimeRefIdx());
//	}


//	@Test
//	void testPrimeDist() {
//		Assertions.assertEquals(Optional.of(BigInteger.ONE), primeSrc.getPrimeRef(BigInteger.TWO).get().getDistToNextPrime());
//		Assertions.assertEquals(Optional.of(BigInteger.ONE.negate()), primeSrc.getPrimeRef(BigInteger.TWO).get().getDistToPrevPrime());
//
//		var bi7 = BigInteger.valueOf(7L);
//		Assertions.assertEquals(Optional.of(BigInteger.valueOf(4L)), primeSrc.getPrimeRef(bi7).get().getDistToNextPrime());
//		Assertions.assertEquals(Optional.of(BigInteger.valueOf(2L).negate()), primeSrc.getPrimeRef(bi7).get().getDistToPrevPrime());
//	}

}
