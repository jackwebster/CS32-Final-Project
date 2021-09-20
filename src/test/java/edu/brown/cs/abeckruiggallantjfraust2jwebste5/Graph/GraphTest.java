package edu.brown.cs.abeckruiggallantjfraust2jwebste5.Graph;

import edu.brown.cs.abeckruiggallantjfraust2jwebste5.App.User;
import edu.brown.cs.abeckruiggallantjfraust2jwebste5.Graph.Graph;
import edu.brown.cs.abeckruiggallantjfraust2jwebste5.RecipeObjects.Ingredient;
import edu.brown.cs.abeckruiggallantjfraust2jwebste5.RecipeObjects.Recipe;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.Database.*;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.UserDatabase.*;
import java.util.HashSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
* MethodTesting of Ingredient and Recipe Classes
*/
public class GraphTest {
  private User testUser;

  /**
    * Create new User object for creating ingredients and recipes
    */
  @Before
  public void setUp() {
    HashSet<String> testIngredients = new HashSet<>();
    try {
      initialize("data/newdb.sqlite3");
      initializeConn(getConn());
    } catch (SQLException e) {
      System.out.println("ERROR");
    } catch (ClassNotFoundException e) {
      System.out.println("ERROR");
    }
    addUserToDatabase("testUser", "test1@gmail.com");
    testUser = new User("test1@gmail.com", testIngredients);
  }

  /**
   * Clear variables for future tests.
   */
  @After
  public void tearDown() {
    testUser = null;
    try {
      deleteUser("test1@gmail.com");
      closeConn();
      closeUserConn();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  @Test
  public void emptyGraphTest() {
    Graph<Recipe, Ingredient> graph = testUser.getUserGraph();
    assertTrue(graph.getCentralNodeMap().size()==0);
    assertTrue(graph.getCentralNodeMap().size()==0);
  }

  @Test
  public void basicGraphTest() {
    Graph<Recipe, Ingredient> graph = testUser.getUserGraph();
    Recipe recipe = getRecipeObject("ten-minute pizza", testUser);
    TreeMap<Recipe, Double> topSimilar = graph.search(recipe);
    Recipe firstNode = graph.getCentralNodeMap().get("tagliatelle with broccoli and tomato sauce");
    assertTrue(topSimilar.get(firstNode) > .94);
  }

  @Test
  public void basicGraphTestNewRecipe() {
    Graph<Recipe, Ingredient> graph = testUser.getUserGraph();
    Recipe recipe = getRecipeObject("fresh pasta with a classic ragú sauce", testUser);
    TreeMap<Recipe, Double> topSimilar = graph.search(recipe);
    Recipe firstNode = graph.getCentralNodeMap().get("beef ragu with linguine");
    assertTrue(topSimilar.get(firstNode) > 1.01);
  }

  @Test
  public void testGraphWithCentralValue() {
    Graph<Recipe, Ingredient> graph = testUser.getUserGraph();
    testUser.addIngredientRating("plain flour", .5);
    testUser.addIngredientRating("olive oil", .5);
    testUser.addIngredientRating("tomato purée", .5);
    Recipe recipe = getRecipeObject("ten-minute pizza", testUser);
    TreeMap<Recipe, Double> topSimilar = graph.search(recipe);
    Recipe firstNode = graph.getCentralNodeMap().get("tagliatelle with broccoli and tomato sauce");
    assertTrue(topSimilar.get(firstNode) < .94);
  }

  @Test
  public void testGraphCentralValueAdjustRating() {
    Graph<Recipe, Ingredient> graph = testUser.getUserGraph();
    testUser.addIngredientRating("plain flour", .5);
    testUser.addIngredientRating("olive oil", .5);
    testUser.addIngredientRating("tomato purée", .5);
    Recipe recipe = getRecipeObject("ten-minute pizza", testUser);
    TreeMap<Recipe, Double> topSimilar = graph.search(recipe);
    testUser.addIngredientRating("tomato purée", 5.0);
    TreeMap<Recipe, Double> topSimilar2 = graph.search(recipe);
    assertTrue(topSimilar != topSimilar2);
  }

  @Test
  public void testGraphWithNonCentralValue() {
    Graph<Recipe, Ingredient> graph = testUser.getUserGraph();    testUser.addRecipeRating("tagliatelle with broccoli and tomato sauce", .5);
    Recipe recipe = getRecipeObject("ten-minute pizza", testUser);
    TreeMap<Recipe, Double> topSimilar = graph.search(recipe);
    Recipe firstNode = graph.getCentralNodeMap().get("goats' cheese pizza");
    assertTrue(topSimilar.get(firstNode) > .93);
  }

  @Test
  public void testGraphWithCentralValueAdjustRating() {
    Graph<Recipe, Ingredient> graph = testUser.getUserGraph();
    testUser.addRecipeRating("tagliatelle with broccoli and tomato sauce", .5);
    Recipe recipe = getRecipeObject("ten-minute pizza", testUser);
    TreeMap<Recipe, Double> topSimilar = graph.search(recipe);
    Recipe firstNode = graph.getCentralNodeMap().get("tagliatelle with broccoli and tomato sauce");
    double firstVal = topSimilar.get(firstNode);
    testUser.addRecipeRating("tagliatelle with broccoli and tomato sauce", 5.0);
    TreeMap<Recipe, Double> topSimilar2 = graph.search(recipe);
    assertTrue(firstVal != topSimilar2.get(firstNode));
  }

  @Test
  public void testGraphWithCentralValueAdjustRatingAdvanced() {
    Graph<Recipe, Ingredient> graph = testUser.getUserGraph();
    testUser.addRecipeRating("tagliatelle with broccoli and tomato sauce", 5.0);
    Recipe recipe = getRecipeObject("ten-minute pizza", testUser);
    TreeMap<Recipe, Double> topSimilar = graph.search(recipe);
    Recipe firstNode = graph.getCentralNodeMap().get("tagliatelle with broccoli and tomato sauce");
    assertTrue(topSimilar.get(firstNode) >= .94);
  }

  @Test
  public void testGraphWithoutUser() {
    Graph<Recipe, Ingredient> graph = new Graph<>();
    Recipe recipe = getRecipeObject("ten-minute pizza", testUser);
    TreeMap<Recipe, Double> topSimilar = graph.search(recipe);
    Recipe firstNode = graph.getCentralNodeMap().get("tagliatelle with broccoli and tomato sauce");
    assertTrue(topSimilar.get(firstNode) > .94);
  }

  @Test
  //recipes should override ingredient ratings
  public void rateRecipesAndIngredients() {
    Graph<Recipe, Ingredient> graph = testUser.getUserGraph();
    testUser.addIngredientRating("plain flour", .5);
    testUser.addIngredientRating("olive oil", .5);
    testUser.addIngredientRating("tomato purée", .5);
    Recipe recipe = getRecipeObject("ten-minute pizza", testUser);
    TreeMap<Recipe, Double> topSimilar = graph.search(recipe);
    Recipe firstNode = graph.getCentralNodeMap().get("tagliatelle with broccoli and tomato sauce");
    assertTrue(topSimilar.get(firstNode) < .94);
    testUser.addRecipeRating("tagliatelle with broccoli and tomato sauce", 5.0);
    TreeMap<Recipe, Double> topSimilar2 = graph.search(recipe);
    assertTrue(topSimilar2.get(firstNode) > .94);
  }
}