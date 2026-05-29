package com.p001yd.electricecollector;

import com.p001yd.electricecollector.entities.AuthData;
import com.p001yd.electricecollector.entities.Users;
import com.p001yd.electricecollector.model.AuthRepository;
import com.p001yd.electricecollector.p002ui.BaseView;
import java.util.List;
import org.json.JSONException;

/* loaded from: classes6.dex */
public class LoginPresenter {
    private final String TAG = getClass().getSimpleName();
    private AuthRepository _repository;
    private BaseView<Users> _view;

    public LoginPresenter(String str, String str2, BaseView<Users> baseView) {
        this._repository = new AuthRepository(str, str2);
        this._view = baseView;
    }

    public void auth(AuthData authData) throws JSONException {
        this._repository.auth(authData, new HttpClientIntCallback<Users>() { // from class: com.yd.electricecollector.LoginPresenter.1
            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onDataloaded(List<Users> list) {
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onError(Throwable th) {
                Users users = new Users();
                users.setErrorNo(0);
                users.setErrorMsg(th.getMessage());
                LoginPresenter.this._view.onFailed(users);
            }

            @Override // com.p001yd.electricecollector.HttpClientIntCallback
            public void onSucceed(Object obj) {
                Users users = (Users) obj;
                if (users.getErrorNo() != 0) {
                    LoginPresenter.this._view.onFailed(users);
                } else {
                    LoginPresenter.this._view.onSucceed(users);
                }
            }
        });
    }
}
