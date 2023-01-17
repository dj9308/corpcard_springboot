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
    initSearchForm();       //사용 내역 검색 Form
    initHistForm();         //결제 내역 Form
    initHistTable();        //결제 내역 table
  }

  /**
  * 버튼 이벤트
  */
  const initBtnEvt = function () {
    //사용내역 검색 조회 버튼
    $("#histSubmit").on('click', function () {
      //1.form data 생성
      const data = $cmmn.serializeObject("approvalForm");

      //2.결재 건 목록 조회
      $.ajax({
        type: "GET",
        url: "/admin/searchApprovalList",
        data: data,
        success: function (data) {
          if (data.CODE === "SUCCESS") {
            paintApprovalTable(data.result);
          } else if (data.CODE === "EMPTY") {
            paintApprovalTable();
          } else {
            paintApprovalTable();
            alert("분류 목록 조회에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
          }
        },
        error: function () {
          paintApprovalTable();
          alert("분류 목록 조회에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
        }
      });
    });

    //결제 내역 작성 or 수정
    $("#histForm").on("submit", function (event) {
      event.preventDefault();
      const histMoney = document.querySelector("#histMoney");
      if (new RegExp(/[^0-9,]/, "g").test(histMoney.value)) {
        alert("금액은 숫자만 입력 가능합니다.");
        histMoney.focus();
        return false;
      }
      //1.카드 정보 설정 & 금액 쉼표 삭제
      const data = $cmmn.serializeObject("histForm");
      const cardSelect = document.querySelector("#cardSelect");
      const cardInfo = cardSelect.options[cardSelect.selectedIndex].text;
      data.money = $cmmn.uncomma(data.money);
      data.cardComp = cardInfo.split(" ")[0];
      data.cardNum = cardInfo.split(" ")[1];
      data.classInfo = {
        seq: data.classSeq
      }
      delete data.cardSelect;

      //2.결제 내역 저장 or 수정
      $.ajax({
        type: "POST",
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

    //초기화 버튼
    $("#histReset").on('click', function () {
      changeForm("save");
    });
    //첨부파일 버튼
    $("#histAtchBtn").on('click', function () {
      $cmmn.initHistToast("liveToast");
    });
    //사용내역 테이블 Row 삭제 버튼
    $("#deleteRow").on('click', function () {
      const submitInfo = $("#approvalTable > tbody > tr.table-active");
      //1.체크된 row 조회
      const seqList = [];
      $('.table-check').each(function (index) {
        if ($(this).is(":checked")) {
          seqList.push($(this).val() * 1);
        }
      });
      if (seqList.length === 0) {
        return alert("삭제하려는 결제 내역이 없습니다.");
      }

      //2.체크된 row list 삭제
      $.ajax({
        type: "DELETE",
        url: "/payhist/deleteList",
        dataType: "json",
        data: {
          WRITER_ID: $(submitInfo).find("td:eq(8)").text(),
          WRT_YM: $(submitInfo).find("td:eq(5)").text(),
          SEQ_LIST: JSON.stringify(seqList)
        },
        success: function (data) {
          if (data.CODE === "SUCCESS") {
            selectPayhistList();
          } else {
            return alert("결제 내역 삭제를 싪패했습니다. 관리자에게 문의해주시기 바랍니다.");
          }
        },
        error: function () {
          return alert("결제 내역 삭제를 싪패했습니다. 관리자에게 문의해주시기 바랍니다.");
        }
      });
      $("#checkAll").prop('checked', false);
    });

    //첨부파일 toast 추가 btn
    $("#atchAddBtn").on("click", function (event) {
      $("#atchUpload").trigger('click');
    });

    $("#atchUpload").on('click', function () {
      this.value = null;
    });
    $("#atchUpload").on('change', function (e) {
      //formData 설정
      const formData = new FormData();
      const data = {
        WRITER_ID: $("#approvalTable > tbody > tr.table-active").find("td:eq(8)").text(),
        WRT_YM: $("#approvalTable > tbody > tr.table-active").find("td:eq(5)").text(),
      };
      formData.append('key', new Blob([JSON.stringify(data)], { type: "application/json" }));
      for (let i = 0; i < this.files.length; i++) {
        formData.append("files", this.files[i]);
      }
      //첨부파일 업로드
      $.ajax({
        type: "POST",
        url: "/payhist/uploadAtch",
        enctype: 'multipart/form-data',
        data: formData,
        processData: false,
        contentType: false,
        success: function (data) {
          if (data.CODE === "SUCCESS") {
            selectAtchList(data.result[0].usehistSubmitInfo.seq);
          }
        }
      });
    });

    //첨부파일 toast 삭제 btn
    $("#atchDelBtn").on("click", function (event) {
      //1.체크된 row 조회
      const atchList = [];
      $('.atch-check').each(function (index) {
        if ($(this).is(":checked")) {
          atchList.push($(this).val() * 1);
        }
      });
      if (atchList.length === 0) {
        return alert("삭제하려는 첨부파일이 없습니다.");
      }

      //2.체크된 row list 삭제
      $.ajax({
        type: "DELETE",
        url: "/payhist/deleteAtchList",
        dataType: "json",
        data: {
          SEQ_LIST: JSON.stringify(atchList)
        },
        success: function (data) {
          if (data.CODE === "SUCCESS") {
            deleteAtchInfo();
          } else {
            return alert("결제 내역 삭제를 싪패했습니다. 관리자에게 문의해주시기 바랍니다.");
          }
        },
        error: function () {
          return alert("결제 내역 삭제를 싪패했습니다. 관리자에게 문의해주시기 바랍니다.");
        }
      });
    });
  }

  /**
   * 결재 건 table 생성
   * @param {Array} data : 결재 건 list
   */
  const paintApprovalTable = function (data) {
    const tbody = document.querySelector("#approvalTable>tbody");
    //1.table 초기화
    $("#approvalTable>tbody").empty();
    //2.table row 생성
    if (data.length === 0) {
      const newCell = tbody.insertRow().insertCell();
      newCell.setAttribute('colspan', '7');
      newCell.classList.add("fw-bold");
      newCell.innerText = "해당 조건의 결재 건 내역이 없습니다.";
    } else {
      for (let i = 0; i < data.length; i++) {
        const newRow = tbody.insertRow();
        //No.
        newRow.insertCell().innerHTML = (i + 1).toString();
        //부서
        newRow.insertCell().innerHTML = data[i].writerDept;
        //팀
        newRow.insertCell().innerHTML = data[i].writerTeam;
        //직위
        newRow.insertCell().innerHTML = data[i].writerOfcds;
        //기안자
        newRow.insertCell().innerHTML = data[i].writerNm;
        //제출날짜
        newRow.insertCell().innerHTML = data[i].wrtYm;
        //합계
        newRow.insertCell().innerHTML = $cmmn.isNullorEmpty(data[i].sum) ? '-' : $cmmn.convertToCurrency(data[i].sum);
        //submit seq
        const seqCell = newRow.insertCell();
        seqCell.innerHTML = data[i].seq.toString();
        seqCell.style.display = "none";
        //기안자 ID
        const writerIdCell = newRow.insertCell();
        writerIdCell.innerHTML = data[i].userId;
        writerIdCell.style.display = "none";
        //row 이벤트 핸들러
        rowEvtHandler(newRow);
      }
    }
  }

  /**
    * 결재 건 테이블 row event handler
    * @param {Object} row : 결제 내역 테이블 row
    */
  const rowEvtHandler = function (row) {
    $(row).on("click", function () {
      if (!$(this).hasClass("table-active")) {
        //사용 내역 조회
        const submitSeq = $(this).find("td:eq(7)").text();
        $("#approvalTable > tbody > tr").removeClass("table-active");
        $(this).addClass("table-active");
        selectPayhistList();
        selectAtchList(submitSeq);

        //사용 일시 범위 제한
        const chosenDate = $(this).find("td:eq(5)").text();
        const dateInput = document.querySelector("#useDate");
        const date = new Date(chosenDate);
        const year = date.getFullYear();
        const month = date.getMonth() + 1;
        dateInput.setAttribute('min', `${year}-${month < 10 ? `0${month}` : month}-01`);
        dateInput.setAttribute('max', `${year}-${month < 10 ? `0${month}` : month}-${new Date(year, month, 0).getDate()}`);

        //버튼 disable 해제
        disableBtn(false);
      }
    });
  }

  /**
     * 첨부파일 리스트 조회
     * @param {String} seq : 제출 내역 seq
     */
  const selectAtchList = function (seq) {
    if (!$cmmn.isNullorEmpty(seq)) {
      $.ajax({
        type: "GET",
        url: "/payhist/searchAtchList",
        dataType: "json",
        data: {
          SEQ: seq
        },
        success: function (data) {
          if (data.CODE === "SUCCESS") {
            paintAtchList(data.result);
          } else if (data.CODE === "ERR") {
            return alert("첨부파일 조회에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
          }
        },
        error: function () {
          return alert("첨부파일 조회에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
        }
      });
    }
  }

  /**
   * 첨부파일 리스트 생성
   * @param {Array} data : 첨부파일 정보 Array
   */
  const paintAtchList = function (data) {
    //1.기존 첨부파일 list 초기화
    $("#atchList > li").remove();
    $("#atchEmpty").css("display", "");
    $("#atchCnt").text("0");

    if ($cmmn.isNullorEmpty(data)) {
      return false;
    }
    $("#atchEmpty").css("display", "none");
    $("#atchCnt").text(data.length);
    const atchList = document.querySelector("#atchList");
    for (let i = 0; i < data.length; i++) {
      const list = document.createElement("li");
      const chkbox = document.createElement("input");
      const aTag = document.createElement("a");
      list.className += "list-group-item";
      chkbox.setAttribute("type", "checkbox");
      chkbox.setAttribute("value", data[i].seq);
      chkbox.className += "form-check-input me-1 atch-check";
      aTag.innerText = data[i].fileNm;
      aTag.className += "text-decoration-none"
      aTag.setAttribute("href", "http://" + `${location.host}/payhist/downloadAtch?seq=${data[i].usehistSubmitInfo.seq}&fileNm=${data[i].fileNm}&fileSeq=${data[i].seq}`);
      list.appendChild(chkbox);
      list.appendChild(aTag);
      document.querySelector("#atchList").appendChild(list);
    }
  }

  /**
   * 선택한 첨부파일 리스트 삭제
   * @param {Array} data : 삭제된 첨부파일 Seq Array
   */
  const deleteAtchInfo = function () {
    $(".atch-check:checked").each(function (index) {
      $(this).parent("li").remove();
    });
    if ($("#atchList > li").length == 0) {
      $("#atchEmpty").css("display", "");
      $("#atchCnt").text("0");
    }
  }

  /**
      * 결제 내역 목록 조회
      * @param {String} seq : 제출 내역 seq
      */
  const selectPayhistList = function () {
    const submitSeq = $("#approvalTable > tbody > tr.table-active").find("td:eq(7)").text();
    $.ajax({
      type: "GET",
      url: "/approval/searchPayhistList",
      dataType: "json",
      data: {
        SEQ: submitSeq
      },
      success: function (data) {
        if (data.CODE == "SUCCESS") {
          paintPayhistTable(data.result);
          changeForm("save");
        } else {
          paintPayhistTable();
        }
      },
      error: function () {
        return alert("결제 내역 조회에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
      }
    });
  }

  /**
  * 결제 내역 테이블 생성
  * @param {Array} data : 결제 내역 Array
  */
  const paintPayhistTable = function (data) {
    const tbody = document.querySelector("#histTable > tbody");

    $("#histTable>tbody").empty();

    if ($cmmn.isNullorEmpty(data)) {
      const newCell = tbody.insertRow().insertCell();
      newCell.setAttribute('colspan', '14');
      newCell.classList.add("fw-bold");
      newCell.innerText = "해당 결재 건의 법인카드 사용 내역이 없습니다.";
      $("#approvalTable > tbody > tr.table-active").find("td:eq(6)").text("-");
      return false;
    } else {
      //1.총 건수 설정
      document.querySelector("#listTotCnt").innerText = data.list.length;

      //2.결제 내역 row 생성
      for (let i = 0; i < data.list.length; i++) {
        const rowData = data.list[i];
        const newRow = tbody.insertRow();
        //삭제 체크 박스
        const chkbox = document.createElement("input");
        chkbox.setAttribute("type", "checkbox");
        chkbox.setAttribute("value", rowData.seq);
        chkbox.className += "form-check-input me-1 table-check";
        chkbox.addEventListener("click", function(event){
          event.stopPropagation();
        });
        newRow.insertCell().appendChild(chkbox);
        //사용일
        newRow.insertCell().innerHTML = $cmmn.formatDate(rowData.useDate, "mm.dd");
        //카드 정보
        newRow.insertCell().innerHTML = `${rowData.cardComp} ${rowData.cardNum.split("-")[3]}`;
        //사용 내역
        newRow.insertCell().innerHTML = rowData.useHist;
        //분류별 금액
        for (let j = 1; j <= 10; j++) {
          if (rowData.classInfo.seq === j) {
            newRow.insertCell().innerHTML = $cmmn.convertToCurrency(rowData.money);
          } else {
            newRow.insertCell().innerHTML = "-";
          }
        }
        //합계
        newRow.insertCell().innerHTML = $cmmn.convertToCurrency(rowData.money);
        histRowEvtHandler(newRow);
      }

      //3.총계 row 생성
      const newRow = tbody.insertRow();
      const totalCell = newRow.insertCell();
      newRow.classList.add("fw-bold");
      totalCell.setAttribute('colspan', '4');
      totalCell.innerHTML = "합계";
      //분류별 합계
      for (let i = 1; i <= 10; i++) {
        const newCell = newRow.insertCell();
        for (let j in data.sumByClass) {
          if (data.sumByClass[j].seq === i) {
            newCell.innerHTML = $cmmn.convertToCurrency(data.sumByClass[j].sum);
            break;
          } else {
            newCell.innerHTML = "-";
          }
        }
      }
      //총계
      newRow.insertCell().innerHTML = $cmmn.convertToCurrency(data.sum);
      $("#approvalTable > tbody > tr.table-active").find("td:eq(6)").text($cmmn.convertToCurrency(data.sum));
    }
  }

  /**
  * 결제 내역 테이블 row event handler
  * @param {Object} row : 결제 내역 테이블 row
  */
  const histRowEvtHandler = function (row) {
    $(row).on("click", function (event) {
      if ($(this).hasClass("table-active")) {
        $(this).removeClass("table-active");
        changeForm("save");
      } else {
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
  const changeForm = function (type) {
    if (type === "update") {
      $("#histSave").css("display", "none");
      $("#histUpdate").css("display", "");
    } else {
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
  const selectPayhistInfo = function (seq) {
    //1.해당 seq의 결제 내역 조회
    $.ajax({
      type: "GET",
      url: "/common/searchInfo",
      dataType: "json",
      data: {
        seq: seq,
      },
      contentType: "application/json",
      success: function (data) {
        const result = data.result
        if (data.CODE === "SUCCESS") {
          //2.결제 내역 Form에 정보 삽입
          const optionList = $(`#histForm select[name=cardSelect]`).children("option");
          $("#histForm input[name=seq]").val(result.seq);
          $("#histForm input[name=useHist]").val(result.useHist);
          $("#histForm input[name=money]").val($cmmn.convertToCurrency(result.money));
          $(`#histForm select[name=classSeq] option:eq(${result.classInfo.seq})`)
            .prop("selected", true);
          $("#histForm input[name=useDate]").val($cmmn.formatDate(result.useDate));
          for (let i = 1; i < optionList.length; i++) {
            const cardComp = optionList[i].innerText.split(" ")[0];
            const cardNum = optionList[i].innerText.split(" ")[1];
            if (cardComp === result.cardComp && cardNum === result.cardNum) {
              $(optionList[i]).prop("selected", true);
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
   * 기간별 통계 date picker 설정
   */
  const initStatsDatePicker = function () {
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
      url: "/common/searchTotalSumList",
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
  const paintTeamChart = function (yearMonth, data) {
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
  const paintClassChart = function (yearMonth, data) {
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

  /**
   * 사용내역 검색 Form 설정
   */
  const initSearchForm = function () {
    /**
     * select 생성
     * @param {array} list : Dept list
     * @param {Object} select : select Object
     */
    function paintSelect(select, deptList) {
      $(select).find('option').remove();
      addDeptOption(select);

      if (!$cmmn.isNullorEmpty(deptList)) {
        for (let i = 0; i < deptList.length; i++) {
          addDeptOption(select, deptList[i]);
        }
      }
    }

    /**
     * option 추가
     * @param {Object} select : select Object
     * @param {JSON} deptInfo : 부서 or 팀 정보
     */
    function addDeptOption(select, deptInfo) {
      const option = document.createElement('option');
      if ($cmmn.isNullorEmpty(deptInfo)) {
        option.text = "전체";
        option.value = "ALL";
      } else {
        option.text = deptInfo.deptNm;
        option.value = deptInfo.deptCd;
      }
      select.options.add(option);
    }

    //1.부서 및 팀 select 설정
    $.ajax({
      type: "GET",
      url: "/admin/searchTopDeptInfo",
      dataType: "json",
      async: false,
      success: function (data) {
        if (data.CODE === "SUCCESS") {
          const deptSelect = document.querySelector("#deptSelect");
          const secDeptList = data.result.lower;

          //1)부서 select 설정
          paintSelect(deptSelect, secDeptList);

          //2)부서 select 변경 시 팀 select 생성
          $("#deptSelect").on('change', function () {
            const teamSelect = document.querySelector("#teamSelect");
            const value = $(this).val();
            if (value !== "ALL") {
              $.each(secDeptList, function (idx, row) {
                //team option 추가 function
                function searchAndAddTeam(select, deptList) {
                  if (deptList.length === 0) {
                    return false;
                  } else {
                    for (let i = 0; i < deptList.length; i++) {
                      if (deptList[i].lower.length === 0) {
                        addDeptOption(select, deptList[i]);
                      } else {
                        searchAndAddTeam(select, deptList[i].lower);
                      }
                    }
                  }
                }

                if (secDeptList[idx].deptCd === value) {
                  $(teamSelect).find('option').remove();
                  addDeptOption(teamSelect);
                  searchAndAddTeam(teamSelect, secDeptList[idx].lower);
                  return false;
                }
              });
            } else {
              paintSelect(teamSelect);
            }
          });
        } else {
          alert("부서 조회에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
        }
      },
      error: function () {
        alert("부서 조회에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
      }
    });

    //2.기간 Date picker
    const now = new Date;
    const startYm = $cmmn.formatDate(new Date(now.getFullYear(), now.getMonth() - 3), 'YYYY-mm');
    const endYm = $cmmn.formatDate(new Date(now.getFullYear(), now.getMonth()), 'YYYY-mm');
    $("#datepicker").val(`${startYm} - ${endYm}`);

    $('#datepicker').daterangepicker({
      "minViewMode": "month",
      format: 'YYYY-MM',
      "autoApplyClickedRange": true,
      "alwaysShowCalendars": true,
    }, function (start, end, label) { });

    //3.Search Input 이벤트
    $("#listSearch").on('keyup', function () {
      $("#approvalTable > tbody > tr").hide();
      const value = $(this).val().toLowerCase();
      $("#approvalTable > tbody > tr").filter(function () {
        return $(this).text().toLowerCase().indexOf(value) > -1;
      }).show();
      document.querySelector("#listTotCnt").innerText = $("#approvalTable > tbody > tr:visible").length;
    });

    //4.결재 건 테이블 조회
    $("#histSubmit").trigger("click");

    //5.초기 버튼 disable 처리
    disableBtn();
  }

  /**
    * 버튼 disable 처리
    * @param {boolean} isDisable : disable 처리 여부
    */
  const disableBtn = function(isDisable) {
    if($cmmn.isNullorEmpty(isDisable)){
        isDisable = true;
    }
    isDisable ? $("#deleteRow").css("display", "none") : $("#deleteRow").css("display", "");
    isDisable ? $("#submitHist").css("display", "none") : $("#submitHist").css("display", "");
    isDisable ? $("#atchAddBtn").css("display", "none") : $("#atchAddBtn").css("display", "");
    isDisable ? $("#atchDelBtn").css("display", "none") : $("#atchDelBtn").css("display", "");
    $("#histForm :input").attr("disabled", isDisable);
    $("#histForm select").attr("disabled", isDisable);
  }

  /**
    * 결제 내역 form event handler
    */
  const initHistForm = function () {
    //분류 select
    const classSelect = document.querySelector("#classSelect");
    $cmmn.selectClassList(true, function (json) {
      for (let i in json) {
        const option = document.createElement('option');
        option.text = json[i].classNm;
        option.value = json[i].seq;
        classSelect.options.add(option);
      }
    });

    //카드 select
    const cardSelect = document.querySelector("#cardSelect");
    selectCardList();

    //결제 내역 form 금액 쉼표 표시
    document.querySelector(".input-money").addEventListener("keyup", function (e) {
      $(this).val($cmmn.convertToCurrency(this.value));
    });
  }

  /**
   * 해당연월 수령한 카드 리스트 조회 AJAX
   * @param {String} yearMonth : 작성연월
   */
  const selectCardList = function (yearMonth) {
    if ($cmmn.isNullorEmpty(yearMonth)) {
      yearMonth = $cmmn.formatDate("", "YYYY-mm");
    }

    $.ajax({
      type: "GET",
      url: "/common/cardList",
      success: function (data) {
        if (data.CODE === "SUCCESS") {
          const cardSelect = document.querySelector("#cardSelect");
          const json = data.result;
          $("#cardSelect option").not("option:first").remove();

          for (let i in json) {
            const option = document.createElement('option');
            option.text = `${json[i].cardComp} ${json[i].cardNum}`;
            option.value = json[i].seq;
            cardSelect.options.add(option);
          }
        } else {
          alert("카드 목록 조회에 싪패했습니다. 관리자에게 문의해주시기 바랍니다.");
        }
      },
      error: function () {
        return alert("카드 목록 조회에 싪패했습니다. 관리자에게 문의해주시기 바랍니다.");
      }
    });
  }

  /**
   * 결제 내역 table
   */
  const initHistTable = function () {
    // 결제 내역 빈 페이지 생성
    paintPayhistTable();

    //체크박스 전체 설정
    document.querySelector("#checkAll").addEventListener('click', function () {
      $(".table-check").prop('checked', $(this).prop('checked'));
    });
  }

  return {
    init: init,
  }
}());
