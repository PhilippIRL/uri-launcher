package de.ppluss.urilauncher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView activityList;
    EditText uriInput;

    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activityList = findViewById(R.id.activityList);
        uriInput = findViewById(R.id.uriInput);

        uriInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {} @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateActivityList();
            }
        });

        activityList.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        activityList.setLayoutManager(layoutManager);

        updateActivityList();

    }

    public void updateActivityList() {
        String uri = uriInput.getText().toString();

        if(uri.length() == 0) {
            AppInfo info = createDummyAppInfo("No URI", "Please enter your URI in the text field above");
            activityList.setAdapter(new ActivityAdapter(Collections.singletonList(info), this));
            return;
        }

        Uri androidUri = null;
        try {
            androidUri = Uri.parse(uri);
        } catch(Exception ignored) {}

        if(androidUri != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(androidUri);
            List<ResolveInfo> resolveInfos = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_ALL);

            List<AppInfo> infos = new ArrayList<>();

            PackageManager pm = getPackageManager();

            for(ResolveInfo rinfo : resolveInfos) {
                AppInfo info = new AppInfo();
                info.activityName = rinfo.activityInfo.name;
                info.appName = rinfo.activityInfo.applicationInfo.loadLabel(getPackageManager()).toString();
                info.packageName = rinfo.activityInfo.applicationInfo.packageName;
                try {
                    info.icon = pm.getApplicationIcon(rinfo.activityInfo.applicationInfo.packageName);
                } catch (PackageManager.NameNotFoundException e) {
                    info.icon = null;
                }
                infos.add(info);
            }

            if(infos.size() == 0) {
                AppInfo info = createDummyAppInfo("Nothing found", "No app was found that can handle this URI");
                activityList.setAdapter(new ActivityAdapter(Collections.singletonList(info), this));
            } else {
                activityList.setAdapter(new ActivityAdapter(infos, this));
            }

        } else {
            AppInfo info = createDummyAppInfo("Invalid URI", "The entered URI is invalid");
            activityList.setAdapter(new ActivityAdapter(Collections.singletonList(info), this));
        }
    }

    public void launchApp(AppInfo info) {
        if(info.dummy) return;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setClassName(info.packageName, info.activityName);
        intent.setData(Uri.parse(uriInput.getText().toString()));
        startActivity(intent);
    }

    private AppInfo createDummyAppInfo(String title, String text) {
        AppInfo info = new AppInfo();
        info.appName = title;
        info.packageName = text;
        info.dummy = true;
        info.icon = ContextCompat.getDrawable(this, R.drawable.dummy_entry);
        return info;
    }

}