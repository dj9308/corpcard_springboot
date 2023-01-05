/************************************************************
 파일명    :    adminHist.js
 설명     : 결제 내역 페이지 JavaScript
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
    initStatsDatePicker();  //월별 통계 Date picker
    paintCharts();          //차트 조회
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
  * 월별 통계 Date picker
  */
  const initStatsDatePicker = function(){
    const now = new Date;


    $('#statDatePicker').daterangepicker({
      "autoApply": true,
      "alwaysShowCalendars": true,
      "startDate": new Date(now.setMonth(now.getMonth())),
      "endDate": new Date(now.setMonth(now.getMonth()))
    }, function (start, end, label) { });

    $('#statDatePicker').on('apply.daterangepicker', function (ev, picker) {
      const startDate = picker.startDate.format('YYYY-MM-DD');
      const endDate = picker.endDate.format('YYYY-MM-DD');
      paintCharts();
    });
  }

 /**
  * 차트 조회
  */
  const paintCharts = function(){
    
  }

  return {
    init: init,
  }
}());
