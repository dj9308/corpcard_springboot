/************************************************************
 파일명    :    auth.js
 설명     : 관리자 권한 관리 페이지 JavaScript
 수정일        수정자        Version        설명
 ----------    ----------    ----------    ----------
 2023.01.00    설동재        1.0            최초 생성
 ************************************************************/

const $auth = (function () {
  'use strict';

  const userId = document.querySelector('#userId').value;   // 사용자 ID

  /**
   * init
   */
  const init = function () {
    initBtnEvt();           //버튼 이벤트
    initAuthTable();        //권한 테이블 설정
  }

  /**
  * 버튼 이벤트
  */
  const initBtnEvt = function () {
    //search input
    $("#listSearch").on('keyup', function () {
      $("#authTable > tbody > tr").hide();
      const value = $(this).val().toLowerCase();
      $("#authTable > tbody > tr").filter(function () {
        return $(this).text().toLowerCase().indexOf(value) > -1;
      }).show();
      document.querySelector("#listTotCnt").innerText = $("#authTable > tbody > tr:visible").length;
    });

    //추가 버튼
    $("#addAuth").on('click', function () {
      $("#addSearch").val("");
      selectManagerList("N");
    });

    //삭제 버튼
    $("#deleteAuth").on('click', function () {
      if (confirm("선택한 사용자의 관리자 권한을 삭제하겠습니까?")) {
        //1.체크된 row 조회
        const idList = [];
        $('.table-check').each(function (index) {
          if ($(this).is(":checked") && $(this).is(":visible")) {
            idList.push($(this).parent().parent().find("td:eq(6)").text());
          }
        });
        if (idList.length === 0) {
          return alert("삭제하려는 결제 내역이 없습니다.");
        }
        //2.관리자 권한 삭제
        updateAuth(idList, "N", selectManagerList);
        $("#checkAll").prop('checked', false);
        $(".table-check").prop('checked', false);
      }
    });

    //관리자 추가 모달 Search Input
    $("#addSearch").on('keyup', function () {
      $("#addManagerTable > tbody > tr").hide();
      const value = $(this).val().toLowerCase();
      $("#addManagerTable > tbody > tr").filter(function () {
        return $(this).text().toLowerCase().indexOf(value) > -1;
      }).show();
    });

    //관리자 추가 모달 추가 btn
    $("#modalAddBtn").on("click", function(){
        const userId = $("#addManagerTable > tbody > tr.table-active").find("td:eq(4)").text();
        if(!$cmmn.isNullorEmpty(userId)){
            //관리자 권한 추가
            updateAuth(userId, "Y", function(){
                $("#modalCloseBtn").trigger("click");
                selectManagerList();
            });
        }else{
            return alert("권한을 추가할 사용자를 선택하십시오.");
        }
    });
  }

  /**
   * 권한 테이블 설정
   */
  const initAuthTable = function () {
    //관리자 조회
    selectManagerList();

    //체크박스 전체 설정
    document.querySelector("#checkAll").addEventListener('click', function () {
      $(".table-check").prop('checked', $(this).prop('checked'));
    });
  }

  /**
   * 관리자 조회
   * @param {String} adminYn : 관리자여부
   */
  const selectManagerList = function (adminYn) {
    if ($cmmn.isNullorEmpty(adminYn)) {
      adminYn = "Y";
    }

    $.ajax({
      type: "GET",
      data: {
        adminYn: adminYn
      },
      url: "/admin/searchManagerList",
      success: function (data) {
        if (data.CODE === "SUCCESS") {
          if (adminYn === "Y") {
            paintTable(data.result);
          } else {
            paintUserTable(data.result);
          }
        } else if (data.CODE === "EMPTY") {
          paintTable();
        } else {
          alert("관리자 목록 조회에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
        }
      },
      error: function () {
        return alert("관리자 목록 조회에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
      }
    });
  }

  /**
   * 테이블 생성
   * @param {JSON} data : 관리자 조회 목록
   */
  const paintTable = function (data) {
    const tbody = document.querySelector("#authTable>tbody");

    $cmmn.emptyTable("authTable");

    if ($cmmn.isNullorEmpty(data)) {
      const newCell = tbody.insertRow().insertCell();
      document.querySelector("#listTotCnt").innerText = "0";
      newCell.setAttribute('colspan', '6');
      newCell.classList.add("fw-bold");
      newCell.innerText = "등록된 관리자가 없습니다.";

    } else {
      //1.총 건수 설정
      document.querySelector("#listTotCnt").innerText = data.length;
      //2.row 생성
      for (let i = 0; i < data.length; i++) {
        const rowData = data[i];
        const newRow = tbody.insertRow();
        //삭제 체크 박스
        const chkbox = document.createElement("input");
        chkbox.setAttribute("type", "checkbox");
        chkbox.setAttribute("value", rowData.seq);
        chkbox.className += "form-check-input me-1 table-check";
        newRow.insertCell().appendChild(chkbox);
        //No.
        newRow.insertCell().innerHTML = (i + 1).toString();
        //부서
        newRow.insertCell().innerHTML = $cmmn.isNullorEmpty(rowData.dept.upper) ?
          '' : rowData.dept.upper.deptNm;
        //팀
        newRow.insertCell().innerHTML = rowData.dept.deptNm;
        //직급
        newRow.insertCell().innerHTML = rowData.ofcds;
        //이름
        newRow.insertCell().innerHTML = rowData.userNm;
        //userId
        const userIdCell = newRow.insertCell();
        userIdCell.innerHTML = rowData.userId;
        userIdCell.style.display = "none";
      }
    }
  }

  /**
    * 관리자 추가 테이블 생성
    * @param {JSON} data : 사용자 조회 목록
    */
  const paintUserTable = function (data) {
    const tbody = document.querySelector("#addManagerTable>tbody");

    $cmmn.emptyTable("addManagerTable");

    if ($cmmn.isNullorEmpty(data)) {
      const newCell = tbody.insertRow().insertCell();
      newCell.setAttribute('colspan', '4');
      newCell.classList.add("fw-bold");
      newCell.innerText = "조회된 사용자가 없습니다.";
    } else {
      //2.row 생성
      for (let i = 0; i < data.length; i++) {
        const rowData = data[i];
        const newRow = tbody.insertRow();
        //부서
        newRow.insertCell().innerHTML = $cmmn.isNullorEmpty(rowData.dept.upper) ?
          '' : rowData.dept.upper.deptNm;
        //팀
        newRow.insertCell().innerHTML = rowData.dept.deptNm;
        //직급
        newRow.insertCell().innerHTML = rowData.ofcds;
        //이름
        newRow.insertCell().innerHTML = rowData.userNm;
        //userId
        const userIdCell = newRow.insertCell();
        userIdCell.innerHTML = rowData.userId;
        userIdCell.style.display = "none";
        //row 이벤트 핸들러
        $(newRow).on("click", function () {
          if ($(this).hasClass("table-active")) {
            $(this).removeClass("table-active");
          } else {
            $("#addManagerTable > tbody > tr").removeClass("table-active");
            $(this).addClass("table-active");
          }
        });
      }
    }
  }

  /**
   * 관리자 권한 변경 AJAX
   * @param {Array} userIdList : 변경할 사용자 ID Array
   * @param {String} adminYn : 관리자 권한 여부
   * @param {function} callback : Callback function
   */
  const updateAuth = function (userIdList, adminYn, callback) {
    if (!Array.isArray(userIdList)) {
      userIdList = [userIdList];
    }

    $.ajax({
      type: "PATCH",
      url: "/admin/updateAuth",
      data: {
        userIdList: JSON.stringify(userIdList),
        adminYn: adminYn
      },
      success: function (data) {
        if (data.CODE === "SUCCESS") {
          callback();
        } else {
          alert("권한 수정에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
        }
      },
      error: function () {
        return alert("권한 수정에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
      }
    });
  }

  return {
    init: init,
  }
}());
