package edu.brown.cs.abeckruiggallantjfraust2jwebste5.RecipeObjects;

import edu.brown.cs.abeckruiggallantjfraust2jwebste5.App.User;
import edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.Database;
import edu.brown.cs.abeckruiggallantjfraust2jwebste5.Graph.Vertex;
import java.util.HashMap;
import java.util.HashSet;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.App.ConstantHyperparameters.DEFAULT_RATING;

/**
 * This class models the ingredient class. It implements the Vertex class so it can reference
 * its adjacent Recipe nodes.
 */
public class Ingredient implements Vertex<Recipe> {
  private String name;
  private User owner;
  private Double rating;
  //refers to its adjacent recipes
  private HashSet<Recipe> adjRecipes = new HashSet<>();

  public Ingredient(String name, User user) {
    this.name = name;
    this.owner = user;
    //if the ingredient has been rated use that, otherwise use default rating
    if (user.getIngredientRatings().keySet().contains(name)) {
      this.rating = user.getIngredientRatings().get(name);
    } else {
      this.rating = DEFAULT_RATING;
    }
  }

  /**
   * Gets the adjacent recipes nodes (all the recipes that use that ingredient).
   * @param recipesAlreadyAdded Takes in the recipes already add to the graph
   *                            so we can use these.
   * @return HashSet of of adjacent recipes
   */
  @Override
  public HashSet<Recipe> getAdjacentVertices(HashMap<String, Recipe> recipesAlreadyAdded) {
    if (adjRecipes.size() == 0) {
      String recipesString = Database.getRecipesWithIngredient(name);
      //splits on commas
      String[] rec = recipesString.trim().split("\\s*,\\s*");
      for (String recipeName : rec) {
        //if already added return that recipe object
        if (recipesAlreadyAdded.containsKey(recipeName)) {
          adjRecipes.add(recipesAlreadyAdded.get(recipeName));
        } else { //otherwise create a new recipe object
          adjRecipes.add(Database.getRecipeObject(recipeName, this.owner));
        }
      }
    }
    HashSet<Recipe> adjRecipesToReturn = new HashSet<>();
    adjRecipesToReturn.addAll(adjRecipes);
    return adjRecipesToReturn;
  }

  /**
   * @return name of this ingredient
   */
  public String getName() {
    return name;
  }

  /**
   * @return rating of this ingredient
   */
  @Override
  public Double getValue() {
    return rating;
  }

  /**
   * @param value is the rating of this ingredient
   */
  @Override
  public void setValue(double value) {
    this.rating = value;
  }

}
