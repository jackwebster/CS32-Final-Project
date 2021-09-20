package edu.brown.cs.abeckruiggallantjfraust2jwebste5.Graph;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Interface used in similarity Graph to represent nodes. In
 * graph both central and non-central nodes implement Vertex.
 * @param <V>
 */
public interface Vertex<V extends Vertex> {
  /**
   * @param nodesAlreadyAdded a HashMap of vertices that are already part of the graph
   * @return the adjacent vertices (in a HashSet) to this vertices
   */
  HashSet<V> getAdjacentVertices(HashMap<String, V> nodesAlreadyAdded);

  /**
   * @return Gets the name of the current node. Used to identify the node.
   */
  String getName();

  /**
   * @return Gets the value of the current node. Used to compute similarity.
   */
  Double getValue();

  /**
   * @param value of this vertex
   */
  void setValue(double value);

}
