package com.xiaoningmeng.download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.litepal.crud.DataSupport;

import com.xiaoningmeng.bean.AudioDownLoad;

import android.database.Cursor;

public class DownLoadRunnableImpl extends DownLoadRunnable<AudioDownLoad> {
	private static final int BUFFER_SIZE = 1024 * 20;

	public DownLoadRunnableImpl(AudioDownLoad video) {
		super(video);
	}

	@Override
	public void run() {
		if (isCancelled()) {
			return;
		}
		File realFile = new File(bean.getRealPath());
		BufferedInputStream bis = null;
		RandomAccessFile fos = null;
		byte[] buf = new byte[BUFFER_SIZE];
		try {
			long contentLength = bean.getEndPos();
			if (contentLength < 0) {
				contentLength = getContetnLengteh(bean.getUrl(), client);
				if (contentLength < 0) {
					DownLoadClientImpl.getInstance().notifyData(bean,
							DownLoadState.DOWN_ERROR);
					return;
				}
				bean.setEndPos(contentLength);
				this.endPos = contentLength;
			}
			if (realFile.exists()) {
				if (contentLength != realFile.length()) {
					realFile.delete();
				}
			}

			if (!realFile.exists()) {
				DownLoadClientImpl.getInstance().notifyData(bean,
						DownLoadState.DOWN_CONNECT);
				Header header_size = new BasicHeader("Range", "bytes="
						+ startPos + "-" + endPos);
				get.addHeader(header_size);

				if (isCancelled())
					return;

				HttpClient httpclient = new DefaultHttpClient();
				HttpResponse response = httpclient.execute(get);

				@SuppressWarnings("unused")
				int statusCode = response.getStatusLine().getStatusCode();
				fos = new RandomAccessFile(file, "rw");
				fos.seek(startPos);
				bis = new BufferedInputStream(response.getEntity().getContent());
				this.curPosition = this.bean.getStartPos();

				while (curPosition < endPos) {
					int len = bis.read(buf, 0, BUFFER_SIZE);
					if (len == -1) {
						break;
					}
					fos.write(buf, 0, len);
					curPosition = curPosition + len;

					bean.setCurPos(curPosition);
					bean.setStartPos(curPosition);
					DownLoadClientImpl.getInstance().notifyData(bean,
							DownLoadState.DOWN_UPDATE);
				}
				file.renameTo(new File(bean.getRealPath()));
			}
			bean.setCurPos(endPos);
			bean.setStartPos(endPos);
			DownLoadClientImpl.getInstance().notifyData(bean,
					DownLoadState.DOWN_SUC);
		} catch (Exception e) {
			e.printStackTrace();
			if (isCancelled()) {
				DownLoadClientImpl.getInstance().notifyData(bean,
						DownLoadState.DOWN_CANCEL);
			} else if (isPause) {
				DownLoadClientImpl.getInstance().notifyData(bean,
						DownLoadState.DOWN_PAUSE);
			} else {
				DownLoadClientImpl.getInstance().notifyData(bean,
						DownLoadState.DOWN_ERROR);
			}
		} finally {
			isFinished = true;
			try {
				if (bis != null)
					bis.close();
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				if (fos != null)
					try {
						fos.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
			}
			String sql = "select * from AudioDownLoad where audioId ='"
					+ bean.getAudioId() + "' and uid= '" + bean.getUid() + "'";
			Cursor cursor = DataSupport.findBySQL(sql);
			if (cursor.moveToFirst()) {
				bean.updateAll("audioId =? and uid=?", bean.getAudioId(),
						bean.getUid());
			} else {
				bean.save();
			}
		}
	}
}
