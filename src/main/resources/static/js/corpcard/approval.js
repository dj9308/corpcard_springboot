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

    });
    //첨부파일 btn
    $("#histAtchBtn").on("click", function(){
        $cmmn.initHistToast("liveToast");
    });
    //확인(결재 완료) btn
    $("#approvalHist").on("click", function(){

    })
    //반려 btn
    $("#rejectHist").on("click", function(){

    })
  }

  /**
   * 결재 건 검색 Form
   */
  const initSearchForm = function(){
    //부서 select
    const deptSelect = document.querySelector("#deptSelect");
    $.ajax({
          type: "GET",
          url: "/approval/searchDept",
          data: {
            USER_ID: userId
          },
          success: function (data) {
            if (data.CODE === "SUCCESS") {
              const json = data.result;
              for (let i in json) {
                const option = document.createElement('option');
                option.text = json[i].deptNm;
                option.value = json[i].seq;
                deptSelect.options.add(option);
              }
            } else {
              alert("부서 목록 조회에 싪패했습니다. 관리자에게 문의해주시기 바랍니다.");
            }
          },
          error: function () {
            return alert("부서 목록 조회에 싪패했습니다. 관리자에게 문의해주시기 바랍니다.");
          }
        });

    //팀 select
    const teamSelect = document.querySelector("#teamSelect");
    $.ajax({
          type: "GET",
          url: "/approval/searchTeam",
          data: {
            DEPT_SEQ : deptSeq;
          },
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
  }

  /**
   * 결제내역 리스트 table
   */
  const initHistTable = function(){

  }
  /**
   * 첨부 파일 toast
   */
  const initAtchToast = function(data){
        //1.기존 첨부파일 list 초기화
        $("#atchList > li").remove();
        $("#atchEmpty").css("display", "");
        $("#atchCnt").text("0");

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

    return {
      init: init,
    }
  }());