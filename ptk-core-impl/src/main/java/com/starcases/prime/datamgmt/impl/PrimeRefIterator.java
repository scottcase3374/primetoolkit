package com.starcases.prime.datamgmt.impl;

import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.datamgmt.api.PrimeRefIteratorIntfc;

import lombok.NonNull;

public class PrimeRefIterator<T extends PrimeRefIntfc> implements PrimeRefIteratorIntfc<T>
{
	private Object primeRef;

	public PrimeRefIterator(@NonNull final T primeRef)
	{
		this.primeRef = primeRef;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean hasNext()
	{
		return ((T)primeRef).hasNext();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T next()
	{
		primeRef = ((T)primeRef).getNextPrimeRef().orElse(null);
		return (T)primeRef;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean hasPrevious()
	{
		return ((T)primeRef).hasPrev();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T previous()
	{
		primeRef = ((T)primeRef).getPrevPrimeRef().orElseThrow();
		return (T)primeRef;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int nextIndex()
	{
		return (int)((T)primeRef).getNextPrimeRef().orElseThrow().getPrimeRefIdx();
	}

	@SuppressWarnings("unchecked")
	@Override
	public int previousIndex()
	{
		return (int)((T)primeRef).getPrevPrimeRef().orElseThrow().getPrimeRefIdx();
	}

	@Override
	public void remove()
	{
		// NO-OP
	}

	@Override
	public void set(T e)
	{
		// NO-OP
	}

	@Override
	public void add(T e)
	{
		// NO-OP
	}
}
