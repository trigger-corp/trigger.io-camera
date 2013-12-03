package io.trigger.forge.android.modules.camera;

import io.trigger.forge.android.core.ForgeActivity;
import io.trigger.forge.android.core.ForgeApp;
import io.trigger.forge.android.core.ForgeLog;
import io.trigger.forge.android.core.ForgeTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.google.common.base.Throwables;

public class ModalView {
	// Reference to the last created modal view (for back button, etc)
	static ModalView lastModal = null;
	View view = null;
	ForgeTask task = null;
	int requestedOrientation = -10;

	public ModalView() {
		lastModal = this;
	}

	public void closeModal(final ForgeActivity currentActivity, final boolean cancelled, final File result) {
		if (view == null) {
			return;
		}
		
		if (requestedOrientation != -10) {
			ForgeApp.getActivity().setRequestedOrientation(requestedOrientation);
			requestedOrientation = -10;
		}

		currentActivity.removeModalView(view, new Runnable() {
			public void run() {
				if (cancelled) {
					task.error("User cancelled");
				} else {
					task.success(Uri.fromFile(result).toString());
				}

			}
		});

		if (lastModal == this) {
			lastModal = null;
		}

		view = null;
	}

	public void openModal(final ForgeTask task) {
		this.task = task;
		task.performUI(new Runnable() {
			public void run() {
				requestedOrientation = ForgeApp.getActivity().getRequestedOrientation();
				ForgeApp.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				
				// Create new layout
				RelativeLayout layout = new RelativeLayout(ForgeApp.getActivity());
				view = layout;
				layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
				layout.setBackgroundColor(Color.BLACK);

				int width = task.params.has("width") ? task.params.get("width").getAsInt() : 0;
				int height = task.params.has("height") ? task.params.get("height").getAsInt() : 0;
				
				final CameraView preview = new CameraView(ForgeApp.getActivity(), width, height);
				
				layout.addView(preview);
				
				ImageButton captureButton = new ImageButton(ForgeApp.getActivity());
				
				captureButton.setImageDrawable(ForgeApp.getActivity().getResources().getDrawable(ForgeApp.getResourceId("camera_capture", "drawable")));
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.addRule(RelativeLayout.CENTER_HORIZONTAL);
				params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				params.bottomMargin = 20;
				captureButton.setLayoutParams(params);
				captureButton.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
							preview.takePicture(new PictureCallback() {
								@Override
								public void onPictureTaken(byte[] data, Camera camera) {
									String fileName = String.valueOf(new java.util.Date().getTime()) + ".jpg";
									java.io.File dir = null;
									if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
										dir = ForgeApp.getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
									}
									if (dir == null) {
										dir = Environment.getExternalStorageDirectory();
										dir = new java.io.File(dir, "Android/data/" + ForgeApp.getActivity().getApplicationContext().getPackageName() + "/files/");
									}
									dir.mkdirs();
									java.io.File file = new java.io.File(dir, fileName);
									try {
										file.createNewFile();
										OutputStream output = new FileOutputStream(file);
										output.write(data);
										output.close();
									} catch (Exception e) {
										ForgeLog.e(Throwables.getStackTraceAsString(e));
									}
									closeModal(ForgeApp.getActivity(), false, file);
								}
							});
						}
						return false;
					}
				}); 
				
				layout.addView(captureButton);
				
				// Add to the view group and switch
				ForgeApp.getActivity().addModalView(layout);
				layout.requestFocus(View.FOCUS_DOWN);
			}
		});
	}
}