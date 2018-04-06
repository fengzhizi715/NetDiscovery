var refreshNav = true;

$(function() {
	initLeftNav();

	bindEvent();
	
    registerRoute();

    var engineUrl = localStorage.getItem("engineUrl");
    if(null == engineUrl) {
        systemSet();
    }
});

function initLeftNav(){
	var indexNavStr = sessionStorage.getItem("index-nav");
	var indexNav = JSON.parse(indexNavStr);
	if(indexNav == null){
        var apiPath = "index/menu";
        axios.get(apiPath)
             .then(function(response){
                var result = response.data;
                if(result.code == 200) {
                    sessionStorage.setItem("index-nav", JSON.stringify(result.menus));
                    initLeftNav();
                } else {
                    layer.msg("左侧菜单加载失败，请刷新页面", {icon: 2});
                }
             })
            .catch(function(err){
                layer.msg("左侧菜单加载异常，请刷新页面", {icon: 2});
            });
	}else{
		layui.laytpl(sideNavTpl.innerHTML).render(indexNav, function(resultHtml){
			$("#index-nav").html(resultHtml);
			layui.element.render('nav', 'index-nav');
		});
	}
}

function bindEvent() {
	//点击导航切换页面时不刷新导航,其他方式切换页面要刷新导航
	layui.element.on('nav(index-nav)', function(elem){
		refreshNav = false;
		if(document.body.clientWidth<=750){
			switchNav(true);
		}
	});
}

function registerRoute() {
	Q.reg('home',function(){load('home');});
    Q.reg('spider',function(path){load("spider/"+path);});
    Q.reg('proxy',function(path){load("proxy/"+path);});

    Q.init({index: 'home'});
}

function load(path) {
	if(refreshNav){
		activeNav(path);
	}
	refreshNav = true;
	$("#main-content").load(path +".html",function(){
		layui.element.render('breadcrumb');
		layui.form.render('select');
	});
}

function systemSet(){
	layer.open({
		type: 1,
        title: ["系统设置", 'background:#32AA9F;color:#FFF'],
        area: ['400px', '200px'],
        offset: 'auto',
		content: $("#systemModel").html()
	});

	var engineUrl = localStorage.getItem("engineUrl");
	if(null != engineUrl) {
	    $('#engineUrl').val(engineUrl);
	}

    //弹框里的事件绑定，在弹框代码里单独写
    //保存系统设置
	layui.form.on('submit(saveBtn)', function(data){
	    //保存到localStorage
	    localStorage.setItem("engineUrl", $('#engineUrl').val());
        layer.alert('保存成功', {icon: 1, skin: 'layer-ext-moon'});
		layer.closeAll('page');
		return false;
	});

    //关闭系统设置弹框
	$("#closeBtn").click(function(){
		layer.closeAll('page');
	});
}