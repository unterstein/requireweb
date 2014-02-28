package neo4j.models.user;

import helper.HashHelper;
import neo4j.models.CommentAbleModel;
import neo4j.services.Neo4JServiceProvider;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

import java.util.ArrayList;
import java.util.Iterator;

@NodeEntity
@TypeAlias("User")
public class User extends CommentAbleModel {

  @Indexed
  public String email;

  public String password;

  public UserState userState;

  public UserRole userRole;

  public static User create(String email, String password, User creator) {
    User user = new User();
    user.email = email;
    user.password = HashHelper.getInstance().sha(password);
    user.userState = UserState.ACTIVE;
    user.userRole = UserRole.USER;
    user.author = creator;
    save(user);
    return user;
  }

  public static User authenticate(String email, String password) {
    String hashedPw = HashHelper.getInstance().sha(password);
    User user = Neo4JServiceProvider.get().userRepository.findByEmail(email);
    if (user == null) {
      return null;
    }
    if (user.password != null && user.password.equals(hashedPw) && user.userState != UserState.LOCK) {
      return user;
    }
    return null;
  }

  public static boolean checkIfUserExsists(String email) {
    User user = getUserByEMail(email);
    return user != null && user.userState != UserState.LOCK;
  }

  public static User getUserByEMail(String email) {
    if (email == null || email.trim().length() == 0) {
      return null;
    }
    return Neo4JServiceProvider.get().userRepository.findByEmail(email);
  }

  public static void save(User user) {
    Neo4JServiceProvider.get().userRepository.save(user);
  }

  public static Iterator<User> findListForUser(User user) {
    switch (user.userRole) {
      case ADMIN:
        return Neo4JServiceProvider.get().userRepository.findAll().iterator();
      case USER:
        ArrayList<User> result = new ArrayList<User>();
        result.add(user);
        return result.iterator();
      default:
        return new ArrayList<User>().iterator();
    }
  }

  public boolean hasMinRole(UserRole userRole) {
    return this.userRole.getRoleNumber() >= userRole.getRoleNumber();
  }

  @Override
  public String toString() {
    String result = "";
    if (name != null) {
      result += name + " (";
    }
    result += email;
    if (name != null) {
      result += ")";
    }
    return result;
  }
}
