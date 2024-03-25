package com.starcases.prime.kern.api;

import java.util.BitSet;

import lombok.NonNull;

public interface Permutation
{
	static final long [] masks = { 0xFFL, 0xFF00L, 0xFF000000L,  0xFF00000000000000L };
	static final int [] bitStart = {7, 15, 31, 63};
	static final int [] bitEnd = {0, 8, 16, 32};

	static void incrementPermutation(@NonNull final BitSet primePermutation)
	{
		var bit = 0;
		// Generate next permutation of the bit indexes
		for(;;)
		{
			// performs add and carry if needed
			primePermutation.flip(bit);

			if (primePermutation.get(bit++)) // true means no carry
			{
				break;
			}
		}
	}

	static int getNumBitsRequired(final long value)
	{
		var ret = 1;
		for (int idx = masks.length-1; idx >= 0 ; idx--)
		{
			if ((value & masks[idx]) > 0)
			{
				for(int bit = bitStart[idx] ; bit > bitEnd[idx]; bit--)
				{
					if ( (value & (1<<bit)) > 0)
					{
						ret = bit+1;
					}
				}
			}
		}
		return ret;
	}

	/**
	 * Bitset for # of bits equal to log-base-2 (+1) of the value.
	 *
	 * @param value
	 * @return
	 */
	static BitSet getBitsRequired(final long value)
	{
		var ret = 1;
		for (int idx = masks.length-1; idx >= 0 ; idx--)
		{
			if ((value & masks[idx]) > 0)
			{
				for(int bit = bitStart[idx] ; bit > bitEnd[idx]; bit--)
				{
					if ( (value & (1<<bit)) > 0)
					{
						ret = bit+1;
					}
				}
			}
		}
		return new BitSet(ret);
	}
}
