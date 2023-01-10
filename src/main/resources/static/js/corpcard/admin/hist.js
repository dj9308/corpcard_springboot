/************************************************************
 파일명    :    hist.js
 설명     : 내역 관리 페이지 JavaScript
 수정일        수정자        Version        설명
 ----------    ----------    ----------    ----------
 2023.01.00    설동재        1.0            최초 생성
 ************************************************************/

const $adminHist = (function () {
  'use strict';

  const userId = document.querySelector('#userId').value;   // 사용자 ID

  /**
   * init
   */
  const init = function () {
    initBtnEvt();           //버튼 이벤트
    initStatsDatePicker();  //기간별 통계 date picker
    paintCharts();          //차트 생성
  }

  /**
  * 버튼 이벤트
  */
  const initBtnEvt = function(){
    //사용내역 검색 조회 버튼
    $("#histSubmit").on('click', function(){

    });
    //작성 버튼
    $("#histSave").on('click', function(){

    });
    //작성 버튼
    $("#histReset").on('click', function(){

    });
    //첨부파일 버튼
    $("#histAtchBtn").on('click', function(){

    });
    //사용내역 삭제 버튼
    $("#deleteRow").on('click', function(){

    });
  }

  /**
   * 기간별 통계 date picker 설정
   */
  const initStatsDatePicker = function(){
    const now = new Date;
    const startYm = $cmmn.formatDate(new Date(now.getFullYear(), now.getMonth() - 6), 'YYYY-mm');
    const endYm = $cmmn.formatDate(new Date(now.getFullYear(), now.getMonth()), 'YYYY-mm');
    $("#statDatePicker").val(`${startYm} - ${endYm}`);

    $('#statDatePicker').daterangepicker({
      "minViewMode": "month",
      format: 'YYYY-MM',
      "autoApplyClickedRange": true,
      "alwaysShowCalendars": true,
    }, function (start, end, label) { });

    $('#statDatePicker').on('apply.daterangepicker', function (ev, picker) {
      const startYm = $cmmn.formatDate(picker.startDate, "YYYY-mm");
      const endYm = $cmmn.formatDate(picker.endDate, "YYYY-mm");
      paintCharts(startYm, endYm);
    });
  }

  /**
   * 차트 조회
   * @param {String} startYm : 검색 기간 첫 월
   * @param {String} endYm : 검색 기간 마지막 월
   */
  const paintCharts = function (startYm, endYm) {
    function addData(wrtYm, money, seq) {
      wrtYmList.push(wrtYm);
      sumList.push(money);
//      barChartSeqList.push(endYm);
    }

    if ($cmmn.isNullorEmpty(startYm) || $cmmn.isNullorEmpty(endYm)) {
      const now = new Date;
      startYm = $cmmn.formatDate(new Date(now.getFullYear(), now.getMonth() - 6), 'YYYY-mm');
      endYm = $cmmn.formatDate(new Date(now.getFullYear(), now.getMonth()), 'YYYY-mm');
    }

    const wrtYmList = [];
    const sumList = [];
    let dataList = [];
    let totalSum = 0;

    //1.월별 총계 조회
    $.ajax({
      type: "GET",
      url: "/payhist/searchTotalSumList",
      dataType: "json",
      async: false,
      data: {
        startYm: startYm,
        endYm: endYm,
      },
      success: function (data) {
        if (data.CODE === "SUCCESS") {
          dataList = data.result;
          for (let i = 0; i < data.result.length; i++) {
            totalSum += dataList[i].sum;
          }
        }
      },
    });

    let i = 0;
    while (startYm <= endYm && i < dataList.length) {
      if (startYm < dataList[i].wrtYm) {
        addData(startYm, 0, '');
        startYm = $cmmn.formatDate(new Date(new Date(startYm).getFullYear(),
          new Date(startYm).getMonth() + 1), 'YYYY-mm');
      } else if (startYm > dataList[i].wrtYm) {
        i++;
      } else {
        addData(dataList[i].wrtYm, dataList[i].sum, dataList[i].seq);
        startYm = $cmmn.formatDate(new Date(new Date(startYm).getFullYear(),
          new Date(startYm).getMonth() + 1), 'YYYY-mm');
        i++;
      }
    }

    while (i < dataList.length) {
      addData(dataList[i].wrtYm, dataList[i].sum, dataList[i].seq);
      i++;
    }

    while (startYm <= endYm) {
      addData(startYm, 0, '');
      startYm = $cmmn.formatDate(new Date(new Date(startYm).getFullYear(),
        new Date(startYm).getMonth() + 1), 'YYYY-mm');
    }

        const barChartDom = document.querySelector('#monthChart');
        const barOption = {
          title: {
            text: '월별 합계',
            left: 'center'
          },
          xAxis: {
            type: 'category',
            data: wrtYmList
          },
          yAxis: {
            type: 'value'
          },
          series: [
            {
              data: sumList,
              type: 'bar',
              showBackground: true,
              backgroundStyle: {
                color: 'rgba(180, 180, 180, 0.2)'
              },
              label: {
                show: true,
                position: 'top'
              },
            },
          ]
        };
        initChartAnimation(barChartDom, barOption);
        selectHistList(endYm, function (data) {
          paintTeamChart(endYm, data);
          paintClassChart(endYm, data);
        });

        document.querySelector('#chartSum').innerText = `${$cmmn.convertToCurrency(totalSum)}원`

  }

/**
   * 팀별 통계 차트 조회
   * @param {String} yearMonth : 검색 연월
   * @param {JSON} data : 조회 결과 data
   */
  const paintTeamChart = function(yearMonth, data){
    const dataList = [];
    let result = data.result;

    if (data.CODE === "SUCCESS") {
      for (let i = 0; i < result.sumByTeam.length; i++) {
        const object = {};
        object.value = result.sumByTeam[i].sum;
        object.name = result.sumByTeam[i].team;
        dataList.push(object);
      }
    }

        const pieChartDom = document.querySelector('#teamChart');
        const pieOption = {
          title: {
            text: '부서별 합계',
            subtext: yearMonth,
            left: 'center'
          },
          tooltip: {
            trigger: 'item'
          },
          series: [
            {
              name: '분류별 합계',
              type: 'pie',
              radius: '50%',
              data: dataList,
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
        initChartAnimation(pieChartDom, pieOption);
  }

/**
   * 분류별 차트 조회
  * @param {String} yearMonth : 검색 연월
  * @param {JSON} data : 조회 결과 data
   */
  const paintClassChart = function(yearMonth, data){
    const dataList = [];
    let result = data.result;

    if (data.CODE === "SUCCESS") {
      for (let i = 0; i < result.sumByClass.length; i++) {
        const object = {};
        object.value = result.sumByClass[i].sum;
        object.name = result.sumByClass[i].classNm;
        dataList.push(object);
      }
    }

        const pieChartDom = document.querySelector('#classChart');
        const pieOption = {
          title: {
            text: '분류별 합계',
            subtext: yearMonth,
            left: 'center'
          },
          tooltip: {
            trigger: 'item'
          },
          series: [
            {
              name: '분류별 합계',
              type: 'pie',
              radius: '50%',
              data: dataList,
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

      if (chartDom.id === "monthChart") {
        myChart.on('click', function (params) {
          selectHistList(params.name, function (data) {
            paintTeamChart(params.name, data);
            paintClassChart(params.name, data);
          });
        });
      }
    }

      /**
       * 결제 내역 리스트 조회 AJAX
       * @param {String} yearMonth : 작성연월
       * @param {function} callback : Callback function
       */
      const selectHistList = function (yearMonth, callback) {
        $.ajax({
          type: "GET",
          url: "/admin/searchPayList",
          dataType: "json",
          data: {
            wrtYm: yearMonth
          },
          success: function (data) {
            callback(data);
          },
          error: function () {
            alert("결제 내역 조회에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
          }
        });
      }

  return {
    init: init,
  }
}());
