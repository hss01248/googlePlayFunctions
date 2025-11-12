package com.hss01248.googleplay;

import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.LogUtils;

/**
 * @Despciption todo
 * @Author hss
 * @Date 11/12/25 9:55 AM
 * @Version 1.0
 */
public class TransActivity extends AppCompatActivity {

    ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        LogUtils.d("Update flow returned: " + result.getResultCode(),result.toString());
                        LogUtils.d(result.toString());
                        finish();
                        // handle callback
                        if (result.getResultCode() != RESULT_OK) {
                            // If the update is canceled or fails,
                            // you can request to start the update again.
                        }
                    }
                });
    }

    public ActivityResultLauncher<IntentSenderRequest> getActivityResultLauncher() {
        return activityResultLauncher;
    }
}
