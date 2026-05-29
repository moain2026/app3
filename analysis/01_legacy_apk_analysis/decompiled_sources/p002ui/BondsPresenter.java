package com.p001yd.electricecollector.p002ui;

import com.itextpdf.text.pdf.PdfBoolean;
import com.p001yd.electricecollector.HttpClientIntCallback;
import com.p001yd.electricecollector.entities.ItemBonds;
import com.p001yd.electricecollector.model.BondsRepository;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes12.dex */
public class BondsPresenter {
    private final String TAG = getClass().getSimpleName();
    private BondsRepository _repository;
    private BaseView<ItemBonds> _view;

    public BondsPresenter(String str, String str2, String str3, BaseView<ItemBonds> baseView) {
        this._repository = new BondsRepository(str, str2, str3);
        this._view = baseView;
    }

    public void delete(final ItemBonds itemBonds) throws JSONException {
        this._repository.delete(itemBonds, new HttpClientIntCallback<ItemBonds>() { // from class: com.yd.electricecollector.ui.BondsPresenter.1
            public void onDataLoaded(List<ItemBonds> list) {
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onDataloaded(List<ItemBonds> list) {
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onError(Throwable th) {
                BondsPresenter.this._view.onFailed(itemBonds);
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onSucceed(Object obj) {
                if (obj.toString().equals(PdfBoolean.TRUE)) {
                    BondsPresenter.this._view.onSucceed(itemBonds);
                } else {
                    BondsPresenter.this._view.onFailed(itemBonds);
                }
            }
        });
    }

    public void loadData(int i, int i2, int[] iArr, String str) {
        this._repository.getAll(i, i2, iArr, str, new HttpClientIntCallback<ItemBonds>() { // from class: com.yd.electricecollector.ui.BondsPresenter.2
            public void onDataLoaded(List<ItemBonds> list) {
                BondsPresenter.this._view.onLoadDataSucceed(list);
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onDataloaded(List<ItemBonds> list) {
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onError(Throwable th) {
                BondsPresenter.this._view.onLoadDataFailure();
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onSucceed(Object obj) {
            }
        });
    }

    public void save(final ItemBonds itemBonds) throws JSONException {
        this._repository.save(itemBonds, new HttpClientIntCallback<ItemBonds>() { // from class: com.yd.electricecollector.ui.BondsPresenter.3
            public void onDataLoaded(List<ItemBonds> list) {
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onDataloaded(List<ItemBonds> list) {
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onError(Throwable th) {
                BondsPresenter.this._view.onFailed(itemBonds);
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onSucceed(Object obj) {
                String str = null;
                try {
                    str = ((JSONObject) obj).getString("Result");
                    itemBonds.setFinalBalance(Double.parseDouble(((JSONObject) obj).getString("Balance")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (str.length() > 0) {
                    BondsPresenter.this._view.onSucceed(itemBonds);
                } else {
                    BondsPresenter.this._view.onFailed(itemBonds);
                }
            }
        });
    }

    public void update(final ItemBonds itemBonds) throws JSONException {
        this._repository.update(itemBonds, new HttpClientIntCallback<ItemBonds>() { // from class: com.yd.electricecollector.ui.BondsPresenter.4
            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onDataloaded(List<ItemBonds> list) {
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onError(Throwable th) {
                BondsPresenter.this._view.onFailed(itemBonds);
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onSucceed(Object obj) {
                String str = null;
                try {
                    str = ((JSONObject) obj).getString("Result");
                    itemBonds.setFinalBalance(Double.parseDouble(((JSONObject) obj).getString("Balance")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (str.equals(PdfBoolean.TRUE)) {
                    BondsPresenter.this._view.onSucceed(itemBonds);
                } else {
                    BondsPresenter.this._view.onFailed(itemBonds);
                }
            }
        });
    }
}
