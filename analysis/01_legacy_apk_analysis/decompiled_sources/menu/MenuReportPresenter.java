package com.p001yd.electricecollector.menu;

import android.content.Context;
import android.os.AsyncTask;
import com.p001yd.electricecollector.AppMenu;
import com.p001yd.electricecollector.model.AppMenuRepository;
import com.p001yd.electricecollector.p002ui.BaseView;
import java.lang.ref.WeakReference;
import java.util.List;

/* loaded from: classes15.dex */
public class MenuReportPresenter {
    private Context _context;
    private BaseView<AppMenu> _view;

    /* loaded from: classes15.dex */
    class AppMenuAsync extends AsyncTask<Void, Void, List<AppMenu>> {
        private final WeakReference<BaseView<AppMenu>> _callbak;
        private final WeakReference<AppMenuRepository> _repository;

        private AppMenuAsync(AppMenuRepository appMenuRepository, BaseView<AppMenu> baseView) {
            this._repository = new WeakReference<>(appMenuRepository);
            this._callbak = new WeakReference<>(baseView);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public List<AppMenu> doInBackground(Void... voidArr) {
            return this._repository.get().getMenuReports();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(List<AppMenu> list) {
            super.onPostExecute((AppMenuAsync) list);
            this._callbak.get().onLoadDataSucceed(list);
        }
    }

    public MenuReportPresenter(BaseView<AppMenu> baseView, Context context) {
        this._view = baseView;
        this._context = context;
    }

    public void loadData() {
        new AppMenuAsync(new AppMenuRepository(this._context), this._view).execute(new Void[0]);
    }
}
