package com.shuangling.software.interf;

import android.net.Uri;
import android.view.View;

import java.io.Serializable;

/**
 * 项目名称：AntFM
 * 创建人：YoungBean
 * 创建时间：2021/3/10 13:44
 * 类描述：
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public interface LoadImageInterface <T extends View> extends Serializable {
   void getBitmap(Uri uri);
}
