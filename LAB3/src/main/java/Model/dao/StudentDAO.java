package Model.dao;

import Model.DB.DBConnection;
import Model.bean.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class StudentDAO {
    public ArrayList<Student> searchStudents(String keyword) {
        ArrayList<Student> list = new ArrayList<>();
        String trimmed = (keyword == null) ? "" : keyword.trim();

        String sql;
        boolean hasKeyword = !trimmed.isEmpty();
        if (hasKeyword) {
            sql = "SELECT id, name, age, university FROM sinhvien "
                + "WHERE name LIKE ? OR university LIKE ? OR CAST(id AS CHAR) LIKE ? "
                + "ORDER BY id";
        } else {
            sql = "SELECT id, name, age, university FROM sinhvien ORDER BY id";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (hasKeyword) {
                String pattern = "%" + trimmed + "%";
                stmt.setString(1, pattern);
                stmt.setString(2, pattern);
                stmt.setString(3, pattern);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Student student = new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("university")
                    );
                    list.add(student);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;
    }

    public Student getStudentById(int id) {
        if (id <= 0) {
            return null;
        }

        String sql = "SELECT id, name, age, university FROM sinhvien WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("university")
                    );
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void insertStudent(Student student) {
        if (student == null) {
            return;
        }

        boolean hasId = student.getId() > 0;
        String sql = hasId
                ? "INSERT INTO sinhvien(id, name, age, university) VALUES (?, ?, ?, ?)"
                : "INSERT INTO sinhvien(name, age, university) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (hasId) {
                stmt.setInt(1, student.getId());
                stmt.setString(2, student.getName());
                stmt.setInt(3, student.getAge());
                stmt.setString(4, student.getUniversity());
            } else {
                stmt.setString(1, student.getName());
                stmt.setInt(2, student.getAge());
                stmt.setString(3, student.getUniversity());
            }
            stmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateStudent(Student student) {
        if (student == null || student.getId() <= 0) {
            return;
        }

        String sql = "UPDATE sinhvien SET name = ?, age = ?, university = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getName());
            stmt.setInt(2, student.getAge());
            stmt.setString(3, student.getUniversity());
            stmt.setInt(4, student.getId());
            stmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteStudent(int id) {
        if (id <= 0) {
            return;
        }

        String sql = "DELETE FROM sinhvien WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
