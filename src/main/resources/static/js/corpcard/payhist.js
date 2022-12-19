/************************************************************
 파일명    :    payhist.js
 설명    : 결제내역 페이지 JavaScript
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
    initHistFormEvt();
    paintCharts();
  }

  /**
  * event handler
  */
  const initEvt = function () {
    const now = new Date;
    const payhistMonth = document.querySelector("#payhistMonth");

    //결제내역 현재 년월 표현
    payhistMonth.value = `${now.getFullYear()}-${now.getMonth()+1}`;
    selectPayhistList(payhistMonth.value);

    //년월 변경 시 결제내역 조회
    payhistMonth.addEventListener('change', function () {
      const wrtYm = this.value;
      selectPayhistList(wrtYm);
    });

    //결제내역 작성 btn
    document.querySelector("#histForm").addEventListener('submit', function (event) {
        event.preventDefault();
        const data = $cmmn.serializeObject("histForm");

        //1.카드 정보 설정
        const cardSelect = document.querySelector("#cardSelect");
        const cardInfo = cardSelect.options[cardSelect.selectedIndex].text;
        data.cardComp = cardInfo.split(" ")[0];
        data.cardNum = cardInfo.split(" ")[1];
        data.classInfo = {
            seq : data.classSeq
        }
        delete data.cardSelect;

        //2.결제 내역 저장
         $.ajax({
             type: "post",
             url: "/payhist/saveInfo",
             data: JSON.stringify(data),
             contentType: "application/json",
             dataType: 'json',
             success: function (data) {
                 if (data.CODE === "SUCCESS") {
                 console.log(data.MSG);
              } else if (data.CODE === "ERR") {
                console.log(data.MSG);
              }
             },
             error: function (request, status, error) {
                console.log(data.MSG);
             }
         });
    });

    //첨부파일 btn
    document.querySelector("#payhistAtch").onclick = function () {
      const toastElList = [].slice.call(document.querySelectorAll('.toast'))
      const toastList = toastElList.map(function (toastEl) {
        return new bootstrap.Toast(toastEl)
      });
      toastList.forEach(toast => toast.show());
    };
  }

/**
  * 결제 내역 조회
  * @param wrtYm : String (작성 년월)
  */
  const selectPayhistList = function (wrtYm){
    //1.결제내역 리스트 조회
    $.ajax({
            type: "GET",
            url: "/payhist/searchList",
            dataType: "json",
            data: {
              WRITER_ID: userId,
              WRT_YM: wrtYm
            },
            success: function (data) {
              //1) 테이블 초기화
              $cmmn.emptyTable("histTable");

              //2) 테이블 row 생성
              if (data.CODE === "SUCCESS") {
                paintHistList(data.result);
              } else if (data.CODE === "EMPTY") {
                paintHistList();
              }
            },
            error: function () {
              return alert("결제내역 조회에 싪패했습니다. 관리자에게 문의해주시기 바랍니다.");
            }
          });

          //2.사용일시 선택 제한
          setLimitHistDate();
  }

  /**
  * 결제 내역 form event handler
  */
  const initHistFormEvt = function () {
  
    //1.분류 select
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

    //2.카드 select
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

    //3.사용일시 선택 제한
    setLimitHistDate();
    
    //4.금액 표현

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

    dateInput.setAttribute('min', `${year}-${month}-01`);
    dateInput.setAttribute('max', `${year}-${month}-${new Date(year, month, 0).getDate()}`);
  }

  /**
   * 결제내역 row 작성
   */
  const paintHistList = function (data) {
    const tbody = document.querySelector("#histTable>tbody");
    const newRow = tbody.insertRow();
    const newCell = newRow.insertCell();

    //1.리스트 및 총 건수 조회
    if ($cmmn.isNullorEmpty(data)) {
        document.querySelector("#listTotCnt").innerText = "0";
      newCell.setAttribute('colspan', '15');
      newCell.classList.add("fw-bold");
      newCell.innerText = "해당 년월의 결제 내역이 없습니다.";
    } else {

    }
    //결재 상태 조회
    const submitStts = document.querySelector("#submitStts");
    if($cmmn.isNullorEmpty(data)){
        submitStts.value = "제출 전";
    }else{

    }
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













  return {
    init: init,
  }
}());
