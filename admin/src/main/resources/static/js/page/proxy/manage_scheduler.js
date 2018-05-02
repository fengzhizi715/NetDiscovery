$(function() {
    bindEvent();

    initData();
});

function bindEvent() {
      layui.form.on('switch(jobName)', function(obj){
          var jobName = this.value;
          if(obj.elem.checked) {
            changeJobState('工作', jobName, 'resume');
          } else {
            changeJobState('暂停', jobName, 'pause');
          }
      });

    layui.form.on('select(jobType)', function(data){
        loadData(data.value);
    });

    $("#queryBtn").click(function() {
        loadData($('#jobType').val());
    });

    layui.table.on('tool(resultTable)', function(rowObj){
        var rowData = rowObj.data;
        var layEvent = rowObj.event;

        if(layEvent === 'runJobEvent') {
            runJob(rowData);
        } else if (layEvent === 'removeEvent') {
            removeJob(rowData);
        }
    });
}

function initData() {
    loadData('all');
}

function loadData(jobType) {
    var apiPath = '/job/list/'+jobType;

    if(jobType == 'run') {
        layui.table.render({
            elem: '#resultTable',
            url: apiPath,
            page: false,
            cols: [[
                {type:'numbers'},
                {field:'jobName', title: 'Job名称', sort: true, align:'center', width:120},
                {field:'cronExpression', title: 'Cron表达式', sort: true, align:'center', width:160},
                {field:'state', title: '当前状态', sort: true, align:'center', width:120},
                {field:'jobGroupName', title: 'Job组', sort: true, align:'center', width:120},
                {field:'triggerName', title: 'Trigger名称', sort: true, align:'center', width:120},
                {field:'triggerGroupName', title: 'Trigger组', sort: true, align:'center', width:120}
            ]]
        });
    } else {
        layui.table.render({
            elem: '#resultTable',
            url: apiPath,
            page: false,
            cols: [[
                {type:'numbers'},
                {field:'jobName', title: 'Job名称', sort: true, align:'center', width:120},
                {field:'cronExpression', title: 'Cron表达式', sort: true, align:'center', width:160},
                {field:'state', title: '当前状态', sort: true, align:'center', width:120},
                {field:'jobGroupName', title: 'Job组', sort: true, align:'center', width:120},
                {field:'triggerName', title: 'Trigger名称', sort: true, align:'center', width:120},
                {field:'triggerGroupName', title: 'Trigger组', sort: true, align:'center', width:120},
                {field:'state', title: '状态控制', sort: true, align:'center', width:120, templet:'#changeStatusTpl'},
                {toolbar: '#barTpl', title: '操作', align:'center'}
            ]]
        });
    }
}

function runJob(rowData) {
    axios.post("/job/run", rowData)
        .then(function(response){
            var result = response.data;
            if(result.code == 200) {
              layer.msg("启动Job成功",{icon: 1});
            }else{
              layer.msg("启动Job失败",{icon: 2});
            }
        })
        .catch(function(err){
            layer.msg("启动Job异常",{icon: 2});
        });
}

function changeJobState(tag, jobName, action) {
    axios.get("/job/"+jobName+"/"+action)
        .then(function(response){
            var result = response.data;
            if(result.code == 200) {
              layer.msg(tag+"Job成功",{icon: 1});
              layui.table.reload('resultTable', {});
            }else{
              layer.msg(tag+"Job失败",{icon: 2});
            }
        })
        .catch(function(err){
            layer.msg(tag+"Job异常",{icon: 2});
        });
}

function pauseJob(rowData) {
    axios.post("/job/pause", rowData)
        .then(function(response){
            var result = response.data;
            if(result.code == 200) {
              layer.msg("暂停Job成功",{icon: 1});
              layui.table.reload('resultTable', {});
            }else{
              layer.msg("暂停Job失败",{icon: 2});
            }
        })
        .catch(function(err){
            layer.msg("暂停Job异常",{icon: 2});
        });
}

function resumeJob(rowData) {
    axios.post("/job/resume", rowData)
        .then(function(response){
            var result = response.data;
            if(result.code == 200) {
              layer.msg("继续Job成功",{icon: 1});
              layui.table.reload('resultTable', {});
            }else{
              layer.msg("继续Job失败",{icon: 2});
            }
        })
        .catch(function(err){
            layer.msg("继续Job异常",{icon: 2});
        });
}

function removeJob(rowData) {
    axios.post("/job/remove", rowData)
        .then(function(response){
            var result = response.data;
            if(result.code == 200) {
              layer.msg("移除Job成功",{icon: 1});
              layui.table.reload('resultTable', {});
            }else{
              layer.msg("移除Job失败",{icon: 2});
            }
        })
        .catch(function(err){
            layer.msg("移除Job异常",{icon: 2});
        });
}