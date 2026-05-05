package Model.bo;

import java.util.ArrayList;

import Model.bean.User;
import Model.dao.UserDAO;

public class UserBO {
    private UserDAO dao = new UserDAO();

    public ArrayList<User> searchUsers(String keyword) {
        return dao.searchUsers(keyword);
    }

    public User getUserByUsername(String username) {
        return dao.getUserByUsername(username);
    }

    public void updateUser(User user) {
        dao.updateUser(user);
    }

    public void deleteUser(String username) {
        dao.deleteUser(username);
    }

    public boolean existsUsername(String username) {
        return dao.existsUsername(username);
    }

    public boolean registerUser(User user) {
        if (user == null || dao.existsUsername(user.getUsername())) {
            return false;
        }
        dao.insertUser(user);
        return true;
    }
}
