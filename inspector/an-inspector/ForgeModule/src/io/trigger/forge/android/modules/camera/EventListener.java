package io.trigger.forge.android.modules.camera;

import io.trigger.forge.android.core.ForgeApp;
import io.trigger.forge.android.core.ForgeEventListener;
import io.trigger.forge.android.core.ForgeLog;
import android.view.KeyEvent;

public class EventListener extends ForgeEventListener {
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
