package com.starcases.prime.cache.impl.subset;

import java.lang.reflect.Array;

import com.starcases.prime.cache.api.subset.SubsetIntfc;

import lombok.Getter;
import lombok.NonNull;

/**
 * Container with configurable size for holding multiple prime/base items
 * as a group for storage in cache/external storage. Per item storage is
 * high at large scale so bundling items reduces the overhead.
 */

public class Subset<K> implements SubsetIntfc<K>
{
	private final Class<K> classK;

	/**
	 * Array of entries bundled together
	 */
	@Getter
	protected K [] entries;

	@Getter
	protected int maxOffsetAssigned = -1;

	/**
	 * Constructor for the container used for bundling prime/bases.
	 * @param entries
	 */
	public Subset(@NonNull final Class<K> classK, final K ... entries)
	{
		this.entries = entries.clone();
		this.classK = classK;
		for (int i=entries.length-1; i>=0; i--)
		{
			if (entries[i] != null)
			{
				maxOffsetAssigned = i;
				break;
			}
		}
	}

	/**
	 * Constructor for the container used for bundling prime/bases.
	 * @param entries
	 */
	public Subset(@NonNull final Class<K> classK, final int count)
	{
		this.classK = classK;
		alloc(count);
	}

	/**
	 * Method for runtime allocation of the desired container size.
	 * @param subsetSize
	 */
	@Override
	public void alloc(final int subsetSize)
	{
		entries = (K[])Array.newInstance(classK, subsetSize);
	}

	/**
	 * Assign prime/base to location in container
	 *
	 * @param offset
	 * @param val
	 */
	@Override
	public void set(final int offset, final K val)
	{
		entries[offset] = val;
		maxOffsetAssigned = offset;
	}

	/**
	 * retrieve prime/base from container
	 * @param offset
	 * @return
	 */
	@Override
	public K get(final int offset)
	{
		K ret = null;
		if (offset <= maxOffsetAssigned)
		{
			ret = entries[offset];
		}
		return ret;
	}

	@Override
	public String toString()
	{
		return String.format("entry-storage:[%d] max-offset-assigned[%d]", entries.length, maxOffsetAssigned);
	}

	/**
	 * Returns count of entries-1
	 */
	@Override
	public int getMaxOffset()
	{
		return entries.length-1;
	}
}
