/************************************************************
 파일명    :    common.js
 설명    : 공통 JavaScript
 수정일        수정자        Version        설명
 ----------    ----------    ----------    ----------
 2022.12.00    설동재        1.0            최초 생성
 ************************************************************/

const $cmmn = (function() {
    'use strict';

    /**
     * null이면 true 반환
     * @param checkValue : node
     */
    const isNullorEmpty = function(checkValue) {
        //null 확인
        if(checkValue == null || typeof checkValue === 'undefined') {
            return true;
        }
        //빈 문자열값 체크
        if(typeof checkValue === 'string' && checkValue === '') {
            return true;
        }
        return false;
    }

    /**
   * form 정보를 json 형식으로 변환
   * @param formId : formId
   */
    const serializeObject = function(formId){
        const form = $(`#${formId}`);
        let obj = null;

        try {
            if (form[0].tagName && form[0].tagName.toUpperCase() == "FORM") {
                var arr = form.serializeArray();
                if (arr) {
                    obj = {};
                    jQuery.each(arr, function() {
                        obj[this.name] = this.value;
                    });
                }//if ( arr ) {
            }
        } catch (e) {
            alert(e.message);
        }
        return obj;
    }

  /**
   * 테이블 초기화
   * @param tableId : String (Table tag id)
   */
    const emptyTable = function(tableId) {
        $(`#${tableId}>tbody`).empty();
    }

    /**
       * 테이블 row 생성
       * @param tableId : String (Table tag id)
       * @param json : JSON(1.컬럼 정보 리스트, 2.데이터 리스트)
       */
    const paintTableRows = function(tableId, json){

    }

    return{
        isNullorEmpty : isNullorEmpty,      //null이면 true 반환
        serializeObject : serializeObject,   //form 정보를 json 형식으로 변환
        emptyTable : emptyTable,            //테이블 초기화
        paintTableRows : paintTableRows,    //테이블 row 생성
    }
}());