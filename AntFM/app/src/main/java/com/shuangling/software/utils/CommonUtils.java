package com.shuangling.software.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
//import androidx.annotation.ColorInt;
//import androidx.fragment.app.DialogFragment;
//import androidx.fragment.app.FragmentManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.shuangling.software.activity.AlbumDetailActivity;
import com.shuangling.software.activity.ArticleDetailActivity02;
import com.shuangling.software.activity.AudioDetailActivity;
import com.shuangling.software.activity.BindPhoneActivity;
import com.shuangling.software.activity.CluesActivity;
import com.shuangling.software.activity.ContentActivity;
import com.shuangling.software.activity.GalleriaActivity;
import com.shuangling.software.activity.MainActivity;
import com.shuangling.software.activity.NewLoginActivity;
import com.shuangling.software.activity.RadioDetailActivity;
import com.shuangling.software.activity.RadioListActivity;
import com.shuangling.software.activity.SpecialDetailActivity;
import com.shuangling.software.activity.TvDetailActivity;
import com.shuangling.software.activity.VideoDetailActivity;
import com.shuangling.software.activity.WebViewBackActivity;
import com.shuangling.software.entity.Column;
import com.shuangling.software.entity.User;

import android.webkit.WebSettings;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import com.gyf.immersionbar.ImmersionBar;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigDialog;
import com.mylhyl.circledialog.params.DialogParams;
import com.mylhyl.circledialog.res.drawable.CircleDrawable;
import com.mylhyl.circledialog.res.values.CircleDimen;
import com.mylhyl.circledialog.view.listener.OnCreateBodyViewListener;
import com.shuangling.software.MyApplication;
import com.shuangling.software.R;
import com.shuangling.software.activity.FontSizeSettingActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class CommonUtils {
    public static final int NETWORKTYPE_WIFI = 0x1;
    public static final int NETWORKTYPE_MOBILE = 0x2;
    public static final int NETWORKTYPE_INVALID = 0x3;
    private static AudioManager am;

    public static String getStoragePublicDirectory(String dirType) {
        String state = Environment.getExternalStorageState();
        Context context = MyApplication.getInstance();
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            File file = context.getFilesDir();
            if (!file.exists()) {
                file.mkdirs();
            }
            return file.getAbsolutePath();
        } else {
            //Environment.DIRECTORY_DOCUMENTS
            //此文件目录在app被卸载时不会删除掉
            File file1 = Environment.getExternalStorageDirectory();
            File file = Environment.getExternalStoragePublicDirectory(dirType);
            if (!file.exists()) {
                file.mkdirs();
            }
            return file.getAbsolutePath();
        }
    }

    public static String getStoragePrivateDirectory(String dirType) {
        String state = Environment.getExternalStorageState();
        Context context = MyApplication.getInstance();
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            File file = context.getFilesDir();
            if (!file.exists()) {
                file.mkdirs();
            }
            return file.getAbsolutePath();
        } else {
            //Environment.DIRECTORY_DOCUMENTS
            //此文件目录在app被卸载时会删除掉
            File file = context.getExternalFilesDir(dirType);
            if (!file.exists()) {
                file.mkdirs();
            }
            return file.getAbsolutePath();
        }
    }

    public static @ColorInt
    int getThemeColor(Context context) {
        int[] attrs = new int[]{R.attr.themeColor};
        TypedArray typedArray = context.obtainStyledAttributes(attrs);
        int color = typedArray.getColor(0, 0xffffffff);
        typedArray.recycle();
        return color;
    }

    public static String saveBitmap(Bitmap b) {
        String path = getStoragePrivateDirectory(Environment.DIRECTORY_PICTURES);
        long dataTake = System.currentTimeMillis();
        String jpegName = path + File.separator + "picture_" + dataTake + ".jpg";
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            return jpegName;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    public static Boolean CopyAssetsFile(Context context, String filename, String des) {
        Boolean isSuccess = true;
        //复制安卓apk的assets目录下任意路径的单个文件到des文件夹，注意是否对des有写权限
        AssetManager assetManager = context.getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(filename);
            String newFileName = des + File.separator + filename;
            out = new FileOutputStream(newFileName);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            isSuccess = false;
        }
        return isSuccess;
    }

    public static boolean CopyAssetsDir(Context context, String src, String des) {
        //复制安卓Assets下的“非空目录”到des文件夹，注意是否对des有写权限
        Boolean isSuccess = true;
        String[] files;
        try {
            files = context.getResources().getAssets().list(src);
            if (files.length == 0) {
                isSuccess = CopyAssetsFile(context, src, des);
                if (!isSuccess)
                    return isSuccess;
            } else {
                File srcfile = new File(des + File.separator + src);
                if (!srcfile.exists()) {
                    boolean b = srcfile.mkdirs();
                    if (b) {
                        for (int i = 0; i < files.length; i++) {
                            isSuccess = CopyAssetsDir(context, src + File.separator + files[i], des);//递归调用
                            if (!isSuccess)
                                return isSuccess;
                        }
                    } else {
                        return false;
                    }
                }
            }
            return isSuccess;
        } catch (IOException e) {
            return false;
        }
    }

    public static String creatUUID() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
                fs.close();
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
    }

    /**
     * 复制整个文件夹内容
     *
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public static void copyFolder(String oldPath, String newPath) {
        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }
                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + File.separator +
                            (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {//如果是子文件夹
                    copyFolder(oldPath + File.separator + file[i], newPath + File.separator + file[i]);
                }
            }
        } catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();
        }
    }

    public static void saveBitmap(String filePath, Bitmap bitmap) {
        File f = new File(filePath);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void saveBitmapToPNG(String filePath, Bitmap bitmap) {
        File f = new File(filePath);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    public static void renameFile(String path, String oldname, String newname) {
        if (!oldname.equals(newname)) {//新的文件名和以前文件名不同时,才有必要进行重命名
            File oldfile = new File(path + File.separator + oldname);
            File newfile = new File(path + File.separator + newname);
            if (!oldfile.exists()) {
                return;//重命名文件不存在
            }
            if (newfile.exists())//若在该目录下已经有一个文件和新文件名相同，则不允许重命名
            {
                newfile.delete();
                oldfile.renameTo(newfile);
            } else {
                oldfile.renameTo(newfile);
            }
        }
    }

    public static String getCurrentTimeString() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        return format.format(date);
    }

    public static String getDateTimeString() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    public static int dip2px(float dpValue) {
        final float scale = MyApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px2dp
     */
    public static int px2dip(float pxValue) {
        final float scale = MyApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int px2sp(float pxValue) {
        final float fontScale = MyApplication.getInstance().getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(float spValue) {
        final float fontScale = MyApplication.getInstance().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int dp2sp(float dp) {
        final float density = MyApplication.getInstance().getResources().getDisplayMetrics().density;
        final float fontScale = MyApplication.getInstance().getResources().getDisplayMetrics().scaledDensity;
        float s = CommonUtils.getFontSize();
        //int sp = (int)((dp*density+0.5)/CommonUtils.getFontSize() +0.5);
        //int sp = (int) (dip2px(dp) / (float) sp2px(dp));
        int sp = px2sp(dip2px(dp));
        return sp;
    }

    public static boolean isMobileNO(String mobiles) {
        /*
        移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
	    联通：130、131、132、152、155、156、185、186 
	    电信：133、153、180、189、（1349卫通） 
	    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9 
	    */
        String telRegex = "[1][3456789]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles))
            return false;
        else
            return mobiles.matches(telRegex);
    }

    public static boolean isIdentityNumber(String number) {
	    /* 
	    身份证号码验证 
	    */
        String telRegex = "(^\\d{15}$)|(^\\d{17}([0-9]|X)$)";
        if (TextUtils.isEmpty(number))
            return false;
        else
            return number.matches(telRegex);
    }

    public static boolean isValidPassword01(String password) {
        //6-22位的数字字母组合
        String telRegex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,22}$";
        if (TextUtils.isEmpty(password))
            return false;
        else {
            return password.matches(telRegex);
        }
    }

    public static boolean isValidPassword(String password) {
        //需由6-20位数字、字母或符号组成，至少两种
        String telRegex = "^(?![0-9]+$)(?![a-zA-Z]+$)(?![\\W]+$)[0-9A-Za-z\\W]{6,20}$";
        if (TextUtils.isEmpty(password))
            return false;
        else {
            return password.matches(telRegex);
        }
    }

    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public static boolean ipCheck(String text) {
        if (!TextUtils.isEmpty(text)) {
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            if (text.matches(regex)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public static boolean isURL2(String str) {
        String regex = "^((https|http|ftp|rtsp|mms)?://)"
                + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" //ftp的user@
                + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP形式的URL- 199.194.52.184
                + "|" // 允许IP和DOMAIN（域名）
                + "([0-9a-z_!~*'()-]+\\.)*" // 域名- www.
//                 + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." // 二级域名
                + "[a-z]{2,6})" // first level domain- .com or .museum
                + "(:[0-9]{1,4})?" // 端口- :80
                + "((/?)|" // a slash isn't required if there is no file name
                + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
        return match(regex, str);
    }

    public static String randomStr(int len) {
        char[] chArr = new char[len];
        Random ra = new Random();
        char[] codes = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
                'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
                'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
                'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
                'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
                'y', 'z'};
        for (int i = 0; i < len; i++) {
            chArr[i] = codes[ra.nextInt(codes.length)];
        }
        return new String(chArr);
    }

    public static byte[] toByteArray(File f) throws IOException {
        if (!f.exists()) {
            throw new FileNotFoundException(f.getAbsolutePath());
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bos.close();
        }
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static int getNetWorkType(Context context) {
        int mNetWorkType = NETWORKTYPE_INVALID;
//		Context context = getApplicationContext();
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();
            if (type.equalsIgnoreCase("WIFI")) {
                mNetWorkType = NETWORKTYPE_WIFI;
            } else if (type.equalsIgnoreCase("MOBILE")) {
                mNetWorkType = NETWORKTYPE_MOBILE;
            }
        }
        return mNetWorkType;
    }

    public static Boolean getNetWork() {
        ConnectivityManager connMgr = (ConnectivityManager) MyApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        return activeInfo != null && activeInfo.isConnected();
    }

    public static int getScreenWidth() {
        WindowManager wm = (WindowManager) MyApplication.getInstance().getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);
        return point.x;
    }

    public static int getScreenHeight() {
        WindowManager wm = (WindowManager) MyApplication.getInstance().getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);
        return point.y;
    }

    public static int getStateBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static void setTransparentStatusBar(Activity activity) {
        if (hasSoftKeys(activity.getWindowManager())) {
//有虚拟键的取消状态栏渲染防止底部导航栏被虚拟键遮挡
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        } else {
//			if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT) {
//
//				getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
//
//				getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//
//			}
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//给状态栏设置颜色。我设置透明。
                window.setStatusBarColor(Color.TRANSPARENT);
                //window.setNavigationBarColor(Color.TRANSPARENT);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Window window = activity.getWindow();
                window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//android6.0以后可以对状态栏文字颜色和图标进行修改
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }

    public static int timeToSecond(String time) {
        String[] my = time.split(":");
        int hour = Integer.parseInt(my[0]);
        int min = Integer.parseInt(my[1]);
        int sec = Integer.parseInt(my[2]);
        return hour * 3600 + min * 60 + sec;
    }

    private static DecimalFormat fileIntegerFormat = new DecimalFormat("#0");
    private static DecimalFormat fileDecimalFormat = new DecimalFormat("#0.#");

    /**
     * 单位换算
     *
     * @param size      单位为B
     * @param isInteger 是否返回取整的单位
     * @return 转换后的单位
     */
    public static String formatFileSize(long size, boolean isInteger) {
        DecimalFormat df = isInteger ? fileIntegerFormat : fileDecimalFormat;
        String fileSizeString = "0M";
        if (size < 1024 && size > 0) {
            fileSizeString = df.format((double) size) + "B";
        } else if (size < 1024 * 1024) {
            fileSizeString = df.format((double) size / 1024) + "K";
        } else if (size < 1024 * 1024 * 1024) {
            fileSizeString = df.format((double) size / (1024 * 1024)) + "M";
        } else {
            fileSizeString = df.format((double) size / (1024 * 1024 * 1024)) + "G";
        }
        return fileSizeString;
    }

    public static boolean hasSoftKeys(WindowManager windowManager) {
        Display d = windowManager.getDefaultDisplay();
        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getRealMetrics(realDisplayMetrics);
        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);
        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;
        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
//        BigInteger bigInt = new BigInteger(1, digest.digest());
//        return bigInt.toString(16);
        return bytesToHexString(digest.digest());
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static String getFileMD5(InputStream in) {
        MessageDigest digest = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
//        BigInteger bigInt = new BigInteger(1, digest.digest());
//        return bigInt.toString(16);
        return bytesToHexString(digest.digest());
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * Desc: 获取虚拟按键高度 放到工具类里面直接调用即可
     */
    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        if (hasNavBar(context)) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    /**
     * 检查是否存在虚拟按键栏
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static boolean hasNavBar(Context context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
        if (resourceId != 0) {
            boolean hasNav = res.getBoolean(resourceId);
            // check override flag
            String sNavBarOverride = getNavBarOverride();
            if ("1".equals(sNavBarOverride)) {
                hasNav = false;
            } else if ("0".equals(sNavBarOverride)) {
                hasNav = true;
            }
            return hasNav;
        } else { // fallback
            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }

    /**
     * 判断虚拟按键栏是否重写
     *
     * @return
     */
    private static String getNavBarOverride() {
        String sNavBarOverride = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable e) {
            }
        }
        return sNavBarOverride;
    }

    public static void hideInput(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null && imm.isActive()) {
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void closeInputMethod(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();
        if (isOpen) {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//没有显示则显示
            //imm.hideSoftInputFromWindow(mobile_topup_num.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            view.requestFocus();
            imm.showSoftInput(view, 0);
        }
    }

    public static void deleteDir(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File subFile : files) {
                    if (subFile.isDirectory())
                        deleteDir(subFile.getPath());
                    else
                        subFile.delete();
                }
            }
            file.delete();
        }
    }

    public static String getDurationString(long second) {
        String timecodeHours = "" + ((int) second / 60 / 60);
        String timecodeMinutes = "" + ((int) second / 60) % 60;
        String timecodeSeconds = "" + (int) second % 60;
        if ((int) second % 60 < 10) {
            timecodeSeconds = "0" + timecodeSeconds;
        }
        if (((int) second / 60) % 60 < 10) {
            timecodeMinutes = "0" + timecodeMinutes;
        }
        if (((int) second / 60 / 60) < 10) {
            timecodeHours = "0" + timecodeHours;
        }
        return timecodeHours + ":" + timecodeMinutes + ":" + timecodeSeconds;
    }

    public static String getShowTime(long milliseconds) {
        // 获取日历函数
        long seconds = milliseconds / 1000;
        String timecodeSeconds = "" + (seconds % 60);
        String timecodeMinutes = "" + (seconds / 60) % 60;
        String timecodeHours = "" + (seconds / 3600);
        if ((seconds % 60) < 10) {
            timecodeSeconds = "0" + timecodeSeconds;
        }
        if ((seconds / 60) % 60 < 10) {
            timecodeMinutes = "0" + timecodeMinutes;
        }
        if (seconds / 3600 < 10) {
            timecodeHours = "0" + timecodeHours;
        }
        if (seconds / 3600 < 1) {
            return timecodeMinutes + ":" + timecodeSeconds;
        } else {
            return timecodeHours + ":" + timecodeMinutes + ":" + timecodeSeconds;
        }
    }

    public static void setSpeakerPhone(boolean on) {
        if (am == null) {
            am = (AudioManager) MyApplication.getInstance().getSystemService(Context.AUDIO_SERVICE);
        }
        am.setMode(AudioManager.MODE_IN_COMMUNICATION);
        //am.setMode(AudioManager.MODE_NORMAL);
        if (on) {
            //免提设置最大音量
//am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, am.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);
            am.setSpeakerphoneOn(true);
            //am.setBluetoothScoOn(true);
        } else {
            //非免提设置一半音量，FH688设备中，非免提状态下麦克风增益很大
            //am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, am.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL) * 2 / 3, 0);
            am.setSpeakerphoneOn(false);
        }
    }

    public static SpannableString tagKeyword(String str, String keyword) {
        int start = str.indexOf(keyword);
        int length = keyword.length();
        SpannableString spannableString = new SpannableString(str);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#1CA0FF"));
        spannableString.setSpan(colorSpan, start, start + length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public static SpannableString tagKeyword(String str, String keyword, int color) {
        int start = str.indexOf(keyword);
        int length = keyword.length();
        SpannableString spannableString = new SpannableString(str);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
        spannableString.setSpan(colorSpan, start, start + length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public static DialogFragment showLoadingDialog(FragmentManager manager) {
        DialogFragment dialogFragment = new CircleDialog.Builder()
                .setWidth(0.4f)
                .setBodyView(R.layout.share_page_loading01, new OnCreateBodyViewListener() {
                    @Override
                    public void onCreateBodyView(View view) {
                        CircleDrawable bgCircleDrawable = new CircleDrawable(0x88000000
                                , CircleDimen.DIALOG_RADIUS, CircleDimen.DIALOG_RADIUS, CircleDimen.DIALOG_RADIUS, CircleDimen.DIALOG_RADIUS);
                        view.setBackground(bgCircleDrawable);
                    }
                }).configDialog(new ConfigDialog() {
                    @Override
                    public void onConfig(DialogParams params) {
                        params.isDimEnabled = false;
                        params.gravity = Gravity.CENTER;
                        //params.backgroundColor=Color.argb(128,255,255,255);
                        //params.alpha=1.0f;
                        //params.backgroundColor=Color.DKGRAY;
                    }
                })
                .setCanceledOnTouchOutside(false)
                .setCancelable(false)
                .show(manager);
        return dialogFragment;
    }

    /**
     * 设置textView结尾...后面显示的文字和颜色
     *
     * @param context    上下文
     * @param textView   textview
     * @param minLines   最少的行数
     * @param originText 原文本
     * @param endText    结尾文字
     * @param endColorID 结尾文字颜色id
     * @param isExpand   当前是否是展开状态
     */
    public void toggleEllipsize(final Context context,
                                final TextView textView,
                                final int minLines,
                                final String originText,
                                final String endText,
                                final int endColorID,
                                final boolean isExpand) {
        if (TextUtils.isEmpty(originText)) {
            return;
        }
        textView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                .OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (isExpand) {
                    textView.setText(originText);
                } else {
                    int paddingLeft = textView.getPaddingLeft();
                    int paddingRight = textView.getPaddingRight();
                    TextPaint paint = textView.getPaint();
                    float moreText = textView.getTextSize() * endText.length();
                    float availableTextWidth = (textView.getWidth() - paddingLeft - paddingRight) *
                            minLines - moreText;
                    CharSequence ellipsizeStr = TextUtils.ellipsize(originText, paint,
                            availableTextWidth, TextUtils.TruncateAt.END);
                    if (ellipsizeStr.length() < originText.length()) {
                        CharSequence temp = ellipsizeStr + endText;
                        SpannableStringBuilder ssb = new SpannableStringBuilder(temp);
                        ssb.setSpan(new ForegroundColorSpan(context.getResources().getColor
                                        (endColorID)),
                                temp.length() - endText.length(), temp.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                        textView.setText(ssb);
                    } else {
                        textView.setText(originText);
                    }
                }
                if (Build.VERSION.SDK_INT >= 16) {
                    textView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    textView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    static public void transparentStatusBar(Activity activity) {
        ImmersionBar.with(activity).transparentStatusBar().statusBarDarkFont(true).fitsSystemWindows(true).init();
    }

    public static void setWebviewUserAgent(WebSettings s) {
        String ua = s.getUserAgentString();
        s.setUserAgentString(ua + "/webview");
    }

    public static String getOssResize(int width, int height) {
        return "?x-oss-process=image/resize,m_fill,h_" + height + ",w_" + width;
    }

    public static String getShowNumber(int total) {
        if (total / 10000 > 0) {
            if (total % 10000 >= 1000) {
                return String.format("%.1f", (float) total / 10000) + "万";
            } else {
                return total / 10000 + "万";
            }
        } else {
            return "" + total;
        }
    }

    public static void setReadsAndComment(TextView tv, int coment, int view) {
        if (coment >= 1) {
            tv.setVisibility(View.VISIBLE);
            if (view >= 10) {
                tv.setText(CommonUtils.getShowNumber(view) + "阅读   " + CommonUtils.getShowNumber(coment) + "评论");
            } else {
                tv.setText(coment + "评论");
            }
        } else if (view >= 10 && coment < 1) {
            tv.setVisibility(View.VISIBLE);
            tv.setText(CommonUtils.getShowNumber(view) + "阅读");
        } else {
            tv.setVisibility(View.GONE);
            tv.setText("");
        }
    }

    public static Resources setFontSize(Resources res) {
        res.getConfiguration().fontScale = getFontSize();
        res.updateConfiguration(res.getConfiguration(), res.getDisplayMetrics());
        return res;
    }

    public static float getFontSize() {
        float appFontSize = SharedPreferencesUtils.getFloatValue(FontSizeSettingActivity.FONT_SIZE, 1.00f);
        float systemFontSize = Constant.SYSTEM_FONT_SCALE;
        float mixFontSize = appFontSize * systemFontSize;
        float max = (appFontSize > systemFontSize) ? appFontSize : systemFontSize;
        float fontSize = Math.min(mixFontSize, max);
        return fontSize;
    }

    /**
     * 截取字符串前面的正整数,如"22天"得"22","18个人"得到"18". * @return
     */
    public static String getQuantity(String regular) {
        int index = 0;
        for (int i = 0; i < regular.length(); i++) {
            char c = regular.charAt(i);
            if (Character.isDigit(c)) {
                if (i == regular.length() - 1) {
                    index = i + 1;
                } else {
                    index = i;
                }
                continue;
            } else {
                index = i;
                break;
            }
        }
        return regular.substring(0, index);
    }


    public static void  setStatusHeight(Context context,View statusBar){
        ViewGroup.LayoutParams lp=statusBar.getLayoutParams();
        lp.height= QMUIStatusBarHelper.getStatusbarHeight(context);
        statusBar.setLayoutParams(lp);
    }


    public static void jumpTo(Context context,String url, String title) {
        if (url.startsWith(ServerInfo.h5IP + "/tv") || url.startsWith(ServerInfo.h5HttpsIP + "/tv")) {
            Intent it = new Intent(context, RadioListActivity.class);
            it.putExtra("type", "2");
            context.startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/lives/") || url.startsWith(ServerInfo.h5HttpsIP + "/lives/")) {
            String radioId = CommonUtils.getQuantity(url.substring(url.lastIndexOf("/") + 1));
            Intent it = new Intent(context, TvDetailActivity.class);
            it.putExtra("radioId", Integer.parseInt(radioId));
            context.startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/radios/") || url.startsWith(ServerInfo.h5HttpsIP + "/radios/")) {
            String radioId = CommonUtils.getQuantity(url.substring(url.lastIndexOf("/") + 1));
            Intent it = new Intent(context, RadioDetailActivity.class);
            it.putExtra("radioId", Integer.parseInt(radioId));
            context.startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/radios") || url.startsWith(ServerInfo.h5HttpsIP + "/radios")) {
            Intent it = new Intent(context, RadioListActivity.class);
            it.putExtra("type", "1");
            context.startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/gover") || url.startsWith(ServerInfo.h5HttpsIP + "/gover")) {
            Intent it = new Intent(context, WebViewBackActivity.class);
            it.putExtra("addParams",true);
            it.putExtra("title", title);
            it.putExtra("url", url);
            context.startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/dj") || url.startsWith(ServerInfo.h5HttpsIP + "/dj")) {
            Intent it = new Intent(context, WebViewBackActivity.class);
            it.putExtra("addParams",true);
            it.putExtra("title", title);
            it.putExtra("url", url);
            context.startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/interact") || url.startsWith(ServerInfo.h5HttpsIP + "/interact")) {
            Intent it = new Intent(context, WebViewBackActivity.class);
            it.putExtra("addParams",true);
            it.putExtra("title", title);
            it.putExtra("url", url);
            context.startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/guide") || url.startsWith(ServerInfo.h5HttpsIP + "/guide")) {
            Intent it = new Intent(context, WebViewBackActivity.class);
            it.putExtra("addParams",true);
            it.putExtra("title", title);
            it.putExtra("url", url);
            context.startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/cates/") || url.startsWith(ServerInfo.h5HttpsIP + "/cates/")) {
            //跳转栏目
            if(context instanceof MainActivity){
                String columnid = CommonUtils.getQuantity(url.substring(url.lastIndexOf("/") + 1));
                Column column = new Column();
                column.setId(Integer.parseInt(columnid));
                ((MainActivity)context).switchRecommend(column);
            }else if(context instanceof Activity){
                //跳转栏目
                Intent it = new Intent(context, WebViewBackActivity.class);
                it.putExtra("addParams",true);
                it.putExtra("url", url);
                it.putExtra("title", title);
                context.startActivity(it);
            }



        } else if (url.startsWith(ServerInfo.h5IP + "/specials") || url.startsWith(ServerInfo.h5HttpsIP + "/specials")) {
            //跳转热门
            if(context instanceof MainActivity){
                Column column = null;
                List<Column> allColumns = JSONObject.parseArray(SharedPreferencesUtils.getStringValue("all_column", null), Column.class);
                for (int i = 0; allColumns != null && i < allColumns.size(); i++) {
                    if (allColumns.get(i).getType() == 1) {
                        column = allColumns.get(i);
                        break;
                    }
                }
                if (column != null) {
                    ((MainActivity) context).switchRecommend(column);
                }
            }else if(context instanceof Activity){
                //跳转热门
                Intent it = new Intent(context, WebViewBackActivity.class);
                it.putExtra("addParams",true);
                it.putExtra("url", url);
                it.putExtra("title", title);
                context.startActivity(it);
            }

        } else if (url.startsWith(ServerInfo.h5IP + "/orgs/") || url.startsWith(ServerInfo.h5HttpsIP + "/orgs/")) {
            String organizationId = url.substring(url.lastIndexOf("/") + 1);

            Intent it = new Intent(context, WebViewBackActivity.class);
            it.putExtra("addParams",true);
            it.putExtra("title", title);
            it.putExtra("url", ServerInfo.h5HttpsIP + "/orgs/" + organizationId);
            context.startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/anchors/") || url.startsWith(ServerInfo.h5HttpsIP + "/anchors/")) {
            String anchorId = url.substring(url.lastIndexOf("/") + 1);
//            Intent it = new Intent(getContext(), AnchorDetailActivity.class);
//            it.putExtra("anchorId", Integer.parseInt(anchorId));
//            startActivity(it);
            Intent it = new Intent(context, WebViewBackActivity.class);
            it.putExtra("addParams",true);
            it.putExtra("url", ServerInfo.h5HttpsIP + "/anchors/" + anchorId);
            it.putExtra("title", title);
            context.startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/atlas/") || url.startsWith(ServerInfo.h5HttpsIP + "/atlas/")) {
            String galleriaId = CommonUtils.getQuantity(url.substring(url.lastIndexOf("/") + 1));
            Intent it = new Intent(context, GalleriaActivity.class);
            it.putExtra("galleriaId", Integer.parseInt(galleriaId));
            context.startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/albums/") || url.startsWith(ServerInfo.h5HttpsIP + "/albums/")) {
            String albumId = CommonUtils.getQuantity(url.substring(url.lastIndexOf("/") + 1));
            Intent it = new Intent(context, AlbumDetailActivity.class);
            it.putExtra("albumId", Integer.parseInt(albumId));
            context.startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/audios/") || url.startsWith(ServerInfo.h5HttpsIP + "/audios/")) {
            String audioId = CommonUtils.getQuantity(url.substring(url.lastIndexOf("/") + 1));
            Intent it = new Intent(context, AudioDetailActivity.class);
            it.putExtra("audioId", Integer.parseInt(audioId));
            context.startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/posts/") || url.startsWith(ServerInfo.h5HttpsIP + "/posts/")) {
            String articleId = CommonUtils.getQuantity(url.substring(url.lastIndexOf("/") + 1));
            Intent it = new Intent(context, ArticleDetailActivity02.class);
            it.putExtra("articleId", Integer.parseInt(articleId));
            context.startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/specials/") || url.startsWith(ServerInfo.h5HttpsIP + "/specials/")) {
            String specialId = CommonUtils.getQuantity(url.substring(url.lastIndexOf("/") + 1));
            Intent it = new Intent(context, SpecialDetailActivity.class);
            it.putExtra("specialId", Integer.parseInt(specialId));
            context.startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/videos/") || url.startsWith(ServerInfo.h5HttpsIP + "/videos/")) {
            String videoId = CommonUtils.getQuantity(url.substring(url.lastIndexOf("/") + 1));
            Intent it = new Intent(context, VideoDetailActivity.class);
            it.putExtra("videoId", Integer.parseInt(videoId));
            context.startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/subcates/") || url.startsWith(ServerInfo.h5IP + "/subcates/")) {
            String columnid = CommonUtils.getQuantity(url.substring(url.lastIndexOf("/") + 1));
            Column column = new Column();
            column.setId(Integer.parseInt(columnid));
            column.setName(url.substring(url.lastIndexOf("=") + 1));
            Intent it = new Intent(context, ContentActivity.class);
            it.putExtra("column", column);
            context.startActivity(it);
        } else if (url.startsWith(ServerInfo.scs + "/broke-create")) {
            if (User.getInstance() == null) {
                Intent it = new Intent(context, NewLoginActivity.class);
                context.startActivity(it);
            } else if (User.getInstance() != null && TextUtils.isEmpty(User.getInstance().getPhone())) {
                Intent it = new Intent(context, BindPhoneActivity.class);
                //it.putExtra("hasLogined",true);
                context.startActivity(it);
            } else {
                Intent it = new Intent(context, CluesActivity.class);
                it.putExtra("url", ServerInfo.scs + "/broke-create");
                context.startActivity(it);
            }
        } else if (url.startsWith(ServerInfo.h5IP + "/invitation-post") || url.startsWith(ServerInfo.h5HttpsIP + "/invitation-post")) {
            Intent it = new Intent(context, WebViewBackActivity.class);
            it.putExtra("addParams",true);
            it.putExtra("url", url);
            it.putExtra("title", title);
            context.startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/actrank") || url.startsWith(ServerInfo.h5HttpsIP + "/actrank")) {
            Intent it = new Intent(context, WebViewBackActivity.class);
            it.putExtra("addParams",true);
            it.putExtra("url", url);
            it.putExtra("title", title);
            context.startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/wish") || url.startsWith(ServerInfo.h5HttpsIP + "/wish")) {
            Intent it = new Intent(context, WebViewBackActivity.class);
            it.putExtra("addParams",true);
            it.putExtra("url", url);
            it.putExtra("title", title);
            context.startActivity(it);
        } else if (url.startsWith(ServerInfo.h5IP + "/actlist") || url.startsWith(ServerInfo.h5HttpsIP + "/actlist")||url.startsWith(ServerInfo.activity)) {
            Intent it = new Intent(context, WebViewBackActivity.class);
            it.putExtra("addParams",true);
            it.putExtra("url", url);
            it.putExtra("title", title);
            context.startActivity(it);
        } else {
            Intent it = new Intent(context, WebViewBackActivity.class);
            it.putExtra("addParams",false);
            it.putExtra("url", url);
            it.putExtra("title", title);
            context.startActivity(it);
        }
    }

    public static void downloadPic(Context context,final String downloadUrl) {
        String[] preFix = downloadUrl.split("/");
        String fileName = preFix[preFix.length-1];
        if(fileName.lastIndexOf(".")==-1){
            fileName=fileName+".jpg";
        }

        File file = new File(CommonUtils.getStoragePublicDirectory(DIRECTORY_DOWNLOADS) + File.separator + fileName);
        if (file.exists()) {
            file.delete();
        }

        final FileDownloadListener downloadListener = new FileDownloadListener() {
            @Override
            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                Log.i("test", "pending");
            }

            @Override
            protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                Log.i("test", "connected");
            }

            @Override
            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                //StyledDialog.updateProgress(dialog, (int)((long)soFarBytes * 100 / (long)totalBytes), 100, "素材下载中...", true);
            }

            @Override
            protected void blockComplete(BaseDownloadTask task) {
                try {

                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri uri = Uri.fromFile(file);
                    intent.setData(uri);
                    context.sendBroadcast(intent);
                    ToastUtils.show("保存图片成功");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                Log.i("test", ex.toString());
            }

            @Override
            protected void completed(BaseDownloadTask task) {
            }

            @Override
            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
                Log.i("test", e.toString());
            }

            @Override
            protected void warn(BaseDownloadTask task) {
            }
        };
        final FileDownloadQueueSet queueSet = new FileDownloadQueueSet(downloadListener);
        final List<BaseDownloadTask> tasks = new ArrayList<>();
        tasks.add(FileDownloader.getImpl().create(downloadUrl).setPath(file.getPath()));
        //queueSet.setCallbackProgressMinInterval(200);
        //queueSet.disableCallbackProgressTimes();
        // 由于是队列任务, 这里是我们假设了现在不需要每个任务都回调`FileDownloadListener#progress`, 我们只关系每个任务是否完成, 所以这里这样设置可以很有效的减少ipc.
        // 所有任务在下载失败的时候都自动重试一次
        queueSet.setAutoRetryTimes(1);
        // 串行执行该任务队列
        queueSet.downloadSequentially(tasks);
        //queueSet.downloadTogether(tasks);
        queueSet.start();
    }
}
