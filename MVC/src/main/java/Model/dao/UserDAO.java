package Model.dao;

import Model.DB.DBConnection;
import Model.bean.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class UserDAO {
    public boolean existsUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public void insertUser(User user) {
        if (user == null) {
            return;
        }

        String sql = "INSERT INTO users(username, password, firstname, lastname, roles) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setString(5, user.getRoles());
            stmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public User getUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT username, firstname, lastname, roles FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getString("username"),
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getString("roles")
                    );
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public ArrayList<User> searchUsers(String keyword) {
        ArrayList<User> list = new ArrayList<>();
        String trimmed = (keyword == null) ? "" : keyword.trim();

        String sql;
        boolean hasKeyword = !trimmed.isEmpty();
        if (hasKeyword) {
            sql = "SELECT username, firstname, lastname, roles FROM users "
                + "WHERE firstname LIKE ? OR lastname LIKE ? ORDER BY username";
        } else {
            sql = "SELECT username, firstname, lastname, roles FROM users ORDER BY username";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (hasKeyword) {
                String pattern = "%" + trimmed + "%";
                stmt.setString(1, pattern);
                stmt.setString(2, pattern);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User(
                        rs.getString("username"),
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getString("roles")
                    );
                    list.add(user);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;
    }

    public void updateUser(User user) {
        if (user == null) {
            return;
        }

        String sql = "UPDATE users SET firstname = ?, lastname = ?, roles = ? WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getRoles());
            stmt.setString(4, user.getUsername());
            stmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteUser(String username) {
        if (username == null || username.trim().isEmpty()) {
            return;
        }

        String sql = "DELETE FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username.trim());
            stmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
