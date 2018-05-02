var engineUrl;

$(function() {
    bindClickEvent();

    initLoadData();
});

function bindClickEvent() {
      layui.form.on('switch(spiderStatus)', function(obj){
          var spiderName = this.value;
          if(obj.elem.checked) {
            changeSpiderStatus('继续', spiderName, 3);
          } else {
            changeSpiderStatus('暂停', spiderName, 2);
          }
      });

    $("#queryBtn").click(function() {
        if(!checkEngineUrl()) return;

        var apiPath = '/spider/list/';
        var spiderName = isValidQueryValue($('#spiderName').val()) ? $('#spiderName').val() : "all";
        var apiData = {engineUrl: engineUrl,
                       spiderName: spiderName};

        layui.table.reload('resultTable', {url: apiPath,
                                             method:'POST',
                                             where:apiData});
    });

    layui.table.on('tool(resultTable)', function(rowObj){
        var rowData = rowObj.data;
        var layEvent = rowObj.event;

        if(layEvent === 'stopEvent') {
            changeSpiderStatus('停止', rowData.spiderName, 4);
        }
    });
}

function initLoadData() {
    if(!checkEngineUrl()) return;

    var apiPath = '/spider/list';
    var spiderName = isValidQueryValue($('#spiderName').val()) ? $('#spiderName').val() : "all";
    var apiData = {engineUrl: engineUrl,
                   spiderName: spiderName};

    layui.table.render({
        elem: '#resultTable',
        url: apiPath,
        method: 'post',
        where: apiData,
        page: true,
        cols: [[
            {type:'numbers'},
            {field:'spiderName', title: '爬虫名称', sort: true, align:'center', width:160},
            {field:'spiderStatus', title: '爬虫状态', templet: '#statusTpl', sort: true, align:'center', width:160},
            {field:'remainCount', title: '队列中剩余任务', sort: true, align:'center', width:160},
            {field:'finishCount', title: '已完成任务数', sort: true, align:'center', width:160},
            {field:'spiderStatus', title: '状态控制', sort: true, align:'center', width:120, templet:'#changeStatusTpl'},
            {toolbar: '#barTpl', title: '操作', align:'center', width:160}
        ]]
    });
}

function changeSpiderStatus(tag, spiderName, toStatus) {
    axios.post('/spider/status', {spiderName:spiderName, toStatus: toStatus, engineUrl: engineUrl})
            .then(function(response){
                var result = response.data;
                if(result.code == 200) {
                  layer.msg(tag+" 操作成功", {icon: 1});
                  layui.table.reload('resultTable', {});
                }else{
                  layer.msg(tag+" 操作失败", {icon: 2});
                }
            })
            .catch(function(err){
                layer.msg(tag+" 操作异常", {icon: 2});
            });
}

function checkEngineUrl() {
    engineUrl = localStorage.getItem("engineUrl");
    if(null == engineUrl || "" == engineUrl) {
        layer.alert('请先设置【引擎地址】', {icon: 0, skin: 'layer-ext-moon'});
        systemSet();
        return false;
    }
    return true;
}