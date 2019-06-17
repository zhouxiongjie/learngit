package com.shuangling.software.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.jaeger.library.StatusBarUtil;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.utils.ServerInfo;
import com.shuangling.software.utils.StatusBarManager;
import butterknife.BindView;
import butterknife.ButterKnife;


public class GalleriaActivity extends AppCompatActivity {


	private int mGalleriaId;


	@BindView(R.id.webView)
	WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(MyApplication.getInstance().getCurrentTheme());
		setContentView(R.layout.activity_galleria);
		ButterKnife.bind(this);
		init();
	}


	private void init(){

		mGalleriaId=getIntent().getIntExtra("galleriaId",0);
		String url=ServerInfo.h5IP+ ServerInfo.getGalleriaDetail+mGalleriaId;
		WebSettings s = webView.getSettings();
		s.setJavaScriptEnabled(true);       //js
		s.setDomStorageEnabled(true);       //localStorage

//        s.setBuiltInZoomControls(true);
//        s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
//        s.setUseWideViewPort(true);
//        s.setLoadWithOverviewMode(true);
//        s.setSavePassword(true);
//        s.setSaveFormData(true);
//        s.setGeolocationEnabled(true);
//        s.setGeolocationDatabasePath("/data/data/org.itri.html5webview/databases/");

//        webView.requestFocus();
//		  webView.setScrollBarStyle(0);


		webView.setWebChromeClient(new WebChromeClient());
		webView.loadUrl(url);
	}
	
}