package edu.brown.cs.abeckruiggallantjfraust2jwebste5.App;
import edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.Database;

import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.Database.initialize;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.UserDatabase.initializeConn;

import java.sql.SQLException;

/**
 * This class handles all the top level information for the app. Right now
 * this includes the connection to the database, and the current user.
 */
public class RecipeApp {
  private User curUser = null;

  public RecipeApp() {
    try {
      //initialize connection in database class
      initialize("data/newdb.sqlite3");
      //passes connection to user database class
      initializeConn(Database.getConn());
    } catch (SQLException ClassNotFoundException) {
      System.out.println("ERROR");
    } catch (ClassNotFoundException e) {
      System.out.println("ERROR");
    }
  }

  /**
   * @return current user
   */
  public User getCurUser() {
    return curUser;
  }

  /**
   * @param curUser used to set the current user
   */
  public void setCurUser(User curUser) {
    this.curUser = curUser;
  }
}
