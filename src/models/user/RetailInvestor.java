package models.user;

public class RetailInvestor implements User {

  private final String username;
  private final Role role;

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
