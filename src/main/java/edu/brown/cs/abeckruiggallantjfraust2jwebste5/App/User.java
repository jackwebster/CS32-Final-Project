package edu.brown.cs.abeckruiggallantjfraust2jwebste5.App;

import edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.Database;
import edu.brown.cs.abeckruiggallantjfraust2jwebste5.Graph.Graph;
import edu.brown.cs.abeckruiggallantjfraust2jwebste5.RecipeObjects.Ingredient;
import edu.brown.cs.abeckruiggallantjfraust2jwebste5.RecipeObjects.Recipe;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.App.ConstantHyperparameters.DEFAULT_RATING;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.App.ConstantHyperparameters.NUM_RECOMMENDATIONS;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.App.RecipeFinder.findRecipesWithIngredients;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.UserDatabase.userIngredientRatingsToMapHelper;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.UserDatabase.userRecipeRatingsToMapHelper;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.UserDatabase.addUserIngredient;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.UserDatabase.removeUserIngredient;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.UserDatabase.addUserIngredientRating;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.UserDatabase.addUserRecipeRating;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.Database.getRecipeObject;

/**
 * This class handles all functionality specific to a user. It keeps track of the users graph which
 * stores each recipe + ingredient with their associated rating. It also tracks the ingredients in
 * the user's fridge.
 */
public class User {
  private HashSet<String> ingredients;
  private HashMap<String, Double> recipeRatings;
  private HashMap<String, Double> ingredientRatings;
  private String name;
  private Graph<Recipe, Ingredient> recipeGraph;

  /**
   * Constructor for user.
   * @param username belonging to this user
   * @param ingredients correspond to ingredients in user's inventory.
   */
  public User(String username, HashSet<String> ingredients) {
    this.name = username;
    this.ingredients = ingredients;
    recipeGraph = new Graph();
    recipeRatings = userRecipeRatingsToMapHelper(name);
    ingredientRatings = userIngredientRatingsToMapHelper(name);
  }

  /**
   * Getter for ingredient ratings map.
   * @return ingredient ratings map
   */
  public HashMap<String, Double> getIngredientRatings() {
    return ingredientRatings;
  }

  /**
   * Getter for recipe ratings maps.
   * @return recipe ratings
   */
  public HashMap<String, Double> getRecipeRatings() {
    return recipeRatings;
  }

  /**
   * Getter for user's recipe/ingredient graph.
   * @return user's graph
   */
  public Graph getUserGraph() {
    return recipeGraph;
  }

  /**
   * Getter for hashset of ingredients in user's fridge.
   * @return all ingredients in users fridge
   */
  public HashSet<String> getIngredients() {
    return ingredients;
  }

  /**
   * Setter for ingredients in user's fridge.
   * @param ingredients gets ingredient in user's fridge.
   */
  public void setIngredients(HashSet<String> ingredients) {
    this.ingredients = ingredients;
  }

  /**
   * @return name of user
   */
  public String getName() {
    return name;
  }

  /**
   * Adds an ingredient's to user's inventory.
   * @param newIngredient to add to inventory
   */
  public void addIngredient(String newIngredient)  {
    if (this.ingredients == null) {
      this.ingredients = new HashSet<String>();
    }
    this.ingredients.add(newIngredient);
    addUserIngredient(this.name, newIngredient);
    this.addIngredientRating(newIngredient, DEFAULT_RATING);
  }

  /**
   * @param ingredient to remove from User's inventory
   * @throws SQLException
   */
  public void removeIngredient(String ingredient) {
    if (this.ingredients.contains(ingredient)) {
      this.ingredients.remove(ingredient);
      removeUserIngredient(this.name, ingredient);
    }
  }

  /**
   * Adds an ingredient rating to ingredient rating map (doesn't overwrite).
   * @param ingredient being rated
   * @param rating rating to assign to ingredient
   */
  public void addIngredientRating(String ingredient, Double rating) {
    addUserIngredientRating(this, ingredient, rating);
    ingredientRatings.put(ingredient, rating);
    Ingredient obj = recipeGraph.getNonCentralNodes().get(ingredient);
    if (obj != null) {
      obj.setValue(rating);
    }
  }

  /**
   * Adds a recipe rating to recipe rating map (doesn't overwite).
   * @param recipe to rate
   * @param rating to assign to recipe
   */
  public void addRecipeRating(String recipe, Double rating) {
    addUserRecipeRating(this, recipe, rating);
    recipeRatings.put(recipe, rating);
    Recipe obj = recipeGraph.getCentralNodeMap().get(recipe);
    if (obj != null) {
      obj.setValue(rating);
    }
  }

  /**
   * Called when user wants to cook something. Retrieves the recipes
   * with the ingredients that are in the user's inventory (in the ingredients
   * hashset)
   * @return ArrayList of all the recipes that are most highly rated, and
   * have the most overlapping ingredients.
   */
  public ArrayList<Recipe> cook() {
    ArrayList<String> recipeNames = findRecipesWithIngredients(
            NUM_RECOMMENDATIONS, this);
    ArrayList<Recipe> recipes = new ArrayList<>();
    if (recipeNames == null) {
      recipeNames = new ArrayList<String>();
    }
    for (String recipe : recipeNames) {
      recipes.add(getRecipeObject(recipe, this));
    }
    return recipes;
  }

  /**
   * Given a recipe returns a map between each recipe and its similarity index.
   * @param recipe to query the similar recipes on
   * @return map between each recipe and its similarity index
   */
  public ArrayList<Map<String, String>> findSimilarRecipes(String recipe) {
    ArrayList<Map<String, String>> similarRecipes = new ArrayList<>();
    TreeMap<Recipe, Double> map = recipeGraph.search(this.findRecipe(recipe));
    for (Map.Entry<Recipe, Double> entry : map.entrySet()) {
      similarRecipes.add(entry.getKey().toSmallMap());
    }
    return similarRecipes;
  }

  /**
   * @param recipeName name of recipe to find the recipe object of.
   * @return recipe object with the given recipe name
   */
  public Recipe findRecipe(String recipeName) {
    Recipe recipe = recipeGraph.getCentralNodeMap().get(recipeName);
    if (recipe != null) {
      return recipe;
    } else {
      return Database.getRecipeObject(recipeName, this);
    }
  }
}

