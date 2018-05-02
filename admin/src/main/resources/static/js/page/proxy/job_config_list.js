$(function() {
    bindEvent();

    initData();
});

function bindEvent() {
//	$('#jobName').on('blur', uniqueCheck);

    layui.form.on('select(jobClassPath)', function(data){
        $('#jobName').val($("#jobClassPath").find("option:selected").text());
    });

    $("#addBtn").click(function() {
		showEditModel(null);
    });

    layui.table.on('tool(resultTable)', function(rowObj){
        var rowData = rowObj.data;
        var layEvent = rowObj.event;

        if(layEvent === 'addJobEvent') {
            addJob(rowData);
        } else if (layEvent === 'updateEvent') {
            showEditModel(rowData);
        } else if (layEvent === 'deleteEvent') {
            deleteResource(rowData);
        }
    });
}

function initData() {
    var apiPath = '/jobconfig/list';

    layui.table.render({
        elem: '#resultTable',
        url: apiPath,
        page: false,
        cols: [[
            {type:'numbers'},
            {field:'jobClassPath', title: 'Job类', sort: true, align:'center', width:200},
            {field:'cronExpression', title: 'Cron表达式', sort: true, align:'center', width:120},
            {field:'resourceName', title: '资源名称', sort: true, align:'center', width:160},
            {field:'jobName', title: 'Job名称', sort: true, align:'center', width:160},
//            {field:'jobGroupName', title: 'Job组名称', sort: true, align:'center', width:100},
            {field:'updateTime', title: '更新时间', sort: true, align:'center', width:160, templet: '<div>{{ layui.laytpl.toDateString(d.updateTime) }}</div>'},
            {toolbar: '#barTpl', title: '操作', align:'center'}
        ]]
    });
}

function showEditModel(rowData) {
	layer.open({
		type: 1,
		title: [rowData == null ? "新增Job配置":"更新Job配置", 'background:#32AA9F;color:#FFF'],
		area:  ['650px', '450px'],
		offset: 'auto',
		content: $("#editModel").html()
	});

    if(rowData != null){
        $('#primaryId').val(rowData.primaryId);
        $('#cronExpression').val(rowData.cronExpression);
        $('#jobName').val(rowData.jobName);
//        $('#jobGroupName').val(rowData.jobGroupName);
//        $('#triggerName').val(rowData.triggerName);
//        $('#triggerGroupName').val(rowData.triggerGroupName);
        $('#remark').val(rowData.remark);
    }

    initResourceSelect(rowData);
    initJobSelect(rowData);

    layui.form.on('submit(saveBtn)', function(data) {
        if(rowData == null) data.field.primaryId = null;

        axios.post('/jobconfig/save', data.field)
                .then(function(response){
                    var result = response.data;
                    if(result.code == 200) {
                      layer.msg("保存Job配置成功", {icon: 1});
                      layer.closeAll('page');
                      layui.table.reload('resultTable', {});
                    }else{
                      layer.msg("保存Job配置失败："+result.msg, {icon: 2});
                    }
                })
                .catch(function(err){
                    layer.msg("保存Job配置异常", {icon: 2});
                });

        return false;
    });

	$("#closeBtn").click(function(){
		layer.closeAll('page');
		return false;
	});
}

function deleteResource(rowData) {
    layer.confirm('确定要删除这个Job配置吗？', {icon: 3, title:'重要提醒'}, function(index){
            axios.delete('/jobconfig/delete/'+rowData.primaryId)
                .then(function(response){
                    var result = response.data;
                    if(result.code == 200) {
                      layer.msg("删除Job配置成功", {icon: 1});
                      layui.table.reload('resultTable', {});
                    }else{
                      layer.msg("删除Job配置失败", {icon: 2});
                    }
                })
                .catch(function(err){
                    layer.msg(tag+"删除Job配置异常", {icon: 2});
                });
    });
}

function initResourceSelect(rowData) {
     axios.get("/resource/list")
            .then(function(response){
                var result = response.data;
                if(result.code == 0) {
                  layui.laytpl(resourceSelect.innerHTML).render(result.data, function(resultHtml){
                      $("#resourceName").html(resultHtml);
                      if(rowData != null) $("#resourceName").val(rowData.resourceName);
                      layui.form.render('select');
                  });
                }else{
                  layer.msg("资源类加载失败",{icon: 2});
                }
            })
            .catch(function(err){
                layer.msg("资源类加载异常",{icon: 2});
            });
}

function initJobSelect(rowData) {
     axios.get("/resource/options/jobClass")
            .then(function(response){
                var result = response.data;
                if(result.code == 0) {
                  layui.laytpl(jobSelect.innerHTML).render(result.data, function(resultHtml){
                      $("#jobClassPath").html(resultHtml);
                      if(rowData != null) $("#jobClassPath").val(rowData.jobClassPath);
                      layui.form.render('select');
                  });
                }else{
                  layer.msg("Job类加载失败",{icon: 2});
                }
            })
            .catch(function(err){
                layer.msg("Job类加载异常",{icon: 2});
            });
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

function addJob(rowData) {
    axios.post("/job/add", rowData)
        .then(function(response){
            var result = response.data;
            if(result.code == 200) {
              layer.msg("添加Job成功",{icon: 1});
            }else{
              layer.msg("添加Job失败",{icon: 2});
            }
        })
        .catch(function(err){
            layer.msg("添加Job异常",{icon: 2});
        });
}