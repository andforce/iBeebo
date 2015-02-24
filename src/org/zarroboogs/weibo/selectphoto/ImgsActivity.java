
package org.zarroboogs.weibo.selectphoto;

import java.io.FileNotFoundException;
import java.util.HashMap;

import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.selectphoto.ImgsAdapter.OnItemClickClass;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class ImgsActivity extends Activity {

    public static final int REQUEST_CODE = 0x0001;
    private SendImgData mSendImgData = SendImgData.getInstance();
    private Bundle bundle;
    private FileTraversal fileTraversal;
    private GridView imgGridView;
    private ImgsAdapter imgsAdapter;
    private Util util;
    private Button mButton;
    private HashMap<Integer, ImageView> hashImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photogrally);

        mButton = (Button) findViewById(R.id.img_select_done);
        updateCount();
        mButton.setOnClickListener(new SelectDoneListener());
        imgGridView = (GridView) findViewById(R.id.gridView1);
        bundle = getIntent().getExtras();
        fileTraversal = bundle.getParcelable("data");
        imgsAdapter = new ImgsAdapter(this, fileTraversal.filecontent, onItemClickClass);
        imgGridView.setAdapter(imgsAdapter);
        hashImage = new HashMap<Integer, ImageView>();
        util = new Util(this);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MobclickAgent.onPageStart(this.getClass().getName());
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().getName());
        MobclickAgent.onPause(this);
    }

    private void updateCount() {
        mButton.setText(getString(R.string.img_select) + "(" + mSendImgData.getSendImgs().size() + ")");
    }

    class SelectDoneListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            setResult(RESULT_OK);
            finish();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        getMenuInflater().inflate(R.menu.imgs_action_bars, menu);
        return super.onCreateOptionsMenu(menu);
    }

    class BottomImgIcon implements OnItemClickListener {

        int index;

        public BottomImgIcon(int index) {
            this.index = index;
        }

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

        }
    }

    public ImageView iconImage(String filepath, int index, CheckBox checkBox) throws FileNotFoundException {
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundResource(R.drawable.imgbg);
        float alpha = 100;
        imageView.setAlpha(alpha);
        util.imgExcute(imageView, imgCallBack, filepath);
        imageView.setOnClickListener(new ImgOnclick(filepath, checkBox));
        return imageView;
    }

    ImgCallBack imgCallBack = new ImgCallBack() {
        @Override
        public void resultImgCall(ImageView imageView, Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    };

    class ImgOnclick implements OnClickListener {
        String filepath;
        CheckBox checkBox;

        public ImgOnclick(String filepath, CheckBox checkBox) {
            this.filepath = filepath;
            this.checkBox = checkBox;
        }

        @Override
        public void onClick(View arg0) {
            checkBox.setChecked(false);
        }
    }

    ImgsAdapter.OnItemClickClass onItemClickClass = new OnItemClickClass() {
        @Override
        public void OnItemClick(View v, int Position, CheckBox checkBox) {
            String filapath = fileTraversal.filecontent.get(Position);
            if (checkBox.isChecked()) {
                checkBox.setChecked(false);
                mSendImgData.removeSendImg(filapath);
            } else {
                try {
                    if (mSendImgData.getSendImgs().size() >= 9) {
                        Toast.makeText(getApplicationContext(), R.string.send_tomanay_pics, Toast.LENGTH_SHORT).show();
                    } else {
                        checkBox.setChecked(true);
                        Log.i("img", "img choise position->" + Position);
                        ImageView imageView = iconImage(filapath, Position, checkBox);
                        if (imageView != null) {
                            hashImage.put(Position, imageView);
                            mSendImgData.addSendImg(filapath);
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            updateCount();
        }
    };
}
