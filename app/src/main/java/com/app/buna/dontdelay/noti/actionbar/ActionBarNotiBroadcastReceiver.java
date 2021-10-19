package com.app.buna.dontdelay.noti.actionbar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.buna.dontdelay.activity.PreferenceActivity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ActionBarNotiBroadcastReceiver extends BroadcastReceiver {

    private String lightAction = "TURN_ON_LIGHT";
    private String webAction = "SHOW_ME_WEB";

    private boolean mFlashOn;
    private CameraManager mCameraManager;
    private String mCameraId = null;

    final int MY_PERMISSION_REQUEST_CODE = 100;
    int APIVersion = Build.VERSION.SDK_INT;

    @Override
    public void onReceive(Context context, Intent intent) {


        mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);

        if(intent.getAction().equals(lightAction)){ // 플래시 라이트 제어
            if(APIVersion >= android.os.Build.VERSION_CODES.M){
                if(checkCameraPermssion(context)){  // 권한 있는 경우
                    flashLight(context);
                }else if(!checkCameraPermssion(context)){   // 권한 없는 경우
                    Intent prefIntent = new Intent(context, PreferenceActivity.class);
                    prefIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(prefIntent);
                    Toast.makeText(context, "\'카메라 권한\'을 요청하세요", Toast.LENGTH_LONG).show();
                }
            } else{
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, MY_PERMISSION_REQUEST_CODE);
            }
        }else if(intent.getAction().equals(webAction)){ // 웹 켜기
            Intent internetIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.naver.com"));
            internetIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(internetIntent);
        }
    }


    @SuppressLint("NewApi")
    private void flashLight(Context context) {
        if(mCameraId == null) {
            try {
                for (String id : mCameraManager.getCameraIdList()) {
                    CameraCharacteristics c = mCameraManager.getCameraCharacteristics(id);
                    Boolean flashAvailable = c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                    Integer lensFacing = c.get(CameraCharacteristics.LENS_FACING);
                    if (flashAvailable != null && flashAvailable && lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                        mCameraId = id;
                        break;
                    }
                }
            } catch (CameraAccessException e) {
                mCameraId = null;
                e.printStackTrace();
                return;
            }
        }

        mFlashOn = !mFlashOn;

        try {
            mCameraManager.setTorchMode(mCameraId, mFlashOn);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e){
            Toast.makeText(context, "플래시 라이트가 지원되지 않습니다.\n설정 -> 메일 보내기 에서 관리자에게 문의바랍니다.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    private boolean checkCameraPermssion(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);

        return result == PackageManager.PERMISSION_GRANTED;
    }
}
