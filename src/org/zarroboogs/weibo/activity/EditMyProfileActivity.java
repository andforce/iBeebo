
package org.zarroboogs.weibo.activity;

import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.utils.Constants;
import org.zarroboogs.utils.ImageUtility;
import org.zarroboogs.weibo.GlobalContext;
import org.zarroboogs.weibo.R;
import org.zarroboogs.weibo.asynctask.MyAsyncTask;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.dao.EditMyProfileDao;
import org.zarroboogs.weibo.dao.ShowUserDao;
import org.zarroboogs.weibo.db.task.AccountDBTask;
import org.zarroboogs.weibo.dialogfragment.SelectProfilePictureDialog;
import org.zarroboogs.weibo.support.asyncdrawable.ProfileAvatarReadWorker;
import org.zarroboogs.weibo.support.utils.Utility;

import com.umeng.analytics.MobclickAgent;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.Toolbar.OnMenuItemClickListener;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * User: qii Date: 13-2-28
 */
public class EditMyProfileActivity extends AbstractAppActivity implements DialogInterface.OnClickListener {

    private static final int CAMERA_RESULT = 0;

    private static final int PIC_RESULT = 1;

    private UserBean userBean;

    private Layout layout;

    private MenuItem save;

    private ProfileAvatarReadWorker avatarTask;

    private SaveAsyncTask saveAsyncTask;

    private NewProfileAvatarReaderWorker newProfileAvatarReaderWorker;

    private Uri imageFileUri;

    private String picPath;

    private Toolbar mEditToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editmyprofileactivity_layout);
        mEditToolBar = (Toolbar) findViewById(R.id.editMyProFileToolbar);

        initLayout();
        userBean = (UserBean) getIntent().getParcelableExtra(Constants.USERBEAN);
        initValue(savedInstanceState);

        mEditToolBar.inflateMenu(R.menu.actionbar_menu_editmyprofileactivity);
        save = mEditToolBar.getMenu().findItem(R.id.menu_save);
        mEditToolBar.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // TODO Auto-generated method stub
                Intent intent;
                switch (item.getItemId()) {
                    case android.R.id.home:
                        intent = MainTimeLineActivity.newIntent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        return true;
                    case R.id.menu_save:
                        save();
                        return true;
                }
                return false;
            }
        });
    }

    // @Override
    // public boolean onCreateOptionsMenu(Menu menu) {
    // getMenuInflater().inflate(R.menu.actionbar_menu_editmyprofileactivity, menu);
    // save = menu.findItem(R.id.menu_save);
    // return super.onCreateOptionsMenu(menu);
    // }
    //
    // @Override
    // public boolean onOptionsItemSelected(MenuItem item) {
    // Intent intent;
    // switch (item.getItemId()) {
    // case android.R.id.home:
    // intent = MainTimeLineActivity.newIntent();
    // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
    // startActivity(intent);
    // return true;
    // case R.id.menu_save:
    // save();
    // return true;
    // }
    // return false;
    // }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("picPath", picPath);
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

    private void initLayout() {
        layout = new Layout();
        layout.avatar = (ImageView) findViewById(R.id.avatar);
        layout.avatar.setOnClickListener(avatarOnClickListener);
        layout.nickname = (EditText) findViewById(R.id.nickname);
        layout.website = (EditText) findViewById(R.id.website);
        layout.info = (EditText) findViewById(R.id.info);

    }

    private View.OnClickListener avatarOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            SelectProfilePictureDialog dialog = new SelectProfilePictureDialog();
            dialog.show(getFragmentManager(), "");
        }
    };

    private void initValue(Bundle savedInstanceState) {

        if (savedInstanceState == null || TextUtils.isEmpty(savedInstanceState.getString("picPath"))) {
            String avatarUrl = userBean.getAvatar_large();
            if (!TextUtils.isEmpty(avatarUrl)) {
                avatarTask = new ProfileAvatarReadWorker(layout.avatar, avatarUrl);
                avatarTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
            }
        } else if (!TextUtils.isEmpty(savedInstanceState.getString("picPath"))) {
            displayPic();
        }
        layout.nickname.setText(userBean.getScreen_name());
        layout.nickname.setSelection(layout.nickname.getText().toString().length());
        layout.nickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isNicknameEmpty();
            }
        });
        layout.website.setText(userBean.getUrl());
        layout.info.setText(userBean.getDescription());
    }

    private boolean isNicknameEmpty() {
        int sum = Utility.length(layout.nickname.getText().toString());
        if (sum == 0) {
            layout.nickname.setError(getString(R.string.nickname_cant_be_empty));
        }
        return sum == 0;
    }

    private boolean doesNicknameHaveSpace() {
        boolean result = layout.nickname.getText().toString().contains(" ");
        if (result) {
            layout.nickname.setError(getString(R.string.nickname_cant_have_space));
        }
        return result;
    }

    private void save() {
        if (Utility.isTaskStopped(saveAsyncTask) && !isNicknameEmpty() && !doesNicknameHaveSpace()) {
            saveAsyncTask = new SaveAsyncTask();
            saveAsyncTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case 0:
                imageFileUri = getContentResolver()
                        .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
                if (imageFileUri != null) {
                    Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);
                    if (Utility.isIntentSafe(EditMyProfileActivity.this, i)) {
                        startActivityForResult(i, CAMERA_RESULT);
                    } else {
                        Toast.makeText(EditMyProfileActivity.this, getString(R.string.dont_have_camera_app),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditMyProfileActivity.this, getString(R.string.cant_insert_album), Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case 1:
                Intent choosePictureIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(choosePictureIntent, PIC_RESULT);
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_RESULT:
                    picPath = Utility.getPicPathFromUri(imageFileUri, this);
                    break;
                case PIC_RESULT:
                    Uri imageFileUri = intent.getData();
                    picPath = Utility.getPicPathFromUri(imageFileUri, this);
                    break;
            }
        }
        if (!TextUtils.isEmpty(picPath)) {
            displayPic();
        }
    }

    private void displayPic() {
        if (Utility.isTaskStopped(newProfileAvatarReaderWorker)) {
            newProfileAvatarReaderWorker = new NewProfileAvatarReaderWorker();
            newProfileAvatarReaderWorker.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void startSaveAnimation() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ProgressBar pb = (ProgressBar) inflater.inflate(R.layout.editmyprofileactivity_refresh_actionbar_view_layout, null);
        save.setActionView(pb);
        layout.nickname.setEnabled(false);
        layout.website.setEnabled(false);
        layout.info.setEnabled(false);
        layout.avatar.setOnClickListener(null);
    }

    private void stopSaveAnimation() {
        if (save.getActionView() != null) {
            save.getActionView().clearAnimation();
            save.setActionView(null);
        }

        layout.nickname.setEnabled(true);
        layout.website.setEnabled(true);
        layout.info.setEnabled(true);
        layout.avatar.setOnClickListener(avatarOnClickListener);

    }

    private class SaveAsyncTask extends MyAsyncTask<Void, UserBean, UserBean> {

        String screenName;

        String url;

        String description;

        WeiboException e;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            screenName = layout.nickname.getText().toString();
            url = layout.website.getText().toString();
            description = layout.info.getText().toString();
            startSaveAnimation();
        }

        @Override
        protected UserBean doInBackground(Void... params) {
            EditMyProfileDao dao = new EditMyProfileDao(GlobalContext.getInstance().getSpecialToken(), screenName);
            dao.setUrl(url);
            dao.setDescription(description);
            dao.setAvatar(picPath);

            try {
                return dao.update();
            } catch (WeiboException e) {
                this.e = e;
                e.printStackTrace();
                cancel(true);
            }
            return null;
        }

        /**
         * sina weibo have a bug, after modify your profile, the return UserBean object dont have
         * large avatar url so must refresh to get actual data;
         */
        @Override
        protected void onPostExecute(UserBean userBean) {
            super.onPostExecute(userBean);
            if (userBean != null) {
                Toast.makeText(EditMyProfileActivity.this, R.string.edit_successfully, Toast.LENGTH_SHORT).show();
                new RefreshTask().executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
            }
        }

        @Override
        protected void onCancelled(UserBean userBean) {
            super.onCancelled(userBean);
            if (this.e != null) {
                Toast.makeText(EditMyProfileActivity.this, e.getError(), Toast.LENGTH_SHORT).show();
            }
            stopSaveAnimation();
        }
    }

    private class Layout {

        ImageView avatar;

        EditText nickname;

        EditText website;

        EditText info;

    }

    private class NewProfileAvatarReaderWorker extends MyAsyncTask<String, Integer, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... url) {
            if (isCancelled()) {
                return null;
            }

            int avatarWidth = getResources().getDimensionPixelSize(R.dimen.profile_avatar_width);
            int avatarHeight = getResources().getDimensionPixelSize(R.dimen.profile_avatar_height);

            return ImageUtility.getRoundedCornerPic(picPath, avatarWidth, avatarHeight);

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            if (bitmap != null) {
                layout.avatar.setImageBitmap(bitmap);
            } else {
                layout.avatar.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
            }

        }
    }

    private class RefreshTask extends MyAsyncTask<Object, UserBean, UserBean> {

        WeiboException e;

        @Override
        protected UserBean doInBackground(Object... params) {
            UserBean user = null;
            try {
                ShowUserDao dao = new ShowUserDao(GlobalContext.getInstance().getSpecialToken());
                dao.setUid(GlobalContext.getInstance().getAccountBean().getUid());
                user = dao.getUserInfo();
            } catch (WeiboException e) {
                this.e = e;
                cancel(true);
            }
            if (user != null) {
                AccountDBTask.updateMyProfile(GlobalContext.getInstance().getAccountBean(), user);
            } else {
                cancel(true);
            }
            return user;
        }

        @Override
        protected void onCancelled(UserBean userBean) {
            super.onCancelled(userBean);
            Toast.makeText(EditMyProfileActivity.this, e.getError(), Toast.LENGTH_SHORT).show();
            stopSaveAnimation();

        }

        @Override
        protected void onPostExecute(UserBean userBean) {
            super.onPostExecute(userBean);
            stopSaveAnimation();
            GlobalContext.getInstance().updateUserInfo(userBean);
            finish();
        }
    }
}
