import models.User;

import play.GlobalSettings;
import play.Application;
import play.api.libs.Crypto;

/**
 * This is where operations to be performed like fixtures will be done.
 *
 * @author Victor Igbokwe (vicsstar@yahoo.com) 2012-07-29 2:35 AM
 */
public class Global extends GlobalSettings {

  @Override
  public void onStart(Application app) {

    // check if there are no users in the database.
    if (User.count() == 0) {
      // there is no user in the database, fix one in.
      User user = new User();
      // set the user's properties.
      user.firstName = "Victor";
      user.lastName = "Igbokwe";
      user.username = "vicsstar";
      user.password = Crypto.sign("password123");
      // save the user to the database.
      user.save();
    }
  }
}