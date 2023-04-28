package com.starcases.prime.service_api;

import org.eclipse.collections.api.collection.ImmutableCollection;

@FunctionalInterface
public interface SvcProviderBaseIntfc
{
	int countAttributesMatch(final ImmutableCollection<String> attributes);
}
