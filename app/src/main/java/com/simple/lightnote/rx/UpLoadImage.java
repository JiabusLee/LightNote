package com.simple.lightnote.rx;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
/**
 * RxJava Demo类。
 * Lambda表达式的应用
 */
public class UpLoadImage {
    private static final String TAG = "UpLoadImage";

    public static void uploadImage(ArrayList<ImageInfo> imageInfos, final String sMethod) {

        Observable.from(imageInfos)

                .map(new Func1<ImageInfo, ImageInfo>() {
                         @Override
                         public ImageInfo call(ImageInfo imageInfo) {
                             System.out.println(imageInfo);
//                        LogUtils.e(TAG,imageInfo);

//                        byte[] bitmapByte = BitmapUtil.getBitmapByte(imageFilePath);

                             uploadImg(imageInfo);


                             return imageInfo;
                         }
                     }
                )
                .observeOn(Schedulers.io())
                .map(new Func1<ImageInfo, String>() {
                    @Override
                    public String call(ImageInfo imageInfo) {
//                        LogUtils.e(TAG,imageInfo);

                        return submitImgInfo(imageInfo);


                    }
                })
                .take(5)
                .doOnNext(string -> System.out.println(string))
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("onCompleted: 上传完成");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(String s) {
//                        Log.e(TAG, "onNext: " + s);
                        System.out.println("onNext:" + s);
                    }
                });


    }

    private static String submitImgInfo(ImageInfo imageInfo) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("bjSpkBillId", imageInfo.bjSpkBillId);
        map.put("imageUrl", imageInfo.serverUrl);
//                        CleanBean.request(mContext, sMethod, map, false);
//                            String s = HttpUtil.sendHttpPost(null, sMethod, map);
        return "submit success" + imageInfo.hashCode();
    }

    public static ImageInfo uploadImg(ImageInfo imageInfo) {

        try {
            String imageFilePath = imageInfo.imageFilePath;
            int lastIndexOf = imageFilePath.lastIndexOf("/");
            String imageName = imageFilePath.subSequence(
                    lastIndexOf + 1, imageFilePath.length()).toString();
//                        String res = HttpUtil.httpUpload(imageName, bitmapByte);
            String res = "upload success";
            int i = (new Random().nextInt(5) + 1) * 1000;
            Thread.sleep(i);
            System.out.println("睡眠" + i + "秒");
            JSONObject json = new JSONObject(res);
            imageInfo.serverUrl =/* json.getString("data");*/"tomcat";
            return imageInfo;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用Lambda来写RxJava
     * @param imageInfos
     * @param sMethod
     */
    public static  void updateImageL(ArrayList<ImageInfo> imageInfos, final String sMethod) {
        Subscription subscribe = Observable.from(imageInfos)
                .map(imageInfo -> uploadImg(imageInfo))
                .observeOn(Schedulers.io())
                .map(imageInfo -> submitImgInfo(imageInfo))
                .take(2)
                .doOnNext(str -> System.out.println(str))
                .subscribe(result -> System.out.println(result));
        //取消订阅
//        subscribe.unsubscribe();
    }

}