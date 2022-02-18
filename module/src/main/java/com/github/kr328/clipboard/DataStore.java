package com.github.kr328.clipboard;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import android.os.UserHandleHidden;
import android.util.Pair;

import com.github.kr328.clipboard.shared.Log;
import com.github.kr328.zloader.ZygoteLoader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class DataStore {
    public static final Path DATA_PATH = Paths.get(ZygoteLoader.getDataDirectory(), "whitelist.list");

    public final static DataStore instance = new DataStore();

    private Map<Integer, Set<String>> packages = new HashMap<>();

    private DataStore() {

    }

    private static Pair<Integer, String> parseWhitelistLine(String line) {
        String[] segments = line.split(":", 2);
        switch (segments.length) {
            case 1: {
                return new Pair<>(UserHandleHidden.USER_SYSTEM, segments[0]);
            }
            case 2: {
                final int userId = Integer.parseInt(segments[0]);
                return new Pair<>(userId, segments[1]);
            }
        }

        return null;
    }

    synchronized void reload() {
        Log.i("Load data from " + DATA_PATH);

        try {
            List<String> data = Files.readAllLines(DATA_PATH);

            packages = data.stream()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(DataStore::parseWhitelistLine)
                    .filter(Objects::nonNull)
                    .peek(s -> Log.i("userId=" + s.first + ", packageName=" + s.second))
                    .collect(Collectors.groupingBy(p -> p.first))
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e ->
                            e.getValue().stream().map(p -> p.second).collect(Collectors.toSet())
                    ));

            Log.i("Reloaded");
        } catch (IOException e) {
            if (!(e instanceof FileNotFoundException)) {
                Log.w("Load config file " + DATA_PATH + ": " + e, e);
            }
        }
    }

    synchronized boolean isExempted(String packageName, int userId) {
        final Set<String> scoped = packages.get(userId);
        if (scoped == null) {
            return false;
        }

        return scoped.contains(packageName);
    }

    synchronized void addExempted(String packageName, int userId) {
        packages.computeIfAbsent(userId, usr -> new HashSet<>()).add(packageName);

        writePackages();
    }

    synchronized void removeExempted(String packageName, int userId) {
        final Set<String> scoped = packages.get(userId);
        if (scoped == null) {
            return;
        }

        scoped.remove(packageName);

        writePackages();
    }

    synchronized Set<String> getAllExempted(int userId) {
        final Set<String> scoped = packages.get(userId);
        if (scoped == null) {
            return Collections.emptySet();
        }

        return scoped;
    }

    synchronized Map<Integer, Set<String>> getAllExempted() {
        return packages;
    }

    private void writePackages() {
        try {
            final List<String> lines = packages.entrySet()
                    .stream()
                    .flatMap(entry -> entry.getValue().stream().map(pkg -> entry.getKey() + ":" + pkg))
                    .collect(Collectors.toList());

            Files.write(DATA_PATH, lines, WRITE, CREATE, TRUNCATE_EXISTING);
        } catch (IOException e) {
            Log.w("Save whitelist.list: " + e, e);
        }
    }
}
