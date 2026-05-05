package Model.bo;

import java.util.ArrayList;

import Model.bean.Student;
import Model.dao.StudentDAO;

public class StudentBO {
    private StudentDAO dao = new StudentDAO();

    public ArrayList<Student> searchStudents(String keyword) {
        return dao.searchStudents(keyword);
    }

    public Student getStudentById(int id) {
        return dao.getStudentById(id);
    }

    public void insertStudent(Student student) {
        dao.insertStudent(student);
    }

    public void updateStudent(Student student) {
        dao.updateStudent(student);
    }

    public void deleteStudent(int id) {
        dao.deleteStudent(id);
    }
}
