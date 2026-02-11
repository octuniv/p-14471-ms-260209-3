package com.ll.wiseSaying.iohandler;

import java.util.Scanner;

public class ConsoleIOHandler implements IOHandler {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public String readLine() {
        return scanner.nextLine();
    }

    @Override
    public void print(String text) {
        System.out.print(text);
    }

    @Override
    public void println(String text) {
        System.out.println(text);
    }
}