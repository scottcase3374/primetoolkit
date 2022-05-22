package com.starcases.prime.base.nprime;

import java.math.BigInteger;

import org.eclipse.collections.api.bag.sorted.MutableSortedBag;

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
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	private final MutableSortedBag<BigInteger> countForBaseIdx;

	public NPrimeBaseMetadata(@NonNull final MutableSortedBag<BigInteger> countForBaseIdx)
	{
		this.countForBaseIdx = countForBaseIdx;
	}
}
