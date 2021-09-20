package edu.brown.cs.abeckruiggallantjfraust2jwebste5.Graph;
import java.util.Comparator;
import java.util.HashMap;

/**
 * This class is used by the Graph class to compare two vertices similarity scores.
 * @param <centralVertex> central vertex of graph
 */
public class VertexComparator<centralVertex extends Vertex> implements Comparator<centralVertex> {
  private HashMap<centralVertex, Double> similarityMap;
  public VertexComparator(HashMap<centralVertex, Double> simMap) {
    similarityMap = simMap;
  }
  @Override
  public int compare(Vertex e1, Vertex e2) {
    if (similarityMap.get(e1) < similarityMap.get(e2)) {
      return 1;
    }
    if (similarityMap.get(e1) > similarityMap.get(e2)) {
      return -1;
    }
    return 0;
  }
}
