package com.github.kr328.clipboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.github.kr328.clipboard.shared.IClipboardWhitelist;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class MainActivity extends Activity {
    private final ExecutorService threads = Executors.newSingleThreadExecutor();
    private final AppAdapter adapter = new AppAdapter(this);

    private ListView appsList;
    private ProgressBar loading;

    private boolean showSystemApps = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        appsList = findViewById(R.id.apps);
        loading = findViewById(R.id.loading);

        appsList.setAdapter(adapter);

        appsList.setOnItemClickListener((parent, view, position, id) -> {
            final boolean status = adapter.invertSelected(position);

            applyPackage(((App) adapter.getItem(position)).getPackageName(), status);
        });

        getActionBar().setTitle(R.string.app_name);

        loadApps();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        threads.shutdownNow();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if ( item.getItemId() == R.id.systemApps ) {
            item.setChecked(!item.isChecked());

            showSystemApps = item.isChecked();

            loadApps();

            return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    private void loadApps() {
        threads.submit(() -> {
            updateLoading(false);

            try {
                final PackageManager pm = getPackageManager();
                final IClipboardWhitelist service = Service.getService();
                final HashSet<String> whitelist = new HashSet<>(Arrays.asList(service.queryPackages()));

                final List<App> apps = pm.getInstalledApplications(0).stream()
                        .filter((info) -> showSystemApps || (info.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
                        .map((info) -> App.fromApplicationInfo(info, pm, whitelist.contains(info.packageName)))
                        .sorted()
                        .collect(Collectors.toList());

                runOnUiThread(() -> adapter.updateApps(apps));
                runOnUiThread(this::showNotice);
            } catch (Exception e) {
                showError(e);
            } finally {
                updateLoading(true);
            }
        });
    }

    private void updateLoading(boolean loaded) {
        runOnUiThread(() -> {
            if (loaded) {
                appsList.setVisibility(View.VISIBLE);
                loading.setVisibility(View.INVISIBLE);
            } else {
                appsList.setVisibility(View.INVISIBLE);
                loading.setVisibility(View.VISIBLE);
            }
        });
    }

    private void applyPackage(String packageName, boolean status) {
        threads.submit(() -> {
            try {
                final IClipboardWhitelist service = Service.getService();

                if (status) {
                    service.addPackage(packageName);
                } else {
                    service.removePackage(packageName);
                }
            } catch (Exception e) {
                showError(e);
            }
        });
    }

    private void showError(Exception e) {
        final int title;
        final int content;

        if (e instanceof Service.ServiceNotFoundException) {
            title = R.string.service_not_found;
            content = R.string.service_not_found_description;
        } else if (e instanceof Service.VersionNotMatchedException) {
            title = R.string.version_not_mismatch;
            content = R.string.version_not_mismatch_description;
        } else {
            title = R.string.unknown_error;
            content = R.string.unknown_error_description;
        }

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(content)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, (dialog, which) -> finish())
                .show();
    }

    private void showNotice() {
        if (getSharedPreferences("ui", MODE_PRIVATE).getBoolean("skip_notice", false))
            return;

        new AlertDialog.Builder(this)
                .setTitle(R.string.notice)
                .setMessage(R.string.application_clipboard_detect)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, (d, w) -> {})
                .show();

        getSharedPreferences("ui", MODE_PRIVATE).edit()
                .putBoolean("skip_notice", true)
                .apply();
    }
}
