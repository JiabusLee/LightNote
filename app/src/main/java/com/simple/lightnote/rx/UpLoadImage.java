package com.simple.lightnote.rx;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * RxJava Demo类。
 * Lambda表达式的应用
 */
public class UpLoadImage {
    private static final String TAG = "UpLoadImage";

    public static void uploadImage(ArrayList<ImageInfo> imageInfos, final String sMethod) {

        Observable
                .create(new ObservableOnSubscribe<ImageInfo>() {
                    @Override
                    public void subscribe(ObservableEmitter<ImageInfo> e) throws Exception {
                        for (ImageInfo imageInfo : imageInfos) {
                            e.onNext(imageInfo);
                        }
                        e.onComplete();
                    }
                })

                .map(new Function<ImageInfo, ImageInfo>() {

                         @Override
                         public ImageInfo apply(ImageInfo imageInfo) throws Exception {
                             System.out.println(imageInfo);
                             uploadImg(imageInfo);
                             return imageInfo;
                         }


                     }
                )
                .observeOn(Schedulers.io())
                .map(new Function<ImageInfo, String>() {
                    @Override
                    public String apply(ImageInfo imageInfo) throws Exception {

                        return submitImgInfo(imageInfo);
                    }
                })
                .take(5)
                .doOnNext(string -> System.out.println(string))
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
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
     *
     * @param imageInfos
     * @param sMethod
     */
    public static void updateImageL(ArrayList<ImageInfo> imageInfos, final String sMethod) {

        Observable
                .create((ObservableOnSubscribe<ImageInfo>) e -> {
                    for (ImageInfo imageInfo : imageInfos) {
                        e.onNext(imageInfo);
                    }
                    e.onComplete();
                })

                .map(imageInfo -> {
                            System.out.println(imageInfo);
                            uploadImg(imageInfo);
                            return imageInfo;
                        }
                )
                .observeOn(Schedulers.io())
                .map(imageInfo -> submitImgInfo(imageInfo))
                .take(5)
                .doOnNext(string -> System.out.println(string))
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
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

}