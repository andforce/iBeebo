
package lib.org.zarroboogs.weibo.login.javabean;

public class HasloginBean {
    private boolean result = false;
    private UserInfo userinfo;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public UserInfo getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(UserInfo userinfo) {
        this.userinfo = userinfo;
    }

    public class UserInfo {
        private String uniqueid;
        private String userid;
        private String displayname;
        private String userdomain;

        public String getUniqueid() {
            return uniqueid;
        }

        public void setUniqueid(String uniqueid) {
            this.uniqueid = uniqueid;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getDisplayname() {
            return displayname;
        }

        public void setDisplayname(String displayname) {
            this.displayname = displayname;
        }

        public String getUserdomain() {
            return userdomain;
        }

        public void setUserdomain(String userdomain) {
            this.userdomain = userdomain;
        }

    }
}
