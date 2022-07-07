package com.starcases.prime.base.nprime;

import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;

import com.starcases.prime.intfc.BaseMetadataIntfc;

import lombok.Getter;
import lombok.NonNull;

/**
 * container for meta data regarding a base. In this case,
 * for NPRIME style bases.
 */
public class NPrimeBaseMetadata implements BaseMetadataIntfc
{
	/**
	 * serial version id
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Track count for subset of bases
	 */
	@Getter
	private final ImmutableLongCollection countForBaseIdx;

	/**
	 * Create the count metadata
	 * @param countForBaseIdx
	 */
	public NPrimeBaseMetadata(@NonNull final ImmutableLongCollection countForBaseIdx)
	{
		this.countForBaseIdx = countForBaseIdx;
	}
}
