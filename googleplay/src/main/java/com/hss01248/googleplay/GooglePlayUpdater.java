package com.hss01248.googleplay;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;

/**
 * @Despciption todo
 * @Author hss
 * @Date 11/11/25 5:24 PM
 * @Version 1.0
 */
public class GooglePlayUpdater {
    public static void checkUpdate(boolean toastError){
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(Utils.getApp());

// Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            LogUtils.d("availableVersionCode:"+appUpdateInfo.availableVersionCode(),
                    "updateAvailability:"+appUpdateInfo.updateAvailability(),
                    "isUpdateTypeAllowed(AppUpdateType.IMMEDIATE):"+appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE),
                    "bytesDownloaded:"+appUpdateInfo.bytesDownloaded(),
                    "totalBytesToDownload:"+appUpdateInfo.totalBytesToDownload(),
                    "clientVersionStalenessDays:"+appUpdateInfo.clientVersionStalenessDays(),
                    "packageName:"+appUpdateInfo.packageName(),
                    "installStatus:"+appUpdateInfo.installStatus(),
                    "updatePriority:"+appUpdateInfo.updatePriority()
            );
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE){
                if(appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){
                    //整个页面,强制更新
                    forceUpdate(appUpdateManager, appUpdateInfo,toastError);

                }else if(appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)){
                    //弹窗,非强制更新
                        // Checks that the update is not stalled during 'onStart()'.
                        // However, you should execute this check at all entry points into the app.
                    dialogUpdate(appUpdateManager, appUpdateInfo,toastError);
                }else {
                    LogUtils.w("不支持的更新类型");
                }
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                // If an in-app update is already running, resume the update.
                dialogUpdate(appUpdateManager,appUpdateInfo,toastError);
            } else {
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    appUpdateManager.completeUpdate();
                }else {
                    LogUtils.d("无更新");
                }
            }
        });
        appUpdateInfoTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //The Play Store app is either not installed or not the official version
                LogUtils.w(e);
                if(toastError){
                    ToastUtils.showShort(e.getMessage());
                }
                //Install Error(-10): The app is not owned by any user on this device. An app is "owned" if it has been acquired from Play
            }
        });

    }

    private static void dialogUpdate(AppUpdateManager appUpdateManager, AppUpdateInfo appUpdateInfo,boolean toastError) {
// 创建安装状态更新监听器
        InstallStateUpdatedListener installListener = new InstallStateUpdatedListener() {
            @Override
            public void onStateUpdate(InstallState installState) {
                // 处理下载中状态
                if (installState.installStatus() == InstallStatus.DOWNLOADING) {
                    // 已下载字节数
                    long bytesDownloaded = installState.bytesDownloaded();
                    // 总需下载字节数
                    long totalBytesToDownload = installState.totalBytesToDownload();
                    // TODO: 向用户展示下载进度提示
                    LogUtils.d("已下载字节数:"+bytesDownloaded,"总字节数:"+totalBytesToDownload);

                } else if (installState.installStatus() == InstallStatus.PENDING) {
                    LogUtils.d("等待网络链接,或者连接中...");
                }
                // 处理下载完成状态
                else if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                    // 注销监听器
                    appUpdateManager.unregisterListener(this);
                    // 完成更新（触发应用重启安装）
                    appUpdateManager.completeUpdate();
                    //Error committing session: INSTALL_FAILED_UID_CHANGED: 
                    // Package xxxpackagename shared user changed from <nothing> to xxxpackagename
                    //popupSnackbarForCompleteUpdate();
                }
            }
        };

        // 注册安装状态监听器
        appUpdateManager.registerListener(installListener);
        try{
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.FLEXIBLE,
                    ActivityUtils.getTopActivity(),
                    57 // Now uses a valid 16-bit value
            );
        }catch (Throwable throwable){
            LogUtils.w(throwable);
            if(toastError){
                ToastUtils.showShort(throwable.getMessage());
            }
        }
    }

    private static void forceUpdate(AppUpdateManager appUpdateManager, AppUpdateInfo appUpdateInfo,boolean toastError) {
        try{
            //垃圾谷歌api,自己的api自己崩溃掉:Can only use lower 16 bits for requestCode:
                                   /* appUpdateManager.startUpdateFlowForResult(
                                            // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                            appUpdateInfo,
                                            // an activity result launcher registered via registerForActivityResult
                                            activity.getActivityResultLauncher(),
                                            // Or pass 'AppUpdateType.FLEXIBLE' to newBuilder() for
                                            // flexible updates.
                                            AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE)
                                                    .setAllowAssetPackDeletion(true)
                                                    .build());*/
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.FLEXIBLE,
                    ActivityUtils.getTopActivity(),
                    56 // Now uses a valid 16-bit value
            );
        }catch (Throwable throwable){
            LogUtils.w(throwable);
            if(toastError){
                ToastUtils.showShort(throwable.getMessage());
            }
        }
    }
}
