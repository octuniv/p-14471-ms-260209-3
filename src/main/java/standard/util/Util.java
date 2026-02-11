package standard.util;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Util {
    public static class file {

        private static Path getPath(String filePath) {
            return Paths.get(filePath);
        }

        private static void writeFile(Path path, String content) throws IOException {
            Files.writeString(path, content,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        }

        public static boolean rmdir(String dirPath) {
            return delete(dirPath);
        }

        public static void mkdir(String dirPath) {
            try {
                Files.createDirectories(getPath(dirPath));
            } catch (IOException e) {
                throw new RuntimeException("디렉토리 생성 실패: " + dirPath, e);
            }
        }

        private static void handleFileWriteError(Path path, String content, IOException e) {
            Path parentDir = path.getParent();
            if (parentDir != null && Files.notExists(parentDir)) {
                try {
                    Files.createDirectories(parentDir);
                    writeFile(path, content);
                } catch (IOException ex) {
                    throw new RuntimeException("파일 쓰기 실패: " + path, ex);
                }
            } else {
                throw new RuntimeException("파일 접근 실패: " + path, e);
            }
        }

        public static void set(String filePath, String content) {
            Path path = getPath(filePath);
            try {
                writeFile(path, content);
            } catch (IOException e) {
                handleFileWriteError(path, content, e);
            }
        }

        public static String get(String filePath, String defaultValue) {
            try {
                return Files.readString(getPath(filePath));
            } catch (IOException e) {
                return defaultValue;
            }
        }

        public static int getAsInt(String filePath, int defaultValue) {
            try {
                return Integer.parseInt(Files.readString(getPath(filePath)));
            } catch (IOException e) {
                return defaultValue;
            }
        }

        public static void touch(String filePath) {
            set(filePath, "");
        }

        public static boolean exists(String filePath) {
            return Files.exists(getPath(filePath));
        }

        private static class FileDeleteVisitor extends SimpleFileVisitor<Path> {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        }


        public static boolean delete(String filePath) {
            try {
                Files.walkFileTree(getPath(filePath), new FileDeleteVisitor());
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        public static Stream<Path> walkRegularFiles(String dirPath, String fileNameRegex) {
            try {
                return Files.walk(Path.of(dirPath))
                        .filter(Files::isRegularFile)
                        .filter(path -> path.getFileName().toString().matches(fileNameRegex));
            } catch (IOException e) {
                return Stream.empty();
            }
        }
    }

    public static class json {
        public static String toString(Map<String, Object> map) {
            if (map == null || map.isEmpty()) return "{}";

            StringBuilder sb = new StringBuilder("{\n");
            int size = map.size();
            int count = 0;

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                sb.append("    \"").append(entry.getKey()).append("\": ");
                appendValue(sb, entry.getValue());
                if (++count < size) sb.append(",");
                sb.append("\n");
            }

            sb.append("}");
            return sb.toString();
        }

        public static String toString(List<Map<String, Object>> list) {
            if (list == null || list.isEmpty()) return "[]";

            StringBuilder sb = new StringBuilder("[\n");
            int size = list.size();
            int count = 0;

            for (Map<String, Object> map : list) {
                // 각 맵을 4칸 추가 들여쓰기 적용
                String mapJson = toString(map);
                String[] lines = mapJson.split("\n", -1);
                for (String line : lines) {
                    sb.append("    ").append(line).append("\n");
                }
                if (++count < size) {
                    sb.setLength(sb.length() - 1); // 마지막 \n 제거
                    sb.append(",\n");
                }
            }

            sb.append("]");
            return sb.toString();
        }

        public static Map<String, Object> toMap(String jsonStr) {
            Map<String, Object> map = new LinkedHashMap<>();
            jsonStr = jsonStr.trim();

            // 중괄호 제거
            if (jsonStr.startsWith("{")) jsonStr = jsonStr.substring(1);
            if (jsonStr.endsWith("}")) jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
            jsonStr = jsonStr.trim();

            if (jsonStr.isEmpty()) return map;

            // 쉼표 기반 분리 (문자열 내 쉼표 없음 가정 - 단순 구현)
            String[] entries = jsonStr.split(",\\s*");

            for (String entry : entries) {
                entry = entry.trim();
                if (entry.isEmpty()) continue;

                // 콜론 위치 찾기 (문자열 내부 콜론 방지)
                int colonIdx = -1;
                boolean inString = false;
                for (int i = 0; i < entry.length(); i++) {
                    char c = entry.charAt(i);
                    if (c == '"' && (i == 0 || entry.charAt(i - 1) != '\\')) {
                        inString = !inString;
                    }
                    if (c == ':' && !inString) {
                        colonIdx = i;
                        break;
                    }
                }

                if (colonIdx == -1) continue;

                // 키 추출 (양쪽 따옴표 제거)
                String key = entry.substring(0, colonIdx).trim();
                if (key.startsWith("\"") && key.endsWith("\"")) {
                    key = key.substring(1, key.length() - 1);
                }

                // 값 파싱
                String valueStr = entry.substring(colonIdx + 1).trim();
                map.put(key, parseValue(valueStr));
            }
            return map;
        }

        public static List<Map<String, Object>> toListedMap(String jsonStr) {
            List<Map<String, Object>> list = new ArrayList<>();
            jsonStr = jsonStr.trim();

            // 대괄호 제거
            if (jsonStr.startsWith("[")) jsonStr = jsonStr.substring(1);
            if (jsonStr.endsWith("]")) jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
            jsonStr = jsonStr.trim();

            if (jsonStr.isEmpty()) return list;

            // 중괄호 깊이 기반 파싱 (문자열 내부 중괄호 무시)
            int depth = 0;
            int start = -1;
            boolean inString = false;

            for (int i = 0; i < jsonStr.length(); i++) {
                char c = jsonStr.charAt(i);

                // 이스케이프 처리된 따옴표 제외
                if (c == '"' && (i == 0 || jsonStr.charAt(i - 1) != '\\')) {
                    inString = !inString;
                }
                if (inString) continue;

                if (c == '{') {
                    if (depth == 0) start = i;
                    depth++;
                } else if (c == '}') {
                    depth--;
                    if (depth == 0 && start != -1) {
                        String mapJson = jsonStr.substring(start, i + 1);
                        list.add(toMap(mapJson));
                        start = -1;
                    }
                }
            }

            return list;
        }

        // --- 헬퍼 메서드 ---
        private static void appendValue(StringBuilder sb, Object value) {
            if (value == null) {
                sb.append("null");
            } else if (value instanceof String) {
                sb.append("\"").append(value).append("\"");
            } else if (value instanceof Number || value instanceof Boolean) {
                sb.append(value);
            } else {
                sb.append("\"").append(value.toString()).append("\"");
            }
        }

        private static Object parseValue(String valueStr) {
            valueStr = valueStr.trim();

            if (valueStr.startsWith("\"") && valueStr.endsWith("\"")) {
                return valueStr.substring(1, valueStr.length() - 1);
            } else if ("true".equals(valueStr)) {
                return true;
            } else if ("false".equals(valueStr)) {
                return false;
            } else if (valueStr.contains(".")) {
                return Double.parseDouble(valueStr);
            } else {
                return Integer.parseInt(valueStr);
            }
        }
    }
}
