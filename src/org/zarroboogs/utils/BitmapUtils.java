/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.zarroboogs.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.FloatMath;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BitmapUtils {

	private static final String TAG = "BitmapUtils";

	private static final int DEFAULT_JPEG_QUALITY = 90;

	public static final int UNCONSTRAINED = -1;

	private BitmapUtils() {
	}

	public static Bitmap resizeBitmapByScale(Bitmap bitmap, float scale, boolean recycle) {
		int width = Math.round(bitmap.getWidth() * scale);
		int height = Math.round(bitmap.getHeight() * scale);
		if (width == bitmap.getWidth() && height == bitmap.getHeight())
			return bitmap;
		Bitmap target = Bitmap.createBitmap(width, height, getConfig(bitmap));
		Canvas canvas = new Canvas(target);
		canvas.scale(scale, scale);
		Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
		canvas.drawBitmap(bitmap, 0, 0, paint);
		if (recycle)
			bitmap.recycle();
		return target;
	}

	private static Bitmap.Config getConfig(Bitmap bitmap) {
		Bitmap.Config config = bitmap.getConfig();
		if (config == null) {
			config = Bitmap.Config.ARGB_8888;
		}
		return config;
	}

	public static Bitmap resizeDownBySideLength(Bitmap bitmap, int maxLength, boolean recycle) {
		int srcWidth = bitmap.getWidth();
		int srcHeight = bitmap.getHeight();
		float scale = Math.min((float) maxLength / srcWidth, (float) maxLength / srcHeight);
		if (scale >= 1.0f)
			return bitmap;
		return resizeBitmapByScale(bitmap, scale, recycle);
	}

	public static Bitmap resizeAndCropCenter(Bitmap bitmap, int size, boolean recycle) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		if (w == size && h == size)
			return bitmap;

		// scale the image so that the shorter side equals to the target;
		// the longer side will be center-cropped.
		float scale = (float) size / Math.min(w, h);

		Bitmap target = Bitmap.createBitmap(size, size, getConfig(bitmap));
		int width = Math.round(scale * bitmap.getWidth());
		int height = Math.round(scale * bitmap.getHeight());
		Canvas canvas = new Canvas(target);
		canvas.translate((size - width) / 2f, (size - height) / 2f);
		canvas.scale(scale, scale);
		Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
		canvas.drawBitmap(bitmap, 0, 0, paint);
		if (recycle)
			bitmap.recycle();
		return target;
	}

	// AUT:wangwf1@lenovo.com. DATE:2012-12-22. START.
	public static Bitmap resizeAndCropCenterExt(Bitmap bitmap, int size, boolean recycle) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		if (w == size && h == size)
			return bitmap;

		// scale the image so that the shorter side equals to the target;
		// the longer side will be center-cropped.
		float scale = (float) size / Math.min(w, h);

		int width = Math.round(scale * bitmap.getWidth());
		int height = Math.round(scale * bitmap.getHeight());
		if (width > height) {
			int largeSize = (int) (width <= size * 1.5 ? width : size * 1.5);
			Bitmap target = Bitmap.createBitmap(largeSize, size, getConfig(bitmap));
			Canvas canvas = new Canvas(target);
			canvas.translate((largeSize - width) / 2f, (size - height) / 2f);
			canvas.scale(scale, scale);
			Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
			canvas.drawBitmap(bitmap, 0, 0, paint);
			if (recycle)
				bitmap.recycle();
			return target;
		} else {
			int largeSize = (int) (height <= size * 1.5 ? height : size * 1.5);
			Bitmap target = Bitmap.createBitmap(size, largeSize, getConfig(bitmap));
			Canvas canvas = new Canvas(target);
			canvas.translate((size - width) / 2f, (largeSize - height) / 2f);
			canvas.scale(scale, scale);
			Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
			canvas.drawBitmap(bitmap, 0, 0, paint);
			if (recycle)
				bitmap.recycle();
			return target;
		}
	}

	// AUT:wangwf1@lenovo.com. DATE:2012-12-22. END.

	public static void recycleSilently(Bitmap bitmap) {
		if (bitmap == null)
			return;
		try {
			bitmap.recycle();
		} catch (Throwable t) {
			Log.w(TAG, "unable recycle bitmap", t);
		}
	}

	public static Bitmap rotateBitmap(Bitmap source, int rotation, boolean recycle) {
		if (rotation == 0)
			return source;
		int w = source.getWidth();
		int h = source.getHeight();
		Matrix m = new Matrix();
		m.postRotate(rotation);
		Bitmap bitmap = Bitmap.createBitmap(source, 0, 0, w, h, m, true);
		if (recycle)
			source.recycle();
		return bitmap;
	}

	public static Bitmap createVideoThumbnail(String filePath) {
		// MediaMetadataRetriever is available on API Level 8
		// but is hidden until API Level 10
		Class<?> clazz = null;
		Object instance = null;
		try {
			clazz = Class.forName("android.media.MediaMetadataRetriever");
			instance = clazz.newInstance();

			Method method = clazz.getMethod("setDataSource", String.class);
			method.invoke(instance, filePath);

			// The method name changes between API Level 9 and 10.
			if (Build.VERSION.SDK_INT <= 9) {
				return (Bitmap) clazz.getMethod("captureFrame").invoke(instance);
			} else {
				byte[] data = (byte[]) clazz.getMethod("getEmbeddedPicture").invoke(instance);
				if (data != null) {
					Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
					if (bitmap != null)
						return bitmap;
				}
				return (Bitmap) clazz.getMethod("getFrameAtTime").invoke(instance);
			}
		} catch (IllegalArgumentException ex) {
			// Assume this is a corrupt video file
		} catch (RuntimeException ex) {
			// Assume this is a corrupt video file.
		} catch (InstantiationException e) {
			Log.e(TAG, "createVideoThumbnail", e);
		} catch (InvocationTargetException e) {
			Log.e(TAG, "createVideoThumbnail", e);
		} catch (ClassNotFoundException e) {
			Log.e(TAG, "createVideoThumbnail", e);
		} catch (NoSuchMethodException e) {
			Log.e(TAG, "createVideoThumbnail", e);
		} catch (IllegalAccessException e) {
			Log.e(TAG, "createVideoThumbnail", e);
		} finally {
			try {
				if (instance != null) {
					clazz.getMethod("release").invoke(instance);
				}
			} catch (Exception ignored) {
			}
		}
		return null;
	}

	public static byte[] compressToBytes(Bitmap bitmap) {
		return compressToBytes(bitmap, DEFAULT_JPEG_QUALITY);
	}

	public static byte[] compressToBytes(Bitmap bitmap, int quality) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(65536);
		bitmap.compress(CompressFormat.JPEG, quality, baos);
		return baos.toByteArray();
	}

	public static boolean isSupportedByRegionDecoder(String mimeType) {
		if (mimeType == null)
			return false;
		mimeType = mimeType.toLowerCase();
		return mimeType.startsWith("image/") && (!mimeType.equals("image/gif") && !mimeType.endsWith("bmp"));
	}

	public static boolean isRotationSupported(String mimeType) {
		if (mimeType == null)
			return false;
		mimeType = mimeType.toLowerCase();
		return mimeType.equals("image/jpeg");
	}

	/**
	 * 压缩一个Bitmap
	 */
	public static Bitmap getScaleBitmap(Bitmap bitmap, float scale) {
		// 1,600px × 900px
		if (bitmap != null && bitmap.getWidth() > 1600 && bitmap.getHeight() > 900) {
			float sx = 1600 / (float) bitmap.getWidth();
			float sy = 900 / (float) bitmap.getHeight();
			Bitmap result = Bitmap.createBitmap(1600, 900, Config.ARGB_8888);
			Canvas canvas = new Canvas(result);
			Matrix matrix = new Matrix();
			matrix.postScale(sx, sy);
			canvas.drawBitmap(bitmap, matrix, null);
			return result;
		}
		return bitmap;
	}

	/**
	 * 保存Bitmap到文件
	 */
	public static String saveBitmapToFile(File dir, String name, Bitmap bitmap, String picKind) {
		File f = new File(dir, name + picKind);
		try {
			f.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
		if (picKind != null && picKind.equalsIgnoreCase(".png")) {
			format = Bitmap.CompressFormat.PNG;
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
			bitmap.compress(format, 100, fOut);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			if (fOut != null) {
				fOut.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if (fOut != null) {
				fOut.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		bitmap.recycle();
		bitmap = null;
		return f.getAbsolutePath();
	}

	/**
	 * 旋转bitmap
	 */
	public static Bitmap rotateBitmap(Bitmap b, int degrees) {
		if (degrees != 0 && b != null && !b.isRecycled()) {
			Matrix m = new Matrix();
			m.setRotate(degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
			try {
				Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
				if (b != b2) {
					b.recycle();
				}
				b = b2;
			} catch (OutOfMemoryError ex) {
			}
		}
		return b;
	}

	/**
	 * 旋转bitmap
	 */
	public static Bitmap rotateBitmapNoRecycle(Bitmap b, int degrees) {
		if (degrees != 0 && b != null && !b.isRecycled()) {
			Matrix m = new Matrix();
			m.setRotate(degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
			try {
				Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
				return b2;
			} catch (OutOfMemoryError ex) {
			}
		}
		return b;
	}

	public static Bitmap compressBitmap(byte[] imageBytes) {
		ByteArrayInputStream isBm = new ByteArrayInputStream(imageBytes);
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 900f;// 这里设置高度为900f
		float ww = 1600f;// 这里设置宽度为1600f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(imageBytes);
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return bitmap;// 压缩好比例大小后再进行质量压缩
	}

	public static Bitmap imageBytes2Bitmap(Context context, byte[] imageBytes) {
		ByteArrayInputStream isBm = new ByteArrayInputStream(imageBytes);
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		int oriWidth = newOpts.outWidth;
		int oriHeight = newOpts.outHeight;
		if (oriWidth > oriHeight) {
			newOpts.inSampleSize = (int) (oriHeight / context.getResources().getDisplayMetrics().widthPixels);
		} else {
			newOpts.inSampleSize = (int) (oriWidth / context.getResources().getDisplayMetrics().widthPixels);
		}
		newOpts.inPreferredConfig = Bitmap.Config.RGB_565; // 默认是Bitmap.Config.ARGB_8888
		newOpts.inPurgeable = true;
		newOpts.inInputShareable = true;
		isBm = new ByteArrayInputStream(imageBytes);
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return bitmap;// 压缩好比例大小后再进行质量压缩
	}

	public static Bitmap scaleBitmap(Bitmap bitmap, int width, int height) {
		int newWidth = width;
		int newHeight = height;
		// if(newWidth < bitmap.getWidth()){
		// return bitmap;
		// }
		if (newHeight == 0) {
			newHeight = (int) (newWidth / (float) bitmap.getWidth() * bitmap.getHeight());
		}
		Bitmap result = Bitmap.createBitmap(newWidth, newHeight, Config.ARGB_8888);
		Canvas canvas = new Canvas(result);
		Matrix matrix = new Matrix();
		float scaleX = 1;
		float scaleY = 1;
		// if(newWidth>bitmap.getWidth()){
		scaleX = newWidth / (float) bitmap.getWidth();
		if (height != 0) {
			scaleY = newHeight / (float) bitmap.getHeight();
		} else {
			scaleY = scaleX;
		}
		// }
		matrix.postScale(scaleX, scaleY);
		canvas.drawBitmap(bitmap, matrix, null);
		return result;
	}

	public static Bitmap roateBitmap(Bitmap bitmap, int roatation) {
		Matrix matrix = new Matrix();
		matrix.setRotate(roatation);
		if (bitmap == null) {
			return null;
		}
		try {
			return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		} catch (OutOfMemoryError e) {
			return bitmap;
		}
	}

	public static Bitmap cutBitmap(int newWidth, int newHeight, Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
		if (bitmap.getWidth() > newWidth) {
			int startX = (bitmap.getWidth() - newWidth) / 2;
			Bitmap targetBitmap = Bitmap.createBitmap(bitmap, startX, 0, newWidth, newHeight);
			if (!bitmap.isRecycled()) {
				bitmap.recycle();
			}
			return targetBitmap;
		} else if (bitmap.getHeight() > newHeight) {
			int startY = (bitmap.getHeight() - newHeight) / 2;
			Bitmap targetBitmap = Bitmap.createBitmap(bitmap, 0, startY, newWidth, newHeight);
			if (!bitmap.isRecycled()) {
				bitmap.recycle();
			}
			return targetBitmap;
		}
		return bitmap;
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
				drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		// canvas.setBitmap(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

}
