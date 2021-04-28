package com.github.kr328.clipboard;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.github.kr328.clipboard.shared.Constants.TAG;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

public class DataStore {
    public static final String DATA_PATH = "/data/misc/clipboard/";
    public static final String DATA_FILE = "whitelist.list";

    public final static DataStore instance = new DataStore();

    private final Set<String> packages = new HashSet<>();

    private DataStore() {
        try {
            List<String> data = Files.readAllLines(Paths.get(DATA_PATH, DATA_FILE));

            packages.clear();

            data.stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .peek(s -> Log.i(TAG, "package:" + s))
                    .forEach(packages::add);

            Log.i(TAG, "Reloaded");
        } catch (IOException e) {
            Log.w(TAG, "Load config file " + DATA_PATH + DATA_FILE + " failure", e);
        }
    }

    synchronized boolean shouldIgnore(String packageName) {
        return !packages.contains(packageName);
    }

    synchronized void addPackage(String packageName) {
        packages.add(packageName);

        writePackages();
    }

    synchronized void removePackage(String packageName) {
        packages.remove(packageName);

        writePackages();
    }

    synchronized Set<String> queryPackages() {
        return new HashSet<>(packages);
    }

    private void writePackages() {
        try {
            //noinspection ResultOfMethodCallIgnored
            new File(DATA_PATH).mkdirs();

            Files.write(Paths.get(DATA_PATH, DATA_FILE), packages, WRITE, CREATE, TRUNCATE_EXISTING);
        } catch (IOException e) {
            Log.w(TAG, "Save whitelist.list failure", e);
        }
    }
}