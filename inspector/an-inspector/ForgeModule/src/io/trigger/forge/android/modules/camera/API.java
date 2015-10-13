package io.trigger.forge.android.modules.camera;

import io.trigger.forge.android.core.ForgeApp;
import io.trigger.forge.android.core.ForgeFile;
import io.trigger.forge.android.core.ForgeTask;
import io.trigger.forge.android.modules.camera.EventListener;

public class API {
	public static void getImage(final ForgeTask task) {
		if (!EventListener.checkPermissions()) {
			task.error("Permission denied", "UNEXPECTED_FAILURE", null);
			return;
		}
		ModalView modal = new ModalView();
		modal.openModal(task);
	}
	public static void URL(final ForgeTask task) {
		if (!EventListener.checkPermissions()) {
			task.error("Permission denied", "UNEXPECTED_FAILURE", null);
			return;
		}
		task.success(new ForgeFile(ForgeApp.getActivity(), task.params).url());
	}
}
