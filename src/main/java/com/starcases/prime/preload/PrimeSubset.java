package com.starcases.prime.preload;

import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

import java.io.UncheckedIOException;

import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.SerializationContextInitializer;

@AutoProtoSchemaBuilder(dependsOn = {org.infinispan.protostream.types.java.CommonTypes.class,
		org.infinispan.protostream.types.java.CommonContainerTypes.class},
		schemaFileName = "library.proto",
		schemaFilePath = "proto",
		schemaPackageName = "com.starcases.prime.proto")
public class PrimeSubset implements SerializationContextInitializer
{
	@ProtoField(number = 1)
	long [] entries;

	@ProtoFactory
	PrimeSubset(long [] entries)
	{
		this.entries = entries;
	}

	public PrimeSubset()
	{}

	public void alloc(final int subsetSize)
	{
		entries = new long[subsetSize];
	}

	public void set(int offset, long val)
	{
		entries[offset] = val;
	}

	public long get(int offset)
	{
		return entries[offset];
	}

	@Override
	public String getProtoFileName()
	{
		return null;
	}

	@Override
	public String getProtoFile() throws UncheckedIOException
	{
		return null;
	}

	@Override
	public void registerSchema(SerializationContext serCtx)
	{
	}

	@Override
	public void registerMarshallers(SerializationContext serCtx)
	{
	}
}
