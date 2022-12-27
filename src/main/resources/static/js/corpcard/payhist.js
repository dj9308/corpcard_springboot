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
  let wrtYm                                                 // 작성연월

  /**
   * init
   */
  const init = function () {
    initBtnEvt();           //버튼 이벤트
    initStatsDatePicker();  //기간별 통계 date picker
    paintCharts();          //차트 조회
    initHistForm();         //결제 내역 Form
    initHistTable();        //결제 내역 Table
    initAtchToast();        //첨부 파일 toast
  }

  /**
  * 버튼 이벤트
  */
  const initBtnEvt = function () {
    //결제 내역 작성 or 수정
    $("#histForm").on("submit", function (event) {
      const histMoney = document.querySelector("#histMoney").value;
      if (new RegExp(/[^0-9,]/, "g").test(histMoney)) {
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

    //결제 내역 초기화 btn
    $("#histReset").on("click", function (event) {
      changeForm("save");
    });

    //첨부파일 btn
    $("#histAtchBtn").on("click", function (event) {
      const toastElList = [].slice.call(document.querySelectorAll('.toast'))
      const toastList = toastElList.map(function (toastEl) {
        return new bootstrap.Toast(toastEl)
      });
      if ($("#liveToast").hasClass("show")) {
        toastList.forEach(toast => toast.hide());
      } else {
        toastList.forEach(toast => toast.show());
      }
    });

    //PDF btn
    $("#histPDFBtn").on("click", function (event) {
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
          if (data.CODE === "SUCCESS") {
            makePDF(data.result);
          } else if (data.CODE === "EMPTY") {
            return alert("해당 연월의 결제 내역이 없습니다.");
          }
        },
        error: function () {
          return alert("결제 내역 조회에 싪패했습니다. 관리자에게 문의해주시기 바랍니다.");
        }
      });
    });

    //CSV btn
    $("#histCSVBtn").on("click", function (event) {
      const array = [];
      const rows = document.querySelector("#histTable").rows;
      let cells, t;
      //1.table row data 추출
      for (let i = 0; i < rows.length - 1; i++) {
        cells = rows[i].cells;
        t = [];
        for (let j = 1, jLen = cells.length; j < jLen; j++) {
          t.push(cells[j].textContent);
        }
        array.push(t);
      }
      //2.CSV 파일 생성 및 저장
      let a = "";
      $.each(array, function (i, row) {
        $.each(row, function (j, cell) {
          if (cell.includes(",")) {
            cell = cell.replace(",", "");
          }
          a += cell;
          a += ",";
        });
        a = a.slice(0, -1);
        a += "\r\n";
      });
      //        const submistInfo = data.list[0].usehistSubmitInfo;
      const downloadLink = document.createElement("a");
      const blob = new Blob(["\ufeff" + a], { type: "text/csv;charset=utf-8" });
      downloadLink.href = URL.createObjectURL(blob);
      downloadLink.download = `법인카드 결제내역.csv`;
      document.body.appendChild(downloadLink);
      downloadLink.click();
      document.body.removeChild(downloadLink);
    });

    //제출 btn
    $("#submitHist").on("click", function () {
      if (confirm("제출 하시겠습니까?")) {
        if ($("#histTable > tbody > tr").length === 0) {
          return alert("저장된 결제 내역이 없습니다.");
        }
        $.ajax({
          type: "PATCH",
          url: "/payhist/updateState",
          dataType: "json",
          data: {
            WRITER_ID: userId,
            WRT_YM: wrtYm,
          },
          success: function (data) {
            if (data.CODE === "SUCCESS") {
              selectPayhistList();
            } else if (data.CODE === "ERR") {
              return alert("제출에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
            }
          },
          error: function () {
            return alert("제출에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
          }
        });
      }
    });

    //Table row 삭제 btn
    document.querySelector("#deleteRow").addEventListener("click", function (e) {
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
          WRITER_ID: userId,
          WRT_YM: wrtYm,
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
        WRITER_ID: userId,
        WRT_YM: wrtYm,
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
            paintAtchList(data.result);
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
  * 결제 내역 조회
  */
  const selectPayhistList = function () {
    //Form & 첨부파일 disable 처리
    function disableFormAndAtch(isDisable) {
      $("#histForm :input").attr("disabled", isDisable);
      $("#histForm select").attr("disabled", isDisable);
      $("#atchAddBtn").attr("disabled", isDisable);
      $("#atchDelBtn").attr("disabled", isDisable);
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
          //3)제출 상태 조회 및 disable 처리
          const stateInfo = data.result.submitInfo.stateInfo;
          $("#stateNm").text(stateInfo.stateNm);
          if (stateInfo.stateCd === "B" || stateInfo.stateCd === "C") {
            disableFormAndAtch(true);
          } else {
            disableFormAndAtch(false);
          }
        } else if (data.CODE === "EMPTY") {
          const newCell = tbody.insertRow().insertCell();
          disableFormAndAtch(false);
          document.querySelector("#stateNm").innerText = "제출 전";
          document.querySelector("#listTotCnt").innerText = "0";
          newCell.setAttribute('colspan', '15');
          newCell.classList.add("fw-bold");
          newCell.innerText = "해당 연월의 결제 내역이 없습니다.";
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
  const paintTable = function (tbody, data) {
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
      //row 이벤트 핸들러
      if (data.submitInfo.stateInfo.stateCd === "A" || data.submitInfo.stateInfo.stateCd === "D") {
        rowEvtHandler(newRow);
      }
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
  }

  /**
  * 결제 내역 테이블 row event handler
  * @param {Object} row : 결제 내역 테이블 row
  */
  const rowEvtHandler = function (row) {
    $(row).on("click", function () {
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
      url: "/payhist/searchInfo",
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
          $("#histForm input[name=useDate]").val($cmmn.formatDate(result.useDate, "mm.dd"));
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
  * 결제 내역 form event handler
  */
  const initHistForm = function () {
    //분류 select
    const classSelect = document.querySelector("#classSelect");
    selectClassList(true, function (json) {
      for (let i in json) {
        const option = document.createElement('option');
        option.text = json[i].classNm;
        option.value = json[i].seq;
        classSelect.options.add(option);
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
  const setLimitHistDate = function () {
    const chosenDate = document.querySelector("#payhistMonth").value;
    const dateInput = document.querySelector("#useDate");
    const date = new Date(chosenDate);
    const year = date.getFullYear();
    const month = date.getMonth() + 1;

    dateInput.setAttribute('min', `${year}-${month < 10 ? `0${month}` : month}-01`);
    dateInput.setAttribute('max', `${year}-${month < 10 ? `0${month}` : month}-${new Date(year, month, 0).getDate()}`);
  }

  /**
   * 기간별 통계 date picker
   */
  const initStatsDatePicker = function () {
    const now = new Date;

    $('#datepicker').daterangepicker({
      "autoApply": true,
      "alwaysShowCalendars": true,
      "startDate": new Date(now.setMonth(now.getMonth() - 6)),
      "endDate": now
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
  const initHistTable = function () {
    const payhistMonth = document.querySelector("#payhistMonth");

    //결제 내역 현재 연월 표현
    payhistMonth.value = $cmmn.formatDate("", "YYYY-mm");
    wrtYm = payhistMonth.value;
    selectPayhistList();

    //연월 변경 시 결제 내역 조회
    $(payhistMonth).on('change', function () {
      wrtYm = this.value;
      selectPayhistList();
      initAtchToast();
    });

    //체크박스 전체 설정
    document.querySelector("#checkAll").addEventListener('click', function () {
      $(".table-check").prop('checked', $(this).prop('checked'));
    });
  }

  /**
   * 첨부파일 toast 설정
   */
  const initAtchToast = function () {
    //1.기존 첨부파일 list 초기화
    $("#atchList > li").remove();
    $("#atchEmpty").css("display", "");
    $("#atchCnt").text("0");

    //2.해당 연월의 첨부파일 리스트 조회
    $.ajax({
      type: "GET",
      url: "/payhist/searchAtchList",
      dataType: "json",
      data: {
        WRITER_ID: userId,
        WRT_YM: wrtYm,
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

  /**
   * 첨부파일 리스트 생성
   * @param {Array} data : 첨부파일 정보 Array
   */
  const paintAtchList = function (data) {
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
    console.dir($(".atch-check"));
    console.dir($(".atch-check").find('input:checked'));
    $(".atch-check:checked").each(function (index) {
      $(this).parent("li").remove();
    });
    if ($("#atchList > li").length == 0) {
      $("#atchEmpty").css("display", "");
      $("#atchCnt").text("0");
    }
  }

  /**
   * 분류 목록 조회
   * @param {boolean} isAsync : 비동기 여부
   * @param {function} callback : Callback function
   */
  const selectClassList = function (isAsync, callback) {
    $.ajax({
      type: "GET",
      url: "/base/classList",
      async: isAsync,
      success: function (data) {
        if (data.CODE === "SUCCESS") {
          callback(data.result);
        } else {
          alert("분류 목록 조회에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
        }
      },
      error: function () {
        return alert("분류 목록 조회에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
      }
    });
  }

  /**
   * PDF 생성
   * @param {JSON} data : 결제 금액 리스트 및 합계
   */
  const makePDF = function (data) {
    const documentDefinition = {
      content: [
        {
          text: '신용카드 결제금액 보고서',
          style: 'title'
        },
        {
          style: 'table_right',
          table: {
            widths: [80, 80],
            body: [
              [{
                text: '기안자',
                style: 'th'
              },
              {
                text: '확인자',
                style: 'th'
              }],
              [{
                text: data.submitInfo.writerNm,
                style: 'default'
              },
              {
                text: data.submitInfo.checkerNm || '',
                style: 'default'
              }]
            ],
            alignment: "right"
          }
        },
        {
          text: '단위 : 원',
          style: 'unit'
        },
        {
          table: {
            widths: [120, "*", 120, "*"],
            body: [
              [
                {
                  text: '년/월',
                  style: 'th',
                },
                {
                  text: data.submitInfo.wrtYm,
                  style: 'default',
                },
                {
                  text: '결재 완료 날짜',
                  style: 'th',
                },
                {
                  text: data.submitInfo.approveDate || '',
                  style: 'default',
                }],
              [{
                text: '총 액',
                style: 'th',
              }, {
                text: $cmmn.convertToCurrency(data.sum),
                style: 'money',
                colSpan: 3,
              }],
            ]
          }
        },
        {
          columns: [
            {
              width: 'auto',
              text: '■ 결제 내역',
              style: "h3",
            },
          ],
          // optional space between columns
          columnGap: 10
        },
        {
          table: {
            widths: [20, 50, 80, 70, "*", 70],
            body: [
              [{
                text: 'No.',
                style: 'th',
              },
              {
                text: '거래일시',
                style: 'th',
              },
              {
                text: '카드정보',
                style: 'th',
              },
              {
                text: '분류',
                style: 'th',
              },
              {
                text: '사용처',
                style: 'th',
              },
              {
                text: '금액(원)',
                style: 'th',
              }],
            ],
          }
        },
      ],
      footer: function (currentPage, pageCount) {
        return {
          margin: 10,
          columns: [{
            fontSize: 9,
            text: [{
              text: '--------------------------------------------------------------------------' +
                '\n',
              margin: [0, 20]
            },
            {
              text: '' + currentPage.toString() + ' of ' +
                pageCount,
            }
            ],
            alignment: 'center'
          }]
        };
      },
      styles: {
        table_right: {
          margin: [335, 15, 0, 15],
        },
        th: {
          fontSize: 11,
          bold: true,
          margin: [0, 0, 0, 0],
          alignment: 'center',
          fillColor: '#eaeaea',
        },
        title: {
          fontSize: 20,
          bold: true,
          margin: [0, 0, 0, 0],
          alignment: 'center'
        },
        h3: {
          fontSize: 13,
          bold: true,
          margin: [0, 10, 0, 2],
        },
        unit: {
          fontSize: 8,
          margin: [0, 0, 0, 0],
          alignment: 'right',

        },
        money: {
          fontSize: 11,
          margin: [0, 0, 0, 0],
          alignment: 'right'
        },
        default: {
          fontSize: 11,
          margin: [0, 0, 0, 0],
          alignment: 'center'
        },
      },
      pageSize: 'A4',
      pageOrientation: 'portrait',
    };

    //분류별 합계
    let sumList = [];
    selectClassList(false, function (classList) {
      const rowList = [];
      let pi = 0;
      let pj = 0;
      while (pi < classList.length && pj < data.sumByClass.length) {
        let list = [];
        if (classList[pi].seq > data.sumByClass[pj].seq) {
          pj++;
        } else if (classList[pi].seq < data.sumByClass[pj].seq) {
          rowList.push({
            text: classList[pi].classNm,
            style: 'th'
          });
          rowList.push({
            text: "0",
            style: 'money'
          });
          pi++;
        } else {
          rowList.push({
            text: classList[pi].classNm,
            style: 'th'
          });
          rowList.push({
            text: $cmmn.convertToCurrency(data.sumByClass[pj].sum),
            style: 'money'
          });
          pi++;
          pj++;
        }
        if (rowList.length === 4) {
          list = list.concat(rowList);
          documentDefinition.content[3].table.body.push(list);
          rowList.length = 0;
        }
      }
      for (pi; pi < classList.length; pi++) {
        let list = [];
        rowList.push({
          text: classList[pi].classNm,
          style: 'th'
        });
        rowList.push({
          text: "0",
          style: 'money'
        });
        if (rowList.length === 4) {
          list = list.concat(rowList);
          documentDefinition.content[3].table.body.push(list);
          rowList.length = 0;
        }
      }
    });
    //결제 내역
    for (let i = 0; i < data.list.length; i++) {
      const rowData = data.list[i];
      const cardNum = rowData.cardNum.split("-")[3];
      documentDefinition.content[5].table.body.push([{
        text: (i + 1).toString(),
        style: 'default',
      },
      {
        text: $cmmn.formatDate(rowData.useDate, "mm.dd"),
        style: 'default',
      },
      {
        text: `${rowData.cardComp} ${cardNum}`,
        style: 'default',
      },
      {
        text: rowData.classInfo.classNm,
        style: 'default',
      },
      {
        text: rowData.useHist,
        style: 'default',
      },
      {
        text: $cmmn.convertToCurrency(rowData.money),
        style: 'money',
      }])
    }


    const pdf_name = 'pdf파일 만들기.pdf'; // pdf 만들 파일의 이름
    pdfMake.createPdf(documentDefinition).download(pdf_name);
  }

  return {
    init: init,
  }
}());
