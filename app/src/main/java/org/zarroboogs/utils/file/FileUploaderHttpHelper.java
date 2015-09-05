
package org.zarroboogs.utils.file;

public class FileUploaderHttpHelper {

    public interface ProgressListener {
        void transferred(long data);

        void waitServerResponse();

        void completed();
    }
}
