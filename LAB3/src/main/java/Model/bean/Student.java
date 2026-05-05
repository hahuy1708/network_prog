package Model.bean;

public class Student {
    private int id;
    private String name;
    private int age;
    private String university;

    public Student() {
    }

    public Student(int id, String name, int age, String university) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.university = university;
    }

    public Student(String name, int age, String university) {
        this(0, name, age, university);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }
}
