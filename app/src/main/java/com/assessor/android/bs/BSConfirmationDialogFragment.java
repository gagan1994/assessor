package com.assessor.android.bs;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.assessor.android.R;
import com.assessor.android.iface.OnDialogClickListener;
import com.assessor.android.retrofit.RetrofitServiceListener;
import com.assessor.android.retrofit.response.StringResponse;
import com.assessor.android.utility.LocalConstants;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class BSConfirmationDialogFragment extends BottomSheetDialogFragment implements RetrofitServiceListener {
    String message, title;

    OnDialogClickListener mListener;
    RetrofitServiceListener mServiceListener;
    ProgressDialog mProgressBar = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            message = getArguments().getString(LocalConstants.CONST_MESSAGE);
            title = getArguments().getString(LocalConstants.CONST_TITLE);
            mListener = (OnDialogClickListener) getArguments().getSerializable(LocalConstants.CONST_LISTENER);
        }
        mServiceListener = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bs_confirmdialog, container, false);
        ((TextView) view.findViewById(R.id.dialog_title)).setText(title);
        ((TextView) view.findViewById(R.id.dialog_message)).setText(message);

        view.findViewById(R.id.action_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onOkClicked("");
                }
                getDialog().dismiss();
            }
        });
        view.findViewById(R.id.action_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onCancelClicked();
                }
                getDialog().dismiss();
            }
        });
        return view;
    }

    private void initDialog() {
        if (isAdded() && isVisible()) {
            mProgressBar = new ProgressDialog(getContext());
            mProgressBar.setCancelable(false);//you can cancel it by pressing back button
        }
    }

    public void hideDialog() {
        if (isAdded() && isVisible()) {
            if (mProgressBar != null && mProgressBar.isShowing()) {
                mProgressBar.dismiss();
            }
        }
    }

    public void showProgressDialog(String message) {
        if (isAdded() && isVisible()) {
            if (mProgressBar == null) {
                initDialog();
            }
            mProgressBar.setMessage(message);
            if (!mProgressBar.isShowing())
                mProgressBar.show();
        }
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16) {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
                FrameLayout bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
                BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                //behavior.setPeekHeight(0); // Remove this line to hide a dark background if you manually hide the dialog.
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context != null && context instanceof OnDialogClickListener) {
            mListener = (OnDialogClickListener) context;
        }
    }

    @Override
    public void onRequestStarted(Object mObject) {
        showProgressDialog("");
    }

    @Override
    public void onResponse(Object mObject) {
        if (mObject instanceof StringResponse) {
            StringResponse response = (StringResponse) mObject;
            if (response.getMessage().contains("success")) {

            }
        }
    }

    @Override
    public void onFailure(Object mObject, Throwable t) {

    }
}