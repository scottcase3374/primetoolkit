package com.starcases.prime.base.nprime_impl;

import org.eclipse.collections.api.bag.primitive.ImmutableLongBag;

import com.starcases.prime.base_api.BaseMetadataIntfc;

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
	private final ImmutableLongBag countForBaseIdx;

	/**
	 * Create the count metadata
	 * @param countForBaseIdx
	 */
	public NPrimeBaseMetadata(@NonNull final ImmutableLongBag countForBaseIdx)
	{
		this.countForBaseIdx = countForBaseIdx;
	}
}
