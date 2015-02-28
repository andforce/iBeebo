package org.zarroboogs.weibo.hot.hean;

import java.util.ArrayList;
import java.util.List;

import org.zarroboogs.weibo.activity.HotGeoBean;
import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.bean.MessageBean.PicUrls;
import org.zarroboogs.weibo.bean.data.DataItem;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableString;

public class HotMblogBean extends DataItem implements Parcelable {

	private String created_at = "Wed Feb 25 21:03:57 +0800 2015";
	private String id = "";
	private String mid = "3814241230897892";
	private String idstr = "3814241230897892";
	private String text = "";
	private int source_allowclick = 0;
	private int source_type = 1;
	private String source = "<a href=\"http://app.weibo.com/t/feed/5yiHuw\" rel=\"nofollow\">iPhone 6 Plus</a>";
	private boolean favorited = false;
	private boolean truncated = false;
	private String in_reply_to_status_id = "";
	private String in_reply_to_user_id = "";
	private String in_reply_to_screen_name = "";
	private List<String> pic_ids = null;
	private HotGeoBean geo = null;

	private List<String> darwin_tags = null;
	private String mblogid = "C5YtJ3LA8";
	private String scheme = "sinaweibo://detail/?mblogid=C5YtJ3LA8";
	private String mblogtypename = "";
	private int attitudes_status = 0;
	private int recom_state = -1;

	private int reposts_count = 29779;
	private int comments_count = 37707;
	private int attitudes_count = 528125;
	private int mlevel = 0;

	private HotVisibleBean visible = null;
	private UserBean user = null;
	private List<HotUrlStructBean> url_struct = null;

	private HotPageInfoBean page_info = null;

	private List<HotButtonsBean> buttons = null;

	// ====================================================================//
	// MessageBean
	private String thumbnail_pic = "";

	private String bmiddle_pic = "";

	private String original_pic = "";

	private String sourceString = "";

	// =====================================================//

	public String getThumbnail_pic() {
		return thumbnail_pic;
	}

	public void setThumbnail_pic(String thumbnail_pic) {
		this.thumbnail_pic = thumbnail_pic;
	}

	public String getBmiddle_pic() {
		return bmiddle_pic;
	}

	public void setBmiddle_pic(String bmiddle_pic) {
		this.bmiddle_pic = bmiddle_pic;
	}

	public String getOriginal_pic() {
		return original_pic;
	}

	public void setOriginal_pic(String original_pic) {
		this.original_pic = original_pic;
	}

	public String getSourceString() {
		return sourceString;
	}

	public void setSourceString(String sourceString) {
		this.sourceString = sourceString;
	}

	public List<String> getDarwin_tags() {
		return darwin_tags;
	}

	public void setDarwin_tags(List<String> darwin_tags) {
		this.darwin_tags = darwin_tags;
	}

	public HotPageInfoBean getPage_info() {
		return page_info;
	}

	public void setPage_info(HotPageInfoBean page_info) {
		this.page_info = page_info;
	}

	public List<String> getPic_ids() {
		return pic_ids;
	}

	public void setPic_ids(List<String> pic_ids) {
		this.pic_ids = pic_ids;
	}

	public String getMblogid() {
		return mblogid;
	}

	public void setMblogid(String mblogid) {
		this.mblogid = mblogid;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getMblogtypename() {
		return mblogtypename;
	}

	public void setMblogtypename(String mblogtypename) {
		this.mblogtypename = mblogtypename;
	}

	public int getAttitudes_status() {
		return attitudes_status;
	}

	public void setAttitudes_status(int attitudes_status) {
		this.attitudes_status = attitudes_status;
	}

	public int getRecom_state() {
		return recom_state;
	}

	public void setRecom_state(int recom_state) {
		this.recom_state = recom_state;
	}

	public int getReposts_count() {
		return reposts_count;
	}

	public void setReposts_count(int reposts_count) {
		this.reposts_count = reposts_count;
	}

	public int getComments_count() {
		return comments_count;
	}

	public void setComments_count(int comments_count) {
		this.comments_count = comments_count;
	}

	public int getAttitudes_count() {
		return attitudes_count;
	}

	public void setAttitudes_count(int attitudes_count) {
		this.attitudes_count = attitudes_count;
	}

	public int getMlevel() {
		return mlevel;
	}

	public void setMlevel(int mlevel) {
		this.mlevel = mlevel;
	}

	public HotVisibleBean getVisible() {
		return visible;
	}

	public void setVisible(HotVisibleBean visible) {
		this.visible = visible;
	}

	public UserBean getUser() {
		return user;
	}

	public void setUser(UserBean user) {
		this.user = user;
	}

	public List<HotUrlStructBean> getUrl_struct() {
		return url_struct;
	}

	public void setUrl_struct(List<HotUrlStructBean> url_struct) {
		this.url_struct = url_struct;
	}

	public List<HotButtonsBean> getButtons() {
		return buttons;
	}

	public void setButtons(List<HotButtonsBean> buttons) {
		this.buttons = buttons;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getIdstr() {
		return idstr;
	}

	public void setIdstr(String idstr) {
		this.idstr = idstr;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getSource_allowclick() {
		return source_allowclick;
	}

	public void setSource_allowclick(int source_allowclick) {
		this.source_allowclick = source_allowclick;
	}

	public int getSource_type() {
		return source_type;
	}

	public void setSource_type(int source_type) {
		this.source_type = source_type;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public boolean isFavorited() {
		return favorited;
	}

	public void setFavorited(boolean favorited) {
		this.favorited = favorited;
	}

	public boolean isTruncated() {
		return truncated;
	}

	public void setTruncated(boolean truncated) {
		this.truncated = truncated;
	}

	public String getIn_reply_to_status_id() {
		return in_reply_to_status_id;
	}

	public void setIn_reply_to_status_id(String in_reply_to_status_id) {
		this.in_reply_to_status_id = in_reply_to_status_id;
	}

	public String getIn_reply_to_user_id() {
		return in_reply_to_user_id;
	}

	public void setIn_reply_to_user_id(String in_reply_to_user_id) {
		this.in_reply_to_user_id = in_reply_to_user_id;
	}

	public String getIn_reply_to_screen_name() {
		return in_reply_to_screen_name;
	}

	public void setIn_reply_to_screen_name(String in_reply_to_screen_name) {
		this.in_reply_to_screen_name = in_reply_to_screen_name;
	}

	public HotGeoBean getGeo() {
		return geo;
	}

	public void setGeo(HotGeoBean geo) {
		this.geo = geo;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public SpannableString getListViewSpannableString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getListviewItemShowTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMills(long mills) {
		// TODO Auto-generated method stub

	}

	@Override
	public long getMills() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getIdLong() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean havePicture() {
		if (pic_ids != null && pic_ids.size() > 0) {
			return true;
		}
		return false;
	}

	public boolean isMultiPics() {
		if (havePicture() && pic_ids.size() > 1) {
			return true;
		} else {
			return false;
		}
	}

	public int getPicCount() {
		if (havePicture()) {
			return pic_ids.size();
		} else {
			return 0;
		}
	}

	public ArrayList<String> getHighPicUrls() {

		ArrayList<String> value = new ArrayList<String>();

		if (value.size() == 0) {
			String prefStr = "http://ww4.sinaimg.cn/large/";
			for (String url : pic_ids) {
				value.add(prefStr + url + ".jpg");
			}
		}
		return value;
	}

	public ArrayList<String> getThumbnailPicUrls() {
		ArrayList<String> value = new ArrayList<String>();

		if (value.size() == 0) {
			String prefStr = "http://ww4.sinaimg.cn/thumbnail/";
			for (String url : pic_ids) {
				value.add(prefStr + url + ".jpg");
			}
		}
		return value;
	}

}
