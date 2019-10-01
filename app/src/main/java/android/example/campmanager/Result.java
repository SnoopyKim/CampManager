package android.example.campmanager;

import java.io.Serializable;

public class Result implements Serializable {

    private String path;
    private String name;
    private String volume;
    private String eng;
    private String math;
    private String remarks;

    public Result() {}

    public Result(String path, String name, String volume, String eng, String math, String remarks) {
        this.path = path;
        this.name = name;
        this.volume = volume;
        this.eng = eng;
        this.math = math;
        this.remarks = remarks;
    }

    public String getPath() {
        return path;
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

    public String getVolume() {
        return volume;
    }
}
