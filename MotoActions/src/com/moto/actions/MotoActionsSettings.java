/*
 * Copyright (c) 2015 The CyanogenMod Project
 * Copyright (c) 2017 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.moto.actions;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;

import android.util.Log;

import com.moto.actions.Constants;
import com.moto.actions.actions.UpdatedStateNotifier;
import com.moto.actions.actions.TorchAction;

public class MotoActionsSettings {
    private static final String TAG = "MotoActions";

    private static final String GESTURE_CHOP_CHOP_KEY = "gesture_chop_chop";
    private static final String GESTURE_PICK_UP_KEY = "gesture_pick_up";
    private static final String GESTURE_IR_WAKEUP_KEY = "gesture_hand_wave";
    private static final String GESTURE_IR_SILENCER_KEY = "gesture_ir_silencer";
    private static final String GESTURE_FLIP_TO_MUTE_KEY = "gesture_flip_to_mute";
    private static final String GESTURE_LIFT_TO_SILENCE_KEY = "gesture_lift_to_silence";

    private final Context mContext;
    private final UpdatedStateNotifier mUpdatedStateNotifier;

    private boolean mChopChopEnabled;
    private boolean mPickUpGestureEnabled;
    private boolean mIrWakeUpEnabled;
    private boolean mIrSilencerEnabled;
    private boolean mFlipToMuteEnabled;
    private boolean mLiftToSilenceEnabled;

    public MotoActionsSettings(Context context, UpdatedStateNotifier updatedStateNotifier) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        loadPreferences(sharedPrefs);
        sharedPrefs.registerOnSharedPreferenceChangeListener(mPrefListener);
        mContext = context;
        mUpdatedStateNotifier = updatedStateNotifier;
    }

    public boolean isChopChopGestureEnabled() {
        return mChopChopEnabled;
    }

    public static boolean isDozeEnabled(ContentResolver contentResolver) {
        return (Settings.Secure.getInt(contentResolver, Settings.Secure.DOZE_ENABLED, 1) != 0);
    }

    public boolean isDozeEnabled() {
        return isDozeEnabled(mContext.getContentResolver());
    }

    public boolean isIrWakeupEnabled() {
        return isDozeEnabled() && mIrWakeUpEnabled;
    }

    public boolean isPickUpEnabled() {
        return isDozeEnabled() && mPickUpGestureEnabled;
    }

    public boolean isIrSilencerEnabled() {
        return mIrSilencerEnabled;
    }

    public boolean isFlipToMuteEnabled() {
        return mFlipToMuteEnabled;
    }

    public boolean isLiftToSilenceEnabled() {
        return mLiftToSilenceEnabled;
    }

    public void chopChopAction() {
        new TorchAction(mContext).action();
    }

    private void loadPreferences(SharedPreferences sharedPreferences) {
        mChopChopEnabled = sharedPreferences.getBoolean(GESTURE_CHOP_CHOP_KEY, true);
        mIrWakeUpEnabled = sharedPreferences.getBoolean(GESTURE_IR_WAKEUP_KEY, true);
        mPickUpGestureEnabled = sharedPreferences.getBoolean(GESTURE_PICK_UP_KEY, true);
        mIrSilencerEnabled = sharedPreferences.getBoolean(GESTURE_IR_SILENCER_KEY, false);
        mFlipToMuteEnabled = sharedPreferences.getBoolean(GESTURE_FLIP_TO_MUTE_KEY, false);
        mLiftToSilenceEnabled = sharedPreferences.getBoolean(GESTURE_LIFT_TO_SILENCE_KEY, false);
    }

    private SharedPreferences.OnSharedPreferenceChangeListener mPrefListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            boolean updated = true;

            if (GESTURE_CHOP_CHOP_KEY.equals(key)) {
                mChopChopEnabled = sharedPreferences.getBoolean(GESTURE_CHOP_CHOP_KEY, true);
            } else if (GESTURE_IR_WAKEUP_KEY.equals(key)) {
                mIrWakeUpEnabled = sharedPreferences.getBoolean(GESTURE_IR_WAKEUP_KEY, true);
            } else if (GESTURE_PICK_UP_KEY.equals(key)) {
                mPickUpGestureEnabled = sharedPreferences.getBoolean(GESTURE_PICK_UP_KEY, true);
            } else if (GESTURE_IR_SILENCER_KEY.equals(key)) {
                mIrSilencerEnabled = sharedPreferences.getBoolean(GESTURE_IR_SILENCER_KEY, false);
            } else if (GESTURE_FLIP_TO_MUTE_KEY.equals(key)) {
                mFlipToMuteEnabled = sharedPreferences.getBoolean(GESTURE_FLIP_TO_MUTE_KEY, false);
            } else if (GESTURE_LIFT_TO_SILENCE_KEY.equals(key)) {
                mLiftToSilenceEnabled = sharedPreferences.getBoolean(GESTURE_LIFT_TO_SILENCE_KEY, false);
            } else {
                Constants.setSystemSetting(mContext, key);
                updated = false;
            }

            if (updated) {
                mUpdatedStateNotifier.updateState();
            }
        }
    };
}
