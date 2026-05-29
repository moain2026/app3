package com.p001yd.electricecollector.p002ui;

import android.content.Context;
import android.os.AsyncTask;
import com.p001yd.electricecollector.entities.Accounts;
import com.p001yd.electricecollector.model.AccountsRepository;
import java.lang.ref.WeakReference;
import java.util.List;

/* loaded from: classes12.dex */
public class AccountsPresenter {
    private Context _context;
    private BaseView<Accounts> _view;

    /* loaded from: classes12.dex */
    class KabupatenAsync extends AsyncTask<String, Void, List<Accounts>> {
        private final WeakReference<BaseView<Accounts>> _callback;
        private final WeakReference<AccountsRepository> _repository;

        private KabupatenAsync(AccountsRepository accountsRepository, BaseView<Accounts> baseView) {
            this._repository = new WeakReference<>(accountsRepository);
            this._callback = new WeakReference<>(baseView);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public List<Accounts> doInBackground(String... strArr) {
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(List<Accounts> list) {
            super.onPostExecute((KabupatenAsync) list);
            this._callback.get().onLoadDataSucceed(list);
        }
    }

    public AccountsPresenter(BaseView<Accounts> baseView, Context context) {
        this._view = baseView;
        this._context = context;
    }

    public void loadData(String str) {
    }
}
