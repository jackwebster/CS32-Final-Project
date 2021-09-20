package edu.brown.cs.abeckruiggallantjfraust2jwebste5.App;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.HashSet;

import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.Database.*;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.UserDatabase.*;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.UserDatabase.closeUserConn;
import static org.junit.Assert.assertTrue;

public class UserSetterGetterTest {

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
  public void setAndGetIngredients() {
    HashSet<String> ingredients = new HashSet<>();
    ingredients.add("tomato");
    ingredients.add("lettuce");
    ingredients.add("sauce");
    testUser.setIngredients(ingredients);
    assertTrue(testUser.getIngredients().size() == 3);
    assertTrue(testUser.getIngredients() == ingredients);
  }

  @Test
  public void addIngredientRating() {
    testUser.addIngredientRating("tomato", 3.4);
    assertTrue(testUser.getIngredientRatings().get("tomato") == 3.4);
  }

  @Test
  public void addIngredientRatingOverwrite() {
    testUser.addIngredientRating("tomato", 1.5);
    testUser.addIngredientRating("tomato", 4.5);
    assertTrue(testUser.getIngredientRatings().get("tomato") == 4.5);
    assertTrue(testUser.getIngredientRatings().size()==1);
  }

  @Test
  public void addRecipeRating() {
    testUser.addRecipeRating("tomato", 3.4);
    assertTrue(testUser.getRecipeRatings().get("tomato") == 3.4);
  }

  @Test
  public void addRecipeRatingOverwrite() {
    testUser.addRecipeRating("tomato", 1.5);
    testUser.addRecipeRating("tomato", 4.5);
    assertTrue(testUser.getRecipeRatings().get("tomato") == 4.5);
    assertTrue(testUser.getRecipeRatings().size()==1);
  }

  @Test
  public void removeIngredient() {
    testUser.addIngredient("tomato");
    testUser.removeIngredient("tomato");
    assertTrue(testUser.getIngredients().size()==0);
    testUser.removeIngredient("tomato");
    assertTrue(testUser.getIngredients().size()==0);
  }

  @Test
  public void addIngredient() {
    testUser.addIngredient("tomato");
    assertTrue(testUser.getIngredients().contains("tomato"));
    assertTrue(testUser.getIngredients().size() == 1);
  }

  @Test
  public void addIngredientNoOverwrite() {
    testUser.addIngredient("tomato");
    testUser.addIngredient("tomato");
    assertTrue(testUser.getIngredients().contains("tomato"));
    assertTrue(testUser.getIngredients().size() == 1);
  }

}
