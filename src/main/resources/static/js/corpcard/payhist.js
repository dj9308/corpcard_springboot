/************************************************************
 파일명    :    payhist.js
 설명    : 결제내역 페이지 JavaScript
 수정일        수정자        Version        설명
 ----------    ----------    ----------    ----------
 2022.12.14    설동재        1.0            최초 생성
 ************************************************************/

const $payhist = (function () {
  'use strict';

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

    //결제내역 현재 년월 표현
    document.querySelector("#payhist-month").value = `${now.getFullYear()}-${now.getMonth()}`

    //년월 변경 시 결제내역 조회
    document.querySelector("#payhist-month").addEventListener('change', function () {
      const userId = document.querySelector('#userId').value;
      const wrtYn = this.value;

      $.ajax({
        type: "GET",
        url: "/payhist/searchList",
        dataType: "json",
        data: {
          WRITER_ID: userId,
          WRT_YN: wrtYn
        },
        success: function (data) {
          emptyTable();
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
    });

    //첨부파일 btn
    document.querySelector("#payhist-atch").onclick = function () {
      const toastElList = [].slice.call(document.querySelectorAll('.toast'))
      const toastList = toastElList.map(function (toastEl) {
        return new bootstrap.Toast(toastEl)
      });
      toastList.forEach(toast => toast.show());
    };
  }

   /**
   * 결제 내역 form event handler
   */
  const initHistFormEvt = function(){
    //분류 select
    const classSelect = document.querySelector("#classSelect");

      // 분류
      $.ajax({
        type: "GET",
        url: "/base/classList",
        success: function (data) {
          if (data.CODE === "SUCCESS") {
            $cmmn.
          } else {
            alert("분류 목록 조회에 싪패했습니다. 관리자에게 문의해주시기 바랍니다.");
          }
        },
        error: function () {
          return alert("분류 목록 조회에 싪패했습니다. 관리자에게 문의해주시기 바랍니다.");
        }
      });

  }

  /**
   * 결제 내역 테이블 초기화
   */
  const emptyTable = function () {
    $("#histTable>tbody").empty();
  };

  /**
   * 결제내역 row 작성(저장)
   */
  const paintHistList = function (data) {
    const tbody = document.querySelector("#histTable>tbody");
    const newRow = tbody.insertRow();
    const newCell = newRow.insertCell();

    if ($cmmn.isNullorEmpty(data)) {
      newCell.setAttribute('colspan', '15');
      newCell.classList.add("fw-bold");
      newCell.innerText = "해당 년월의 결제 내역이 없습니다.";
    } else {

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
      "endDate": new Date(now.setMonth(now.getMonth() - 3))
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
