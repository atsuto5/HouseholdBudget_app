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
import android.util.Log;
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
public class SampleRegistrationDialogFragment extends DialogFragment {
    private static final String TAG = "RegistrationDialog";

    private static final String MESSAGE_INVALID_USERNAME = "Invalid Username";
    private static final String MESSAGE_INVALID_PASSWORD = "Invalid Password";
    private static final String MESSAGE_REGISTRATION_FAILED = "Registration is failed.";
    private static final int REQUEST_REGISTER = 2;

    @Bind(R.id.text_message)
    TextView mMessageText;

    @Bind(R.id.edit_username)
    EditText mUsernameEdit;

    @Bind(R.id.edit_password)
    EditText mPasswordEdit;

    @Bind(R.id.button_submit)
    Button mSubmitButton;

    public static SampleRegistrationDialogFragment newInstance(Fragment target, int requestCode) {
        SampleRegistrationDialogFragment fragment = new SampleRegistrationDialogFragment();
        fragment.setTargetFragment(target, requestCode);

        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_login_register, container, false);

        ButterKnife.bind(this, root);

        // set text
        mSubmitButton.setText(R.string.register);

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
        dialog.setTitle(R.string.register_kii_cloud);
        return dialog;
    }

    @OnClick(R.id.button_submit)
    void submitClicked() {
        // gets username / password
        String username = mUsernameEdit.getText().toString();
        String password = mPasswordEdit.getText().toString();

        // check
        if (!KiiUser.isValidUserName(username)) {
            showErrorMessage(MESSAGE_INVALID_USERNAME);
            return;
        }
        if (!KiiUser.isValidPassword(password)) {
            showErrorMessage(MESSAGE_INVALID_PASSWORD);
            return;
        }

        // show progress
        ProgressDialogFragment progress = ProgressDialogFragment.newInstance(getActivity(), R.string.register, R.string.register);
        progress.show(getFragmentManager(), ProgressDialogFragment.FRAGMENT_TAG);

        SharedPreferences prefs = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        FacebookConcealSample fc = new FacebookConcealSample();
        editor.putString(username, Base64.encodeToString(fc.encryption(password,getActivity()), Base64.DEFAULT));
        //editor.putString(username, password);
        editor.apply();

        Fragment target = getTargetFragment();
        target.onActivityResult(REQUEST_REGISTER, Activity.RESULT_OK, null);
        dismiss();

        // call user registration API
//        KiiUser user = KiiUser.builderWithName(username).build();
//        user.register(new KiiUserCallBack() {
//            @Override
//            public void onRegisterCompleted(int token, KiiUser user, Exception e) {
//                super.onRegisterCompleted(token, user, e);
//                ProgressDialogFragment.hide(getFragmentManager());
//
//                if (e != null) {
//                    Log.e(TAG, "Register completed error", e);
//                    showErrorMessage(MESSAGE_REGISTRATION_FAILED);
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
//        }, password);
    }
    
    /**
     * Show error message
     * @param message is error message
     */
    private void showErrorMessage(String message) {
        if (mMessageText == null) { return; }

        mMessageText.setVisibility(View.VISIBLE);
        mMessageText.setText(message);
    }
}
