package com.shuangling.software.oss;

import android.util.Log;
import android.webkit.MimeTypeMap;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.utils.BinaryUtil;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.HeadObjectRequest;
import com.alibaba.sdk.android.oss.model.HeadObjectResult;
import com.alibaba.sdk.android.oss.model.ImagePersistRequest;
import com.alibaba.sdk.android.oss.model.ImagePersistResult;
import com.alibaba.sdk.android.oss.model.ListObjectsRequest;
import com.alibaba.sdk.android.oss.model.ListObjectsResult;
import com.alibaba.sdk.android.oss.model.OSSRequest;
import com.alibaba.sdk.android.oss.model.ObjectMetadata;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.alibaba.sdk.android.oss.model.ResumableUploadRequest;
import com.alibaba.sdk.android.oss.model.ResumableUploadResult;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;


/**
 * Created by mOss on 2015/12/7 0007.
 * 支持普通上传，普通下载
 */
public class OssService {





    public OSS mOss;
    private String mBucket;
    private String mCallbackAddress;
    private static String mResumableObjectKey = "resumableObject";

    public OssService(OSS oss, String bucket) {
        this.mOss = oss;
        this.mBucket = bucket;
    }

    public void setBucketName(String bucket) {
        this.mBucket = bucket;
    }

    public void initOss(OSS _oss) {
        this.mOss = _oss;
    }

    public void setCallbackAddress(String callbackAddress) {
        this.mCallbackAddress = callbackAddress;
    }

    public void asyncGetImage(String object,OSSProgressCallback<GetObjectRequest> progressCallback,OSSCompletedCallback<GetObjectRequest, GetObjectResult> completedCallback) {

        final long get_start = System.currentTimeMillis();
        if ((object == null) || object.equals("")) {
            Log.w("AsyncGetImage", "ObjectNull");
            return;
        }

        GetObjectRequest get = new GetObjectRequest(mBucket, object);
        get.setCRC64(OSSRequest.CRC64Config.YES);
        get.setProgressListener(progressCallback);
        OSSAsyncTask task = mOss.asyncGetObject(get, completedCallback);
    }



    public void asyncUploadFile(String object, final String localFile,OSSProgressCallback<PutObjectRequest> progressCallback ,OSSCompletedCallback<PutObjectRequest, PutObjectResult> completedCallback) {
        final long upload_start = System.currentTimeMillis();

        if (object.equals("")) {
            Log.w("asyncUploadFile", "ObjectNull");
            return;
        }

        File file = new File(localFile);
        if (!file.exists()) {
            Log.w("asyncUploadFile", "FileNotExist");
            Log.w("LocalFile", localFile);
            return;
        }

        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(mBucket, object, localFile);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(getMimeType(localFile));
        put.setMetadata(metadata);
        String md5="";
        try {
            // 设置Md5以便校验
            md5= BinaryUtil.calculateBase64Md5(localFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final String contentMD5=md5;


        put.setCRC64(OSSRequest.CRC64Config.YES);
        if (mCallbackAddress != null) {
            // 传入对应的上传回调参数，这里默认使用OSS提供的公共测试回调服务器地址
            put.setCallbackParam(new HashMap<String, String>() {
                {
                    //put("callbackUrl", mCallbackAddress);
                    //callbackBody可以自定义传入的信息
                    //put("callbackBody", "filename=${object}");
                    put("callbackBody", "filename=${object}&duration=${x:duration}&localname=${x:localname}&mimeType=${mimeType}&size=${size}&photo=${x:photo}&system=${x:system}&content-md5=${x:content-md5}");
                }
            });

            put.setCallbackVars(new HashMap<String, String>() {
                {
                    put("x:photo", "android");
                    put("x:system", "YunOS5.0");
                    put("x:content-md5", contentMD5);
                    put("x:localname", localFile.substring(localFile.lastIndexOf(File.separator)+1));
                    put("x:duration","");
                }
            });
        }

        // 异步上传时可以设置进度回调
        put.setProgressCallback(progressCallback);

        OSSAsyncTask task = mOss.asyncPutObject(put, completedCallback);
    }


    public void asyncPutImage(String object, final String localFile,OSSProgressCallback<PutObjectRequest> progressCallback ,OSSCompletedCallback<PutObjectRequest, PutObjectResult> completedCallback) {
        final long upload_start = System.currentTimeMillis();

        if (object.equals("")) {
            Log.w("AsyncPutImage", "ObjectNull");
            return;
        }

        File file = new File(localFile);
        if (!file.exists()) {
            Log.w("AsyncPutImage", "FileNotExist");
            Log.w("LocalFile", localFile);
            return;
        }

        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(mBucket, object, localFile);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(getMimeType(localFile));
        put.setMetadata(metadata);
        String md5="";
        try {
            // 设置Md5以便校验
            md5= BinaryUtil.calculateBase64Md5(localFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final String contentMD5=md5;


        put.setCRC64(OSSRequest.CRC64Config.YES);
        if (mCallbackAddress != null) {
            // 传入对应的上传回调参数，这里默认使用OSS提供的公共测试回调服务器地址
            put.setCallbackParam(new HashMap<String, String>() {
                {
                    put("callbackUrl", mCallbackAddress);
                    //callbackBody可以自定义传入的信息
                    //put("callbackBody", "filename=${object}");
                    put("callbackBody", "filename=${object}&duration=${x:duration}&localname=${x:localname}&mimeType=${mimeType}&size=${size}&photo=${x:photo}&system=${x:system}&content-md5=${x:content-md5}");
                }
            });

            put.setCallbackVars(new HashMap<String, String>() {
                {
                    put("x:photo", "android");
                    put("x:system", "YunOS5.0");
                    put("x:content-md5", contentMD5);
                    put("x:localname", localFile.substring(localFile.lastIndexOf(File.separator)+1));
                    put("x:duration","");
                }
            });
        }

        // 异步上传时可以设置进度回调
        put.setProgressCallback(progressCallback);

        OSSAsyncTask task = mOss.asyncPutObject(put, completedCallback);
    }

    // Downloads the files with specified prefix in the asynchronous way.
    public void asyncListObjectsWithBucketName(String prefix,OSSCompletedCallback<ListObjectsRequest, ListObjectsResult> completedCallback) {
        ListObjectsRequest listObjects = new ListObjectsRequest(mBucket);
        // Sets the prefix
        listObjects.setPrefix(prefix);
        //listObjects.setDelimiter("/");
        // Sets the success and failure callback. calls the Async API
        OSSAsyncTask task = mOss.asyncListObjects(listObjects, completedCallback);
    }

    // Gets file's metadata
    public void headObject(String objectKey,OSSCompletedCallback<HeadObjectRequest, HeadObjectResult> completedCallback) {
        // Creates a request to get the file's metadata
        HeadObjectRequest head = new HeadObjectRequest(mBucket, objectKey);

        OSSAsyncTask task = mOss.asyncHeadObject(head, completedCallback);
    }



    void asyncResumableUpload(String uploadKey,String resumableFilePath,OSSProgressCallback<ResumableUploadRequest> progressCallback,OSSCompletedCallback<ResumableUploadRequest, ResumableUploadResult> completedCallback) {
        ResumableUploadRequest request = new ResumableUploadRequest(mBucket, uploadKey, resumableFilePath);
        request.setProgressCallback(progressCallback);
        OSSAsyncTask task = mOss.asyncResumableUpload(request, completedCallback);
    }



    public void imagePersist(String fromBucket, String fromObjectKey, String toBucket, String toObjectkey, String action,OSSCompletedCallback<ImagePersistRequest, ImagePersistResult> completedCallback ){

        ImagePersistRequest request = new ImagePersistRequest(fromBucket,fromObjectKey,toBucket,toObjectkey,action);

        OSSAsyncTask task = mOss.asyncImagePersist(request, completedCallback);
    }


    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }

        return type;
    }
}
