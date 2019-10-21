package android.example.campmanager;

import java.io.Serializable;

public class Student implements Serializable {
    private String id;
    private String name;
    private String age;
    private String photo;
    private String teacher;

    /*
        Data objects requires that any objects to have a public no-argument constuctor.
        Thi is needed to instantiate that that particular object.
     */
    public Student() { }
    public Student(String id, String name, String age, String photo, String teacher) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.photo = photo;
        this.teacher = teacher;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getPhoto() {
        return photo;
    }

    public String getId() {
        return id;
    }

    public String getTeacher() {
        return teacher;
    }
}
