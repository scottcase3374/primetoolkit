package com.starcases.prime.intfc;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;

import com.starcases.prime.base.BaseTypes;

import lombok.NonNull;

public interface PrimeBaseIntfc
{
	@NonNull
	static final Comparator<BigInteger> bigIntComp = (b1, b2) -> b1.compareTo(b2);

	//
	// Prime base info
	//

	/**
	 * Not every use case needs multiple bases per prime; the interface
	 * supports it but it is only implemented to actually include
	 * multiple bases per prime in certain implementations.
	 * @return
	 */
	List<BitSet> getPrimeBaseIdxs();

	/**
	 * Not every use case needs multiple bases per prime; the interface
	 * supports it but it is only implemented to actually include
	 * multiple bases per prime in certain implementations.
	 * @return
	 */
	List<BitSet> getPrimeBaseIdxs(@NonNull BaseTypes baseType);

	BigInteger getMinPrimeBase();
	BigInteger getMaxPrimeBase();

	BigInteger getMinPrimeBase(@NonNull BaseTypes baseType);
	BigInteger getMaxPrimeBase(@NonNull BaseTypes baseType);

	int getBaseSize();

	/**
	 * Utility method - not useful in initial context but
	 * desirable for research into alternative bases of a
	 * prime.
	 *
	 * @param primeBase
	 */
	void addPrimeBase(@NonNull BitSet primeBase);
	void addPrimeBase(@NonNull BitSet primeBase, @NonNull BaseTypes baseType);
}
