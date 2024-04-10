package com.starcases.prime.core.impl;

import java.time.LocalTime;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.concurrent.atomic.AtomicBoolean;

import com.starcases.prime.base.api.BaseGenIntfc;
import com.starcases.prime.core.api.PrimeRefFactoryIntfc;
import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.core.api.PrimeSourceFactoryIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.datamgmt.api.CollectionTrackerIntfc;
import com.starcases.prime.datamgmt.impl.PrimeRefIterator;
import com.starcases.prime.kern.api.StatusHandlerProviderIntfc;
import com.starcases.prime.kern.api.StatusHandlerIntfc;
import com.starcases.prime.service.impl.SvcLoader;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import jakarta.validation.constraints.Min;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.impl.parallel.ParallelIterate;
import org.mapdb.BTreeMap;


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
	private final  StatusHandlerIntfc statusHandler =
			new SvcLoader<StatusHandlerProviderIntfc, Class<StatusHandlerProviderIntfc>>(StatusHandlerProviderIntfc.class)
				.provider(Lists.immutable.of("STATUS_HANDLER")).orElseThrow().create();
	/**
	 * default logger
	 */
	@Getter(AccessLevel.PRIVATE)
	private static final Logger LOG = Logger.getLogger(PrimeSource.class.getName());

	//
	// Flags conveyed into this class
	//

	/**
	 * atomic flag preventing duplicate init
	 */
	@Getter(AccessLevel.PRIVATE)
	private final AtomicBoolean doInit = new AtomicBoolean(false);

	/**
	 * Flag indicating whether to currently log mismatch between prime/base
	 * found and the next actual prime in the sequence.
	 */
	@Getter(AccessLevel.PRIVATE)
	private final AtomicBoolean logMismatch = new AtomicBoolean(true);

	@Getter
	@Setter
	private boolean createBases;

	//
	// Context type info/code for prime/base creation that is conveyed into
	// this class
	//

	/**
	 * number of base primes to generate
	 */
	@Getter(AccessLevel.PRIVATE)
	private final long targetPrimeCount;

	/** can be overridden by passing
	* an appropriate "new" function ptr into the PrimeSource
	* constructor  -> param primeRefCtor
	*/
	@Getter(AccessLevel.PRIVATE)
	private final Function<Long, PrimeRefFactoryIntfc> primeRefRawCtor;

	//
	// Internal data used/generated during prime/base creation
	//

	private List<BaseGenIntfc> baseGenerators = Lists.mutable.empty();

	/**
	 * Map Index to prime.
	 */
	private final BTreeMap<Long, Long> primeMap;

	/**
	 * Multi-level container for the tree of primes - all in memory.
	 */
	@Getter(AccessLevel.PRIVATE)
	private CollectionTrackerIntfc collTracker;

	//
	// initialization
	//

	/**
	 * primary constructor of prime source - for lookups of prime/prime refs.
	 * @param maxCount
	 * @param consumersSetPrimeSrc
	 * @param confidenceLevel
	 * @param primeRefRawCtor
	 * @param collTrack
	 */
	public PrimeSource(
			@Min(1) final long maxCount,
			@NonNull final Iterable<Consumer<PrimeSourceIntfc>> consumersSetPrimeSrc,
			@NonNull final Function<Long, PrimeRefFactoryIntfc> primeRefRawCtor,
			final CollectionTrackerIntfc collTracker,
			BTreeMap<Long, Long> primeMap
			)
	{
		super();

		this.collTracker = collTracker;
		this.primeMap = primeMap;

		targetPrimeCount = maxCount;

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
	 * @param aPrime
	 */
	@Override
	public PrimeRefFactoryIntfc addPrimeRef(
			@Min(0) final long nextPrimeIdx,
			@Min(1) final long newPrime
			)
	{
		return primeRefRawCtor.apply(nextPrimeIdx);
	}

	private Consumer<PrimeRefFactoryIntfc> getBasesGenerator()
	{
		return pRef -> ParallelIterate.forEach(
							baseGenerators,
							gen -> gen.genBasesForPrimeRef(pRef) );
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
	 * Get highest prime ref less than specified value and prime.
	 *
	 */
	@Override
	public Optional<PrimeRefIntfc> getPrimeRefCeiling(final long value, @NonNull final PrimeRefIntfc highPrime)
	{
		if (value > highPrime.getPrime())
		{
			return Optional.empty();
		}

		Optional<PrimeRefIntfc> prime = getPrimeRefForPrime(value);
		if (prime.isEmpty())
		{
			Optional<PrimeRefIntfc> tmpPrime = highPrime.getPrevPrimeRef();
			while( tmpPrime.isPresent() && value > tmpPrime.get().getPrime())
			{
				tmpPrime = tmpPrime.get().getPrevPrimeRef();
			}
			prime = tmpPrime;
		}

		return prime;
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
		final var it = primeMap.entryIterator();
		boolean done=false;
		while (it.hasNext() && !done)
		{
			var entry = it.next();
			if (entry.getValue() == prime)
			{
				ret = getPrimeRefForIdx(entry.getKey());
				done=true;
			}
			else if (entry.getValue() > prime)
			{
				done=true;
			}
		}
		return  ret;
	}

	@Override
	public Optional<PrimeRefIntfc> getPrimeRefForPrime(@NonNull final LongSupplier longSupplier)
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
	public Stream<PrimeRefIntfc> getPrimeRefStream(@Min(1) final long skipCount, final boolean preferParallel)
	{
		final Iterator<PrimeRefIntfc> iter = getPrimeRefIter(skipCount-1);
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
		baseGenerators.forEach(bGen -> bGen.genBasesForPrimeRef(pRef) );

		var idx = pRef.getPrimeRefIdx();
		if (idx % 1_000_000 == 0)
		{
			statusHandler.dbgOutput("prime Idx %d at %s", idx, LocalTime.now().toString());
		}
	}
}
