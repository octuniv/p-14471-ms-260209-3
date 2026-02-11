package standard.util.jsonmanager;

import java.util.List;
import java.util.Map;

public interface JSONConverter extends AutoCloseable {
    List<Map<String, Object>> readFile();
    void writeFile(List<Map<String, Object>> contents);
}
