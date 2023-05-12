package com.starcases.prime.shared.api;

import com.starcases.prime.shared.api.OutputableIntfc;

/**
 *
 * enumerations naming the available base types and other data that can be output
 *
 */
public enum OutputOper implements OutputableIntfc
{
	/**
	 * Logs the default node structure of the dataset including default bases
	 */
	NODESTRUCT,

	/**
	 * logs the dataset from the graph structure used in visualizations
	 */
	GRAPHSTRUCT,

	/**
	 * Logs bases if nprime, triples or prefix/prefix_tree generation is used.
	 */
	BASES,

	/**
	 * Metadata regarding progress info for base generation.
	 */
	PROGRESS,

	/**
	 * Lob base creation process
	 */
	CREATE,

	/**
	 * Dumps the list of tree prefixes and the number of counts for each.
	 */
	PRIMETREE_METRICS;


}