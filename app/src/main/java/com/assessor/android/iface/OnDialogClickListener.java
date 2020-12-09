package com.assessor.android.iface;

import java.io.Serializable;

public interface OnDialogClickListener extends Serializable {

    void onOkClicked(Object object);

    void onCancelClicked();
}
