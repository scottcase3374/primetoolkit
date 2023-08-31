package com.starcases.prime.kern.impl;

import com.starcases.prime.kern.api.IdxToSubsetMapperIntfc;

import lombok.NonNull;

public class IdxToSubsetMapperImpl implements IdxToSubsetMapperIntfc
{
	/**
	 * Centralized the mapping from requested idx to subset/offset values.
	 *
	 * @param idx
	 * @param retSubset
	 * @param retOffset
	 */

	@Override
	public void convertIdxToSubsetAndOffset(final long idx, @NonNull final long [] retSubset, @NonNull final int [] retOffset)
	{
		final long subsetId = idx / SUBSET_SIZE;
		final int offset = (int)(idx % SUBSET_SIZE);

		retSubset[0] = subsetId;
		retOffset[0] = offset;
	}
}
