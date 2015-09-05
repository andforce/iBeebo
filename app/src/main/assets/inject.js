<script type="text/javascript">
    function fillAccount(uname, upassword) {
        loginName.value = uname;
        loginPassword.value = upassword;
    }
    
    function doAutoLogIn() {
    	window.JS_CALL_JAVA.jsCallJava(loginName.value+"#&=&#"+loginPassword.value);
        loginApp.doLogin();
    }
</script>