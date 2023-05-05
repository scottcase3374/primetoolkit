package com.starcases.prime.datamgmt.api;

import java.util.Optional;
import java.util.function.LongPredicate;

import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;

import com.starcases.prime.datamgmt.impl.CollectionTreeNode;
import com.starcases.prime.datamgmt.impl.PData;

public interface CollectionTrackerIntfc
{
	CollectionTreeIteratorIntfc iterator();
	Optional<PData> select(final LongPredicate pred);
	MutableLongObjectMap<CollectionTreeNode> getPrefixMap();
	Optional<PData> get(final long key);
	PData track(final ImmutableLongCollection collection);
	void log();
}