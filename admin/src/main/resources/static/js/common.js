//自定义：时间戳的处理
layui.laytpl.toDateString = function(d, format){
  var date = new Date(d || new Date())
  ,ymd = [
    this.digit(date.getFullYear(), 4)
    ,this.digit(date.getMonth() + 1)
    ,this.digit(date.getDate())
  ]
  ,hms = [
    this.digit(date.getHours())
    ,this.digit(date.getMinutes())
    ,this.digit(date.getSeconds())
  ];

  format = format || 'yyyy-MM-dd HH:mm:ss';

  return format.replace(/yyyy/g, ymd[0])
  .replace(/MM/g, ymd[1])
  .replace(/dd/g, ymd[2])
  .replace(/HH/g, hms[0])
  .replace(/mm/g, hms[1])
  .replace(/ss/g, hms[2]);
};

//自定义：数字前置补零
layui.laytpl.digit = function(num, length, end){
  var str = '';
  num = String(num);
  length = length || 2;
  for(var i = num.length; i < length; i++){
    str += '0';
  }
  return num < Math.pow(10, length) ? str + (num|0) : num;
};

//弹出iframe的页面，默认最大化
function openMaxWin(type, title, url) {
    var index = layer.open({
            type: type,
            max: true,
            fix: false,
            title: [title, 'background:#189F92;color:#FFF'],
            content: url
    });

    layer.full(index);
}

function getLoginUser() {
    var loginUser = JSON.parse(sessionStorage.getItem("loginUser"));
    if(!loginUser) location.href='/login.html';
    return loginUser;
}

function getCurrentDateRange() {
    return dateFormatter(new Date()) + " 到 " + dateFormatter(new Date());
}

function isValidInputValue(val) {
    if (typeof(val) == "undefined") {
        return false
    }

    if(val === "" || val === null) {
        return false;
    }

    return true;
}

function isValidQueryValue(val) {
    if (!val) {
        return false
    }

    if (typeof(val) == "undefined") {
        return false
    }

    if (!val && typeof(val)!="undefined" && val!=0){
        return false
    }

    if(val === "") {
        return false;
    }

    return true;
}

function dateFormatter(date){
	var y = date.getFullYear();
	var m = date.getMonth()+1;
	var d = date.getDate();
	return y+'-'+(m<10?('0'+m):m)+'-'+(d<10?('0'+d):d);
}
