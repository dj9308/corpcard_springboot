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
                const option = document.createElement('option');
                option.text = "전체";
                option.value = "ALL";
                select.options.add(option);
            }

            for(let i =0;i<list.length;i++){
                const option = document.createElement('option');
                option.text = list[i].deptNm;
                option.value = list[i].seq;
                select.options.add(option);
            }
        }
        select.options[0].selected = isFixed;
        select.disabled = isFixed;
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

    //부서 및 팀 select
    const deptSelect = document.querySelector("#deptSelect");
    const teamSelect = document.querySelector("#teamSelect");
    $.ajax({
          type: "GET",
          url: "/approval/searchDeptInfo",
          data: {
            USER_ID: userId
          },
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
                  paintSelect([data.result.upperDeptInfo], deptSelect, true);
                  paintSelect([deptInfo], teamSelect, true);
              }else if(level === 2){    //부서장
                paintSelect([deptInfo], deptSelect, true);
                paintSelect(deptInfo.lower, teamSelect, false);
              }else if(level ==3){      //본부장
                deptSelect.options[0].innerText = deptInfo.deptNm;
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

                $.each(deptList, function(idx, row){
                    if(deptList[idx].seq == value){
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