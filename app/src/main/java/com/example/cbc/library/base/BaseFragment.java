package com.example.cbc.library.base;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.example.cbc.library.util.ToastUtil;
import com.example.cbc.library.view.MoeToast;

public class BaseFragment extends Fragment {

    public void showToast(@StringRes int msgId) {
        ToastUtil.showToast(getContext(), msgId);
    }

    public void showToast(String msg) {
        ToastUtil.showToast(getContext(), msg);
    }

    public void showMoeToast(@StringRes int msgId) {
        MoeToast.makeText(getContext(), msgId);
    }

    public void showMoeToast(String msg) {
        MoeToast.makeText(getContext(), msg);
    }

}
