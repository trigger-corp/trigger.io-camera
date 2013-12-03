package io.trigger.forge.android.modules.camera;

import io.trigger.forge.android.core.ForgeApp;
import io.trigger.forge.android.core.ForgeFile;
import io.trigger.forge.android.core.ForgeTask;

public class API {
	public static void getImage(final ForgeTask task) {
		ModalView modal = new ModalView();
		modal.openModal(task);
	}
	public static void URL(final ForgeTask task) {
		task.success(new ForgeFile(ForgeApp.getActivity(), task.params).url());
	}
}
