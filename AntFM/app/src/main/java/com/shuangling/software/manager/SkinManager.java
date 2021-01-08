/*
 * Tencent is pleased to support the open source community by making QMUI_Android available.
 *
 * Copyright (C) 2017-2018 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the MIT License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.shuangling.software.manager;

import android.content.Context;
import android.content.res.Configuration;

import com.qmuiteam.qmui.skin.QMUISkinManager;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.utils.SharedPreferencesUtils;


public class SkinManager {
    public static final int SKIN_BLUE = 1;
    public static final int SKIN_DARK_BLUE = 2;
    public static final int SKIN_PURPLE = 3;
    public static final int SKIN_DARK_PURPLE = 4;
    public static final int SKIN_RED = 5;
    public static final int SKIN_DARK_RED = 6;
    public static final int SKIN_GREEN = 7;
    public static final int SKIN_DARK_GREEN = 8;
    public static final int SKIN_ORANGE = 9;
    public static final int SKIN_DARK_ORANGE = 10;




    public static void install(Context context) {
        QMUISkinManager skinManager = QMUISkinManager.defaultInstance(context);
        skinManager.addSkin(SKIN_BLUE, R.style.AppThemeBlue);
        skinManager.addSkin(SKIN_DARK_BLUE, R.style.AppThemeDarkBlue);

        skinManager.addSkin(SKIN_PURPLE, R.style.AppThemePurple);
        skinManager.addSkin(SKIN_DARK_PURPLE, R.style.AppThemeDarkPurple);

        skinManager.addSkin(SKIN_RED, R.style.AppThemeRed);
        skinManager.addSkin(SKIN_DARK_RED, R.style.AppThemeDarkRed);

        skinManager.addSkin(SKIN_GREEN, R.style.AppThemeGreen);
        skinManager.addSkin(SKIN_DARK_GREEN, R.style.AppThemeDarkGreen);

        skinManager.addSkin(SKIN_ORANGE, R.style.AppThemeOrange);
        skinManager.addSkin(SKIN_DARK_ORANGE, R.style.AppThemeDarkOrange);

        boolean isDarkMode = (context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        int storeSkinIndex = SharedPreferencesUtils.getSkinIndex();

        skinManager.changeSkin(SKIN_DARK_ORANGE);

    }

    public static void changeSkin(int index) {
        QMUISkinManager.defaultInstance(MyApplication.getInstance()).changeSkin(index);
        SharedPreferencesUtils.setSkinIndex(index);
    }

    public static int getCurrentSkin() {
        return QMUISkinManager.defaultInstance(MyApplication.getInstance()).getCurrentSkin();
    }
}
