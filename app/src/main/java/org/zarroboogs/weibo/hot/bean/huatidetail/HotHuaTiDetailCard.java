package org.zarroboogs.weibo.hot.bean.huatidetail;

import org.json.*;

import java.util.ArrayList;

public class HotHuaTiDetailCard {

    private PageInfo pageInfo;
    private ArrayList<Cards> cards;


    public HotHuaTiDetailCard() {

    }

    public PageInfo getPageInfo() {
        return this.pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public ArrayList<Cards> getCards() {
        return this.cards;
    }

    public void setCards(ArrayList<Cards> cards) {
        this.cards = cards;
    }


}
