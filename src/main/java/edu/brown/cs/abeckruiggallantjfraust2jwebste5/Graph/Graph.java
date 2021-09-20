package edu.brown.cs.abeckruiggallantjfraust2jwebste5.Graph;

import java.util.*;

import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.App.ConstantHyperparameters.SIMILARITY_WEIGHT;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.App.ConstantHyperparameters.SCORE_WEIGHT;

/**
 * This class models a graph in which there are central vertices (in this implementation the central
 * vertices are recipes) and non-central vertices (in this implementation the non-central vertices are
 * ingredients. It then uses the Jacard Similarity algorithm + the ratings of the central nodes to
 * find the most similar central nodes to a given central node.
 * @param <centralVertex> represent the central vertices of the graph
 * @param <nonCentralVertex> represent the non-central vertices of the graph
 */
public class Graph<centralVertex extends Vertex, nonCentralVertex extends Vertex> {

  /**
   * These are maps from central/non-central node names to the node objects themselves.
   * They are used to keep track of the nodes already added to the graph/traversed. They
   * are essentially the user's graph cache.
   */
  private HashMap<String, nonCentralVertex> nonCentralNodeMap = new HashMap<>();
  private HashMap<String, centralVertex> centralNodeMap = new HashMap<>();

  public Graph() {
  }

  /**
   * @return the non-central node map. Used to index into node objects.
   */
  public HashMap<String, nonCentralVertex> getNonCentralNodes() {
    return this.nonCentralNodeMap;
  }

  /**
   * @return the central node map. Used to index into node objects.
   */
  public HashMap<String, centralVertex> getCentralNodeMap() {
    return this.centralNodeMap;
  }

  /**
   * Adds a HashSet of non-central vertices, to the main non-central vertex map.
   * @param set to add to non-central vertex map
   */
  private void addNonCentralHashSetToHashMap(HashSet<nonCentralVertex> set) {
    HashMap<String, nonCentralVertex> map = nonCentralNodeMap;
    for (nonCentralVertex vertex : set) {
      map.put(vertex.getName(), vertex);
    }
  }

  /**
   * Adds a HashSet of central vertices, to the main central vertex map.
   * @param set to add to central vertex map
   */
  private void addCentralHashSetToHashMap(HashSet<centralVertex> set) {
    HashMap<String, centralVertex> map = centralNodeMap;
    for (centralVertex vertex : set) {
      map.put(vertex.getName(), vertex);
    }
  }

  /**
   * Given a start node, finds the most similar central nodes to it.
   * @param searchStart node from which to find similar nodes to.
   * @return A map of the most similar nodes, along with their index of how similar they are.
   */
  public TreeMap<centralVertex, Double> search(centralVertex searchStart) {
    //adds start node to central node map
    centralNodeMap.put(searchStart.getName(), searchStart);
    //this map will eventually be sorted by the similarity index
    HashMap<centralVertex, Double> simMap = new HashMap<>();
    //gets adjacent vertices to the central node
    HashSet<nonCentralVertex> adjacentVertices = searchStart.getAdjacentVertices(nonCentralNodeMap);
    //adds these adjacent vertices to the non-central node map
    addNonCentralHashSetToHashMap(adjacentVertices);

    /* for each adjacent vertex, get the adjacent to adjacent
    vertices, and for each one compute similarity
     */
    for (nonCentralVertex nonCentralAdj : adjacentVertices) {
      if (nonCentralAdj == null || nonCentralAdj.getName().equals("null")) {
        continue;
      }
      HashSet<centralVertex> adjToAdjVertices = nonCentralAdj.getAdjacentVertices(centralNodeMap);
      //get the adjacent CENTRAL nodes
      for (centralVertex doubleAdj : adjToAdjVertices) {
        //if we have already computed the similarity/traversed that node, wont go again
        if (doubleAdj == null || doubleAdj.equals("null")
                || searchStart.getName().contains(doubleAdj.getName())
                || simMap.containsKey(doubleAdj.getName())) {
          continue;
        }
        simMap.put(doubleAdj, computeSimilarity(doubleAdj, adjacentVertices));
      }
      addCentralHashSetToHashMap(adjToAdjVertices);
    }

    //sorts the most similar contenders by simMap
    TreeMap<centralVertex, Double> mostSimilarContenders =
            new TreeMap<>(new VertexComparator(simMap));
    mostSimilarContenders.putAll(simMap);
    return mostSimilarContenders;
  }

  /**
   * Computes the similarity between two central nodes using Jacard similarity
   * and ratings of recipes.
   * @param adjToAdj the adjacent central node
   * @param setTwo the adjacent non-central nodes
   * @return a similarity metric that deems how similar original
   * central node is to the adjToAdk
   */
  private double computeSimilarity(centralVertex adjToAdj,
                                  HashSet<nonCentralVertex> setTwo) {
    HashSet<nonCentralVertex> setOne = adjToAdj.getAdjacentVertices(nonCentralNodeMap);
    addNonCentralHashSetToHashMap(setOne);
    int initialSetOneSize = setOne.size();
    addNonCentralHashSetToHashMap(setTwo);
    //get intersection of the non-central adjacent nodes (changes size of setOne)
    setOne.retainAll(setTwo);
    int intersectionSize = setOne.size();
    int totalNumSharedAdjNodes = initialSetOneSize + setTwo.size();
    //weighted sum = similarity metric
    return ((SCORE_WEIGHT) * (adjToAdj.getValue()) + ((SIMILARITY_WEIGHT))
            * ((double) intersectionSize
            / (double) totalNumSharedAdjNodes));
  }
}
