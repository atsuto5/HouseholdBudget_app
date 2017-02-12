/*
 * Copyright 2013 Kii
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kii.sample.balance.samples;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.callback.KiiUserCallBack;
import com.kii.sample.balance.R;
import com.kii.util.ProgressDialogFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This dialog shows user registration form
 */
public class SampleLoginDialogFragment extends DialogFragment {
    private static final String MESSAGE_INVALID_USERNAME = "Invalid Username";
    private static final String MESSAGE_INVALID_PASSWORD = "Invalid Password";
    private static final String MESSAGE_LOGIN_FAILED = "Login is failed.";
    private static byte[] rawDecryptedToken;
    private static String RAWPASS;
    private static String PASS;


    @Bind(R.id.text_message)
    TextView mMessageText;

    @Bind(R.id.edit_username)
    EditText mUsernameEdit;

    @Bind(R.id.edit_password)
    EditText mPasswordEdit;

    @Bind(R.id.button_submit)
    Button mSubmitButton;

    public static SampleLoginDialogFragment newInstance(Fragment target, int requestCode) {
        SampleLoginDialogFragment fragment = new SampleLoginDialogFragment();
        fragment.setTargetFragment(target, requestCode);

        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_login_register, container, false);

        ButterKnife.bind(this, root);

        // set text
        mSubmitButton.setText(R.string.login);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.unbind(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(R.string.login_kii_cloud);
        return dialog;
    }

    @OnClick(R.id.button_submit)
    void submitClicked() {

        // gets username / password
        String username = mUsernameEdit.getText().toString();
        String password = mPasswordEdit.getText().toString();

        SharedPreferences prefs = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);

        if (!prefs.contains(username)) {
            showErrorMessage(MESSAGE_INVALID_USERNAME);
            return;
        }
        RAWPASS = prefs.getString(username,"");
        rawDecryptedToken = Base64.decode(RAWPASS, Base64.DEFAULT);

        FacebookConcealSample fc = new FacebookConcealSample();
        PASS = fc.decryption(rawDecryptedToken,getActivity());

        if (!PASS.equals(password)) {
            showErrorMessage(MESSAGE_INVALID_PASSWORD);
            return;
        }

        // show progress
        ProgressDialogFragment progress = ProgressDialogFragment.newInstance(getActivity(), R.string.login, R.string.login);
        progress.show(getFragmentManager(), ProgressDialogFragment.FRAGMENT_TAG);


        Fragment target = getTargetFragment();
        target.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
        dismiss();



        // call user login API
//        KiiUser.logIn(new KiiUserCallBack() {
//            @Override
//            public void onLoginCompleted(int token, KiiUser user, Exception e) {
//                super.onLoginCompleted(token, user, e);
//
//                ProgressDialogFragment.hide(getFragmentManager());
//                if (e != null) {
//                    showErrorMessage(MESSAGE_LOGIN_FAILED);
//                    return;
//                }
//
//                // notify caller fragment that registration is done.
//                Fragment target = getTargetFragment();
//                if (target == null) {
//                    dismiss();
//                    return;
//                }
//                target.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
//                dismiss();
//            }
//        }, username, password);
    }
    
    /**
     * Show error message
     * @param message is error message
     */
    void showErrorMessage(String message) {
        if (mMessageText == null) { return; }

        mMessageText.setVisibility(View.VISIBLE);
        mMessageText.setText(message);
    }
}
