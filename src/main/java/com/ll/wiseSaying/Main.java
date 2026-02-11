package com.ll.wiseSaying;

import com.ll.wiseSaying.iohandler.ConsoleIOHandler;
import com.ll.wiseSaying.iohandler.IOHandler;
import standard.util.jsonmanager.FileJSONConverter;
import standard.util.jsonmanager.JSONConverter;

public class Main {
    public static void main(String[] args) throws Exception {
        IOHandler io = new ConsoleIOHandler();

        try (JSONConverter converter = new FileJSONConverter(false, "db/wiseSaying");
             App app = new App(io, converter)) {
            app.run();
        }
    }
}
