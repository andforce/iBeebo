package org.zarroboogs.weibo.hot.bean.hotweibo;

import org.json.*;


public class TopicStruct {
	
    private String topicTitle;
    private String topicUrl;
    
    
	public TopicStruct () {
		
	}	
        
    public TopicStruct (JSONObject json) {
    
        this.topicTitle = json.optString("topic_title");
        this.topicUrl = json.optString("topic_url");

    }
    
    public String getTopicTitle() {
        return this.topicTitle;
    }

    public void setTopicTitle(String topicTitle) {
        this.topicTitle = topicTitle;
    }

    public String getTopicUrl() {
        return this.topicUrl;
    }

    public void setTopicUrl(String topicUrl) {
        this.topicUrl = topicUrl;
    }


    
}
