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

	@Override
	public void increment( @NonNull final long [] retSubset, @NonNull final int [] retOffset)
	{
		if (retOffset[0] +1 < IdxToSubsetMapperImpl.SUBSET_SIZE)
		{
			retOffset[0]++;
		}
		else
		{
			retSubset[0]++;
			retOffset[0] = 0;
		}
	}

	@Override
	public void decrement( @NonNull final long [] retSubset, @NonNull final int [] retOffset)
	{
		if (retOffset[0] -1 < 0)
		{
			retSubset[0]--;
			retOffset[0] = IdxToSubsetMapperImpl.SUBSET_SIZE-1;
		}
		else
		{
			retOffset[0]--;
		}
	}
}
