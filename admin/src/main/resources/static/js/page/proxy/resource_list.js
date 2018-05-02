var _updateTag;
var _resourceName;  //用于存放更新时的原始值
$(function() {
    bindEvent();

    initData();
});

function bindEvent() {
//	$('#resourceName').on('blur', uniqueCheck);

    $("#addBtn").click(function() {
		showEditModel(null);
    });

    layui.table.on('tool(resultTable)', function(rowObj){
        var rowData = rowObj.data;
        var layEvent = rowObj.event;

        if(layEvent === 'updateEvent') {
            showEditModel(rowData);
        }else if(layEvent === 'deleteEvent') {
            deleteResource(rowData);
        }
    });

    //自定义表单校验
    layui.form.verify({
      positive: function(value, item){ //value：表单的值、item：表单的DOM对象
        if(value <= 0) {
          return '必须是正整数';
        }
      }
    });
}

function initData() {
    var apiPath = '/resource/list';

    layui.table.render({
        elem: '#resultTable',
        url: apiPath,
        page: false,
        cols: [[
            {type:'numbers'},
            {field:'resourceName', title: '资源名称', sort: true, align:'center', width:150},
            {field:'parserClassPath', title: '解析类', sort: true, align:'center', width:180},
            {field:'urlPrefix', title: '前缀', sort: true, align:'center', width:180},
            {field:'urlSuffix', title: '后缀', sort: true, align:'center', width:80},
            {field:'startPage', title: '开始页', sort: true, align:'center', width:80},
            {field:'endPage', title: '结束页', sort: true, align:'center', width:80},
            {field:'updateTime', title: '更新时间', sort: true, align:'center', width:160, templet: '<div>{{ layui.laytpl.toDateString(d.updateTime) }}</div>'},
            {toolbar: '#barTpl', title: '操作', align:'center', width:120}
        ]]
    });
}

function showEditModel(rowData) {
	layer.open({
		type: 1,
		title: [rowData == null ? "新增资源":"更新资源", 'background:#32AA9F;color:#FFF'],
		area:  ['650px', '450px'],
		offset: 'auto',
		content: $("#editModel").html()
	});

    if(rowData != null){
        $('#primaryId').val(rowData.primaryId);
        $('#resourceName').val(rowData.resourceName);
        $('#parserClassPath').val(rowData.parserClassPath);
        $('#urlPrefix').val(rowData.urlPrefix);
        $('#urlSuffix').val(rowData.urlSuffix);
        $('#startPage').val(rowData.startPage);
        $('#endPage').val(rowData.endPage);
        _resourceName = rowData.resourceName;
        _updateTag = true;
    }else{
        _updateTag = false;
    }

    initParserSelect(rowData);

    layui.form.on('submit(saveBtn)', function(data) {
        if(parseInt($('#startPage').val()) > parseInt($('#endPage').val())) {
            layer.msg("结束页要大于等于开始页", {icon: 2});
            return;
        }

        if(rowData == null) data.field.primaryId = null;

        axios.post('/resource/save', data.field)
                .then(function(response){
                    var result = response.data;
                    if(result.code == 200) {
                      layer.msg("保存资源成功", {icon: 1});
                      layer.closeAll('page');
                      layui.table.reload('resultTable', {});
                    }else{
                      layer.msg("保存资源失败", {icon: 2});
                    }
                })
                .catch(function(err){
                    layer.msg("保存资源异常", {icon: 2});
                });

        return false;
    });

	$("#closeBtn").click(function(){
		layer.closeAll('page');
		return false;
	});

	$('#resourceName').on('blur', uniqueCheck);
}

function deleteResource(rowData) {
    layer.confirm('确定要删除这个资源吗？', {icon: 3, title:'重要提醒'}, function(index){
            axios.delete('/resource/delete/'+rowData.primaryId)
                .then(function(response){
                    var result = response.data;
                    if(result.code == 200) {
                      layer.msg("删除资源成功", {icon: 1});
                      layui.table.reload('resultTable', {});
                    }else{
                      layer.msg("删除资源失败", {icon: 2});
                    }
                })
                .catch(function(err){
                    layer.msg(tag+"删除资源异常", {icon: 2});
                });
    });
}

function uniqueCheck() {
    var newResName = this.value;
    if(isValidInputValue(newResName)) {
        if(_updateTag == false || (_updateTag == true && newResName != _resourceName)) {
            axios.get("/resource/"+newResName)
                 .then(function(response){
                    var result = response.data;
                    if(result.code == 200) {
                        layer.msg(result.msg,{icon: 1});
                        $('#resourceName').val("");
                    }
                 })
                .catch(function(err){
                    layer.msg("校验异常",{icon: 2});
                });
        }
    }
}

function initParserSelect(rowData) {
    axios.get("/resource/options/parserClass")
        .then(function(response){
            var result = response.data;
            if(result.code == 0) {
              layui.laytpl(parserSelect.innerHTML).render(result.data, function(resultHtml){
                  $("#parserClassPath").html(resultHtml);
                  if(rowData != null) $("#parserClassPath").val(rowData.parserClassPath);
                  layui.form.render('select');
              });
            }else{
              layer.msg("解析类加载失败",{icon: 2});
            }
        })
        .catch(function(err){
            layer.msg("解析类加载异常",{icon: 2});
        });
}