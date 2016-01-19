package com.xiaoningmeng.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.xiaoningmeng.constant.Constant;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

public class ImageUtils {
	
	public static String compress(String filePath){
		
		Bitmap bitmap = getSmallBitmap(filePath); 
		return saveBitmap(bitmap);
	}


	public static Bitmap getSmallBitmap(String filePath) {
    	
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		options.inSampleSize = calculateInSampleSize(options, 400, 400);
		options.inJustDecodeBounds = false;
		Bitmap bm = BitmapFactory.decodeFile(filePath, options);
		if(bm == null){
			return  null;
		}
		int degree = readPictureDegree(filePath);
		bm = rotateBitmap(bm,degree) ;
		ByteArrayOutputStream baos = null ;
		try{
			baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.PNG, 80, baos);
			
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
	
	
	public static String saveBitmap(Bitmap bitmap) {

		FileUtils.createSDDir(Constant.FILE_DIR_NAME);
		String filePath = FileUtils.SDPATH() + Constant.FILE_DIR_NAME + "/"+ "tempImage.png";
		File f = new File(filePath);
		try {
			if (f.exists()) {
				f.delete();
			}
			FileOutputStream out = new FileOutputStream(f);
			bitmap.compress(Bitmap.CompressFormat.PNG,80, out);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filePath;
	}
}
