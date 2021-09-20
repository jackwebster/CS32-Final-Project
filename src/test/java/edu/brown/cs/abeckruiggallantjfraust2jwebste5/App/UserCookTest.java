package edu.brown.cs.abeckruiggallantjfraust2jwebste5.App;

import edu.brown.cs.abeckruiggallantjfraust2jwebste5.RecipeObjects.Recipe;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;

import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.Database.*;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.UserDatabase.*;
import static org.junit.Assert.assertTrue;

public class UserCookTest {
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

  /**
   * basic cook test
   */
    @Test
    public void searchTestDefaultRating() {
      this.testUser.addIngredient("tomato");
      this.testUser.addIngredient("egg");
      this.testUser.addIngredient("spinach");
      ArrayList<Recipe> recipeSuggestions = this.testUser.cook();
      assertTrue(recipeSuggestions.get(0).getName().equals("tuna niçoise wrap"));
      assertTrue(recipeSuggestions.get(1).getName().equals("spinach orange and pine nut salad"));
    }


  /**
   * cook test with adding and removing ingredients
   */
  @Test
  public void searchTestAddingRemovingDefaultRatings() {
    testUser.addIngredient("tomato");
    testUser.addIngredient("egg");
    testUser.addIngredient("spinach");
    ArrayList<Recipe> recipeSuggestions = testUser.cook();
    testUser.removeIngredient("tomato");
    ArrayList<Recipe> recipesSuggestions2 = testUser.cook();
    assertTrue(recipeSuggestions != recipesSuggestions2);
  }

  @Test
  public void searchTestIngredientBadRatings() {
    testUser.addIngredient("tomato");
    testUser.addIngredient("egg");
    testUser.addIngredient("spinach");
    ArrayList<Recipe> recipeSuggestions = testUser.cook();
    testUser.addIngredientRating("tomato", .5);
    ArrayList<Recipe> recipeSuggestions2 = testUser.cook();
    assertTrue(!recipeSuggestions.equals(recipeSuggestions2));
  }

  @Test
  public void searchTestIngredientGoodRatings() {
    testUser.addIngredient("tomato");
    testUser.addIngredient("egg");
    testUser.addIngredient("spinach");
    ArrayList<Recipe> recipeSuggestions = testUser.cook();
    testUser.addIngredientRating("tomato", 5.0);
    ArrayList<Recipe> recipeSuggestions2 = testUser.cook();
    assertTrue(!recipeSuggestions.equals(recipeSuggestions2));
  }

  @Test
  public void searchTestRecipeBadRatings() {
    testUser.addIngredient("tomato");
    testUser.addIngredient("egg");
    testUser.addIngredient("spinach");
    ArrayList<Recipe> recipeSuggestions = testUser.cook();
    testUser.addRecipeRating("tuna niçoise wrap", .5);
    ArrayList<Recipe> recipeSuggestions2 = testUser.cook();
    assertTrue(!recipeSuggestions.equals(recipeSuggestions2));
  }

  @Test
  public void searchTestRecipeGoodRatings() {
    testUser.addIngredient("tomato");
    testUser.addIngredient("egg");
    testUser.addIngredient("spinach");
    ArrayList<Recipe> recipeSuggestions = testUser.cook();
    testUser.addRecipeRating("tuna niçoise wrap", 5.0);
    ArrayList<Recipe> recipeSuggestions2 = testUser.cook();
    assertTrue(!recipeSuggestions.equals(recipeSuggestions2));
    assertTrue(recipeSuggestions2.get(0).getName().equals("tuna niçoise wrap"));
  }

  @Test
  public void overwriteIngredientRating() {
    testUser.addIngredient("tomato");
    testUser.addIngredient("egg");
    testUser.addIngredient("spinach");
    testUser.addIngredientRating("tomato", 5.0);
    ArrayList<Recipe> recipeSuggestions = testUser.cook();
    testUser.addIngredientRating("tomato", .5);
    ArrayList<Recipe> recipeSuggestions2 = testUser.cook();
    assertTrue(!recipeSuggestions.equals(recipeSuggestions2));
  }

  @Test
  public void overwriteRecipeRating() {
    testUser.addIngredient("tomato");
    testUser.addIngredient("egg");
    testUser.addIngredient("spinach");
    testUser.addRecipeRating("tuna niçoise wrap", .5);
    ArrayList<Recipe> recipeSuggestions = testUser.cook();
    testUser.addRecipeRating("tuna niçoise wrap", 5.0);
    ArrayList<Recipe> recipeSuggestions2 = testUser.cook();
    assertTrue(!recipeSuggestions.equals(recipeSuggestions2));
    assertTrue(!recipeSuggestions.get(0).equals("tuna niçoise wrap"));
  }

  @Test
  public void addingABunchOfIngredients() {
    testUser.addIngredient("tomato");
    testUser.addIngredient("egg");
    testUser.addIngredient("spinach");
    testUser.addIngredient("lemon curd");
    testUser.addIngredient("apple");
    testUser.addIngredient("double cream");
    testUser.addIngredient("bread");
    testUser.addIngredient("sweet potato");
    testUser.addIngredient("egg");
    testUser.addIngredient("truffles");
    testUser.addIngredient("sparkling wine");
    ArrayList<Recipe> recipeSuggestions = testUser.cook();
    assertTrue(recipeSuggestions.get(0).getName().equals("spring truffle pithiviers with a maderia reduction"));
    assertTrue(recipeSuggestions.get(1).getName().equals("spinach soup with poached egg"));
  }

  @Test
  public void cookWithNoInventory() {
    ArrayList<Recipe> recipeSuggestions = testUser.cook();
    assertTrue(recipeSuggestions.size() == 0);
  }

  @Test
  public void cookWithOneIngredient() {
    testUser.addIngredient("sweet potato");
    ArrayList<Recipe> recipeSuggestions = testUser.cook();
    assertTrue(recipeSuggestions.get(0).getName().equals("sautéed sweet potato"));
  }

  @Test
  public void cookWithBadIngredient() {
    testUser.addIngredient("sweet adgtrwhpotato");
    ArrayList<Recipe> recipeSuggestions = testUser.cook();
    assertTrue(recipeSuggestions.size()==0);
  }
}
