package models;

import java.util.Date;
import javax.persistence.*;

import play.db.ebean.Model;
import play.api.libs.Crypto;

import static play.libs.Scala.Option;

/**
 * Defines a user that is able to write posts.
 * @author Victor Igbokwe (vicsstar@yahoo.com) 2012-07-28 11:28 PM
 */
@Entity
@Table(name = "users")
public class User extends Model {

  @Id
  public Long id;
  @Basic(optional = false)
  public String firstName;
  @Basic(optional = false)
  public String lastName;

  @Column(length = 64, nullable = false)
  public String username;

  @Basic(optional = false)
  public String password;

  @Column(name = "created_on", nullable = false)
  public Date createdOn = new Date();
  @Column(name = "modified_on")
  public Date modifiedOn = new Date();

  // a finder class method for making queries to the database.
  public static Finder<Long, User> finder = new Finder<Long, User>(
    Long.class, User.class
  );

  /**
   * Returns the total number of users in the database.
   */
  public static int count() {
    return finder.where().findPagingList(1).getTotalRowCount();
  }

  /**
   * Finds a user by username and returns an option object.
   */
  public static scala.Option<User> findByUsername(String username) {
    return Option(finder.where().eq("username", username).findUnique());
  }

  /**
   * Finds a user by username and password.
   */
  public static User authenticate(String username, String password) {
    return finder.where().eq("username", username).eq(
      "password", Crypto.sign(password)
    ).findUnique();
  }

  /**
   * Returns the full name as a concatenation of the first and last names.
   */
  public String getFullName() {
    return firstName + " " + lastName;
  }
}
