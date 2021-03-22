package it.cnr.istc.stlab.lgu.commons.semanticweb.quads;

import java.util.Iterator;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.GraphUtil;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.core.DatasetGraph;

public class QuadsUtils {

	public static boolean isIsomorphic(DatasetGraph d1, DatasetGraph d2) {

		Iterator<Node> nodesD1 = d1.listGraphNodes();
		while (nodesD1.hasNext()) {
			Node n = (Node) nodesD1.next();
			Graph g2 = d2.getGraph(n);
			if (g2 == null) {
				return false;
			}

			Graph g1 = d1.getGraph(n);
			if (!g2.isIsomorphicWith(g1)) {
				return false;
			}
		}
		return true;
	}

	public static void removeFrom(DatasetGraph d1, DatasetGraph d2) {
		Iterator<Node> nodesD1 = d1.listGraphNodes();
		while (nodesD1.hasNext()) {
			Node n = (Node) nodesD1.next();
			Graph g1 = d1.getGraph(n);
			Graph g2 = d2.getGraph(n);
			GraphUtil.deleteFrom(g1, g2);
		}
	}

}
