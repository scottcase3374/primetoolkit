package com.starcases.prime.service.api;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.collections.api.collection.ImmutableCollection;

import lombok.NonNull;

public interface SvcProviderBaseIntfc
{
	/**
	 * Logger for use during service creation - log provider and service request attributes and match-data.
	 */
	Logger LOG = Logger.getLogger(SvcProviderBaseIntfc.class.getName());

	default int countAttributesMatch(final ImmutableCollection<String> attributes)
	{

		final var pa = getProviderAttributes();
		final int ret = attributes.count(pa::contains);

		logMatchData(attributes, ret);
		return ret;
	}

	/**
	 * Get the provider attributes.
	 *
	 * @return
	 */
	ImmutableCollection<String> getProviderAttributes();

	/**
	 * Log attribute/match data during provider selection.
	 *
	 * @param attributes
	 * @param matchedItems
	 */
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
