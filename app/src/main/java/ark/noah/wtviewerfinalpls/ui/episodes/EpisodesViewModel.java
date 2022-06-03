package ark.noah.wtviewerfinalpls.ui.episodes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EpisodesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public EpisodesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}