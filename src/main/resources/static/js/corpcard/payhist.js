/************************************************************
 파일명    :    payhist.js
 설명     : 결제 내역 페이지 JavaScript
 수정일        수정자        Version        설명
 ----------    ----------    ----------    ----------
 2022.12.14    설동재        1.0            최초 생성
 ************************************************************/

const $payhist = (function () {
  'use strict';

  const userId = document.querySelector('#userId').value;   // 사용자 ID

  /**
   * init
   */
  const init = function () {
    initEvt();
    initStatsDatepicker();
    paintCharts();
    initHistFormEvt();
    initHistTable();
  }

  /**
  * 초기 이벤트
  */
  const initEvt = function () {
    //결제 내역 작성 or 수정
    document.querySelector("#histForm").addEventListener('submit', function (event) {
        if(new RegExp(/[^0-9,]/, "g").test(document.querySelector("#histMoney").value)){
            alert("금액은 숫자만 입력 가능합니다.");
            return false;
        }

        event.preventDefault();
        const data = $cmmn.serializeObject("histForm");

        //1.카드 정보 설정 & 금액 쉼표 삭제
        const cardSelect = document.querySelector("#cardSelect");
        const cardInfo = cardSelect.options[cardSelect.selectedIndex].text;
        data.money = $cmmn.uncomma(data.money);
        data.cardComp = cardInfo.split(" ")[0];
        data.cardNum = cardInfo.split(" ")[1];
        data.classInfo = {
            seq : data.classSeq
        }
        delete data.cardSelect;

        //2.결제 내역 저장 or 수정
         $.ajax({
             type: "post",
             url: "/payhist/saveInfo",
             data: JSON.stringify(data),
             contentType: "application/json",
             dataType: 'json',
             success: function (data) {
                 if (data.CODE === "SUCCESS") {
                     selectPayhistList();
              } else if (data.CODE === "ERR") {
                console.log(data.MSG);
              }
             },
             error: function (request, status, error) {
                console.log(data.MSG);
             }
         });
    });

    //결제 내역 초기화 btn
    document.querySelector("#histReset").addEventListener("click", function (e) {
        changeForm("save");
    });

    //첨부파일 btn
    document.querySelector("#payhistAtch").addEventListener("click", function (e) {
      const toastElList = [].slice.call(document.querySelectorAll('.toast'))
      const toastList = toastElList.map(function (toastEl) {
        return new bootstrap.Toast(toastEl)
      });
      toastList.forEach(toast => toast.show());
    });

    //삭제 btn
    document.querySelector("#deleteRow").addEventListener("click", function (e) {
        const wrtYm = document.querySelector("#payhistMonth").value;
        const seqList = [];
        //1.체크된 row 조회
        $('.table-check').each(function (index) {
            if($(this).is(":checked")){
                seqList.push($(this).val()*1);
            }
        });

        if(seqList.length === 0){
            return alert("삭제하려는 결제 내역이 없습니다.");
        }
        //2.체크된 row list 삭제
        $.ajax({
            type: "POST",
            url: "/payhist/deleteList",
            dataType: "json",
            data: {
              WRITER_ID: userId,
              WRT_YM: wrtYm,
              SEQ_LIST : JSON.stringify(seqList)
            },
            success: function (data) {
                if(data.CODE === "SUCCESS"){
                    selectPayhistList();
                }else{
                    return alert("결제 내역 삭제를 싪패했습니다. 관리자에게 문의해주시기 바랍니다.");
                }
            },
            error: function () {
              return alert("결제 내역 삭제를 싪패했습니다. 관리자에게 문의해주시기 바랍니다.");
            }
          });

        $("#checkAll").prop('checked', false);
    });

    //제출 btn
    $("#submitHist").on("click", function(){
        $.ajax({
        type: "PATCH",
        url: "/payhist/updateState",
        success: function (data) {
          if (data.CODE === "SUCCESS") {

          } else if (data.CODE === "ERR") {
            return alert("제출에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
          }
        },
        error: function () {
          return alert("제출에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
        }
      });
    })
  }

  /**
  * 결제 내역 조회
  * @param wrtYm : String (작성 년월)
  */
  const selectPayhistList = function (wrtYm){
    if($cmmn.isNullorEmpty(wrtYm)){
        wrtYm = document.querySelector("#payhistMonth").value;
    }

    //1.결제 내역 리스트 조회
    $.ajax({
            type: "GET",
            url: "/payhist/searchList",
            dataType: "json",
            data: {
              WRITER_ID: userId,
              WRT_YM: wrtYm
            },
            success: function (data) {
              //1)테이블 초기화
              $cmmn.emptyTable("histTable");
              //2)테이블 row 생성
              const tbody = document.querySelector("#histTable>tbody");
              if (data.CODE === "SUCCESS") {
                paintTable(tbody, data.result);
                document.querySelector("#stateNm").innerText = data.result.stateNm;
              } else if (data.CODE === "EMPTY") {
                  const newRow = tbody.insertRow();
                  const newCell = newRow.insertCell();

                  document.querySelector("#stateNm").innerText = "제출 전";
                  document.querySelector("#listTotCnt").innerText = "0";
                  newCell.setAttribute('colspan', '15');
                  newCell.classList.add("fw-bold");
                  newCell.innerText = "해당 년월의 결제 내역이 없습니다.";
              }
            },
            error: function () {
              return alert("결제 내역 조회에 싪패했습니다. 관리자에게 문의해주시기 바랍니다.");
            }
          });

    //2.사용일시 선택 제한
    setLimitHistDate();
  }

  /**
  * 결제 내역 조회
  * @param { Object } tbody : 결제 내역 table body 태그
  * @param { JSON } data : 결제 내역 데이터
  */
  const paintTable = function(tbody, data){
    //1.총 건수 설정
    document.querySelector("#listTotCnt").innerText = data.list.length;
    //2.결제 내역 row 생성
    for(let i=0 ; i<data.list.length;i++){
        const rowData = data.list[i];
        const newRow = tbody.insertRow();
        //삭제 체크 박스
        const chkbox = document.createElement("input");
        chkbox.setAttribute("type", "checkbox");
        chkbox.setAttribute("value", rowData.seq);
        chkbox.className += "form-check-input me-1 table-check";
        newRow.insertCell().appendChild(chkbox);
        //사용일
        const date = new Date(rowData.useDate);
        const month = date.getMonth()+1;
        const days = date.getDate();
        newRow.insertCell().innerHTML = `${month < 10 ? `0${month}` : month}.${
            days < 10 ? `0${days}` : days}`;
        //카드 정보
        newRow.insertCell().innerHTML = `${rowData.cardComp} ${rowData.cardNum.split("-")[3]}`;
        //사용 내역
        newRow.insertCell().innerHTML = rowData.useHist;
        //분류별 금액
        for(let i = 1; i <= 10; i++){
            if(rowData.classInfo.seq === i){
                newRow.insertCell().innerHTML = $cmmn.convertToCurrency(rowData.money);
            }else{
                newRow.insertCell().innerHTML = "-";
            }
        }
        //합계
        newRow.insertCell().innerHTML = $cmmn.convertToCurrency(rowData.money);
        //row 이벤트 핸들러
        rowEvtHandler(newRow);
    }

    //3.총계 row 생성
    const newRow = tbody.insertRow();
    newRow.classList.add("fw-bold");

    const totalCell = newRow.insertCell();
    totalCell.setAttribute('colspan', '4');
    totalCell.innerHTML = "합계";
    //분류별 합계
    for(let i = 1; i <= 10; i++){
        const newCell = newRow.insertCell();
        for(let j in data.sumByClass){
            if(data.sumByClass[j].seq === i){
                newCell.innerHTML = $cmmn.convertToCurrency(data.sumByClass[j].sum);
                break;
            }else{
                newCell.innerHTML = "-";
            }
        }
    }
    //총계
    newRow.insertCell().innerHTML = $cmmn.convertToCurrency(data.sum);
  }
  /**
  * 결제 내역 테이블 row event handler
  */
  const rowEvtHandler = function (row){
      $(row).on("click", function(){
        if($(this).hasClass("table-active")){
            $(this).removeClass("table-active");
            changeForm("save");
        }else{
          const seq = $(this).children("td").eq(0).children("input").val();
          $("#histTable > tbody > tr").removeClass("table-active");
          $(this).addClass("table-active");
          changeForm("update");
          selectPayhistInfo(seq);
        }
      });
  }

  /**
  * 결제 내역 form 변경
  * @param {String} type : save(저장) or update(수정)
  * @param {Object} tr : tr tag
  */
  const changeForm = function(type) {
    if(type ==="update"){
        $("#histSave").css("display", "none");
        $("#histUpdate").css("display", "");
    }else{
        $("#histSave").css("display", "");
        $("#histUpdate").css("display", "none");
        $("#histTable > tbody > tr").removeClass("table-active");
        $("#histForm")[0].reset();
    }
  }

  /**
  * 결제 내역 단일 조회
  * @param {int} seq : 선택한 결제 내역 seq
  */
  const selectPayhistInfo = function(seq) {
    $.ajax({
          type: "GET",
          url: "/payhist/searchInfo",
          dataType: "json",
          data: {
            seq : seq,
          },
           contentType: "application/json",
           dataType: 'json',
          success: function (data) {
            const result = data.result
            if (data.CODE === "SUCCESS") {
            const optionList = $(`#histForm select[name=cardSelect]`).children("option");
            const date = new Date(result.useDate);
            const year = date.getFullYear();
            const month = date.getMonth()+1;
            const days = date.getDate();

            $("#histForm input[name=seq]").val(result.seq);
            $("#histForm input[name=useHist]").val(result.useHist);
            $("#histForm input[name=money]").val($cmmn.convertToCurrency(result.money));
            $(`#histForm select[name=classSeq] option:eq(${result.classInfo.seq})`).prop("selected",true);
            $("#histForm input[name=useDate]").val(`${year}-${month < 10 ? `0${month}` : month}-${
                days < 10 ? `0${days}` : days}`);
            for(let i=1; i < optionList.length;i++){
                const cardComp = optionList[i].innerText.split(" ")[0];
                const cardNum = optionList[i].innerText.split(" ")[1];
                if(cardComp === result.cardComp && cardNum === result.cardNum){
                    $(optionList[i]).prop("selected",true);
                    break;
                }
            }
            } else {
              alert("결제 내역 조회에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
            }
          },
          error: function () {
            return alert("결제 내역 조회에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
          }
        });
  }

  /**
  * 결제 내역 form event handler
  */
  const initHistFormEvt = function () {
    //분류 select
    const classSelect = document.querySelector("#classSelect");
    $.ajax({
      type: "GET",
      url: "/base/classList",
      success: function (data) {
        if (data.CODE === "SUCCESS") {
          const json = data.result;
          for (let i in json) {
            const option = document.createElement('option');
            option.text = json[i].classNm;
            option.value = json[i].seq;
            classSelect.options.add(option);
          }
        } else {
          alert("분류 목록 조회에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
        }
      },
      error: function () {
        return alert("분류 목록 조회에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
      }
    });

    //카드 select
    const cardSelect = document.querySelector("#cardSelect");
    $.ajax({
      type: "GET",
      url: "/base/cardList",
      success: function (data) {
        if (data.CODE === "SUCCESS") {
          const json = data.result;
          for (let i in json) {
            const option = document.createElement('option');
            option.text = `${json[i].cardComp} ${json[i].cardNum}`;
            option.value = json[i].seq;
            cardSelect.options.add(option);
          }
        } else {
          alert("분류 목록 조회에 싪패했습니다. 관리자에게 문의해주시기 바랍니다.");
        }
      },
      error: function () {
        return alert("분류 목록 조회에 싪패했습니다. 관리자에게 문의해주시기 바랍니다.");
      }
    });

    //사용일시 선택 제한
    setLimitHistDate();

    //결제 내역 form 금액 쉼표 표시
    document.querySelector(".input-money").addEventListener("keyup", function (e) {
        $(this).val($cmmn.convertToCurrency(this.value));
    });
  }

  /**
   * 사용일시 선택 제한
   */
  const setLimitHistDate = function (){
    const chosenDate = document.querySelector("#payhistMonth").value;
    const dateInput = document.querySelector("#useDate");
    const date = new Date(chosenDate);
    const year = date.getFullYear();
    const month = date.getMonth()+1;

    dateInput.setAttribute('min', `${year}-${month < 10 ? `0${month}` : month}-01`);
    dateInput.setAttribute('max', `${year}-${month < 10 ? `0${month}` : month}-${
        new Date(year, month, 0).getDate()}`);
  }

  /**
   * 기간별 통계 date picker
   */
  const initStatsDatepicker = function () {
    const now = new Date;

    $('#datepicker').daterangepicker({
      "autoApply": true,
      "alwaysShowCalendars": true,
      "startDate": new Date(now.setMonth(now.getMonth() - 6)),
      "endDate": new Date()
    }, function (start, end, label) { });

    $('#datepicker').on('apply.daterangepicker', function (ev, picker) {
      const startDate = picker.startDate.format('YYYY-MM-DD');
      const endDate = picker.endDate.format('YYYY-MM-DD');
      paintCharts();
    });
  }

  /**
   * 차트 조회
   */
  const paintCharts = function () {
    const totalSum = document.querySelector('#totalSum');
    const monthSum = document.querySelector('#monthSum');
    const barChartDom = document.querySelector('#chart-bar');
    const pieChartDom = document.querySelector('#chart-pie');
    const barOption = {
      xAxis: {
        type: 'category',
        data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          data: [120, 200, 150, 80, 70, 110, 130],
          type: 'bar'
        }
      ]
    };
    const pieOption = {
      title: {
        text: 'Referer of a Website',
        subtext: 'Fake Data',
        left: 'center'
      },
      tooltip: {
        trigger: 'item'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: 'Access From',
          type: 'pie',
          radius: '50%',
          data: [
            { value: 1048, name: 'Search Engine' },
            { value: 735, name: 'Direct' },
            { value: 580, name: 'Email' },
            { value: 484, name: 'Union Ads' },
            { value: 300, name: 'Video Ads' }
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    };

    initChartAnimation(barChartDom, barOption);
    initChartAnimation(pieChartDom, pieOption);
  }

  /**
   * 차트 애니메이션 실행
   */
  const initChartAnimation = function (chartDom, option) {
    let myChart;

    echarts.dispose(chartDom);
    myChart = echarts.init(chartDom);
    option && myChart.setOption(option);
  }

  /**
   * 결제 내역 table
   */
  const initHistTable = function(){
    const now = new Date;
    const month = now.getMonth()+1;
    const payhistMonth = document.querySelector("#payhistMonth");

    //결제 내역 현재 년월 표현
    payhistMonth.value = `${now.getFullYear()}-${month < 10 ? `0${month}` : month}`;
    selectPayhistList();

    //년월 변경 시 결제 내역 조회
    payhistMonth.addEventListener('change', function () {
      selectPayhistList(this.value);
    });

    //체크박스 전체 설정
    document.querySelector("#checkAll").addEventListener('click', function(){
        $(".table-check").prop('checked', $(this).prop('checked'));
    });
  }

  return {
    init: init,
  }
}());
