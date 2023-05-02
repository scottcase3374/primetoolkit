package com.starcases.prime.preload.impl;

import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

import lombok.Getter;

import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.SerializationContextInitializer;

/**
 * Container with configurable size for holding multiple prime/base items
 * as a group for storage in cache/external storage. Per item storage is
 * high at large scale so bundling items reduces the overhead.
 */
@AutoProtoSchemaBuilder(dependsOn = {org.infinispan.protostream.types.java.CommonTypes.class,
		org.infinispan.protostream.types.java.CommonContainerTypes.class},
		schemaFileName = "library.proto",
		schemaFilePath = "proto",
		schemaPackageName = "com.starcases.prime.proto")
public class PrimeSubset implements SerializationContextInitializer
{
	/**
	 * Array of entries bundled together
	 */
	@Getter
	@ProtoField(number = 1)
	protected long [] entries;

	/**
	 * Constructor for the container used for bundling prime/bases.
	 * @param entries
	 */
	@ProtoFactory
	public PrimeSubset(final long ... entries)
	{
		this.entries = entries.clone();
	}

	/**
	 * No arg constructor to appease code-analysis/metrics
	 */
	public PrimeSubset()
	{}

	/**
	 * Method for runtime allocation of the desired container size.
	 * @param subsetSize
	 */
	public void alloc(final int subsetSize)
	{
		entries = new long[subsetSize];
	}

	/**
	 * Assign prime/base to location in container
	 *
	 * @param offset
	 * @param val
	 */
	public void set(final int offset, final long val)
	{
		entries[offset] = val;
	}

	/**
	 * retrieve prime/base from container
	 * @param offset
	 * @return
	 */
	public long get(final int offset)
	{
		return entries[offset];
	}

	/**
	 * Overload for get proto file name - cache use.
	 */
	@Override
	public String getProtoFileName()
	{
		// Infinispan support generates the impl
		return null;
	}

	/**
	 * Override - cache use
	 */
	@Override
	public String getProtoFile() //throws UncheckedIOException
	{
		// Infinispan support generates the impl
		return null;
	}

	/**
	 * Override - cache use
	 */
	@Override
	public void registerSchema(final SerializationContext serCtx)
	{
		// Infinispan support generates the impl
	}

	/**
	 * Override - cache use
	 */
	@Override
	public void registerMarshallers(final SerializationContext serCtx)
	{
		// Infinispan support generates the impl
	}
}
