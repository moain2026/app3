package com.p001yd.electricecollector.p002ui;

import com.itextpdf.text.pdf.PdfBoolean;
import com.p001yd.electricecollector.HttpClientIntCallback;
import com.p001yd.electricecollector.entities.ItemReading;
import com.p001yd.electricecollector.model.ReadingRepository;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes12.dex */
public class ReadingPresenter {
    private final String TAG = getClass().getSimpleName();
    private ReadingRepository _repository;
    private BaseView<ItemReading> _view;

    public ReadingPresenter(String str, String str2, String str3, BaseView<ItemReading> baseView) {
        this._repository = new ReadingRepository(str, str2, str3);
        this._view = baseView;
    }

    public void delete(final ItemReading itemReading) throws JSONException {
        this._repository.delete(itemReading, new HttpClientIntCallback<ItemReading>() { // from class: com.yd.electricecollector.ui.ReadingPresenter.1
            public void onDataLoaded(List<ItemReading> list) {
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onDataloaded(List<ItemReading> list) {
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onError(Throwable th) {
                ReadingPresenter.this._view.onFailed(itemReading);
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onSucceed(Object obj) {
                if (obj.toString().equals(PdfBoolean.TRUE)) {
                    ReadingPresenter.this._view.onSucceed(itemReading);
                } else {
                    ReadingPresenter.this._view.onFailed(itemReading);
                }
            }
        });
    }

    public void loadData(int i, int i2, int[] iArr, String str) {
        this._repository.getAll(i, i2, iArr, str, new HttpClientIntCallback<ItemReading>() { // from class: com.yd.electricecollector.ui.ReadingPresenter.2
            public void onDataLoaded(List<ItemReading> list) {
                ReadingPresenter.this._view.onLoadDataSucceed(list);
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onDataloaded(List<ItemReading> list) {
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onError(Throwable th) {
                ReadingPresenter.this._view.onLoadDataFailure();
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onSucceed(Object obj) {
            }
        });
    }

    public void save(final ItemReading itemReading) throws JSONException {
        this._repository.save(itemReading, new HttpClientIntCallback<ItemReading>() { // from class: com.yd.electricecollector.ui.ReadingPresenter.3
            public void onDataLoaded(List<ItemReading> list) {
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onDataloaded(List<ItemReading> list) {
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onError(Throwable th) {
                ReadingPresenter.this._view.onFailed(itemReading);
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onSucceed(Object obj) {
                String str = null;
                try {
                    str = ((JSONObject) obj).getString("Result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (str.length() > 0) {
                    ReadingPresenter.this._view.onSucceed(itemReading);
                } else {
                    ReadingPresenter.this._view.onFailed(itemReading);
                }
            }
        });
    }

    public void update(final ItemReading itemReading) throws JSONException {
        this._repository.update(itemReading, new HttpClientIntCallback<ItemReading>() { // from class: com.yd.electricecollector.ui.ReadingPresenter.4
            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onDataloaded(List<ItemReading> list) {
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onError(Throwable th) {
                ReadingPresenter.this._view.onFailed(itemReading);
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onSucceed(Object obj) {
                String str = null;
                try {
                    str = ((JSONObject) obj).getString("Result");
                    ((JSONObject) obj).getString("Balance");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (str.equals(PdfBoolean.TRUE)) {
                    ReadingPresenter.this._view.onSucceed(itemReading);
                } else {
                    ReadingPresenter.this._view.onFailed(itemReading);
                }
            }
        });
    }
}
