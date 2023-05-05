package com.starcases.prime.datamgmt.impl;

import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;

public record PData(ImmutableLongCollection toCanonicalCollection, long prime)
{

}