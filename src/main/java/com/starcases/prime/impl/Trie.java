package com.starcases.prime.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.TreeSet;


/**
 *  represent large numbers as nodes in a trie where each level is an extension of bits to the previous level.
 *  
 * @author scott
 *
 * @param <T>
 */
public class Trie<U extends Byte, T extends TreeSet<U>> extends TreeSet<T>
{	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	MathContext mcFloor = new MathContext(7, RoundingMode.FLOOR);
	MathContext mcCeil = new MathContext(7, RoundingMode.CEILING);
	
	U higher(BigDecimal val)
	{
		return this.higher(val.round(mcFloor).toBigIntegerExact());
	}
	
	T lower(BigDecimal val)
	{
		return this.lower(val.round(mcCeil).toBigIntegerExact());
	}

	U higher(BigInteger val)
	{
		return this.higher(val); // walk the trie by byte vals
	}
	
	T lower(BigInteger val)
	{
		return this.lower(val); // walk the trie by byte vals
	}

	void add(BigInteger prime)
	{
		
	}

}
