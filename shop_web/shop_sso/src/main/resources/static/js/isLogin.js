$(function () {
    $.ajax({
        url: "http://localhost:8084/sso/isLogin",
        data: "",
        success:function (data) {
            //alert(JSON.stringify(data));//将json转成字符串
            //debugger;
            if (data != null) {
                var html = "<p>您好"+data.nickname+"，欢迎来到<b><a href='#'>ShopCZ商城</a></b><a href='http://localhost:8084/sso/invalid'>注销</a></p>";
                $("#loginInfo").html(html);
            }else{
                /*debugger;
                //获得当前的请求地址
                //这样搞得话浏览器上面的地址还是中文的,要编码后跳转才会改变url的地址
                var returnUrl = location.href;
                //给url做一个编码
                returnUrl = encodeURIComponent(returnUrl);
                console.log(returnUrl);
                //浏览器会自动解码,到了后端有事中文了,除非在后台加码(这个没试过不知道效果是什么样的)
                var html = "<p>您好，欢迎来到<b><a href='#'>ShopCZ商城</a></b>[<a href='http://localhost:8084/sso/toLogin?returnUrl="+returnUrl+"'>登录</a>][<a href='http://localhost:8084/sso/toRegister'>注册</a>]</p>";
                */
                //说明未登录
                html = "[<a onclick='login();'>登录</a>][<a href=\"http://localhost:8084/sso/toregister\">注册</a>]";
                $("#loginInfo").html(html);
            }
        },
        dataType: "jsonp",
        jsonpCallback: "m",
        error: function () {
            alert("异常~~~");
        }
    });

});

function login(){
    debugger;
    //获得当前的请求地址
    var returnUrl = location.href;
    //给url做一个编码
    returnUrl = encodeURIComponent(returnUrl);

    //跳转到登录页
    location.href = "http://localhost:8084/sso/toLogin?returnUrl=" + returnUrl;
}