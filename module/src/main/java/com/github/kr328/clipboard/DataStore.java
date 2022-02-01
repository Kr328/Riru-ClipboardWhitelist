package com.github.kr328.clipboard;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import com.github.kr328.clipboard.shared.Log;
import com.github.kr328.zloader.ZygoteLoader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DataStore {
    public static final String DATA_PATH = ZygoteLoader.getDataDirectory() + "/whitelist.list";

    public final static DataStore instance = new DataStore();

    private final Set<String> packages = new HashSet<>();

    private DataStore() {
        Log.i("Load data from " + DATA_PATH);

        try {
            List<String> data = Files.readAllLines(Paths.get(DATA_PATH));

            packages.clear();

            data.stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .peek(s -> Log.i("package:" + s))
                    .forEach(packages::add);

            Log.i("Reloaded");
        } catch (IOException e) {
            if (!(e instanceof FileNotFoundException)) {
                Log.w("Load config file " + DATA_PATH + ": " + e, e);
            }
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
            final FileAttribute<Set<PosixFilePermission>> permissions = PosixFilePermissions.asFileAttribute(
                    PosixFilePermissions.fromString("rwx------")
            );

            Files.createDirectories(Paths.get(DATA_PATH).getParent(), permissions);

            Files.write(Paths.get(DATA_PATH), packages, WRITE, CREATE, TRUNCATE_EXISTING);
        } catch (IOException e) {
            Log.w("Save whitelist.list: " + e, e);
        }
    }
}