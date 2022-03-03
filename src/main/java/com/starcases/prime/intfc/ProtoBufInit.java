package com.starcases.prime.intfc;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.base.PrimeBaseWithLists;
import com.starcases.prime.impl.PrimeRef;

@AutoProtoSchemaBuilder(includeClasses =
{
		BaseTypes.class,
		PrimeBaseWithLists.class,
		PrimeRef.class
},
schemaFileName = "library.proto",
schemaFilePath = "proto",
schemaPackageName = "primeproto"
		)
public interface ProtoBufInit extends GeneratedSchema
{

}
