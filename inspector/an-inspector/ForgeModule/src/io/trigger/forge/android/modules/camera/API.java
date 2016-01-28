package io.trigger.forge.android.modules.camera;

import android.Manifest;

import io.trigger.forge.android.core.ForgeActivity;
import io.trigger.forge.android.core.ForgeApp;
import io.trigger.forge.android.core.ForgeFile;
import io.trigger.forge.android.core.ForgeTask;

public class API {
	public static void getImage(final ForgeTask task) {
		ForgeApp.getActivity().requestPermission(Manifest.permission.CAMERA, new ForgeActivity.EventAccessBlock() {
			@Override
			public void run(boolean granted) {
				if (!granted) {
					task.error("Permission denied. User didn't grant access to camera.", "EXPECTED_FAILURE", null);
					return;
				}
				ForgeApp.getActivity().requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, new ForgeActivity.EventAccessBlock() {
					@Override
					public void run(boolean granted) {
						if (!granted) {
							task.error("Permission denied. User didn't grant access to filesystem.", "EXPECTED_FAILURE", null);
							return;
						}
						ModalView modal = new ModalView();
						modal.openModal(task);
					}
				});
			}
		});
	}

	public static void URL(final ForgeTask task) {
		ForgeApp.getActivity().requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, new ForgeActivity.EventAccessBlock() {
			@Override
			public void run(boolean granted) {
				if (!granted) {
					task.error("Permission denied. User didn't grant access to filesystem.", "EXPECTED_FAILURE", null);
					return;
				}
				task.success(new ForgeFile(ForgeApp.getActivity(), task.params).url());
			}
		});
	}
}
