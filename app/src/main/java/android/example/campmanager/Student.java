package android.example.campmanager;

public class Student {
    private String id;
    private String name;
    private String age;
    private String photo;

    /*
        Data objects requires that any objects to have a public no-argument constuctor.
        Thi is needed to instantiate that that particular object.
     */
    public Student() {}
    public Student(String id, String name, String age, String photo) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
