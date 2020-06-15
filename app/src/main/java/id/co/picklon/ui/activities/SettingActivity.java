package id.co.picklon.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.picklon.R;
import id.co.picklon.ui.view.MenuView;
import id.co.picklon.utils.Const;
import id.co.picklon.utils.Picklon;

public class SettingActivity extends ToolbarActivity {

    @BindView(R.id.setting_term)
    MenuView termView;
    @BindView(R.id.setting_about_us)
    MenuView aboutUsView;
    @BindView(R.id.setting_feedback)
    MenuView feedbackView;
    @BindView(R.id.setting_change_mobile)
    MenuView changeMobileView;
    @BindView(R.id.setting_version)
    TextView versionView;
    @BindView(R.id.setting_notification_switch)
    SwitchCompat notificationSwitchView;
    @BindView(R.id.setting_voice_switch)
    SwitchCompat voiceSwitchView;

    @Inject
    SharedPreferences.Editor editor;
    @Inject
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        activityComponent().inject(this);

        initUi();
        registerClickEvent();
    }

    private void initUi() {
        setToolbarTitle(R.string.setting);
        versionView.setPaintFlags(versionView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        versionView.setText(getString(R.string.setting_version, Picklon.getVersion(this)));

        Boolean isEnableVoice = sharedPreferences.getBoolean(Const.VOICE, false);
        if (isEnableVoice) {
            voiceSwitchView.setChecked(true);
        } else {
            voiceSwitchView.setChecked(false);
        }

        voiceSwitchView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor.putBoolean(Const.VOICE, isChecked);
            editor.apply();
        });
    }

    private void registerClickEvent() {
        termView.setOnClickListener(view -> startActivity(new Intent(this, TermsAcitivity.class)));
        aboutUsView.setOnClickListener(view -> startActivity(new Intent(this, AboutUsActivity.class)));
        feedbackView.setOnClickListener(view -> startActivity(new Intent(this, FeedbackActivity.class)));
        changeMobileView.setOnClickListener(view -> startActivity(new Intent(this, MobileNumberActivity.class)));
    }
}
