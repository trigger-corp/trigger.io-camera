package io.trigger.forge.android.modules.camera;

import io.trigger.forge.android.core.ForgeApp;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Build;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
	private Camera camera;
	private Camera.Parameters parameters;
	private OrientationEventListener orientationEventListener;
	private int width;
	private int height;

	public CameraView(Context context, int width, int height) {
		super(context);
		getHolder().addCallback(this);
		getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		this.width = width == 0 ? Integer.MAX_VALUE : width;
		this.height = height == 0 ? Integer.MAX_VALUE : height;
	}

	public void surfaceCreated(SurfaceHolder holder) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			camera = Camera.open(0);
		} else {
			camera = Camera.open();
		}
		
		camera.setDisplayOrientation(90);

		parameters = camera.getParameters();

		List<Size> sizes = parameters.getSupportedPictureSizes();
		
		int difference = Integer.MIN_VALUE;
		int best_height = 0;
		int best_width = 0;
		
		for (Size size : sizes) {
			int diff1 = Math.max(size.width - width, size.height - height);
			int diff2 = Math.min(size.width - width, size.height - height);
			
			if (diff1 > 0 && diff2 > 0) {
				if (diff1 < difference || difference < 0) {
					difference = diff1;
					best_width = size.width;
					best_height = size.height;
				}
			} else if (diff1 > 0) {
				if (diff2 > difference) {
					difference = diff2;
					best_width = size.width;
					best_height = size.height;
				}
			} else if (diff2 > 0) {
				if (diff1 > difference) {
					difference = diff2;
					best_width = size.width;
					best_height = size.height;
				}
			} else {
				if (diff2 > difference) {
					difference = diff2;
					best_width = size.width;
					best_height = size.height;
				}
			}
		}
		
		parameters.setPictureSize(best_width, best_height);

		camera.setParameters(parameters);

		this.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (camera != null) {
					camera.autoFocus(null);
				}
				return true;
			}
		});

		orientationEventListener = new OrientationEventListener(ForgeApp.getActivity()) {
			@Override
			public void onOrientationChanged(int orientation) {
				if (camera == null) {
					return;
				}
				if (orientation == ORIENTATION_UNKNOWN) return;
				int rotation = 0;
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
					CameraInfo info = new CameraInfo();
					Camera.getCameraInfo(0, info);
					orientation = (orientation + 45) / 90 * 90;
					
					if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
						rotation = (info.orientation - orientation + 360) % 360;
					} else { // back-facing camera
						rotation = (info.orientation + orientation) % 360;
					}
				}
				
				parameters.setRotation(rotation);
			}
		};
		orientationEventListener.enable();
		try {
			camera.setPreviewDisplay(getHolder());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void takePicture(final PictureCallback cb) {
		camera.stopPreview();
		camera.setParameters(parameters);
		camera.startPreview();
		camera.takePicture(null, null, new PictureCallback() {
			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				releaseCamera();
				cb.onPictureTaken(data, camera);				
			}
		});
	}
	
	public void releaseCamera() {
		if (camera != null) {
			orientationEventListener.disable();
			
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		releaseCamera();
	}

	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.1;
		double targetRatio = (double) h / w;
		if (sizes == null)
			return null;

		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;

		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
				continue;
			}
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		if (getHolder().getSurface() == null || camera == null) {
			return;
		}		

		List<Size> sizes = parameters.getSupportedPreviewSizes();
		Size optimalSize = getOptimalPreviewSize(sizes, w, h);
		parameters.setPreviewSize(optimalSize.width, optimalSize.height);

		camera.stopPreview();
		camera.setParameters(parameters);
		camera.startPreview();
	}
}
