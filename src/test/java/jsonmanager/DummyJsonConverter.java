package jsonmanager;

import standard.util.jsonmanager.JSONConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DummyJsonConverter implements JSONConverter {
    @Override
    public List<Map<String, Object>> readFile() {
        return new ArrayList<>();
    }

    @Override
    public void writeFile(List<Map<String, Object>> contents) {
        return;
    }

    @Override
    public void close() throws Exception {

    }
}
