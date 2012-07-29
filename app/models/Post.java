package models;

import java.util.Date;
import java.util.List;
import javax.persistence.*;

import play.db.ebean.Model;
import static play.data.validation.Constraints.Required;

import static play.libs.Scala.Option;

/**
 * Defines a post created by a user.
 * @author Victor Igbokwe (vicsstar@yahoo.com) 2012-07-29 1:17 AM
 */
@Entity
@Table(name = "posts")
public class Post extends Model {

  @Id
  public Long id;

  @Required
  @Basic(optional = false)
  public String title;

  @Required
  @Column(length = 4096, nullable = false)
  public String content;

  // defined a many to one relationship with a user.
  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", referencedColumnName="id")
  public User user;

  @Column(name = "created_on", nullable = false)
  public Date createdOn = new Date();
  @Column(name = "modified_on")
  public Date modifiedOn = new Date();

  // a finder class method for making queries to the database.
  public static Finder<Long, Post> finder = new Finder<Long, Post>(
    Long.class, Post.class
  );

  /**
   * Returns the total number of posts associated with a user.
   */
  public static int count(User user) {
    return finder.where().eq("user", user)
      .findPagingList(1).getTotalRowCount();
  }

  /**
   * Retrieves "all" posts.
   */
  public static List<Post> all() {
    return finder.where().orderBy("created_on desc").findList();
  }

  /**
   * Retrieves all posts associated with a user.
   */
  public static List<Post> all(User user) {
    return finder.where().eq("user", user)
      .orderBy("created_on desc").findList();
  }

  /**
   * Retrieves a single post belonging to a user.
   */
  public static Post find(Long id, User user) {
    return finder.where().eq("user", user).eq("id", id).findUnique();
  }

  /**
   * Retrieves a single post based on an id.
   */
  public static Post findById(Long id) {
    return finder.where().eq("id", id).findUnique();
  }

  /**
   * Performs a database search on relevant fields of a post.
   */
  public static List<Post> search(String query) {
    return finder.where().like("content", "%" + query + "%")
      .orderBy("created_on desc").findList();
  }

  /**
   * Retrieves a list of posts by page.
   */
  public static List<Post> byPage(int page, int threshold) {
    return finder.where().orderBy("created_on desc")
      .findPagingList(threshold).getPage(page).getList();
  }
}
