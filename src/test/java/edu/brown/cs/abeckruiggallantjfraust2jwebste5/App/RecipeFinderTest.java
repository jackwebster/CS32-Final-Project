package edu.brown.cs.abeckruiggallantjfraust2jwebste5.App;

import edu.brown.cs.abeckruiggallantjfraust2jwebste5.RecipeObjects.Recipe;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;

import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.App.RecipeFinder.findRecipesWithIngredients;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.Database.*;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.UserDatabase.*;
import static org.junit.Assert.assertTrue;

public class RecipeFinderTest {
  User testUser;
  /**
   * Create new User object for creating ingredients and recipes
   */
  @Before
  public void setUp() {
    try {
      initialize("data/newdb.sqlite3");
      initializeConn(getConn());
    } catch (SQLException e) {
      System.out.println("ERROR");
    } catch (ClassNotFoundException e) {
      System.out.println("ERROR");
    }
    addUserToDatabase("testUser", "test@gmail.com");
    testUser = new User("test@gmail.com", null);
  }

  /**
   * Clear variables for future tests.
   */
  @After
  public void tearDown() {
    testUser = null;
    try {
      deleteUser("test@gmail.com");
      closeConn();
      closeUserConn();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  @Test
  public void findRecipesBasicTest() {
    HashSet<String> ingredients = new HashSet<>();
    ingredients.add("lemon curd");
    ingredients.add("double cream");
    ingredients.add("lemon");
    testUser.setIngredients(ingredients);
    ArrayList<String> similarRecipes = findRecipesWithIngredients(10, testUser);
    assertTrue(similarRecipes.get(0).equals("lemon curd ice cream"));
    assertTrue(similarRecipes.get(1).equals("lemon syllabub"));
    assertTrue(similarRecipes.get(2).equals("lemon meringue ice cream with lemon balm"));
  }

  @Test
  public void findRecipesAfterRatingIngredientsPoorly() {
    HashSet<String> ingredients = new HashSet<>();
    ingredients.add("lemon curd");
    ingredients.add("double cream");
    ingredients.add("lemon");
    testUser.setIngredients(ingredients);
    testUser.addIngredientRating("lemon curd", .5);
    testUser.addIngredientRating("lemon", .5);
    testUser.addIngredientRating("double cream", .5);
    ArrayList<String> similarRecipes = findRecipesWithIngredients(10, testUser);
    assertTrue(similarRecipes.get(0).equals("apple charlotte"));
    assertTrue(similarRecipes.get(1).equals("chocolate soufflé"));
    assertTrue(similarRecipes.get(2).equals("lemon syllabub"));
  }

  @Test
  public void findRecipesAfterRatingIngredientsWell() {
    HashSet<String> ingredients = new HashSet<>();
    ingredients.add("lemon curd");
    ingredients.add("double cream");
    ingredients.add("lemon");
    testUser.setIngredients(ingredients);
    testUser.addIngredientRating("lemon curd", 5.0);
    testUser.addIngredientRating("lemon", 5.0);
    testUser.addIngredientRating("double cream", 5.0);
    ArrayList<String> similarRecipes = findRecipesWithIngredients(10, testUser);
    assertTrue(similarRecipes.get(0).equals("lemon curd ice cream"));
    assertTrue(similarRecipes.get(1).equals("cheaty peach ice cream"));
    assertTrue(similarRecipes.get(2).equals("lemon meringue ice cream with lemon balm"));
  }

  @Test
  public void nothingInFridge() {
    ArrayList<String> similarRecipes = findRecipesWithIngredients(10, testUser);
    assertTrue(similarRecipes.size()==0);
  }

  @Test
  public void findALotOfRecipes() {
    HashSet<String> ingredients = new HashSet<>();
    ingredients.add("lemon curd");
    ingredients.add("double cream");
    ingredients.add("lemon");
    testUser.setIngredients(ingredients);
    ArrayList<String> similarRecipes = findRecipesWithIngredients(100, testUser);
    assertTrue(similarRecipes.get(0).equals("lemon curd ice cream"));
    assertTrue(similarRecipes.get(1).equals("lemon syllabub"));
    assertTrue(similarRecipes.get(2).equals("lemon meringue ice cream with lemon balm"));
  }

  @Test
  public void findOneRecipe() {
    HashSet<String> ingredients = new HashSet<>();
    ingredients.add("lemon curd");
    ingredients.add("double cream");
    ingredients.add("lemon");
    testUser.setIngredients(ingredients);
    ArrayList<String> similarRecipes = findRecipesWithIngredients(1, testUser);
    assertTrue(similarRecipes.get(0).equals("lemon curd ice cream"));
  }

  @Test
  public void badIngredientSearch() {
    HashSet<String> ingredients = new HashSet<>();
    ingredients.add("asdfadsfasdf");
    testUser.setIngredients(ingredients);
    ArrayList<String> similarRecipes = findRecipesWithIngredients(1, testUser);
    assertTrue(similarRecipes.size() == 0);
  }

  @Test
  public void manyBadIngredientsSearch() {
    HashSet<String> ingredients = new HashSet<>();
    ingredients.add("asdfadsfasdf");
    ingredients.add("avasvsdfadsfasdf");
    ingredients.add("aerawer3");
    ingredients.add("a23absd");
    testUser.setIngredients(ingredients);
    ArrayList<String> similarRecipes = findRecipesWithIngredients(1, testUser);
    assertTrue(similarRecipes.size() == 0);
  }

  @Test
  public void mixOfGoodAndBadIngredientsSearch() {
    HashSet<String> ingredients = new HashSet<>();
    ingredients.add("asdfadsfasdf");
    ingredients.add("avasvsdfadsfasdf");
    ingredients.add("egg");
    ingredients.add("flour");
    testUser.setIngredients(ingredients);
    ArrayList<String> similarRecipes = findRecipesWithIngredients(1, testUser);
    assertTrue(similarRecipes.get(0).equals("sausage roll’s big night out"));
  }

}
