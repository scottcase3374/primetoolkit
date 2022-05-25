package com.starcases.prime;

import org.junit.jupiter.api.BeforeEach;
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
 * Prime ref unit test
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
@ExtendWith(MockitoExtension.class)
class PrimeRefUnitTest
{
	/**
	 * prime source ref - lookup prime/prime refs
	 */
	@Getter
	@Setter
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
		PTKFactory.setPrimeRefRawCtor( (i, base) -> new PrimeRef(i).init(PTKFactory.getPrimeBaseCtor(), base) );

		final FactoryIntfc factory = PTKFactory.getFactory();
		primeSrc = factory.getPrimeSource();
		primeSrc.init();
	}
}
