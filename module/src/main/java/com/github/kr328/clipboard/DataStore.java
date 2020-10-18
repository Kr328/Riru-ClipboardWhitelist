package com.github.kr328.clipboard;

import android.content.pm.IPackageManager;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DataStore extends FileObserver {
    public static final String DATA_PATH = "/data/misc/clipboard/";
    public static final String DATA_FILE = "whitelist.list";

    private final Set<Integer> packages = Collections.synchronizedSet(new HashSet<>());
    private final IPackageManager packageManager;

    private Handler handler;

    DataStore(IPackageManager packageManager) {
        super(new File(DATA_PATH), CREATE | MOVED_TO | CLOSE_WRITE | DELETE | MOVED_FROM);

        this.packageManager = packageManager;

        new Thread(() -> {
            Looper.prepare();

            handler = new Handler(Looper.myLooper());

            synchronized (DataStore.this) {
                this.notify();
            }

            Looper.loop();
        }).start();

        synchronized (this) {
            if (handler != null)
                return;
            try {
                this.wait();
            } catch (InterruptedException ignored) {}
        }
    }

    @Override
    public void onEvent(int event, String path) {
        if (DATA_FILE.equals(path)) {
            handler.removeMessages(0);
            handler.postDelayed(this::load, 1000);
        }
    }

    private void load() {
        try {
            List<String> data = Files.readAllLines(Paths.get(DATA_PATH, DATA_FILE));

            packages.clear();

            data.stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .peek(s -> Log.i(Injector.TAG, "package:" + s))
                    .map(this::getUidOfPackage)
                    .filter(uid -> uid > 0)
                    .forEach(packages::add);

            Log.i(Injector.TAG, "Reloaded");
        } catch (IOException e) {
            Log.w(Injector.TAG, "Load config file " + DATA_PATH + DATA_FILE + " failure", e);
        }
    }

    void postLoad() {
        handler.removeMessages(0);
        handler.postDelayed(this::load, 1000);
    }

    Set<Integer> getPackageUids() {
        return packages;
    }

    private int getUidOfPackage(String pkg) {
        try {
            return packageManager.getPackageUid(pkg, 0, 0);
        } catch (RemoteException e) {
            Log.w(Injector.TAG, "Obtains uid of " + pkg + " failure.", e);

            return -1;
        }
    }
}