package edu.brown.cs.abeckruiggallantjfraust2jwebste5.DataPreprocess;

import java.util.Comparator;
import java.util.HashMap;

/**
 * This class is only used in preprocessing. It is given a map of
 * each recipe title, to how many ingredients the recipe has. Then it
 * compares two recipes titles, based on their entries in the map.
 *
 * NOTE: this was not junit tested, as it is only run in preprocess, and
 * we can (and did) verify the results by hand.
 */
public final class NumIngredientsComparator implements Comparator<String> {
  private HashMap<String, Integer> numIngredientsMap;
  public NumIngredientsComparator(HashMap<String, Integer> map) {
    numIngredientsMap = map;
  }
  @Override
  public int compare(String r1, String r2) {
    // if numIngredients map doesn't contain the ingredient we are querying on don't do anything
    if (!numIngredientsMap.containsKey(r2) || !numIngredientsMap.containsKey(r1)) {
      return 0;
    }
    int r1NumIngredients = numIngredientsMap.get(r1);
    int r2NumIngredients = numIngredientsMap.get(r2);
    if (r1NumIngredients > r2NumIngredients) {
      return 1;
    }
    if (r1NumIngredients < r2NumIngredients) {
      return -1;
    }
    return 0;
  }
}
