/************************************************************
 파일명    :    approval.js
 설명     : 결재 페이지 JavaScript
 수정일        수정자        Version        설명
 ----------    ----------    ----------    ----------
 2022.12.00    설동재        1.0            최초 생성
 ************************************************************/

const $approval = (function () {
  'use strict';

  const userId = document.querySelector('#userId').value;   // 사용자 ID

  /**
   * init
   */
  const init = function () {
    initBtnEvt();           //버튼 이벤트
    initSearchForm();       //결재 건 검색 Form
    initHistTable();        //결제내역 리스트 table
    initAtchToast();        //첨부 파일 toast
  }

  /**
   * 버튼 event
   */
  const initBtnEvt = function () {
    //결재 건 조회 btn
    $("#approvalSubmit").on("click", function(){
        selectApprovalList();
    });
    //첨부파일 btn
    $("#histAtchBtn").on("click", function(){
        $cmmn.initHistToast("liveToast");
    });
    //확인(결재 완료) btn
    $("#approvalHist").on("click", function(){
        updateState(3, uptStateBtn(true));
    })
    //반려 btn
    $("#rejectHist").on("click", function(){
        updateState(4, uptStateBtn(true));
    })
  }

 /**
  * 결재 건 목록 조회
  */
  const selectApprovalList = function() {
    //1.form data 생성
    const data = $cmmn.serializeObject("approvalForm");
    data.dept = $("#deptSelect").val();
    data.team = $("#teamSelect").val();
    data.userId = userId;

    //2.결재 건 목록 조회
    $.ajax({
      type: "GET",
      url: "/approval/searchList",
      data: data,
      success: function (data) {
        if (data.CODE === "SUCCESS") {
          paintApprovalTable(data.result);
        }else if(data.CODE === "EMPTY"){
            paintApprovalTable();
        }else{
            paintApprovalTable();
            alert("분류 목록 조회에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
        }
      },
      error: function () {
        paintApprovalTable();
        alert("분류 목록 조회에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
      }
    });
  }

 /**
  * 결재 건 table 생성
  * @param {JSON} data : 결재 건 list
  */
  const paintApprovalTable = function (data) {
    const tbody = document.querySelector("#approvalTable>tbody");
    //1.table 초기화
    $cmmn.emptyTable("approvalTable");
    //2.table row 생성
    if($cmmn.isNullorEmpty(data)){
      const newCell = tbody.insertRow().insertCell();
      newCell.setAttribute('colspan', '8');
      newCell.classList.add("fw-bold");
      newCell.innerText = "해당 조건의 결재 건 내역이 없습니다.";
    }else{
        for (let i = 0; i < data.length; i++) {
            const newRow = tbody.insertRow();
            const input = document.createElement('input');
            input.value = data[i].seq.toString();
            input.type = "hidden";
            input.name = "seq";
            //No.
            newRow.insertCell().innerHTML = `${input.outerHTML}${(i+1).toString()}`;
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
            //상태
            newRow.insertCell().innerHTML = data[i].stateInfo.stateNm;
            //합계
            newRow.insertCell().innerHTML = $cmmn.convertToCurrency(data[i].sum);
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
            const seq = $(this).find("input[name=seq]").val();
            $("#approvalTable > tbody > tr").removeClass("table-active");
            $(this).addClass("table-active");
            selectPayhistList(seq);
            selectAtchList(seq);
        }
      });
    }

   /**
    * 결제 내역 목록 조회
    * @param {String} seq : 제출 내역 seq
    */
    const selectPayhistList = function(seq){
        $.ajax({
              type: "GET",
              url: "/approval/searchPayhistList",
              dataType: "json",
              data: {
                SEQ : seq
              },
              success: function (data) {
                if(data.CODE == "SUCCESS"){
                    paintPayhistTable(data.result);
                }else {
                    paintPayhistTable();
                    alert("결제 내역 조회에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
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
    const paintPayhistTable = function (data){
        const tbody = document.querySelector("#histTable > tbody");

        $cmmn.emptyTable("histTable");  //테이블 초기화

        if($cmmn.isNullorEmpty(data)){
          const newCell = tbody.insertRow().insertCell();
          newCell.setAttribute('colspan', '14');
          newCell.classList.add("fw-bold");
          newCell.innerText = "해당 결재 건의 법인카드 사용 내역이 없습니다.";
            return false;
        }else{
        //1.총 건수 설정
                      document.querySelector("#listTotCnt").innerText = data.list.length;

                      //2.결제 내역 row 생성
                      for (let i = 0; i < data.list.length; i++) {
                        const rowData = data.list[i];
                        const newRow = tbody.insertRow();
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
                      }

                      //3.총계 row 생성
                      const newRow = tbody.insertRow();
                      const totalCell = newRow.insertCell();
                      newRow.classList.add("fw-bold");
                      totalCell.setAttribute('colspan', '3');
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
    }

  /**
   * 첨부파일 리스트 조회
   * @param {String} seq : 제출 내역 seq
   */
    const selectAtchList = function(seq){
        $.ajax({
          type: "GET",
          url: "/payhist/searchAtchList",
          dataType: "json",
          data: {
            SEQ : seq
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
   * 결재 건 검색 Form
   */
  const initSearchForm = function(){
    /**
     * select 생성
     * @param {array} list : Dept list
     * @param {Object} select : select Object
     * @param {boolean} isFixed : 고정 여부
     */
    function paintSelect(list, select, isFixed){
        if(list.length === 0){
            const option = document.createElement('option');
            option.text = "---";
            select.options.add(option);
        }else{
            if(select.id === "teamSelect" && list.length > 1){
                paintTotalOption(select);
            }
            for(let i =0;i<list.length;i++){
                const option = document.createElement('option');
                option.text = list[i].deptNm;
                option.value = list[i].deptCd;
                select.options.add(option);
            }
        }
        select.options[0].selected = isFixed;
        select.disabled = isFixed;
    }

    /**
     * select 생성
     * @param {Object} select : select Object
     */
    function paintTotalOption(select){
        const option = document.createElement('option');
        option.text = "전체";
        option.value = "ALL";
        select.options.add(option);
    }

    /**
     * 부서 리스트 생성
     * @param {JSON} deptInfo : 부서 json
     * @return {array} result : 부서 리스트
     */
    function makeDeptList(deptInfo){
        const lowerList = deptInfo.lower;
        let result = [];
        for(let i=0;i<lowerList.length;i++){
            result = result.concat(lowerList[i].lower);
        }
        return result;
    }

    //1.부서 및 팀 select
    const deptSelect = document.querySelector("#deptSelect");
    const teamSelect = document.querySelector("#teamSelect");
    $.ajax({
          type: "GET",
          url: "/approval/searchDeptInfo",
          data: {
            USER_ID: userId
          },
          async: false,
          success: function (data) {
            if (data.CODE === "SUCCESS") {
              let deptInfo = data.result.deptInfo;
              let teamList = deptInfo.lower;
              let level = 0;
              let deptList = [];
              while(!$cmmn.isNullorEmpty(teamList)){
                if(teamList.length !== 0){
                  teamList = teamList[0].lower;
                }else{
                  teamList = null;
                }
                level++;
              }
              if(level === 1){          //팀장
                  $(teamSelect).find('option').remove();
                  paintSelect([data.result.upperDeptInfo], deptSelect, true);
                  paintSelect([deptInfo], teamSelect, true);
              }else if(level === 2){    //부서장
                paintSelect([deptInfo], deptSelect, true);
                paintSelect(deptInfo.lower, teamSelect, false);
              }else if(level ==3){      //본부장
                const option = document.createElement('option');
                option.text = deptInfo.deptNm;
                option.value = "ALL";
                deptSelect.options.add(option);
                deptList = deptInfo.lower;
                paintSelect(deptList, deptSelect, false);
              }else{                    //CEO
                deptList = makeDeptList(deptInfo);
                paintSelect(deptList, deptSelect, false);
              }

              $("#deptSelect").on('change', function(){
                const teamSelect = document.querySelector("#teamSelect");
                const options = $(teamSelect).find('option').map(function() {return $(this).val();}).get();
                const value = $(this).val();
                //팀 select 초기화
                if(options.length > 0){
                    $(teamSelect).find('option').remove();
                }
                if(value === "ALL"){
                    paintTotalOption(teamSelect);
                }
                $.each(deptList, function(idx, row){
                    if(deptList[idx].deptCd == value){
                      paintSelect(deptList[idx].lower, teamSelect, false);
                      return false;
                    }
                });
              });
            } else {
              alert("부서 목록 조회에 싪패했습니다. 관리자에게 문의해주시기 바랍니다.");
            }
          },
          error: function () {
            return alert("부서 목록 조회에 싪패했습니다. 관리자에게 문의해주시기 바랍니다.");
          }
        });

    //2.기간 date picker
    const now = new Date;
    const startDate = $cmmn.formatDate(new Date(now.getFullYear(), now.getMonth() -1), 'YYYY-mm');
    const endDate = $cmmn.formatDate(new Date(now.getFullYear(), now.getMonth()), 'YYYY-mm');
    $("#datepicker").val(`${startDate} - ${endDate}`);

    $('#datepicker').daterangepicker({
      "minViewMode": "month",
      format: 'YYYY-MM',
      "autoApplyClickedRange": true,
      "alwaysShowCalendars": true,
    }, function (start, end, label) { });


    //3.조회 버튼 trigger
    $("#approvalSubmit").trigger("click");
  }

  /**
   * 결제내역 리스트 table
   */
  const initHistTable = function(){
    paintPayhistTable();    //테이블 init


  }
  /**
   * 첨부 파일 toast
   */
  const initAtchToast = function(data){


        //2.선택한 결재 건의 첨부파일 리스트 조회
        if(!$cmmn.isNullorEmpty(data)){
            $.ajax({
              type: "GET",
              url: "/payhist/searchAtchList",
              dataType: "json",
              data: {
                WRITER_ID: data.userId,
                WRT_YM: data.wrtYm,
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
     * 제출 상태 변경 AJAX
     * @param {number} seq : 제출 상태 시퀀스
     * @param {function} callback : Callback function
     */
    const updateState = function (seq, callback) {
      $.ajax({
        type: "PATCH",
        url: "/payhist/updateState",
        dataType: "json",
        data: {
          WRITER_ID: userId,
          WRT_YM: wrtYm,
          SEQ: seq,
        },
        success: function (data) {
          if (data.CODE === "SUCCESS") {
            callback();
          } else if (data.CODE === "ERR") {
            return alert("제출 상태 변경에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
          }
        },
        error: function () {
          return alert("제출 상태 변경에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
        }
      });
    }

    /**
     * 확인 및 삭제 버튼 hidden 처리
     * @params {boolean} isDisable : hidden 처리 여부
     */
    const uptStateBtn = function(isDisable) {
      isDisable ? $("#approvalHist").css("display", "none") : $("#deleteRow").css("display", "");
      isDisable ? $("#rejectHist").css("display", "none") : $("#submitHist").css("display", "");
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
        const aTag = document.createElement("a");
        list.className += "list-group-item";
        aTag.innerText = data[i].fileNm;
        aTag.className += "text-decoration-none"
        aTag.setAttribute("href", "http://" + `${location.host}/payhist/downloadAtch?seq=${data[i].usehistSubmitInfo.seq}&fileNm=${data[i].fileNm}&fileSeq=${data[i].seq}`);
        list.appendChild(aTag);
        document.querySelector("#atchList").appendChild(list);
      }
    }

    return {
      init: init,
    }
  }());