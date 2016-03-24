package com.xiaoningmeng.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.xiaoningmeng.constant.Constant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {

	public static String compress(String filePath,Bitmap.CompressFormat format,int quality) {

		Bitmap bitmap = getCompressBitmap(filePath,format,quality,null);
		return saveBitmap(bitmap,format,quality);
	}

	//400*400
	public static String compressSmall(String filePath,Bitmap.CompressFormat format,int quality){
		
		Bitmap bitmap = getSmallBitmap(filePath, format, quality);
		return saveBitmap(bitmap,format,quality);
	}

	public static Bitmap getCompressBitmap(String filePath,Bitmap.CompressFormat format,int quality,BitmapFactory.Options options) {

		Bitmap bm = BitmapFactory.decodeFile(filePath,options);
		if(bm == null){
			return  null;
		}
		int degree = readPictureDegree(filePath);
		bm = rotateBitmap(bm,degree) ;
		ByteArrayOutputStream baos = null ;
		try{
			baos = new ByteArrayOutputStream();
			bm.compress(format,quality, baos);

		}finally{
			try {
				if(baos != null)
					baos.close() ;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bm ;
	}

	public static Bitmap getSmallBitmap(String filePath,Bitmap.CompressFormat format,int quality) {
    	
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		options.inSampleSize = calculateInSampleSize(options, 400, 400);
		options.inJustDecodeBounds = false;
		Bitmap bm = getCompressBitmap(filePath,format,quality,options);
		return bm ;
	}
	
	private static int readPictureDegree(String path) {  
	       int degree  = 0;  
	       try {  
	               ExifInterface exifInterface = new ExifInterface(path);  
	               int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);  
	               switch (orientation) {  
	               case ExifInterface.ORIENTATION_ROTATE_90:  
	                       degree = 90;  
	                       break;  
	               case ExifInterface.ORIENTATION_ROTATE_180:  
	                       degree = 180;  
	                       break;  
	               case ExifInterface.ORIENTATION_ROTATE_270:  
	                       degree = 270;  
	                       break;  
	               }  
	       } catch (IOException e) {  
	               e.printStackTrace();  
	       }  
	       return degree;  
	   } 
	
	private static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
		}

		return inSampleSize;
	}
	
	private static Bitmap rotateBitmap(Bitmap bitmap, int rotate){
		if(bitmap == null)
			return null ;
		
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		// Setting post rotate to 90
		Matrix mtx = new Matrix();
		mtx.postRotate(rotate);
		return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
	}
	
	
	public static String saveBitmap(Bitmap bitmap,Bitmap.CompressFormat format,int quality) {

		String filePath = makeImageFilePath(format);
		File f = new File(filePath);
		try {
			if (f.exists()) {
				f.delete();
			}
			FileOutputStream out = new FileOutputStream(f);
			bitmap.compress(format,quality, out);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filePath;
	}

	public static String makeImageFilePath(Bitmap.CompressFormat format) {

		File dir = new File(FileUtils.SDPATH() + Constant.FILE_DIR_NAME);
		if (!dir.exists()) {
			FileUtils.createSDDir(FileUtils.SDPATH() + Constant.FILE_DIR_NAME);
		}
		String filePath = FileUtils.SDPATH() + Constant.FILE_DIR_NAME + System.currentTimeMillis();
		switch (format) {
			case JPEG:
				filePath = filePath + ".jpg";
				break;

			case PNG:
				filePath = filePath + ".png";
				break;
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			switch (format) {
				case WEBP:
					filePath = filePath + ".webp";
					break;
			}
		}
		return filePath;
	}

	//基于fresco展示图片并做resize操作
	public static void displayImage(Context mContext, SimpleDraweeView imageView,Uri uri,int width,int height) {

		ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
				.setAutoRotateEnabled(true)
				.setResizeOptions(new ResizeOptions(width, height))
				.build();
		ImagePipelineConfig.newBuilder(mContext).setDownsampleEnabled(true).build();
		PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
				.setTapToRetryEnabled(true)
				.setOldController(imageView.getController())

				.setImageRequest(request)
				.build();
		imageView.setController(controller);
	}
}
