let data_t;
function data_flush(){
    clearInterval(data_t);
    tabledata();
    data_t = setInterval(data_flush,3000);
}

function tabledata() {
    let dataObj = data_get();
    let raw_data = []; let pyro_data = []; let cement_data = [];
    for(let i=0;i<dataObj.length;i++){
        let process = dataObj[i]["process"];
        if(process==="1"){
            raw_data.push(dataObj[i]);
        }else if(process==="2"){
            pyro_data.push(dataObj[i]);
        }else{
            cement_data.push(dataObj[i]);
        }

    }
    table_create(raw_data,"raw_body");
    table_create(pyro_data,"pyro_body");
    table_create(cement_data,"cement_body");
    audio_alarm(dataObj);
}


function data_get() {
    let alarmdata=[];

    $.ajax({
        url: "/alarmreport/getAlarmInfo",
        type: 'get',
        dataType: 'json',
        async:false,
        success: function (jsonObject) {
            alarmdata = jsonObject.data;
        }
    });
    return alarmdata;
}

// function data_get() {
//     let jsonObject =  {"msg":"success","code":0,
//         "data":[{"proline":"10012016101910000001",
//             "almtime":"2020年10月31日 下午6:09:03",
//             "process":"1","is_audio":false,
//             "almchgrate":0.0,
//             "almlevel":3.0,
//             "almcurvalue":173.87057495117188,
//             "almcontent":"生料磨主机电流低报"},
//             {"proline":"10012016101910000001",
//                 "almtime":"2020年10月31日 下午6:09:03",
//                 "process":"2","is_audio":true,
//                 "almchgrate":0.0,
//                 "almlevel":3.0,
//                 "almcurvalue":173.87057495117188,
//                 "almcontent":"回转窑主机电流低报"},
//             {"proline":"10012016101910000001",
//                 "almtime":"2020年10月31日 下午6:09:03",
//                 "process":"3","is_audio":false,
//                 "almchgrate":0.0,
//                 "almlevel":3.0,
//                 "almcurvalue":173.87057495117188,
//                 "almcontent":"水泥磨主机电流低报"}],"count":1};
//
//     return jsonObject.data;
// }


function table_create(dataObj,tbodyid) {
    let param_body =document.getElementById(tbodyid);
    dataremove(param_body);

    for(let i=0;i<dataObj.length;i++){
        let tr=document.createElement('tr');
        let td_level=document.createElement('td');
        let td_content=document.createElement('td');
        let td_curvalue=document.createElement('td');
        let td_chgrate=document.createElement('td');
        let td_almtime=document.createElement('td');
        let td_0=document.createElement('td');

        td_level.setAttribute("class","th1");
        td_content.setAttribute("class","th2");
        td_curvalue.setAttribute("class","th3");
        td_chgrate.setAttribute("class","th4");
        td_almtime.setAttribute("class","th5");

        td_level.innerText = dataObj[i]["almlevel"];
        td_content.innerText = dataObj[i]["almcontent"];
        td_curvalue.innerText = dataObj[i]["almcurvalue"].toFixed(1);
        td_chgrate.innerText = dataObj[i]["almchgrate"];
        td_almtime.innerText = dataObj[i]["almtime"];

        tr.appendChild(td_level);
        tr.appendChild(td_content);
        tr.appendChild(td_curvalue);
        tr.appendChild(td_chgrate);
        tr.appendChild(td_almtime);
        tr.appendChild(td_0);
        param_body.appendChild(tr);
    }

}

/**-------创建表格之前，移除body中所有内容------**/
function dataremove(bodyObj){
    if(bodyObj==null)
    {
        console.log("Body of Table not Exist!");
        return;
    }
    for (let i = 0; i < bodyObj.rows.length;)
        bodyObj.deleteRow(i);
}


function audio_alarm(dataObj) {
    console.log("dataObj:",dataObj);
    for(let i=0;i<dataObj.length;i++){
        let isaudio = dataObj[i]["is_audio"];
        if(isaudio===true){
            let content = dataObj[i]["almcontent"];
            doTTS(content);
        }
    }
}


function doTTS(msgtext) {
    console.log("msgtext:",msgtext);
    let msg = new SpeechSynthesisUtterance(msgtext);
    msg.volume = 100;
    msg.rate = 1;
    msg.pitch = 1.5;
    window.speechSynthesis.speak(msg);
}

