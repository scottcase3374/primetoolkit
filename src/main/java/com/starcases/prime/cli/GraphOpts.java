package com.starcases.prime.cli;

import picocli.CommandLine.Option;

class GraphOpts
{
	@Option(names = {"-g", "--graph"}, arity="0..1", description = "Valid vals: ${COMPLETION-CANDIDATES}" )
	Graph graphType;
}