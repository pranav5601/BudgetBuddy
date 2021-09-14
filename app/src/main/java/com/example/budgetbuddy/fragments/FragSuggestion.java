package com.example.budgetbuddy.fragments;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.budgetbuddy.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class FragSuggestion extends FragBase {
    ProgressBar superProgressBar;
    WebView superWebView;
    RelativeLayout superLinearLayout;
    String myCurrentUrl;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference webDataRef;
    TextView tv;
    @Override
    int getResourceLayout() {
        return R.layout.frag_suggestion;
    }
    @Override
    void setUpView() {
        init();
        getUrlLink();
    }
    private void getUrlLink() {
        webDataRef.child("admin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myCurrentUrl = String.valueOf(snapshot.child("saving_url_1").getValue());
                setUrl(myCurrentUrl);
                Log.e("admin", String.valueOf(snapshot.child("saving_url_1").getValue()));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void setUrl(String url) {
        superWebView.loadUrl(url);
        superWebView.getSettings().setJavaScriptEnabled(true);
        superWebView.setWebChromeClient(new WebChromeClient() {
        });
        superWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                superProgressBar.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                superProgressBar.setVisibility(View.GONE);
                super.onPageFinished(view, url);
                myCurrentUrl = url;
            }
        });
        superWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                superProgressBar.setProgress(newProgress);
            }
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                tv = getActivity().findViewById(R.id.txtTitle);
                tv.setText(title);
            }
            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
            }
        });
    }
    @Override
    public void onStop() {
        super.onStop();
        tv.setText(getResources().getString(R.string.app_name));
    }
    private void init() {
        //layout
        superProgressBar = getFragView().findViewById(R.id.myProgressBar);
        superWebView = getFragView().findViewById(R.id.webViewMain);
        superLinearLayout = getFragView().findViewById(R.id.webLinearLayout);
        superProgressBar.setMax(100);
        //firebase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        webDataRef = mFirebaseDatabase.getReference();
    }
}