package com.simple.lightnote.rx;

import java.util.ArrayList;

import rx.Observable;

/**
 * Created by homelink on 2016/7/18.
 */
public class TestUploadImage {
    String [] str={"url1", "url2", "url3"};
//    @org.junit.TestUploadImage
    public void test() {

        Observable.from(str).subscribe(url-> System.out.println(url));

    }
    @org.junit.Test
    public void tests() {
        String[] strs = {"/DCIM/Camera/IMG_20160713_045834697.jpg",
                "/DCIM/Camera/IMG_20160713_050038313.jpg",
                "/DCIM/Camera/IMG_20160715_170611633.jpg",
                "/DCIM/Camera/IMG_20160715_170621841.jpg"};
        ArrayList<ImageInfo> imageInfos = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            ImageInfo imageInfo = new ImageInfo();
            imageInfo.bjSpkBillId = "ff8080814d992d5f014d99689dfe00ba";
            imageInfo.imageFilePath = strs[i];
            imageInfos.add(imageInfo);
        }


//        UpLoadImage.uploadImage(imageInfos,null);
        UpLoadImage.updateImageL(imageInfos,null);
    }

    public void test2(){

    }
}
