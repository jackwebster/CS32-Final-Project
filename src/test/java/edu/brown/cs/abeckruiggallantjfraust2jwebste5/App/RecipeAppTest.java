package edu.brown.cs.abeckruiggallantjfraust2jwebste5.App;

import org.junit.After;
import org.junit.Test;

import java.sql.SQLException;

import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.Database.closeConn;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.Database.getConn;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.UserDatabase.closeUserConn;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.UserDatabase.getUserConn;
import static org.junit.Assert.assertTrue;

public class RecipeAppTest {

  /**
   * disconnect database
   */
  @After
  public void tearDown() {
    try {
      closeConn();
      closeUserConn();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  @Test
  public void initializeDatabaseTest() {
    assertTrue(getConn()==null);
    assertTrue(getUserConn() == null);
    RecipeApp app = new RecipeApp();
    assertTrue(getConn()!=null);
    assertTrue(getUserConn() != null);
  }

  @Test
  public void setAndGetUser() {
    RecipeApp app = new RecipeApp();
    assertTrue(app.getCurUser() == null);
    User newUser = new User("new", null);
    app.setCurUser(newUser);
    assertTrue(newUser == app.getCurUser());
  }
}
