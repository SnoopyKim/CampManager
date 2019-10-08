package android.example.campmanager;

public class Record {
    private String date;
    private String volume;
    private String eng;
    private String math;
    private String remarks;

    public Record(String date, String volume, String eng, String math, String remarks) {
        this.date = date;
        this.volume = volume;
        this.eng = eng;
        this.math = math;
        this.remarks = remarks;
    }

    public String getDate() {
        return date;
    }

    public String getVolume() {
        return volume;
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
