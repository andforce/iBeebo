package org.zarroboogs.weibo.hot.bean.huati;

import java.util.ArrayList;

public class HotHuaTi {
	
    private CardlistInfo cardlistInfo;
    private ArrayList<HotHuaTiCard> cards;
    
    
	public HotHuaTi () {
		
	}	
        
    
    public CardlistInfo getCardlistInfo() {
        return this.cardlistInfo;
    }

    public void setCardlistInfo(CardlistInfo cardlistInfo) {
        this.cardlistInfo = cardlistInfo;
    }

    public ArrayList<HotHuaTiCard> getCards() {
        return this.cards;
    }

    public void setCards(ArrayList<HotHuaTiCard> cards) {
        this.cards = cards;
    }


    
}
