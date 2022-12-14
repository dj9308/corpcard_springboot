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
        initBtnEvent();
        initStatsDatepicker();
        paintCharts();
    }

    /**
    * 버튼 이벤트
    */
    const initBtnEvent = function(){
        
    }

    /**
     * 기간별 통계 date picker
     */
    const initStatsDatepicker = function(){
        const now = new Date;

        $('#datepicker').daterangepicker({
            "autoApply": true,
            "alwaysShowCalendars": true,
            "endDate": new Date(now.setMonth(now.getMonth() - 3))
        }, function(start, end, label) {});

        $('#datepicker').on('apply.daterangepicker', function(ev, picker) {
            const startDate = picker.startDate.format('YYYY-MM-DD');
            const endDate = picker.endDate.format('YYYY-MM-DD');
            paintCharts();
            // $(this).val(picker.startDate.format('YYYY-MM-DD') + ' - ' + picker.endDate.format('YYYY-MM-DD'));
        });
    }

    /**
     * 차트 조회
     */
    const paintCharts = function(){
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
        
        initChartAnimation(barChartDom,barOption);
        initChartAnimation(pieChartDom,pieOption);

        
    }

    /**
     * 차트 애니메이션 실행
     */
    const initChartAnimation = function(chartDom, option){
        let myChart;
        
        echarts.dispose(chartDom);
        myChart = echarts.init(chartDom);
        option && myChart.setOption(option);
    }





    







    return {
        init: init,
    }
}());
