package com.amosyuen.videorecorder.activity;


import android.os.Build;

import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.amosyuen.videorecorder.R;
import com.amosyuen.videorecorder.activity.params.ActivityThemeParamsI;

/**
 * Activity that sets the colors based on params
 */

public abstract class AbstractDynamicStyledActivity extends AppCompatActivity {

    protected abstract boolean extractIntentParams();

    protected abstract ActivityThemeParamsI getThemeParams();

    protected abstract void layoutView();

    @CallSuper
    protected void setupToolbar(Toolbar toolbar) {
        ActivityThemeParamsI themeParams = getThemeParams();
        setStatusBarColor(themeParams.getStatusBarColor());

        TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);

        toolbar.setBackgroundColor(themeParams.getToolbarColor());
        toolbarTitle.setTextColor(themeParams.getToolbarWidgetColor());

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    protected void setStatusBarColor(@ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = getWindow();
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(color);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
