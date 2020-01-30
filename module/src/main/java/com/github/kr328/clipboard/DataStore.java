package com.github.kr328.clipboard;

import android.os.FileObserver;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class DataStore extends FileObserver {
    private Set<String> packages = Collections.synchronizedSet(new HashSet<>());
    private Handler handler;

    DataStore() {
        super(new File(Constants.DATA_PATH), CREATE | MOVED_TO | CLOSE_WRITE | DELETE | MOVED_FROM);

        new Thread(() -> {
            Looper.prepare();

            handler = new Handler();

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
        if (Constants.WHITELIST_FILE.equals(path)) {
            handler.removeMessages(0);
            handler.postDelayed(this::load, 1000);

        }
    }

    private void load() {
        try {
            String data = Utils.readFile(new File(Constants.DATA_PATH, Constants.WHITELIST_FILE));

            packages.clear();

            Stream.of(data.split("\\s"))
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .peek(s -> Log.i(Constants.TAG, "package:" + s))
                    .forEach(packages::add);

            Log.i(Constants.TAG, "Reloaded");
        } catch (IOException e) {
            Log.w(Constants.TAG, "Load config file " + Constants.DATA_PATH + Constants.WHITELIST_FILE + " failure", e);
        }
    }

    void postLoad() {
        handler.removeMessages(0);
        handler.postDelayed(this::load, 1000);
    }

    Set<String> getPackages() {
        return packages;
    }
}
