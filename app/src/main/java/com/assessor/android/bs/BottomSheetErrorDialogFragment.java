package com.assessor.android.bs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.assessor.android.R;
import com.assessor.android.iface.OnDialogClickListener;
import com.assessor.android.utility.LocalConstants;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetErrorDialogFragment extends BottomSheetDialogFragment {
    String message, title;
    OnDialogClickListener mListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            message = getArguments().getString(LocalConstants.CONST_MESSAGE);
            title = getArguments().getString(LocalConstants.CONST_TITLE);
            mListener = (OnDialogClickListener) getArguments().getSerializable(LocalConstants.CONST_LISTENER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_errordialog, container, false);
        ((TextView) view.findViewById(R.id.dialog_title)).setText(title);
        ((TextView) view.findViewById(R.id.dialog_message)).setText(message);

        view.findViewById(R.id.bs_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onOkClicked(null);
                }
                getDialog().dismiss();
            }
        });
        return view;
    }
}
