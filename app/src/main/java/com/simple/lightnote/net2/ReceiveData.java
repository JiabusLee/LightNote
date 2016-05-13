package com.simple.lightnote.net2;


import com.simple.lightnote.model.ArticleInfo;
import com.simple.lightnote.model.FuLi;

import java.util.List;

/**
 * Created by yangpeiyong on 16/1/12.
 */
public class ReceiveData {

    public static class ArticleListResponse {
        public boolean error;
        public List<ArticleInfo> results;
    }


    public static class  FuliResponse{
        public boolean error = false;
        public List<FuLi> results ;
    }

}
