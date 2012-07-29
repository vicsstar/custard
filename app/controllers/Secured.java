package controllers;

import play.*;
import play.mvc.*;
import play.mvc.Http.*;

/**
 * A class providing authentication for restricted areas.
 */
public class Secured extends Security.Authenticator {

  /**
   * Provides the identifier that must be present
   * for a user to be considered authenticated.
   */
  @Override
  public String getUsername(Context context) {
    return context.session().get("username");
  }

  /**
   * The page that should be displayed
   * if a user attempts accessing restricted pages.
   */
  @Override
  public Result onUnauthorized(Context context) {
    return redirect(routes.Application.login());
  }
}
