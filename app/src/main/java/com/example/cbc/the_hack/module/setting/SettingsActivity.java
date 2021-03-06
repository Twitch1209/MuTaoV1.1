package com.example.cbc.the_hack.module.setting;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import androidx.appcompat.widget.Toolbar;
import android.widget.FrameLayout;

import com.example.cbc.library.base.BaseActivity;
import com.example.cbc.library.util.ToolbarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.example.cbc.R;
import com.example.cbc.the_hack.common.util.SPUtil;

public class SettingsActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fragment_container)
    FrameLayout mFragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        ToolbarUtil.init(mToolbar, this)
                .setTitle(R.string.action_settings)
                .setBack()
                .setTitleCenter(R.style.AppTheme_Toolbar_TextAppearance)
                .build();

//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.fragment_container, new SettingsFragment())
//                .commit();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment {

        private static final String OPEN_TU_PICS = "open_tu_pics";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_setting);
            initPreferences();
        }

        private void initPreferences() {
            Preference openTuPics = findPreference(OPEN_TU_PICS);
            openTuPics.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    SPUtil.build().putBoolean(OPEN_TU_PICS, (Boolean) newValue);
                    return true;
                }
            });
        }
    }

}
