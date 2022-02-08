package com.starcases.prime.base.nprime;

import java.util.List;

import com.starcases.prime.intfc.BaseMetadataIntfc;

import lombok.Getter;
import lombok.NonNull;

public class NPrimeBaseMetadata implements BaseMetadataIntfc
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	private final List<Integer> countForBaseIdx;

	public NPrimeBaseMetadata(@NonNull List<Integer> countForBaseIdx)
	{
		this.countForBaseIdx = countForBaseIdx;
	}
}
