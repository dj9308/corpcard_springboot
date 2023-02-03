/************************************************************
 파일명    :    card.js
 설명     : 카드 관리 페이지 JavaScript
 수정일        수정자        Version        설명
 ----------    ----------    ----------    ----------
 2023.01.00    설동재        1.0            최초 생성
 ************************************************************/

const $card = (function () {
  'use strict';

  const userId = document.querySelector('#userId').value;   // 사용자 ID

  /**
   * init
   */
  const init = function () {
    initBtnEvt();           //버튼 이벤트
    initCardTable();        //카드 테이블 설정
    initModal();            //카드 추가 및 편집 모달 설정
  }

  /**
  * 버튼 이벤트
  */
  const initBtnEvt = function () {
    //search input
    $("#listSearch").on('keyup', function () {
      $("#cardTable > tbody > tr").hide();
      const value = $(this).val().toLowerCase();
      $("#cardTable > tbody > tr").filter(function () {
        return $(this).text().toLowerCase().indexOf(value) > -1;
      }).show();
      $("#listTotCnt").text($("#cardTable > tbody > tr:visible").length);
      $("#cardTable > tbody > tr:visible").find(".table-check:not(:checked)").length === 0 ?
        $("#checkAll").prop('checked', true) : $("#checkAll").prop('checked', false);
    });

    //추가 버튼
    $("#addCard").on('click', function () {
      $("#cardAddForm")[0].reset();
      $("#addSearch").val("");
      $("#receiptentSeq").val("");
      $("#receiptentId").val("");
      $("#cardSeq").val("");
      $("#modalLabel").text("카드 정보 추가");
      $("#modalAddBtn").text("추가");

      searchUserList(paintUserTable);
    });

    //삭제 버튼
    $("#deleteCard").on('click', function () {
        //1.체크된 row 조회
        const seqList = [];
        $('.table-check').each(function (index) {
          if ($(this).is(":checked") && $(this).is(":visible")) {
            seqList.push($(this).parent().parent().find("td:eq(7)").text());
          }
        });
        if (seqList.length === 0) {
          return alert("삭제하려는 카드 정보가 없습니다.");
        }else if (confirm(`선택한 ${seqList.length}개의 카드 정보를 삭제하겠습니까?`)){
            //2.카드 정보 삭제
            deleteCard(seqList, selectCardList);
            $("#checkAll").prop('checked', false);
            $(".table-check").prop('checked', false);
        }
    });

    //카드 추가 모달 Search Input
    $("#addSearch").on('keyup', function () {
      $("#addUserTable > tbody > tr").hide();
      const value = $(this).val().toLowerCase();
      $("#addUserTable > tbody > tr").filter(function () {
        return $(this).text().toLowerCase().indexOf(value) > -1;
      }).show();
    });

    //카드 추가 모달 추가 btn
    $("#modalAddBtn").on("click", function(){
        $("#hiddenAddBtn").trigger("click");
    });
    $("#cardAddForm").on("submit", function (event) {
      event.preventDefault();
        //카드 추가
        saveCardInfo(function(){
            $("#modalCloseBtn").trigger("click");
            selectCardList();
        });
    });

    //카드 추가 모달 수령인 삭제 btn
    $("#nameDelBtn").on("click", function(){
        $("#receiptentNm").val("");
        $("#receiptentId").val("");
    });
  }

  /**
   * 권한 테이블 설정
   */
  const initCardTable = function () {
    //카드 목록 조회
    selectCardList();

    //체크박스 전체 설정
    document.querySelector("#checkAll").addEventListener('click', function () {
      $(".table-check").prop('checked', $(this).prop('checked'));
    });
  }

  /**
   * 카드 목록 조회
   */
  const selectCardList = function () {
    $.ajax({
      type: "GET",
      url: "/admin/cardList",
      success: function (data) {
        if (data.CODE === "SUCCESS") {
          $("#checkAll").prop('checked', false);
          $(".table-check").prop('checked', false);
          $("#listSearch").val("");
          paintTable(data.result);
        } else if (data.CODE === "EMPTY") {
          paintTable();
        } else {
          alert("카드 목록 조회에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
        }
      },
      error: function () {
        return alert("카드 목록 조회에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
      }
    });
  }

  /**
   * 테이블 생성
   * @param {JSON} data : 카드 목록
   */
  const paintTable = function (data) {
    const tbody = document.querySelector("#cardTable>tbody");
    $("#cardTable>tbody").empty();

    if ($cmmn.isNullorEmpty(data)) {
      const newCell = tbody.insertRow().insertCell();
      document.querySelector("#listTotCnt").innerText = "0";
      newCell.setAttribute('colspan', '6');
      newCell.classList.add("fw-bold");
      newCell.innerText = "등록된 카드 정보가 없습니다.";

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
        //카드회사
        newRow.insertCell().innerHTML = rowData.cardComp;
        //카드번호
        newRow.insertCell().innerHTML = rowData.cardNum;
        //수령인
        newRow.insertCell().innerHTML = (rowData.cardReceiptents.length !== 0 && $cmmn.isNullorEmpty(
            rowData.cardReceiptents[rowData.cardReceiptents.length-1].returnedAt)) ?
            rowData.cardReceiptents[rowData.cardReceiptents.length-1].user.userNm : '';
        //수령인 ID
        const userIdCell = newRow.insertCell();
        userIdCell.innerHTML = (rowData.cardReceiptents.length !== 0 && $cmmn.isNullorEmpty(
           rowData.cardReceiptents[rowData.cardReceiptents.length-1].returnedAt)) ?
           rowData.cardReceiptents[rowData.cardReceiptents.length-1].user.userId : '';
        userIdCell.style.display = "none";
        //rec seq
        const userSeqCell = newRow.insertCell();
        userSeqCell.innerHTML = (rowData.cardReceiptents.length !== 0 && $cmmn.isNullorEmpty(
           rowData.cardReceiptents[rowData.cardReceiptents.length-1].returnedAt)) ?
           rowData.cardReceiptents[rowData.cardReceiptents.length-1].seq : '';
        userSeqCell.style.display = "none";
        //카드 seq
        const cardSeqCell = newRow.insertCell();
        cardSeqCell.innerHTML = rowData.seq;
        cardSeqCell.style.display = "none";

        //수령인 편집 모달
        $(newRow).on("click", function (e) {
            if (!$(e.target).is("label,input")) {
                $("#cardComp").val($(this).find("td:eq(2)").text());
                $("#cardNum").val($(this).find("td:eq(3)").text());
                $("#receiptentNm").val($(this).find("td:eq(4)").text());
                $("#receiptentId").val($(this).find("td:eq(5)").text());
                $("#receiptentSeq").val($(this).find("td:eq(6)").text());
                $("#cardSeq").val($(this).find("td:eq(7)").text());
                $("#addSearch").val("");
                $("#modalLabel").text("카드 정보 변경");
                $("#modalAddBtn").text("변경");

                const modal = new bootstrap.Modal(document.querySelector('#addManagerModal'), {
                  keyboard: false
                })
                modal.show();
                searchUserList(paintUserTable);
              }
        });
      }
    }
    //체크박스 설정
    $('.table-check').on('click', function () {
      if ($(".table-check:not(:checked)").length > 0) {
        $("#checkAll").prop('checked', false);
      } else {
        $("#checkAll").prop('checked', true);
      }
    });
  }

  /**
    * 사용자 목록 테이블 생성
    * @param {JSON} data : 사용자 조회 목록
    */
  const paintUserTable = function (data) {
    const tbody = document.querySelector("#addUserTable>tbody");

    $("#addUserTable>tbody").empty();

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
            $("#receiptentNm").val("");
            $("#receiptentId").val("");
          } else {
            $("#addUserTable > tbody > tr").removeClass("table-active");
            $(this).addClass("table-active");
            $("#receiptentNm").val(rowData.userNm);
            $("#receiptentId").val(rowData.userId);
          }
        });
      }
    }
  }

  /**
     * 사용자 목록 조회 AJAX
     */
    const searchUserList = function (callback) {
      $.ajax({
        type: "GET",
        url: "/admin/userList",
        success: function (data) {
          if (data.CODE === "SUCCESS") {
            callback(data.result);
          } else {
            alert("사용자 목록 조회에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
          }
        },
        error: function () {
          return alert("사용자 목록 조회에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
        }
      });
    }

  /**
   * 카드 삭제 AJAX
   * @param {Array} cardSeqList : 삭제할 카드 Array
   * @param {function} callback : Callback function
   */
  const deleteCard = function (cardSeqList, callback) {
    if (!Array.isArray(cardSeqList)) {
      cardSeqList = [cardSeqList];
    }

    $.ajax({
      type: "DELETE",
      url: "/admin/cardList",
      data: {
        cardSeqList: cardSeqList,
      },
      success: function (data) {
        if (data.CODE === "SUCCESS") {
          callback();
        } else {
          alert("카드 정보 삭제에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
        }
      },
      error: function () {
        return alert("카드 정보 삭제에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
      }
    });
  }

  /**
     * 카드 추가 or 저장 AJAX
     * @param {JSON} data : 카드 정보
     * @param {function} callback : Callback function
     */
    const saveCardInfo = function (callback) {
      const data = $cmmn.serializeObject("cardAddForm");

      $.ajax({
        type: "POST",
        url: "/admin/cardInfo",
        data: data,
        dataType: 'json',
        success: function (data) {
          if (data.CODE === "SUCCESS") {
            callback();
          } else {
            alert("카드 정보 삭제에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
          }
        },
        error: function () {
          return alert("카드 정보 삭제에 실패했습니다. 관리자에게 문의해주시기 바랍니다.");
        }
      });
    }

  /**
   * 카드 추가 및 편집 모달 설정
   */
  const initModal = function(){
    $("#cardNum").on("keyup", function(){
        let num = $(this).val();
        num = num.match(/[0-9●]{1,4}/g)?.join('-') || num;
        $(this).val(num);
    });
  }

  return {
    init: init,
  }
}());
