package android.example.campmanager;

import java.io.Serializable;

public class Result implements Serializable {

    private String id;
    private String name;
    private String eng;
    private String math;
    private String remarks;

    public Result() {}

    public Result(String id, String name, String eng, String math, String remarks) {
        this.id = id;
        this.name = name;
        this.eng = eng;
        this.math = math;
        this.remarks = remarks;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEng() {
        return eng;
    }

    public String getMath() {
        return math;
    }

    public String getRemarks() {
        return remarks;
    }
}
