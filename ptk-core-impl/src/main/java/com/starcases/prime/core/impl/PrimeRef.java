package com.starcases.prime.core.impl;

import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

import com.starcases.prime.base.api.PrimeBaseIntfc;
import com.starcases.prime.base.impl.PrimeMultiBaseContainer;
import com.starcases.prime.core.api.PrimeRefFactoryIntfc;
import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.kern.impl.IdxToSubsetMapperImpl;

import lombok.NonNull;

/**
 * Default Prime representation.
 *
* The general algorithm idea is that the next Prime is derived from
* the sum of some subset of previous primes.
*
**/
public class PrimeRef implements PrimeRefFactoryIntfc
{
	private static final Logger LOG = Logger.getLogger(PrimeRef.class.getName());

	/**
	 * Access lookup for prime/primeRefs
	 */
	@NonNull
	private static PrimeSourceIntfc primeSrc;

	private static final IdxToSubsetMapperImpl idxToSubsetMapper = new IdxToSubsetMapperImpl();
	private final long subset;
	private final int offset;

	/**
	 * Base data
	 */
	private PrimeBaseIntfc primeBaseData = new PrimeMultiBaseContainer();

	/**
	 * Handle simple Prime where the base is simply itself - i.e. 1, 2
	 * Simplifies bootstrapping
	 *
	 * @param Prime
	 */
	public PrimeRef(final long primeIdx)
	{
		final long [] subsetLocal = { -1 };
		final int [] offsetLocal = { -1 };
		idxToSubsetMapper.convertIdxToSubsetAndOffset(primeIdx, subsetLocal, offsetLocal);
		subset = subsetLocal[0];
		offset = offsetLocal[0];
	}

	public PrimeRef(
			final long primeIdx
			,@NonNull final Supplier<PrimeBaseIntfc> primeBaseSupplier
		)
	{
		this(primeIdx);
		primeBaseData = primeBaseSupplier.get();
	}

	public PrimeRef(final long subset, final int offset)
	{
		this.subset = subset;
		this.offset = offset;
	}

	public PrimeRef(
				final long subset
				, final int offset
				,@NonNull final Supplier<PrimeBaseIntfc> primeBaseSupplier
			)
	{
		this(subset, offset);
		primeBaseData = primeBaseSupplier.get();
	}

	/**
	 * alt constructor
	 * @param primeBaseSupplier
	 * @param primeBases
	 * @return
	 */
	@Override
	public PrimeRefFactoryIntfc init(
			 @NonNull final Supplier<PrimeBaseIntfc> primeBaseSupplier
			)
	{
		LOG.fine("****** PrimeRef::init ******");
		primeBaseData = primeBaseSupplier.get();
		return this;
	}

	@Override
	public PrimeRefFactoryIntfc generateBases(@NonNull final Consumer<PrimeRefFactoryIntfc> basesGenerate)
	{
		basesGenerate.accept(this);
		return this;
	}

	@Override
	public PrimeBaseIntfc getPrimeBaseData()
	{
		return primeBaseData;
	}

	/**
	 * Assign the prime source reference for performing
	 * prime/primeref lookups.
	 *
	 * @param primeSrcIntfc
	 */
	public static void setPrimeSource(@NonNull final PrimeSourceIntfc primeSrcIntfc)
	{
		primeSrc = primeSrcIntfc;
	}

	@Override
	public Optional<PrimeRefIntfc> getNextPrimeRef()
	{
		final int [] offsetLocal = {offset};
		final long [] subsetLocal = {subset};
		idxToSubsetMapper.increment(subsetLocal, offsetLocal);
		return primeSrc.getPrimeRefForIdx(subsetLocal[0], offsetLocal[0]);
	}

	@Override
	public Optional<PrimeRefIntfc> getPrevPrimeRef()
	{
		final int[] offsetLocal = {offset};
		final long [] subsetLocal = {subset};

		idxToSubsetMapper.decrement(subsetLocal, offsetLocal);
		return subset < 0 ? Optional.empty() : primeSrc.getPrimeRefForIdx(subsetLocal[0], offsetLocal[0]);
	}

	@Override
	public boolean hasNext()
	{
		final int[] offsetLocal = {offset};
		final long [] subsetLocal = {subset};

		idxToSubsetMapper.increment(subsetLocal, offsetLocal);
		return primeSrc.getPrimeRefForIdx(subsetLocal[0], offsetLocal[0]).isPresent();
	}

	@Override
	public boolean hasPrev()
	{
		final int[] offsetLocal = {offset};
		final long [] subsetLocal = {subset};

		idxToSubsetMapper.decrement(subsetLocal, offsetLocal);
		return subsetLocal[0] >= 0 && primeSrc.getPrimeRefForIdx(subsetLocal[0], offsetLocal[0]).isPresent();
	}

	@Override
	public long getPrime()
	{
		return primeSrc.getPrimeForIdx(subset, offset)
				.orElseThrow();
	}

	@Override
	public long getPrimeRefIdx()
	{
		return  subset * IdxToSubsetMapperImpl.SUBSET_SIZE + offset;
	}

	/**
	 * absolute value of difference with next Prime
	 * if the next Prime is known.
	 *
	 * empty optional if next Prime is unknown/not calculated
	 */
	@Override
	public OptionalLong getDistToNextPrime()
	{
		final int[] offsetLocal = {offset};
		final long [] subsetLocal = {subset};

		idxToSubsetMapper.increment(subsetLocal, offsetLocal);
		final var result = primeSrc.getPrimeRefForIdx(subsetLocal[0], offsetLocal[0]);

		return  result.isPresent() ? OptionalLong.of(result.get().getPrime() - getPrime()) : OptionalLong.empty();
	}

	/**
	 * absolute value of difference with prev Prime
	 * if the prev Prime is known/exists.
	 *
	 * empty optional if prev Prime is unknown/doesn't exist
	 */
	@Override
	public OptionalLong getDistToPrevPrime()
	{
		final int[] offsetLocal = {offset};
		final long [] subsetLocal = {subset};

		idxToSubsetMapper.decrement(subsetLocal, offsetLocal);
		final var result =  primeSrc.getPrimeRefForIdx(subsetLocal[0], offsetLocal[0]);

		return result.isPresent() ? OptionalLong.of(result.get().getPrime() - getPrime()) : OptionalLong.empty();
	}

	@Override
	public String toString()
	{
		return Long.toString(this.getPrime());
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + offset;
		result = prime * result + (int)subset;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PrimeRef other = (PrimeRef) obj;
		return offset == other.offset && subset == other.subset;
	}
}