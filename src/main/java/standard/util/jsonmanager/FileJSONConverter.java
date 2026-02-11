package standard.util.jsonmanager;

import standard.util.Util;

import java.util.List;
import java.util.Map;

public class FileJSONConverter implements JSONConverter{
    private final String dbFolderPath;
    private final String dbPath;
    private final boolean devMode;
    public FileJSONConverter(boolean devMode, String dbFolderPath) {
        this.devMode = devMode;
        this.dbPath = dbFolderPath + "/data.json";
        this.dbFolderPath = dbFolderPath;
        Util.file.mkdir(dbFolderPath);
    }

    @Override
    public List<Map<String, Object>> readFile() {
        return Util.json.toListedMap(Util.file.get(dbPath, "[]"));
    }

    @Override
    public void writeFile(List<Map<String, Object>> contents) {
        Util.file.set(dbPath, Util.json.toString(contents));
    }

    @Override
    public void close() throws Exception {
        if (devMode) Util.file.rmdir(dbFolderPath);
    }
}
