package com.starcases.prime.base.prefixtree;

import java.math.BigInteger;
import java.util.Map;
import java.util.stream.Collectors;

import com.starcases.prime.intfc.PrimeSourceIntfc;
import com.starcases.prime.log.AbstractLogBase;

import lombok.NonNull;

public class LogBasePrefixTree extends AbstractLogBase
{


	public LogBasePrefixTree(@NonNull PrimeSourceIntfc ps)
	{
		super(ps);
	}

	@Override
	public void l()
	{
		//logTree(prefixMap);
	}

	void logTree(final Map<BigInteger, PrefixTreeNode> prefixMap)
	{
		prefixMap
		.values()
		.stream()
		.parallel()
		.forEach(treeNode ->
					{
						final var outputStrFinal = new StringBuilder();
						logTree(treeNode, outputStrFinal);
					}
				);
	}

	void logTree(final PrefixTreeNode prefixTreeNode, final StringBuilder outputStr)
	{
		outputStr.append(prefixTreeNode.getPrefixPrime().get());

		if (!prefixTreeNode.getSourcePrimes().isEmpty())
		{
			System.out.println(String.format("Prefix[%s] count[%d] source-primes[%s]",
					outputStr.toString(),
					prefixTreeNode.getSourcePrimes().size(),
					prefixTreeNode.getSourcePrimes().stream().map(BigInteger::toString).collect(Collectors.joining(",", "[", "]"))));
		}

		prefixTreeNode.getNext()
			.values()
			.stream()
			.forEach(treeNode ->
						{
							final var outputStrFinal = new StringBuilder(outputStr);
							outputStrFinal.append(",");

							logTree(treeNode, outputStrFinal);
						}
					);
	}
}

