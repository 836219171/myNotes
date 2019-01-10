/*获取表格内容*/
function getTable(page, size, fields, mytype, myurl) {

    $.ajax({
        type: mytype,
        url: myurl,
        data: {"page": page, "size": size},
        success: function (result) {
            var content = "";
            var finalNum = result.totalPage;
            $.each(result.data, function (index, col) {
                    content = content + "<tr>";
                    //数组的jquery遍历
                    $(fields).each(function (i, field) {
                        //js拼接json对象的属性 用json["属性"]
                        content = content + "<td>" + col[field] + "</td>";
                    })
                    /*   这里添加你的自定义表格  例子如下*/
                    /*             content = content + "<td>" + "详情" + "</td>";
                     content = content + "<td>" + "取消" + "</td>";*/
                    content = content + "</tr>";
                    content = content + '<input type="hidden" id="hidCountNum" value=' + result.count + '>';
                    content = content + '<input type="hidden" id="hidCurPage" value=' + page + '>';
                    content = content + '<input type="hidden" id="hidSize" value=' + size + '>';
                    content = content + '<input type="hidden" id="hidPageCount" value=' + finalNum + '>';
                }
            )

            $("#dataBody").html(content);
            /*显示分页*/
            getPagination(page, finalNum);
            /*显示当前页码*/
            getCurrPage();
        },
        error: function () {

        }
    })

}

/*分页*/
function getPagination(page, finalNum1) {
    var pageContent = "";
    var countNum = 1;
    var startNum = 1;
    var finalNum = finalNum1;
    var indexPage = page;
    var size = $("#hidSize").val();

    if (page == startNum) {
        pageContent = pageContent + '<li class="disabled"><a href="javascript:;" >上一页</a></li>';
    }
    else {
        pageContent = pageContent + '<li><a href="javascript:;"  onclick="getTable(' + (page - 1) + ',' + size + ',fields,mytype,myurl)">上一页</a></li>';
    }
    if (finalNum <= 5 && indexPage <= 5) {
        console.log("第一个判断");
        while (countNum < 6) {
            pageContent = pageContent + '<li><a href="javascript:;" onclick="getTable(' + countNum + ',' + size + ',fields,mytype,myurl)">' + countNum + '</a></li>';
            countNum++;
        }
        pageContent = pageContent + '<li><a href="javascript:;" onclick="getTable(' + finalNum + ',' + size + ',fields,mytype,myurl)">' + finalNum + '</a></li>';
    }

    else if (indexPage <= 4 && finalNum > 5) {
        console.log("第二个判断" + fields + mytype + myurl);
        while (countNum <= 5) {
            pageContent = pageContent + '<li ><a  href="javascript:;" onclick="getTable(' + countNum + ',' + size + ',fields,mytype,myurl); getPagination(this);">' + countNum + '</a></li>';
            countNum++;
        }
        pageContent = pageContent + '<li class="disabled"><a>' + "..." + '</a></li>';
        pageContent = pageContent + '<li><a href="javascript:;" onclick="getTable(' + finalNum + ',' + size + ',fields,mytype,myurl)">' + finalNum + '</a></li>';
    }

    else if (indexPage > 4 && indexPage <= (finalNum - 4)) {
        console.log("第三个判断");
        pageContent = pageContent + '<li><a href="javascript:;" onclick="getTable(' + startNum + ',' + size + ',fields,mytype,myurl)">' + startNum + '</a></li>';
        pageContent = pageContent + '<li class="disabled"><a>' + "..." + '</a></li>';
        while (countNum <= 5) {
            pageContent = pageContent + '<li><a  href="javascript:;" onclick="getTable(' + (indexPage - 3 + countNum) + ',' + size + ',fields,mytype,myurl)">' + (indexPage - 3 + countNum) + '</a></li>';
            countNum++;
        }
        pageContent = pageContent + '<li class="disabled"><a>' + "..." + '</a></li>';
        pageContent = pageContent + '<li><a href="javascript:;" onclick="getTable(' + finalNum + ',' + size + ',fields,mytype,myurl)">' + finalNum + '</a></li>';
    }

    else if (indexPage > finalNum - 4 && indexPage <= finalNum) {
        console.log("第四个判断");
        pageContent = pageContent + '<li><a href="javascript:;" onclick="getTable(' + startNum + ',' + size + ',fields,mytype,myurl)">' + startNum + '</a></li>';
        pageContent = pageContent + '<li class="disabled"><a>' + "..." + '</a></li>';
        while (countNum <= 5) {
            pageContent = pageContent + '<li><a  href="javascript:;" onclick="getTable(' + (finalNum - 5 + countNum) + ',' + size + ',fields,mytype,myurl)">' + (finalNum - 5 + countNum) + '</a></li>';
            countNum++;
        }
    }

    if (page == finalNum) {
        pageContent = pageContent + '<li class="disabled"><a href="javascript:;" >下一页</a></li>';
    }
    else {
        pageContent = pageContent + '<li><a href="javascript:;" onclick="getTable(' + (page + 1) + ',' + size + ',fields,mytype,myurl)">下一页</a></li>';
    }
    $("#XLpage").html(pageContent);
}

/*显示当前页码*/
function getCurrPage() {
    var page = $("#hidCurPage").val();
    $('ul.mypagination > li > a').each(function () {
        var pageNUM = $(this).text();
        if (parseInt(pageNUM) == parseInt(page)) {
            $(this).css("color", "#fff");
            $(this).css("background-color", "#337ab7");
            $(this).css("border-color", "#337ab7");
        }
    })
}

/*跳转页面功能*/

function jumpPage() {

    var page = "";
    var size = $("#selectId").val();
    if ($("#jumpText").val() == "") {
        page = $("#hidCurPage").val();
    }
    else {
        page = $("#jumpText").val();
    }

    getTable(page, size, fields, mytype, myurl);
}

function jumpTextValid(event) {
    var $this = event.target
    var vall = $this.value;
    var reg = /[^\d]/g;
    var pageCount = $("#hidPageCount").val();
    /!* 判断如果当前页码大于最大页码，则查询最大页码，否则查询当前页码*!/
    if (reg.test(vall)) {
        $this.val(vall.replace(reg, ''));
    }
    //最小值为1
    if (parseInt(vall) === 0) {
        $this.value = 1;
    }

    if (parseInt(vall) > pageCount) {
        $this.value = pageCount;
    }

}

function jumpTextEnter(e) {
    if (e.which == 13) {
        jumpPage();
    }
}

function getJumpPageContent(status) {
    if(status === true){
        $("#jumpPageDiv").show();
        var jumpPageContent = "";
        //html5绑定的监听事件。监听输入
        jumpPageContent = jumpPageContent + ' <input type="text" class="Jump" id="jumpText"  oninput="jumpTextValid(event)" onpropertychange="jumpTextValid(event)" onkeydown="jumpTextEnter(event)" placeholder="">';
        jumpPageContent = jumpPageContent + ' <button type="button" class=" mybtn mybtn-default " id="jumpBtn" onclick="jumpPage()" style="outline: none">跳转';
        jumpPageContent = jumpPageContent + ' </button>';
        $("#jumpPageDiv").html(jumpPageContent);
    }
}

/*选择页面显示数量*/
function getSelect(status) {
    if (status === true){
        console.log(true);
        $("#selectDiv").show();
        var selectContent = "";
        selectContent = selectContent + '<select class="form-control input-sm"   id="selectId" onchange="getSelectChange()" >';
        selectContent = selectContent + '<option value="10">10条/页</option>';
        selectContent = selectContent + '<option value="20">20条/页</option>';
        selectContent = selectContent + '<option value="30">30条/页</option>';
        selectContent = selectContent + '<option value="40">40条/页</option>';
        selectContent = selectContent + '</select>';
        $("#selectDiv").html(selectContent);
    }

}
function getOption(size) {
    $("#selectId option").each(function () {
        if (parseInt($(this).val()) === parseInt(size)) {
            $(this).attr("selected", "true");
        }
    })
}
function getSelectChange() {
    var size = $("#selectId").val();
    var page = $("#hidCurPage").val();
    var CountNum = $("#hidCountNum").val();
    var finalNum = 0;
    /!* 判断如果当前页码大于最大页码，则查询最大页码，否则查询当前页码*!/
    if (CountNum % size == 0) {
        finalNum = parseInt(CountNum / size);
    }
    else {
        finalNum = parseInt(CountNum / size) + 1;
    }
    if (page < finalNum) {
        getTable(page, size, fields, mytype, myurl);
    }
    else {
        getTable(finalNum, size, fields, mytype, myurl);
    }
    getOption(size);
}

function XLrender() {
      var renderContent = "";
      var renderContent = renderContent + ' <div style="width: 30%;">';
      var renderContent = renderContent + ' <ul class="mypagination" id="XLpage">';
      var renderContent = renderContent + ' </ul>';
      var renderContent = renderContent + ' </div>';
      var renderContent = renderContent + ' <div class="assembly" id="jumpPageDiv" >';
      var renderContent = renderContent + ' </div>';
      var renderContent = renderContent + ' <div class="assembly"  id="selectDiv" >';
      var renderContent = renderContent + ' </div>';
      $("#XLPageDiv").html(renderContent);

}

