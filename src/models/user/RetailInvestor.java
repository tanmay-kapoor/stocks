package models.user;

/**
 * A class that extends the <code>User</code> class and shows all the methods and attributes
 * available to the <code>RetailInvestor</code> class object.
 * A retail investor is type of user that can create multiple portfolios. They also have the
 * ability to buy in and sell from any of the portfolios created by them. They can also choose to
 * view the value of their portfolios at any point in history.
 */
public class RetailInvestor implements User {

  private final String username;
  private final Role role;

  /**
   * A constructor for the <code>RetailInvestor</code> object that initializes its role
   * and username in the system.
   *
   * @param username
   * @param role
   */
  public RetailInvestor(String username, Role role) {
    this.username = username;
    //now save to file
    this.role = role;
  }

  @Override
  public String getUserName() {
    return this.username;
  }

  @Override
  public Role getRole() {
    return this.role;
  }
}
