package com.starcases.prime.kern.api;

import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.impl.list.immutable.primitive.ImmutableLongListFactoryImpl;

public interface Arrays
{
	/**
	 * Convert to array of Long from native long array.
	 * @param array
	 * @return
	 */
	static Long[] longArrayToLongArray(final long[] array)
	{
		final Long [] tmpArray = new Long[array.length];
		for (int i=0; i < array.length; i++)
		{
			tmpArray[i] = array[i];
		}
		return tmpArray;
	}

	/**
	 * Convert to native long array from array of Long.
	 * @param array
	 * @return
	 */
	static long[] longArrayToLongArray(final Long[] array)
	{
		final long [] tmpArray = new long[array.length];
		for (int i=0; i < array.length; i++)
		{
			tmpArray[i] = array[i];
		}
		return tmpArray;
	}

	static ImmutableLongCollection arrayToImmutableLongColl(final Long[] array)
	{
		return ImmutableLongListFactoryImpl.INSTANCE.of(longArrayToLongArray(array));
	}

	static ImmutableLongCollection arrayToImmutableLongColl(final long[] array)
	{
		return ImmutableLongListFactoryImpl.INSTANCE.of(array);
	}
}
