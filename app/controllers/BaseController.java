package controllers;

import models.User;

import play.*;
import play.mvc.*;
import static play.libs.Scala.Option;
import play.cache.Cache;

/**
 * Base class for all controller classes requiring some recurring detail.
 *
 * @author Victor Igbokwe (vicsstar@yahoo.com) 2012-07-29 2:28 AM
 */
public class BaseController extends Controller {

  /**
   * Retrieves details of the logged in user.
   */
  public static scala.Option<User> connectedUser() {
    // retrieve the logged in user if any.
    String username = session().get("username");

    // check if a username was found in session.
    if (username != null) {
      // there was a username in session; check the cache for a related User.
      User user = (User) Cache.get("user-" + username);

      // check that a user was found.
      if (user != null) {
        // return the user that was found.
        return Option(user);
      } else {
        // attempt finding the user by username.
        scala.Option<User> _user = User.findByUsername(username);

        // check if the user was found.
        if (_user.isDefined()) {
          // put into the cache the just retrieved user details for 30 minutes.
          Cache.set("user-" + username, _user.get(), 60 * 30);
        }
        // return the retrieved user.
        return _user;
      }
    } else {
      return Option(null);
    }
  }
}
