
package org.zarroboogs.util.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Environment;
import android.util.Log;

public class VideoDownloader {
    public String downloadFile(URL url, String saveInDir, String saveAsFilename) {
        return downloadFile(url, saveInDir, saveAsFilename, null);
    }

    public String downloadFile(URL url, String saveInDir, String saveAsFilename,
            DownloadProgressListener downloadProgressListener) {

        // String sdDrive =
        // Environment.getExternalStorageDirectory().getAbsolutePath();

        // Create one directory

        Log.d("warenix", "saved in " + saveInDir);
        boolean success = (new File(saveInDir)).mkdirs();
        if (success) {
            Log.i("warenix", String.format("created dir[%s]", saveInDir));
        }

        InputStream in = null;
        BufferedOutputStream out = null;
        //
        // String filepath = Environment.getExternalStorageDirectory()
        // .getAbsolutePath();
        String full_local_file_path = String.format("%s/%s", saveInDir, saveAsFilename);
        Log.v("warenix_localsize", String.format("of to %s", full_local_file_path));

        try {

            FileOutputStream fos = new FileOutputStream(full_local_file_path);
            BufferedOutputStream bfs = new BufferedOutputStream(fos, IO_BUFFER_SIZE);

            int iFileSize = VideoDownloader.getContentLength(url);
            Log.d("warenix", String.format("going to download file size %d", iFileSize));

            in = new BufferedInputStream(url.openStream(), IO_BUFFER_SIZE);

            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
            copy(in, out, downloadProgressListener, iFileSize);
            out.flush();
            final byte[] data = dataStream.toByteArray();

            bfs.write(data, 0, data.length);
            bfs.flush();

        } catch (IOException e) {
        } finally {
            closeStream(in);
            closeStream(out);
        }

        return full_local_file_path;
    }

    // helper method
    static int IO_BUFFER_SIZE = 4096;

    private void copy(InputStream in, OutputStream out, DownloadProgressListener downloadProgressListener,
            final int iFileSize) throws IOException {
        byte[] b = new byte[IO_BUFFER_SIZE];
        int read;
        int totalDownloaded = 0;
        while ((read = in.read(b)) != -1) {
            totalDownloaded += read;
            Log.d("warenix", String.format("downloaded: %d", totalDownloaded));
            if (downloadProgressListener != null) {
                downloadProgressListener.onReceivedProgressUpdate(totalDownloaded, iFileSize);
            }

            out.write(b, 0, read);
        }
    }

    DownloadProgressListener downloadProgressListener;

    private void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                // android.util.Log.e(LOG_TAG, "Could not close stream", e);
            }
        }
    }

    public interface DownloadProgressListener {
        public void onReceivedProgressUpdate(int totalDownloaded, final int fileSize);
    }

    public interface DownloadCompletedListener {
        public void onCompleted(String full_local_file_path);
    }

    public static int getContentLength(URL urlFileLocation) {
        HttpURLConnection connFile = null;
        int iFileSize = -1;
        try {
            connFile = (HttpURLConnection) urlFileLocation.openConnection();
            connFile.setDoInput(true);
            InputStream is = connFile.getInputStream();
            iFileSize = connFile.getContentLength();
            is.close();
            connFile.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return iFileSize;
    }
}
