package com.github.kr328.clipboard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

class Utils {
    static String readFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder result = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null)
            result.append(line).append("\n");

        reader.close();

        return result.toString();
    }
}
