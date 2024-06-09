package com.starcases.prime.core.impl;

import com.starcases.prime.base.api.BaseGenIntfc;
import com.starcases.prime.core.api.PrimeRefFactoryIntfc;
import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.core.api.PrimeSourceFactoryIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.datamgmt.api.CollectionTrackerIntfc;
import com.starcases.prime.datamgmt.impl.PrimeRefIterator;
import com.starcases.prime.kern.api.StatusHandlerIntfc;
import com.starcases.prime.kern.api.StatusHandlerProviderIntfc;
import com.starcases.prime.service.impl.SvcLoader;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.eclipse.collections.api.factory.Lists;
import org.mapdb.BTreeMap;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.Stream;


/**
 * Provides data structure holding the core data and objects that provide
 * access to the data from the
 * "public interface for primes". Also some utility support to support
 *  alternative algs for finding primes, working with bases of the primes,
 *  and general information/access.
 *
 */
public class PrimeSource implements PrimeSourceFactoryIntfc
{
	private static final  ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();

	private final  StatusHandlerIntfc statusHandler =
			new SvcLoader<StatusHandlerProviderIntfc, Class<StatusHandlerProviderIntfc>>(StatusHandlerProviderIntfc.class)
				.provider(Lists.immutable.of("STATUS_HANDLER")).orElseThrow().create();

	//
	// Flags conveyed into this class
	//

	/**
	 * atomic flag preventing duplicate init
	 */
	private final AtomicBoolean doInit = new AtomicBoolean(false);

	private boolean createBases;

	//
	// Context type info/code for prime/base creation that is conveyed into
	// this class
	//

	/** can be overridden by passing
	* an appropriate "new" function ptr into the PrimeSource
	* constructor  -> param primeRefCtor
	*/
	private final Function<Long, PrimeRefFactoryIntfc> primeRefRawCtor;

	//
	// Internal data used/generated during prime/base creation
	//

	private final List<BaseGenIntfc> baseGenerators = Lists.mutable.empty();

	/**
	 * Map Index to prime.
	 */
	private final BTreeMap<Long, Long> primeMap;

	/**
	 * Map prime to Index.
	 */
	private final BTreeMap<Long, Long> primeToIdxMap;

	/**
	 * Multi-level container for the tree of primes - all in memory.
	 */
	private final CollectionTrackerIntfc collTracker;

	//
	// initialization
	//

	/**
	 * primary constructor of prime source - for lookups of prime/prime refs.
	 * @param consumersSetPrimeSrc
	 * @param primeRefRawCtor
	 * @param collTracker
	 */
	public PrimeSource(
			@NotNull final Iterable<Consumer<PrimeSourceIntfc>> consumersSetPrimeSrc,
			@NotNull final Function<Long, PrimeRefFactoryIntfc> primeRefRawCtor,
			final CollectionTrackerIntfc collTracker,
			final BTreeMap<Long, Long> primeMap,
			final BTreeMap<Long, Long> idxToPrimeMap
			)
	{
		super();

		this.collTracker = collTracker;
		this.primeMap = primeMap;
		this.primeToIdxMap = idxToPrimeMap;

		this.primeRefRawCtor = primeRefRawCtor;
		consumersSetPrimeSrc
			.forEach(c ->
					{
						if (c != null)
						{
							c.accept(this);
						}
					}
						);
	}

	/**
	 * Add new Prime to shared set of all primes
	 *
	 * @param newPrime
	 */
	@Override
	public PrimeRefFactoryIntfc addPrimeRef(
			@Min(0) final long nextPrimeIdx,
			@Min(1) final long newPrime
			)
	{
		primeToIdxMap.putIfAbsent(newPrime, nextPrimeIdx);
		primeMap.putIfAbsent(nextPrimeIdx, newPrime);
		return primeRefRawCtor.apply(nextPrimeIdx);
	}

	/**
	 * Used to create initial mapping which wasn't done at the time of
	 * the prime map creation.
	 */
	public void initIdxToPrime()
	{
		if (primeMap.get(0L) != null)
		{
			for (long idx=0; idx <=50_000_000; idx++)
			{
				primeToIdxMap.putIfAbsent(primeMap.get(idx), idx);
				if (idx % 1_000_000 == 0)
				{
					statusHandler.dbgOutput(String.format("idxtoprime load: %d  prime %d", idx, primeToIdxMap.get(primeMap.get(idx))));
				}
			}
		}
	}

	@Override
	public OptionalLong getPrimeForIdx(@Min(0) final long primeIdx)
	{
		var tmp = primeMap.get(primeIdx);
		return null != tmp ? OptionalLong.of(tmp) : OptionalLong.empty();
	}

	@Override
	public Optional<PrimeRefIntfc> getPrimeRefForIdx(@Min(0) final long primeIdx)
	{
		Optional<PrimeRefIntfc> ret = Optional.empty();
		if (primeMap.containsKey(primeIdx))
		{
			 ret = Optional.of(new PrimeRef(primeIdx));
		}
		return ret;
	}

	/**
	 * Get the highest prime ref less than specified value.
	 *
	 */
	@Override
	public Optional<PrimeRefIntfc> getPrimeRefCeiling(final long value)
	{
		Optional<PrimeRefIntfc> ret = Optional.empty();

		var entry = primeToIdxMap.findLower(value, true);
		if (entry != null)
		{
			ret = this.getPrimeRefForIdx(entry.getValue());
		}
		return ret;
	}

	/**
	 * TODO Do I really need this method?
	 *
	 * @param possiblePrime
	 * @return
	 */
	@Override
	public long searchPrime(final long possiblePrime)
	{
		long ret = -1;
		final var it = primeMap.entryIterator();
		boolean done=false;
		while (it.hasNext() && !done)
		{
			var entry = it.next();
			if (entry.getValue() == possiblePrime)
			{
				ret = entry.getKey();
				done=true;
			}
			else if (entry.getValue() > possiblePrime)
			{
				done=true;
			}
		}

		return ret;
	}

	@Override
	public Optional<PrimeRefIntfc> getPrimeRefForPrime(@Min(0) final long prime)
	{
		Optional<PrimeRefIntfc> ret = Optional.empty();
		final Long primeIdx = primeToIdxMap.get(prime);
		if (primeIdx != null)
		{
			ret = getPrimeRefForIdx(primeIdx);
		}
		return  ret;
	}

	@Override
	public Optional<PrimeRefIntfc> getPrimeRefForPrime(@NotNull final LongSupplier longSupplier)
	{
		final long prime = longSupplier.getAsLong();
		return this.getPrimeRefForPrime(prime);
	}

	@Override
	public Iterator<PrimeRefIntfc> getPrimeRefIter()
	{
		return new PrimeRefIterator<>(new PrimeRef(0));
	}

	@Override
	public Iterator<PrimeRefIntfc> getPrimeRefIter(final long idx)
	{
		return new PrimeRefIterator<>(new PrimeRef(idx));
	}

	@Override
	public Stream<PrimeRefIntfc> getPrimeRefStream(final boolean preferParallel)
	{
		return getPrimeRefStream(0, preferParallel);
	}

	@Override
	public Stream<PrimeRefIntfc> getPrimeRefStream(@Min(0) final long skipCount, final boolean preferParallel)
	{
		final Iterator<PrimeRefIntfc> iter = getPrimeRefIter(skipCount);
		final Supplier<PrimeRefIntfc> supplier = () -> { var it = iter; return it.hasNext() ? it.next() : null; };
		return Stream.generate(supplier).takeWhile( Objects::nonNull);
	}

	@Override
	public Iterator<PrimeRefFactoryIntfc> getPrimeFactoryRefIter()
	{
		return new PrimeRefIterator<>(new PrimeRef(0));
	}

	@Override
	public Iterator<PrimeRefFactoryIntfc> getPrimeFactoryRefIter(@Min(0) final long idx)
	{
		return new PrimeRefIterator<>(new PrimeRef(idx));
	}

	@Override
	public Stream<PrimeRefFactoryIntfc> getPrimeFactoryRefStream(final boolean preferParallel)
	{
		return getPrimeFactoryRefStream(0, preferParallel);
	}

	@Override
	public Stream<PrimeRefFactoryIntfc> getPrimeFactoryRefStream(@Min(0) final long skipCount, final boolean preferParallel)
	{
		final Iterator<PrimeRefFactoryIntfc> iter = getPrimeFactoryRefIter(skipCount);
		final Supplier<PrimeRefFactoryIntfc> supplier = () -> { var it = iter; return it.hasNext() ? it.next() : null; };
		return Stream.generate(supplier).takeWhile( Objects::nonNull);
	}

	@Override
	public void init()
	{

		if (doInit.compareAndExchangeAcquire(false, true))
		{
			statusHandler.dbgOutput("PrimeSource::init - skipping redundant init.");
			// Prevent double init - various valid combinations of code can attempt that.
			return;
		}
		else
		{
			statusHandler.dbgOutput("PrimeSource::init");
		}

		if (!primeToIdxMap.containsKey(0L))
		{
			initIdxToPrime();
		}
		if (createBases)
		{
			statusHandler.dbgOutput("PrimeSource::init - generating bases.");
			getPrimeFactoryRefStream(false).forEach(this::generateBases);
		}
	}

	@Override
	public void addBaseGenerator(final BaseGenIntfc baseGenerator)
	{
		statusHandler.output("adding baseGenerator %s ", baseGenerator.getBaseType());
		baseGenerators.add(baseGenerator);
	}

	@Override
	public void generateBases(final PrimeRefFactoryIntfc pRef)
	{
		baseGenerators.forEach(bGen -> pool.execute( ()	->	bGen.genBasesForPrimeRef(pRef)	));

		var idx = pRef.getPrimeRefIdx();
		if (idx % 1_000_000 == 0)
		{
			statusHandler.output("prime Idx %d", idx);
		}
	}

	@Override
	public void setCreateBases(boolean createBases) {
		this.createBases = createBases;
	}
}
