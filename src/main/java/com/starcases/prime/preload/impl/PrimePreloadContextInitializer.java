package com.starcases.prime.preload.impl;

import org.infinispan.protostream.SerializationContextInitializer;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

@AutoProtoSchemaBuilder(dependsOn = {org.infinispan.protostream.types.java.CommonTypes.class,
		org.infinispan.protostream.types.java.CommonContainerTypes.class},
		schemaFileName = "library.proto",
		schemaFilePath = "proto",

		schemaPackageName = "com.starcases.prime.preload.impl")
public interface PrimePreloadContextInitializer extends SerializationContextInitializer
{}
