package controllers;

import models.Post;
import models.User;

import play.*;
import play.mvc.*;
import play.data.Form;
import play.api.libs.Crypto;
import static play.data.validation.Constraints.Required;

import views.html.*;

@Security.Authenticated(Secured.class)
public class Users extends BaseController {

  /**
   * Renders the dashboard.
   */
  public static Result dashboard() {
    java.util.List<Post> posts = Post.all(connectedUser().get());
    return ok(dashboard.render(posts, connectedUser()));
  }

  /**
   * Renders the page for creating a new blog post.
   */
  public static Result newPost() {
    return ok(new_post.render(form(Post.class), connectedUser()));
  }

  /**
   * Processes details to create a new blog post.
   */
  public static Result doCreatePost() {
    // retrieve and validate post details from the request.
    Form<Post> postForm = form(Post.class).bindFromRequest();

    // check if there are any validation error messages.
    if (postForm.hasErrors()) {
      // return the bad-request HTTP response to the user.
      return badRequest(new_post.render(postForm, connectedUser()));
    } else {
      // retrieve the post.
      Post post = postForm.get();
      // associate the new post with the currently logged in user.
      post.user = connectedUser().get();
      // save the post to the database.
      post.save();
      // redirect to the dashboard page.
      return redirect(routes.Users.dashboard());
    }
  }

  /**
   * Renders the page for editing existing blog posts.
   */
  public static Result editPost(Long id) {
    // retrieve the post belonging to the logged in user whose id is provided.
    Post post = Post.find(id, connectedUser().get());

    if (post != null) {
      return ok(edit_post.render(form(Post.class).fill(post), connectedUser()));
    } else {
      // A dummy page showing that the post was not found.
      return notFound("<h3>The specified post was not found.</h3>").as("text/html");
    }
  }

  /**
   * Processes details to edit an existing blog post.
   */
  public static Result doEditPost() {
    // retrieve and validate post details from the request.
    Form<Post> postForm = form(Post.class).bindFromRequest();

    // check if there are any validation error messages.
    if (postForm.hasErrors()) {
      // return the bad-request HTTP response to the user.
      return badRequest(edit_post.render(postForm, connectedUser()));
    } else {
      Post post = postForm.get();
      /*
       * To make sure the user didn't modify the request while in transit
       * and that the post belongs to the currently logged in user,
       * retrieve the post again from the database with the required details.
       */
      Post otherPost = Post.find(post.id, connectedUser().get());

      if (otherPost != null) {
        // associate the new post with the currently logged in user.
        post.user = connectedUser().get();
        post.createdOn = otherPost.createdOn;
        post.modifiedOn = otherPost.modifiedOn;
        // save the post to the database.
        post.update();
        flash("success", "The post was edited successfully.");
        // redirect to the dashboard page.
        return redirect(routes.Users.editPost(post.id));
      } else {
        return forbidden(
          "<h3>You're not allowed to edit a post that is not yours.</h3>"
        ).as("text/html");
      }
    }
  }

  /**
   * Deletes a single post.
   */
  public static Result deletePost(Long id) {
    // find the post with this id and the current user.
    Post post = Post.find(id, connectedUser().get());

    // check if the post was found belonging to this user.
    if (post != null) {
      // delete the post.
      post.delete();
      // show an appropriate success message.
      flash("success", "The post was deleted successfully.");
    } else {
      // show an appropriate error message.
      flash("error", "That post doesn't exist or you're not allowed to access it.");
    }
    // redirect to the dashboard in each case.
    return redirect(routes.Users.dashboard());
  }

  /**
   * Class that contains password change request parameters.
   */
  public static class PasswordBox {
    @Required(message = "Please provide your current password.")
    public String old_password;
    @Required(message = "You must choose a new password.")
    public String new_password;
    public String password_again;

    public String validate() {
      // check that the two new passwords match.
      if (!new_password.equals(password_again)) {
        return "The new passwords must match.";
      } else {
        // retrieve the currently logged in user.
        User user = connectedUser().get();

        if (!user.password.equals(Crypto.sign(old_password))) {
          return "The current password you entered is incorrect.";
        } else {
          return null;
        }
      }
    }
  }

  /**
   * Renders the page to change a user's password.
   */
  public static Result changePassword() {
    return ok(change_pass.render(form(PasswordBox.class), connectedUser()));
  }

  /**
   * Process request data from user to change password.
   */
  public static Result doChangePassword() {
    Form<PasswordBox> passForm = form(PasswordBox.class).bindFromRequest();

    // check if the are any error messages.
    if (passForm.hasErrors()) {
      return ok(change_pass.render(passForm, connectedUser()));
    } else {
      // retrieve the pass change instance.
      PasswordBox pass = passForm.get();

      // retrieve the currently logged in user.
      User user = connectedUser().get();
      // update the current user's password with the new one.
      user.password = Crypto.sign(pass.new_password);
      user.update();

      flash("success", "You have changed your password.");
      // redirect to the change password page.
      return redirect(routes.Users.changePassword());
    }
  }
}
