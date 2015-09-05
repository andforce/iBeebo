
package org.zarroboogs.weibo.selectphoto;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class FileTraversal implements Parcelable {
    public String filename;
    public List<String> filecontent = new ArrayList<String>();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(filename);
        dest.writeList(filecontent);
    }

    public static final Creator<FileTraversal> CREATOR = new Creator<FileTraversal>() {

        @Override
        public FileTraversal[] newArray(int size) {
            return null;
        }

        @Override
        public FileTraversal createFromParcel(Parcel source) {
            FileTraversal ft = new FileTraversal();
            ft.filename = source.readString();
            ft.filecontent = source.readArrayList(FileTraversal.class.getClassLoader());

            return ft;
        }

    };
}
