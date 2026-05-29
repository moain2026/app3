package com.p001yd.electricecollector.p002ui;

import com.itextpdf.text.pdf.PdfBoolean;
import com.p001yd.electricecollector.HttpClientIntCallback;
import com.p001yd.electricecollector.entities.ItemBonds;
import com.p001yd.electricecollector.model.BondsPaymentRepository;
import java.util.List;
import org.json.JSONException;

/* loaded from: classes12.dex */
public class BondsPaymentPresenter {
    private final String TAG = getClass().getSimpleName();
    private BondsPaymentRepository _repository;
    private BaseView<ItemBonds> _view;

    public BondsPaymentPresenter(String str, String str2, String str3, BaseView<ItemBonds> baseView) {
        this._repository = new BondsPaymentRepository(str, str2, str3);
        this._view = baseView;
    }

    public void delete(final ItemBonds itemBonds) throws JSONException {
        this._repository.delete(itemBonds, new HttpClientIntCallback<ItemBonds>() { // from class: com.yd.electricecollector.ui.BondsPaymentPresenter.1
            public void onDataLoaded(List<ItemBonds> list) {
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onDataloaded(List<ItemBonds> list) {
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onError(Throwable th) {
                BondsPaymentPresenter.this._view.onFailed(itemBonds);
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onSucceed(Object obj) {
                if (obj.toString().equals(PdfBoolean.TRUE)) {
                    BondsPaymentPresenter.this._view.onSucceed(itemBonds);
                } else {
                    BondsPaymentPresenter.this._view.onFailed(itemBonds);
                }
            }
        });
    }

    public void loadData(int i, int i2, int[] iArr, String str) {
        this._repository.getAll(i, i2, iArr, str, new HttpClientIntCallback<ItemBonds>() { // from class: com.yd.electricecollector.ui.BondsPaymentPresenter.2
            public void onDataLoaded(List<ItemBonds> list) {
                BondsPaymentPresenter.this._view.onLoadDataSucceed(list);
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onDataloaded(List<ItemBonds> list) {
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onError(Throwable th) {
                BondsPaymentPresenter.this._view.onLoadDataFailure();
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onSucceed(Object obj) {
            }
        });
    }

    public void save(final ItemBonds itemBonds) throws JSONException {
        this._repository.save(itemBonds, new HttpClientIntCallback<ItemBonds>() { // from class: com.yd.electricecollector.ui.BondsPaymentPresenter.3
            public void onDataLoaded(List<ItemBonds> list) {
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onDataloaded(List<ItemBonds> list) {
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onError(Throwable th) {
                BondsPaymentPresenter.this._view.onFailed(itemBonds);
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onSucceed(Object obj) {
                if (obj.toString().length() > 0) {
                    BondsPaymentPresenter.this._view.onSucceed(itemBonds);
                } else {
                    BondsPaymentPresenter.this._view.onFailed(itemBonds);
                }
            }
        });
    }

    public void update(final ItemBonds itemBonds) throws JSONException {
        this._repository.update(itemBonds, new HttpClientIntCallback<ItemBonds>() { // from class: com.yd.electricecollector.ui.BondsPaymentPresenter.4
            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onDataloaded(List<ItemBonds> list) {
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onError(Throwable th) {
                BondsPaymentPresenter.this._view.onFailed(itemBonds);
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onSucceed(Object obj) {
                if (obj.toString().equals(PdfBoolean.TRUE)) {
                    BondsPaymentPresenter.this._view.onSucceed(itemBonds);
                } else {
                    BondsPaymentPresenter.this._view.onFailed(itemBonds);
                }
            }
        });
    }
}
