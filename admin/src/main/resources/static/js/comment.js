$(function() {
	//切换导航栏按钮点击事件
	$("#switchNav").click(function(){
		var sideNavExpand = !$('body').hasClass('nav-mini');
		switchNav(!sideNavExpand);
	});
	//手机遮罩层点击事件
	$('.site-mobile-shade').click(function(){
		switchNav(true);
	});
});

//设置选中导航栏
function activeNav(path_name){
	$(".layui-side ul.layui-nav li.layui-nav-item .layui-nav-child dd").removeClass("layui-this");
	$(".layui-side ul.layui-nav li.layui-nav-item").removeClass("layui-nav-itemed");
	var $a = $(".layui-side ul.layui-nav>li.layui-nav-item>.layui-nav-child>dd>a[href='#!"+path_name+"']");
	$a.parent("dd").addClass("layui-this");
	$a.parent("dd").parent("dl.layui-nav-child").parent("li.layui-nav-item").addClass("layui-nav-itemed");
	layui.element.render('nav', 'index-nav');
}

//折叠显示导航栏
function switchNav(expand){
	var sideNavExpand = !$('body').hasClass('nav-mini');
	if(expand==sideNavExpand){
		return;
	}
	if (!expand) {
        //$('.layui-side .layui-nav .layui-nav-item.layui-nav-itemed').removeClass('layui-nav-itemed');
        $('body').addClass('nav-mini');
    }else{
        $('body').removeClass('nav-mini');
    }
	$('.nav-mini .layui-side .layui-nav .layui-nav-item').hover(function(){
		var tipText = $(this).find('span').text();
		if($('body').hasClass('nav-mini')&&document.body.clientWidth>750){
			layer.tips(tipText, this);
		}
	},function(){
		layer.closeAll('tips');
	});
}

//导航栏展开
function openNavItem(){
	if($('body').hasClass('nav-mini')&&document.body.clientWidth>750){
		switchNav(true);
	}
}