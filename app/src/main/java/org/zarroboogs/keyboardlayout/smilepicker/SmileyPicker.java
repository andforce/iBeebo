
package org.zarroboogs.keyboardlayout.smilepicker;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import org.zarroboogs.weibo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmileyPicker extends LinearLayout {

    private EditText mEditText;


    private Context mContext;


    private ImageView centerPoint;

    private ImageView leftPoint;

    private ImageView rightPoint;

    public static final Pattern EMOTION_URL = Pattern.compile("\\[(\\S+?)\\]");


    public SmileyPicker(Context paramContext) {
        super(paramContext);

    }

    public SmileyPicker(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        mContext = paramContext;
        LayoutInflater mInflater = LayoutInflater.from(paramContext);
        View view = mInflater.inflate(R.layout.writeweiboactivity_smileypicker, null);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new SmileyPagerAdapter());
        leftPoint = (ImageView) view.findViewById(R.id.left_point);
        centerPoint = (ImageView) view.findViewById(R.id.center_point);
        rightPoint = (ImageView) view.findViewById(R.id.right_point);

        if (true) {
            rightPoint.setVisibility(View.VISIBLE);
        } else {
            rightPoint.setVisibility(View.GONE);
        }
        leftPoint.getDrawable().setLevel(1);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        leftPoint.getDrawable().setLevel(1);
                        centerPoint.getDrawable().setLevel(0);
                        rightPoint.getDrawable().setLevel(0);
                        break;
                    case 1:
                        leftPoint.getDrawable().setLevel(0);
                        centerPoint.getDrawable().setLevel(1);
                        rightPoint.getDrawable().setLevel(0);
                        break;
                    case 2:
                        leftPoint.getDrawable().setLevel(0);
                        centerPoint.getDrawable().setLevel(0);
                        rightPoint.getDrawable().setLevel(1);
                        break;

                }
            }
        });
        addView(view);
    }

    public void setEditText( EditText paramEditText) {
        this.mEditText = paramEditText;

    }


    private class SmileyPagerAdapter extends PagerAdapter {

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.smileypicker_gridview, container, false);

            GridView gridView = (GridView) view.findViewById(R.id.smiley_grid);

            gridView.setAdapter(new SmileyAdapter(mContext, position));

            container.addView(view, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            return view;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);

        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }
    }

    private final class SmileyAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        private List<String> keys;

        private Map<String, Integer> mSmiles;

        private int emotionPosition;

        private int count;


        public SmileyAdapter(Context context, int emotionPosition) {
            this.emotionPosition = emotionPosition;
            this.mInflater = LayoutInflater.from(context);
            this.keys = new ArrayList<>();

            Set<String> keySet;
            switch (emotionPosition) {
                case SmileyMap.GENERAL_EMOTION_POSITION:
                    keySet = SmileyMap.getInstance().getGeneral().keySet();
                    keys.addAll(keySet);
                    mSmiles = SmileyMap.getInstance().getGeneral();
                    count = mSmiles.size();
                    break;
                case SmileyMap.EMOJI_EMOTION_POSITION:
                    keySet = EmojiMap.getInstance().getMap().keySet();
                    keys.addAll(keySet);
                    mSmiles = null;
                    count = keys.size();
                    break;
                case SmileyMap.HUAHUA_EMOTION_POSITION:
                    keySet = SmileyMap.getInstance().getHuahua().keySet();
                    keys.addAll(keySet);
                    mSmiles = SmileyMap.getInstance().getHuahua();
                    count = mSmiles.size();
                    break;
                default:
                    throw new IllegalArgumentException("emotion position is invalid");
            }
        }

        private void bindView(final int position, View contentView) {
            ImageView imageView = ((ImageView) contentView.findViewById(R.id.smiley_item));
            TextView textView = (TextView) contentView.findViewById(R.id.smiley_text_item);

            if (emotionPosition != SmileyMap.EMOJI_EMOTION_POSITION) {
                imageView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.INVISIBLE);
                    imageView.setImageResource(mSmiles.get(keys.get(position)));
            } else {
                imageView.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.VISIBLE);
                textView.setText(keys.get(position));
            }

            contentView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = mEditText.getSelectionStart();
                    String text = keys.get(position);
                    Editable edit = mEditText.getEditableText();// 获取EditText的文字
                    if (index < 0 || index >= edit.length()) {
                        edit.append(text);
                    } else {
                        edit.insert(index, text);// 光标所在位置插入文字
                    }
                    String content = mEditText.getText().toString();
                    addEmotions(mEditText, content, SmileyMap.getInstance().getSmiles());
                    mEditText.setSelection(index + text.length());
                }
            });
        }



        @Override
        public int getCount() {
            return count;
        }

        @Override
        public Object getItem(int paramInt) {
            return null;
        }

        @Override
        public long getItemId(int paramInt) {
            return 0L;
        }

        @Override
        public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
            if (paramView == null) {
                paramView = this.mInflater.inflate(R.layout.writeweiboactivity_smileypicker_item, null);
            }
            bindView(paramInt, paramView);
            return paramView;
        }
    }

    public void addEmotions(EditText et, String txt, Map<String, Integer> smiles) {
        String hackTxt;
        if (txt.startsWith("[") && txt.endsWith("]")) {
            hackTxt = txt + " ";
        } else {
            hackTxt = txt;
        }
        SpannableString value = SpannableString.valueOf(hackTxt);
        addEmotions(value, smiles);
        et.setText(value);
    }


    private void addEmotions(SpannableString value, Map<String, Integer> smiles) {
        Paint.FontMetrics fontMetrics = mEditText.getPaint().getFontMetrics();
        int size = (int)(fontMetrics.descent-fontMetrics.ascent);


        Matcher localMatcher = EMOTION_URL.matcher(value);
        while (localMatcher.find()) {
            String key = localMatcher.group(0);
            if (smiles.containsKey(key)){
                int k = localMatcher.start();
                int m = localMatcher.end();
                if (m - k < 8) {
                    Drawable drawable = mContext.getResources().getDrawable(smiles.get(key));
                    if (drawable != null) {
                        drawable.setBounds(0, 0, size, size);
                    }
                    ImageSpan localImageSpan = new ImageSpan(drawable);
                    value.setSpan(localImageSpan, k, m, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }

        }
    }
}
