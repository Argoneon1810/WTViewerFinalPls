package ark.noah.wtviewerfinalpls.ui.addbyweb;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;

import ark.noah.wtviewerfinalpls.EntryPointGetter;
import ark.noah.wtviewerfinalpls.ExecutorRunner;
import ark.noah.wtviewerfinalpls.MainActivity;
import ark.noah.wtviewerfinalpls.WtwtLinkParser;
import ark.noah.wtviewerfinalpls.databinding.FragmentWebBinding;

public class FragmentWeb extends Fragment implements EntryPointGetter.Callback, MainActivity.BackPressEvent, ExecutorRunner.Callback<Document> {
    private FragmentWebBinding binding;

    private WebViewModel webViewModel;

    private String baseLink;
    private String currentLink;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        webViewModel =
                new ViewModelProvider(this).get(WebViewModel.class);

        binding = FragmentWebBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        ((MainActivity)requireActivity()).assignBackPressEvent(this);

        binding.webView.setWebViewClient(new MyBrowser());
        binding.webView.getSettings().setJavaScriptEnabled(true);
        String baseLink = EntryPointGetter.getLastValidEntryPoint();
        if(baseLink.equals("")) {
            EntryPointGetter.requestEntryPointLink(this);
        }

        binding.btnAddthispage.setOnClickListener(v -> Navigation.findNavController(view).navigate(FragmentWebDirections.actionWebFragmentToFragmentAddNew(new String[] { currentLink })));

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)requireActivity()).resumedFromOtherFragment();
    }

    @Override
    public void onEntryAquired(String url) {
        baseLink = url;
        Log.i("", "this is in web fragment" + url);
        new ExecutorRunner().execute(
                () -> {
                    Document document = null;
                    try {
                        document = Jsoup.connect(url).get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return document;
                },
                this
        );
    }

    @Override
    public boolean onBackPressedExtra() {
        if(binding.webView.canGoBack()) {
            binding.webView.goBack();
            return false;
        }
        binding.webView.stopLoading();
        ((MainActivity)requireActivity()).resetFABsToInitialState();
        return true;
    }

    @Override
    public void onComplete(Document result) {
        if(result == null) return;
        if(binding == null) return;

        binding.webView.loadDataWithBaseURL(baseLink, result.toString(), "text/html", "utf-8", "");
        currentLink = result.location();

        if(WtwtLinkParser.isLinkValid(result.location()))
            binding.btnAddthispage.setVisibility(View.VISIBLE);
        else
            binding.btnAddthispage.setVisibility(View.GONE);
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            final Uri uri = request.getUrl();
            new ExecutorRunner().execute(
                    () -> {
                        Document document = null;
                        try {
                            document = Jsoup.connect(uri.toString()).get();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return document;
                    },
                    FragmentWeb.this
            );
            return true;
        }
    }
}

