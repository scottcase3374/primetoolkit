package com.starcases.newprime;

import java.util.Comparator;

import org.graphstream.graph.Node;

public class NaturalComparator implements Comparator<Node> {

	@Override
	public int compare(Node o1, Node o2) {
		
		return Integer.decode(o1.getId()).compareTo(Integer.decode(o2.getId()));
	}

}
