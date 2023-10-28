package com.starcases.prime.kern.api;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

public interface IdxToSubsetMapperIntfc
{
	/**
	 * Define the number of bits to batch together to reduce the number of
	 * idxToPrimeCache accesses.  This is to reduce the space overhead which is
	 * significant at large scale and to a lesser extent reduce time
	 * spent in idxToPrimeCache activities.
	 */
	@Getter(AccessLevel.PRIVATE)
	static int SUBSET_BITS = 17;

	/**
	 * Convert the number of bits into the size of an array for the
	 * caching.
	 */
	@Getter(AccessLevel.PRIVATE)
	static int SUBSET_SIZE = 1 << SUBSET_BITS;

	/**
	 *
	 * @param idx
	 * @param retSubset
	 * @param retOffset
	 */
	void convertIdxToSubsetAndOffset(final long idx, @NonNull final long [] retSubset, @NonNull final int [] retOffset);

	void increment( @NonNull final long [] retSubset, @NonNull final int [] retOffset);
	void decrement( @NonNull final long [] retSubset, @NonNull final int [] retOffset);
}
