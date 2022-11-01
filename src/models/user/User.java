package models.user;

/**
 * A class representing a client and the functions that they can perform.
 */
public interface User {

  /**
   * Used to get the username that was chosen by a particular user.
   *
   * @return string that represents the username.
   */
  String getUserName();

  /**
   * Used to fetch the type of Role the client is playing.
   *
   * @return <code>Role</code> of the user.
   */
  Role getRole();

}
