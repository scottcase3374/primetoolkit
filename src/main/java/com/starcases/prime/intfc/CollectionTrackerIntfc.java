package com.starcases.prime.intfc;

import java.math.BigInteger;
import java.util.Optional;
import java.util.Set;
import java.util.function.LongPredicate;

import com.starcases.prime.impl.PData;

public interface CollectionTrackerIntfc
{
	PData track(Set<BigInteger> collection);

	Optional<PData> select(LongPredicate pred);
}