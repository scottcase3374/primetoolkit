package com.starcases.prime.service.api;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.collections.api.collection.ImmutableCollection;

import lombok.NonNull;

public interface SvcProviderBaseIntfc
{
	Logger LOG = Logger.getLogger(SvcProviderBaseIntfc.class.getName());

	default int countAttributesMatch(final ImmutableCollection<String> attributes)
	{
		int ret = 0;
		if (attributes.containsAllIterable(getProviderAttributes()))
		{
			ret = getProviderAttributes().size();
		}

		logMatchData(attributes, ret);
		return ret;
	}

	ImmutableCollection<String> getProviderAttributes();

	default void logMatchData(@NonNull final ImmutableCollection<String> attributes, final int matchedItems)
	{
		if (LOG.isLoggable(Level.FINE))
		{
			LOG.fine(String.format("request attributes: %s provider attributes: %s  matched: %d",
					attributes.makeString(),
					getProviderAttributes().makeString(),
					matchedItems));
		}
	}
}
