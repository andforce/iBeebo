package org.zarroboogs.weibo.hot.bean.model;

import org.json.*;
import java.util.ArrayList;

public class HotModel {
	
    private CardlistInfo cardlistInfo;
    private ArrayList<HotModelCards> cards;
    
    
	public HotModel () {
		
	}	
        
    public HotModel (JSONObject json) {
    
        this.cardlistInfo = new CardlistInfo(json.optJSONObject("cardlistInfo"));

        this.cards = new ArrayList<HotModelCards>();
        JSONArray arrayCards = json.optJSONArray("cards");
        if (null != arrayCards) {
            int cardsLength = arrayCards.length();
            for (int i = 0; i < cardsLength; i++) {
                JSONObject item = arrayCards.optJSONObject(i);
                if (null != item) {
                    this.cards.add(new HotModelCards(item));
                }
            }
        }
        else {
            JSONObject item = json.optJSONObject("cards");
            if (null != item) {
                this.cards.add(new HotModelCards(item));
            }
        }


    }
    
    public CardlistInfo getCardlistInfo() {
        return this.cardlistInfo;
    }

    public void setCardlistInfo(CardlistInfo cardlistInfo) {
        this.cardlistInfo = cardlistInfo;
    }

    public ArrayList<HotModelCards> getCards() {
        return this.cards;
    }

    public void setCards(ArrayList<HotModelCards> cards) {
        this.cards = cards;
    }


    
}
