package edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a helper class that converts maps to json object.
 * In the future this could contain more json helpers.
 */
public final class JsonFormatter {
  private  JsonFormatter() {
  }

  /**
   * Converts a string of the ratings for an ingredient/recipe and converts them to a map.
   * @param ratings to convert to a map.
   * @return map where the keys are the ingredients/recipes and the values are the ratings
   */
  public static Map<String, String> ratingMapToJson(String ratings) {
    Map<String, String> ratingMap = new HashMap<>();
    String[] parsed = ratings.trim().split(",");
    if (parsed.length > 0) {
      for (String parse : parsed) {
        String[] rating = parse.trim().split(":");
        if (rating.length > 0) {
          ratingMap.put(rating[0], rating[1]);
        }
      }
    }
    return ratingMap;
  }
}
