package mu.semtech.poc.shacl.rdf;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class JsonReport {
    private boolean conforms;
    private List<Entry> entries;

    @Data
    @Builder
    public static class Entry {
        private String property;
        private String message;
    }
}
