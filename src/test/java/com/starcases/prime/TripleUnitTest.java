package com.starcases.prime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.starcases.prime.base.impl.PrimeMultiBaseContainer;
import com.starcases.prime.core.api.FactoryIntfc;
import com.starcases.prime.core.api.PrimeSourceFactoryIntfc;
import com.starcases.prime.core.impl.PrimeRef;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Testing triple bases
 */
@ExtendWith(MockitoExtension.class)
class TripleUnitTest
{
	/**
	 * Prime source for lookup of prime/primerefs
	 */
	@Getter
	@Setter
	@NonNull
	private PrimeSourceFactoryIntfc primeSrc;

	/**
	 * pre-Initialize test
	 */
	@BeforeEach
	public void init()
	{
		PTKFactory.setMaxCount(100);
		PTKFactory.setConfidenceLevel(100);

		PTKFactory.setBaseSetPrimeSource(PrimeRef::setPrimeSource);
		PTKFactory.setPrimeRefSetPrimeSource(PrimeRef::setPrimeSource);

		PTKFactory.setPrimeBaseCtor(PrimeMultiBaseContainer::new);
		PTKFactory.setPrimeRefRawCtor( (i, base) -> new PrimeRef(i).init(PTKFactory.getPrimeBaseCtor(), base) );

		final FactoryIntfc factory = PTKFactory.getFactory();
		primeSrc = factory.getPrimeSource();
		primeSrc.init();

	}
}
