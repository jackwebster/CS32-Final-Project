package edu.brown.cs.abeckruiggallantjfraust2jwebste5.Database;

import edu.brown.cs.abeckruiggallantjfraust2jwebste5.App.User;
import edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.Database;
import edu.brown.cs.abeckruiggallantjfraust2jwebste5.RecipeObjects.Recipe;
import java.util.HashSet;
import java.sql.SQLException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.Database.getRecipeObject;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.Database.getIngredientForRecipe;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.Database.getRecipesWithIngredient;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.Database.initialize;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.Database.closeConn;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.Database.getConn;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.UserDatabase.addUserToDatabase;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.UserDatabase.initializeConn;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.UserDatabase.deleteUser;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.UserDatabase.closeUserConn;

import static org.junit.Assert.*;

/**
 * Tests Methods of Database Class
 */
public class DatabaseTest {
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
    addUserToDatabase("testUser", "test@gmail.com");
    testUser = new User("test@gmail.com", testIngredients);
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
  public void testGetRecipesWithIngredient() {
    String testIngredient = "tomato";
    String tomatoRecipes = getRecipesWithIngredient(testIngredient);
    assertTrue(tomatoRecipes.contains("keralan crab with currimbhoy salad"));
  }

  @Test
  public void testGetRecipesWithIngredientTwo() {
    String testIngredient = "truffles";
    String tomatoRecipes = getRecipesWithIngredient(testIngredient);
    assertTrue(tomatoRecipes.equals("pan-roasted guinea fowl with truffles and leeks, roasted chicken breast with leeks and potatoes, roast whole suckling pig with truffle mousse jersey royals and wild garlic, spring truffle pithiviers with a maderia reduction, chargrilled beef fillet with cabbage and truffle mash, herb-crusted lamb with truffle gnocchi honey-glazed shallots and roast artichokes"));
  }

  @Test
  public void testGetIngredientsForRecipe() {
    String testRecipe = "spinach mash";

    String spinachMashIngredients = getIngredientForRecipe(testRecipe);
    assertTrue(spinachMashIngredients.equals("olive oil,spinach,garlic,mashed potato"));

  }

  @Test
  public void testRecipeObject() {
    Recipe recipe = getRecipeObject("spinach mash", this.testUser);
    assertTrue(recipe.getIngredients() != null);
    assertTrue(recipe.getChef().equals("Paul Rankin"));
    assertTrue(recipe.getInstructions() != null);
    assertTrue(recipe.getCookingTime().equals("10"));
    assertTrue(recipe.getPrepTime().equals("30"));
    assertTrue(recipe.getPhotourl() != null);
    assertTrue(recipe.getServes().equals("1"));
    assertTrue(recipe.getValue()==2.5);
    assertTrue(recipe.getUrl().equals("http://bbc.co.uk/food/recipes/cheeseandspinachmash_90409"));
  }

  @Test
  public void testRecipeObjectRated() {
    Recipe recipe = getRecipeObject("spinach mash", this.testUser);
    recipe.setValue(5);
    assertTrue(recipe.getIngredients() != null);
    assertTrue(recipe.getChef().equals("Paul Rankin"));
    assertTrue(recipe.getInstructions() != null);
    assertTrue(recipe.getCookingTime().equals("10"));
    assertTrue(recipe.getPrepTime().equals("30"));
    assertTrue(recipe.getPhotourl() != null);
    assertTrue(recipe.getServes().equals("1"));
    assertTrue(recipe.getValue()==5);
    assertTrue(recipe.getUrl().equals("http://bbc.co.uk/food/recipes/cheeseandspinachmash_90409"));
  }
}