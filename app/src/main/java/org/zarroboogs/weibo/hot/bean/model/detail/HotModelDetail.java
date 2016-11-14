package org.zarroboogs.weibo.hot.bean.model.detail;

import org.json.*;

import java.util.ArrayList;

public class HotModelDetail {

    private CardlistInfo cardlistInfo;
    private ArrayList<HotModelDetailCard> cards;


    public HotModelDetail() {

    }

    public HotModelDetail(JSONObject json) {

        this.cardlistInfo = new CardlistInfo(json.optJSONObject("cardlistInfo"));

        this.cards = new ArrayList<HotModelDetailCard>();
        JSONArray arrayCards = json.optJSONArray("cards");
        if (null != arrayCards) {
            int cardsLength = arrayCards.length();
            for (int i = 0; i < cardsLength; i++) {
                JSONObject item = arrayCards.optJSONObject(i);
                if (null != item) {
                    this.cards.add(new HotModelDetailCard(item));
                }
            }
        } else {
            JSONObject item = json.optJSONObject("cards");
            if (null != item) {
                this.cards.add(new HotModelDetailCard(item));
            }
        }


    }

    public CardlistInfo getCardlistInfo() {
        return this.cardlistInfo;
    }

    public void setCardlistInfo(CardlistInfo cardlistInfo) {
        this.cardlistInfo = cardlistInfo;
    }

    public ArrayList<HotModelDetailCard> getCards() {
        return this.cards;
    }

    public void setCards(ArrayList<HotModelDetailCard> cards) {
        this.cards = cards;
    }


}
