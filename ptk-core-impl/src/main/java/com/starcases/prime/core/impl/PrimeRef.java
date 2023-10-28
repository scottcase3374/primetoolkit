package com.starcases.prime.core.impl;

import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

import com.starcases.prime.base.api.PrimeBaseIntfc;
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
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class PrimeRef implements PrimeRefFactoryIntfc
{
	private static final Logger LOG = Logger.getLogger(PrimeRef.class.getName());

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Access lookup for prime/primeRefs
	 */
	@NonNull
	private static PrimeSourceIntfc primeSrc;

	private final static IdxToSubsetMapperImpl idxToSubsetMapper = new IdxToSubsetMapperImpl();
	private final long subset;
	private final int offset;

	/**
	 * Base data
	 */
	private PrimeBaseIntfc primeBaseData;

	/**
	 * Handle simple Prime where the base is simply itself - i.e. 1, 2
	 * Simplifies bootstrapping
	 *
	 * @param Prime
	 */
	public PrimeRef(final long primeIdx)
	{
		long subset_local[] = {-1 };
		int offset_local[] = {-1};
		idxToSubsetMapper.convertIdxToSubsetAndOffset(primeIdx, subset_local, offset_local);
		this.subset = subset_local[0];
		this.offset = offset_local[0];
	}

	public PrimeRef(final long subset, final int offset)
	{
		this.subset = subset;
		this.offset = offset;
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
		if (primeBaseData == null)
		{
			LOG.info("Prime base data is null :  returning null from getPrimeBaseData()");
		}

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
		var offset_local = offset;
		var subset_local = subset;
		if (offset_local+1 < IdxToSubsetMapperImpl.SUBSET_SIZE)
		{
			offset_local++;
		}
		else
		{
			subset_local++;
			offset_local = 0;
		}

		final var ret = primeSrc.getPrimeRefForIdx(subset_local, offset_local);
		return ret;
	}

	@Override
	public Optional<PrimeRefIntfc> getPrevPrimeRef()
	{
		var offset_local = offset;
		var subset_local = subset;

		if (offset_local -1 < 0)
		{
			subset_local--;
			offset_local = IdxToSubsetMapperImpl.SUBSET_SIZE-1;
		}
		else
		{
			offset_local--;
		}
		return subset < 0 ? Optional.empty() : primeSrc.getPrimeRefForIdx(subset_local, offset_local);
	}

	@Override
	public boolean hasNext()
	{
		var offset_local = offset;
		var subset_local = subset;
		if (offset_local+1 < IdxToSubsetMapperImpl.SUBSET_SIZE)
		{
			offset_local++;
		}
		else
		{
			subset_local++;
			offset_local = 0;
		}

		return primeSrc.getPrimeRefForIdx(subset_local, offset_local).isPresent();
	}

	@Override
	public boolean hasPrev()
	{
		var offset_local = offset;
		var subset_local = subset;

		if (offset_local -1 < 0)
		{
			subset_local--;
			offset_local = IdxToSubsetMapperImpl.SUBSET_SIZE-1;
		}
		else
		{
			offset_local--;
		}
		return subset_local < 0 ? false : primeSrc.getPrimeRefForIdx(subset_local, offset_local).isPresent();
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
		var offset_local = offset;
		var subset_local = subset;
		if (offset_local+1 < IdxToSubsetMapperImpl.SUBSET_SIZE)
		{
			offset_local++;
		}
		else
		{
			subset_local++;
			offset_local = 0;
		}

		final var result = primeSrc.getPrimeRefForIdx(subset_local, offset_local);

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
		var offset_local = offset;
		var subset_local = subset;

		if (offset_local -1 < 0)
		{
			subset_local--;
			offset_local = IdxToSubsetMapperImpl.SUBSET_SIZE-1;
		}
		else
		{
			offset_local--;
		}
		final var result =  primeSrc.getPrimeRefForIdx(subset_local, offset_local);
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

	@SuppressWarnings("PMD.ConfusingTernary")
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