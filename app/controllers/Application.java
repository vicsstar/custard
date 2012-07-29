package controllers;

import play.*;
import play.data.Form;
import play.mvc.*;
import play.cache.Cache;

import static play.libs.Scala.Option;
import static play.data.validation.Constraints.Required;

import models.*;

import views.html.*;

/**
 * The main application controller.
 * @author Victor Igbokwe (vicsstar@yahoo.com) 2012-07-28 10:08 PM
 */
public class Application extends BaseController {

  /**
   * This class is only meant for login validation
   * within the context of the login page.
   */
  public static class Login {
    @Required
    public String username;
    @Required
    public String password;

    // validate the login details.
    public String validate() {

      // verify the user's login details.
      if (User.authenticate(username, password) == null) {
        return "Invalid login. Please try again.";
      } else return null;
    }
  }

  // Renders the home page.
  public static Result index() {
    // retrieve posts 10 at a time.
    java.util.List<Post> posts = Post.all();
    return ok(index.render(posts, connectedUser()));
  }

  /**
   * Renders the login page.
   */
  public static Result login() {
    // pass the form and current user as parameters to the render method.
    return ok(login.render(form(Login.class), connectedUser()));
  }

  /**
   * Processes information retrieved from the login form.
   */
  public static Result doLogin() {
    // bind the form request parameters to a form.
    Form<Login> loginForm = form(Login.class).bindFromRequest();

    // check if the resulting form contained errors.
    if (loginForm.hasErrors()) {
      // return back to the login page with a bad-request HTTP response.
      return badRequest(login.render(loginForm, connectedUser()));
    } else {
      session("username", loginForm.get().username);
      return redirect(routes.Users.dashboard());
    }
  }

  /**
   * Logs a user out of the current session.
   */
  public static Result logout() {
    // rid the session of the user's details.
    session().clear();
    return redirect(
      routes.Application.index()
    );
  }

  /*
   * A class meant for searching the blog.
   */
  public static class Search {
    @Required
    private String query;

    public String getQuery() {
      return query;
    }

    public void setQuery(String query) {
      this.query = query;
    }
  }

  /**
   * Processes queries issued by a client.
   */
  public static Result blogSearch() {
    Form<Search> searchForm = form(Search.class).bindFromRequest();

    // make sure the query parameter was provided.
    if (!searchForm.hasErrors()) {
      // retrieve the query parameter.
      String query = searchForm.get().query;
      // retrieve posts based on the query.
      java.util.List<Post> posts = Post.search(query);
      return ok(blog_search.render(query, posts, connectedUser()));
    } else {
      return ok(blog_search.render(null, new java.util.ArrayList(), connectedUser()));
    }
  }

  /**
   * Renders a single post.
   */
  public static Result showPost(Long id) {
    // retrieve a post based on the provided id.
    Post p = Post.findById(id);
    return ok(post.render(p, connectedUser()));
  }
}
