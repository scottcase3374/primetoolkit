package com.starcases.prime.core_impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.starcases.prime.PTKFactory;
import com.starcases.prime.base.impl.PrimeMultiBaseContainer;
import com.starcases.prime.core.api.FactoryIntfc;
import com.starcases.prime.core.api.PrimeSourceFactoryIntfc;
import com.starcases.prime.core.impl.PrimeRef;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Prime ref unit test
 */
@ExtendWith(MockitoExtension.class)
class PrimeRefUnitTest
{
	/**
	 * prime source ref - lookup prime/prime refs
	 */
	@Getter
	@Setter
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
		PTKFactory.setPrimeRefRawCtor( (i, base) -> new PrimeRef(i).init(PTKFactory.getPrimeBaseCtor(), base) );

		final FactoryIntfc factory = PTKFactory.getFactory();
		primeSrc = factory.getPrimeSource();
		primeSrc.init();
	}
}
