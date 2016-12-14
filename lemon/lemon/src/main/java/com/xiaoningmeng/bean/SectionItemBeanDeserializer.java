package com.xiaoningmeng.bean;

import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaoyong on 16/12/14.
 */

public class SectionItemBeanDeserializer implements JsonDeserializer<Index.SectionItemBean> {

    public static final String TYPE_ALBUM = "album";
    public static final String TYPE_AUTHOR = "author";

    @Override
    public Index.SectionItemBean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        final JsonObject jsonObject = json.getAsJsonObject();
        final String type = jsonObject.get("type").getAsString();
        final String title = jsonObject.get("title").getAsString();
        final int total = jsonObject.get("total").getAsInt();
        final String linkurl = jsonObject.get("linkurl").getAsString();
        final JsonArray jsonItemsArray = jsonObject.get("items").getAsJsonArray();

        if (!TextUtils.isEmpty(type) && type.equals(TYPE_ALBUM)) {

            List<AlbumInfo> albumInfoList=new ArrayList<AlbumInfo>();

            for (int i = 0; i < jsonItemsArray.size(); i++) {
                final JsonObject jsonAlbumInfoObj = jsonItemsArray.get(i).getAsJsonObject();
                String albumId = jsonAlbumInfoObj.get("id").getAsString();
                String albumTitle = jsonAlbumInfoObj.get("title").getAsString();
                String albumStarLevel = jsonAlbumInfoObj.get("star_level").getAsString();
                String albumIntro = jsonAlbumInfoObj.get("intro").getAsString();
                String albumCover = jsonAlbumInfoObj.get("cover").getAsString();
                String albumListenNum = jsonAlbumInfoObj.get("listennum").getAsString();
                String albumRecommendDesc = jsonAlbumInfoObj.get("recommenddesc").getAsString();
                albumInfoList.add(new AlbumInfo(albumId, albumTitle, albumIntro, albumStarLevel, albumCover, albumListenNum, albumRecommendDesc));
            }
            final Index.SectionItemBean sectionItemBean = new Index.SectionItemBean<AlbumInfo>();
            sectionItemBean.setType(type);
            sectionItemBean.setTitle(title);
            sectionItemBean.setTotal(total);
            sectionItemBean.setLinkurl(linkurl);
            sectionItemBean.setItems(albumInfoList);
            return sectionItemBean;

        } else if (!TextUtils.isEmpty(type) && type.equals(TYPE_AUTHOR)) {

            List<Index.AuthorItemBean> authorItemList=new ArrayList<Index.AuthorItemBean>();
            for (int i = 0; i < jsonItemsArray.size(); i++) {
                final JsonObject jsonAlbumInfoObj = jsonItemsArray.get(i).getAsJsonObject();
                String authorUid = jsonAlbumInfoObj.get("uid").getAsString();
                String authorNickname = jsonAlbumInfoObj.get("nickname").getAsString();
                String authorAvatar = jsonAlbumInfoObj.get("avatar").getAsString();
                String authorWikiUrl = jsonAlbumInfoObj.get("wiki_url").getAsString();

                authorItemList.add(new Index.AuthorItemBean(authorUid, authorNickname, authorAvatar, authorWikiUrl));
            }

            final Index.SectionItemBean sectionItemBean = new Index.SectionItemBean<Index.AuthorItemBean>();
            sectionItemBean.setType(type);
            sectionItemBean.setTitle(title);
            sectionItemBean.setTotal(total);
            sectionItemBean.setLinkurl(linkurl);
            sectionItemBean.setItems(authorItemList);
            return sectionItemBean;
        }

        return null;
    }
}
