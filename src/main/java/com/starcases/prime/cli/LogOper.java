package com.starcases.prime.cli;

/**
 *
 * enumerations naming the available log production types.
 *
 */
enum LogOper
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
	BASES
}