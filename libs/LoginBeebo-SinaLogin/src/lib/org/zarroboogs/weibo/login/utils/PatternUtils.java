package lib.org.zarroboogs.weibo.login.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

public class PatternUtils {

	public static String getVideoToaken(String dwr) {
		if (dwr == null) {
			return dwr;
		}
		String regex = "key:\"(.*?)\"";

		String tmp = matchString(dwr, regex);
		String result = tmp.split("\"")[1];
		return "key=" + result;
	}

	public static String preasePid(String s) {

		String regex = "(\"pid\":\")(.*?)\"";
		String tmp = matchString(s, regex);
		return "{" + tmp + "}";
	}

	public static List<String> categoryIDs(String dwr) {
		if (dwr == null) {
			return null;
		}
		String regex = "(id=)\\d+";

		List<String> tmp = matcher(dwr, regex);
		List<String> result = new ArrayList<String>();
		for (String id : tmp) {
			result.add(id.split("=")[1]);
		}
		return result;
	}

	public static List<String> categoryMarks(String dwr) {
		if (dwr == null) {
			return null;
		}
		String regex = "mark=\\d";

		List<String> tmp = matcher(dwr, regex);
		List<String> result = new ArrayList<String>();
		for (String id : tmp) {
			result.add(id.split("=")[1]);
		}
		return result;
	}

	public static List<String> categoryNames(String dwr) {
		if (dwr == null) {
			return null;
		}
		String regex = "name=\".*?\"";

		List<String> tmp = matcher(dwr, regex);
		List<String> result = new ArrayList<String>();
		for (String id : tmp) {
			result.add(id.split("\"")[1]);
		}
		return result;
	}

	public static List<String> categoryParentId(String dwr) {
		if (dwr == null) {
			return null;
		}
		String regex = "parentId=(.*?);";

		List<String> tmp = matcher(dwr, regex);
		List<String> result = new ArrayList<String>();
		for (String id : tmp) {
			result.add(id.split(";")[0]);
		}

		List<String> parents = new ArrayList<String>();
		for (String parent : result) {
			parents.add(parent.split("=")[1]);
		}
		return parents;
	}

	public static int cateoryTotalCount(String dwr) {
		if (dwr == null) {
			return 0;
		}
		String regex = "(totalCount:)\\d*";

		List<String> tmp = matcher(dwr, regex);
		int totalCount = Integer.valueOf(tmp.get(0).split(":")[1]);
		return totalCount;
	}

	public static List<String> bigPhotoUrl(String dwr) {
		if (dwr == null) {
			return null;
		}
		String regex = "(bigPhotoUrl=\")(http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
		List<String> tmp = matcher(dwr, regex);
		List<String> result = new ArrayList<String>();

		for (String bpl : tmp) {
			result.add(bpl.split("\"")[1]);
		}

		return result;
	}

	public static List<String> courseIDs(String dwr) {
		if (dwr == null) {
			return null;
		}
		// (id=)\d+
		String regex = "(id=)\\d+";

		List<String> tmp = matcher(dwr, regex);
		List<String> result = new ArrayList<String>();
		for (String id : tmp) {
			result.add(id.split("=")[1]);
		}

		return result;
	}

	public static List<String> courseNames(String dwr) {
		if (dwr == null) {
			return null;
		}
		// (name=)"(.*?)"
		String regex = "(name=)\"(.*?)\"";
		List<String> tmp = matcher(dwr, regex);
		List<String> result = new ArrayList<String>();
		for (String name : tmp) {
			result.add(name.split("\"")[1]);
		}
		return result;
	}

	public static List<String> lessionDescriptions(String dwr) {
		if (dwr == null) {
			return null;
		}

		Log.d("lessionDescriptions_", dwr);

		String regex = "(description=)\"(.*?)\"";

		List<String> descriptions = matcher(dwr, regex);

		List<String> result = new ArrayList<String>();

		for (String tmp : descriptions) {
			String des = tmp.split("description=\"")[0];
			result.add(des);
		}
		return result;
	}

	public static List<String> lessonNames(String dwr) {
		if (dwr == null) {
			return null;
		}
		// (lessonName=)\"(.*?)\"
		String regex = "(lessonName=)\"(.*?)\"";

		List<String> tmp = matcher(dwr, regex);
		List<String> result = new ArrayList<String>();
		for (String lessionName : tmp) {
			result.add(lessionName.split("\"")[1]);
		}
		return result;
	}

	public static List<String> lessionIDs(String dwr) {
		if (dwr == null) {
			return null;
		}
		// (id=)\d+;\w\d+.(learnMark)
		String tmpRegex = "(id=)\\d+;\\w\\d+.(learnMark)";
		List<String> tmpList = matcher(dwr, tmpRegex);
		String tmpStrings = "";
		for (String tmp : tmpList) {
			tmpStrings = tmpStrings + tmp;
		}

		String regex = "(id=)\\d+";
		List<String> ids = matcher(tmpStrings, regex);

		List<String> result = new ArrayList<String>();
		for (String id : ids) {
			result.add(id.split("=")[1]);
		}
		return result;
	}

	public static String lessionflvHdUrl(String dwr) {
		if (dwr == null) {
			return null;
		}
		String regex = "(flvHdUrl:)\"(.*?)\"";

		String tmp = matchString(dwr, regex);
		if ("".equals(tmp)) {
			return "";
		} else {
			return tmp.split("\"")[1];
		}
	}

	public static String lessionflvSdUrl(String dwr) {
		if (dwr == null) {
			return null;
		}
		String regex = "(flvSdUrl:)\"(.*?)\"";
		String tmp = matchString(dwr, regex);
		if ("".equals(tmp)) {
			return "";
		} else {
			return tmp.split("\"")[1];
		}
	}

	public static String lessionflvShdUrl(String dwr) {
		if (dwr == null) {
			return null;
		}
		String regex = "(flvShdUrl:)\"(.*?)\"";
		String tmp = matchString(dwr, regex);
		if ("".equals(tmp)) {
			return "";
		} else {
			return tmp.split("\"")[1];
		}
	}

	public static String lessionvideoHDUrl(String dwr) {
		if (dwr == null) {
			return null;
		}
		String regex = "(videoHDUrl:)\"(.*?)\"";
		String tmp = matchString(dwr, regex);
		if ("".equals(tmp)) {
			return "";
		} else {
			return tmp.split("\"")[1];
		}
	}

	public static String lessionvideoSHDUrl(String dwr) {
		if (dwr == null) {
			return null;
		}
		String regex = "(videoSHDUrl:)\"(.*?)\"";
		String tmp = matchString(dwr, regex);
		if ("".equals(tmp)) {
			return "";
		} else {
			return tmp.split("\"")[1];
		}
	}

	public static String lessionvideoUrl(String dwr) {
		if (dwr == null) {
			return null;
		}
		String regex = "(videoUrl:)\"(.*?)\"";
		String tmp = matchString(dwr, regex);
		if ("".equals(tmp)) {
			return "";
		} else {
			return tmp.split("\"")[1];
		}
	}

	public static String lessionvideoImgUrl(String dwr) {
		if (dwr == null) {
			return null;
		}
		String regex = "(videoImgUrl:)\"(.*?)\"";
		String tmp = matchString(dwr, regex);
		if ("".equals(tmp)) {
			return "";
		} else {
			return tmp.split("\"")[1];
		}
	}

	public static String matchString(String dwr, String regex) {
		if (dwr == null) {
			return null;
		}
		//
		Pattern mPattern = Pattern.compile(regex);
		Matcher m = mPattern.matcher(dwr);
		if (m.find()) {
			return m.group();
		} else {
			return "";
		}
	}

	private static List<String> matcher(String dwr, String regex) {
		if (dwr == null) {
			return null;
		}
		Pattern mPattern = Pattern.compile(regex);
		Matcher m = mPattern.matcher(dwr);
		List<String> mList = new ArrayList<String>();
		while (m.find()) {
			mList.add(m.group());
		}
		return mList;
	}

	public static String macthUID(String src) {
		String uid = matchString(src, "SUS=SID-(.*?)-");
		try {

			String result = uid.substring(8, uid.length() - 1);
			return result;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}

	public static String macthUname(String src) {
		String uname = matchString(src, "&name=(.*?)&");
		// uname.substring(6, uname.length() - 1);
		try {

			String result = uname.substring(6, uname.length() - 1);
			return result;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}
}
