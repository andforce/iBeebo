package org.zarroboogs.weibo.hot.bean.hotweibo;

import org.json.*;

import java.util.ArrayList;

public class HotWeibo {

    private CardlistInfo cardlistInfo;
    private ArrayList<Cards> cards;


    public HotWeibo() {

    }

    public HotWeibo(JSONObject json) {

        this.cardlistInfo = new CardlistInfo(json.optJSONObject("cardlistInfo"));

        this.cards = new ArrayList<Cards>();
        JSONArray arrayCards = json.optJSONArray("cards");
        if (null != arrayCards) {
            int cardsLength = arrayCards.length();
            for (int i = 0; i < cardsLength; i++) {
                JSONObject item = arrayCards.optJSONObject(i);
                if (null != item) {
                    this.cards.add(new Cards(item));
                }
            }
        } else {
            JSONObject item = json.optJSONObject("cards");
            if (null != item) {
                this.cards.add(new Cards(item));
            }
        }


    }

    public CardlistInfo getCardlistInfo() {
        return this.cardlistInfo;
    }

    public void setCardlistInfo(CardlistInfo cardlistInfo) {
        this.cardlistInfo = cardlistInfo;
    }

    public ArrayList<Cards> getCards() {
        return this.cards;
    }

    public void setCards(ArrayList<Cards> cards) {
        this.cards = cards;
    }


}
