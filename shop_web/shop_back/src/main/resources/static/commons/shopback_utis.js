function showMessage(data){
    if (data.status==200) {
        layer.msg(data.message, {icon: 1, anim: 6, time: 1000}, function () {
            var index = parent.layer.getFrameIndex(window.name);// parent是啥玩意
            parent.layer.close(index);
            location.reload();  //之所以在在这里刷新 是删除哪里弹框结束后没有刷新数
	    //window.location.reload();
        });
    }else {
        layer.msg(data.message, {icon: 2, anim: 6, time: 1000});
    }
}
