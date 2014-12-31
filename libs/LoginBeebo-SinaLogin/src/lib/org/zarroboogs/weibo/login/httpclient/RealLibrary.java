package lib.org.zarroboogs.weibo.login.httpclient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import android.content.Context;
import android.content.res.AssetManager;

public class RealLibrary {
	private Scanner scanner;
	private Context mContext;

	public RealLibrary(Context context) {
		this.mContext = context;
	}

	private String loadJs(String fileName) {
		try {
			return ReadFile(mContext, fileName);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	protected String ReadFile(Context context, String fileName)
			throws IOException {
		final AssetManager am = context.getAssets();
		final InputStream inputStream = am.open(fileName);

		scanner = new Scanner(inputStream, "UTF-8");
		return scanner.useDelimiter("\\A").next();
	}

	public String getRsaJs() {
		String js = loadJs("ssologin.js");
		return js;
	}
}
