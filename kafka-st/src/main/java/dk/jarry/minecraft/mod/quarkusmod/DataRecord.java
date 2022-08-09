package dk.jarry.minecraft.mod.quarkusmod;

public class DataRecord {

    Long count;
    String title;
    String body;

    public DataRecord() {
    }

    public DataRecord(Long count) {
        this.count = count;
    }

    public DataRecord(Long count, String title, String body) {
        this.count = count;
        this.title = title;
        this.body = body;
    }

    public Long getCount() {
        return count;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }    
    
}
