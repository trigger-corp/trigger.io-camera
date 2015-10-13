package io.trigger.forge.android.modules.camera;

import io.trigger.forge.android.core.ForgeApp;
import io.trigger.forge.android.core.ForgeEventListener;
import io.trigger.forge.android.core.ForgeLog;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;

public class EventListener extends ForgeEventListener {
	
static final int PERMISSIONS_REQUEST = 1;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		if (!checkPermissions()) {
			ActivityCompat.requestPermissions(ForgeApp.getActivity(), new String[] {  
				Manifest.permission.WRITE_EXTERNAL_STORAGE,
				Manifest.permission.CAMERA
			}, PERMISSIONS_REQUEST);
		}
	}
	
	public static boolean checkPermissions() {
		return ContextCompat.checkSelfPermission(ForgeApp.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
			   ContextCompat.checkSelfPermission(ForgeApp.getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
	}
	
	@Override
	public Boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && ModalView.lastModal != null && ModalView.lastModal.view != null) {
			ForgeLog.i("Back button pressed, closing modal view.");
			ModalView.lastModal.closeModal(ForgeApp.getActivity(), true, null);
			return true;
		}
		return null;
	}
}
