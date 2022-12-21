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

        if (form[0].tagName && form[0].tagName.toUpperCase() == "FORM") {
            var arr = form.serializeArray();
            if (arr) {
                obj = {};
                jQuery.each(arr, function() {
                    obj[this.name] = this.value;
                });
            }
        }
        return obj;
    }

    /**
     * 쉼표 삭제
     * @param value : String
     */
    const uncomma = function(value) {
        const str = new String(value);
        return str.replace(/,/g, "");
    }

    /**
     * 문자 삭제 및 쉼표 추가
     * @param value : String (input value)
     */
    const convertToCurrency = function (obj) {
        let answer = new String(obj);
        let err;

        if (!isNaN(answer.split(",").join(""))) {
            if (answer.includes('.')) {
                err = String(obj).split('.')[0];
                answer = err.toString().split(",").join("").replace(/\B(?=(\d{3})+(?!\d))/g, ',');
                answer = answer.concat("." + String(obj).split('.')[1]);
            } else {
                answer = answer.toString().split(",").join("").replace(/\B(?=(\d{3})+(?!\d))/g, ',');
            }
        }
        return answer;
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
       * @param columnCnt : number (테이블 컬럼 갯수)
       * @param json : JSON(1.paramList 2.dataList)
       */
    const paintTableRows = function(tableId, json){

    }

    return{
        isNullorEmpty : isNullorEmpty,          //null이면 true 반환
        serializeObject : serializeObject,      //form 정보를 json 형식으로 변환
        uncomma : uncomma,                      //쉼표 삭제
        convertToCurrency : convertToCurrency,  //문자 삭제 및 쉼표 추가
        emptyTable : emptyTable,                //테이블 초기화
        paintTableRows : paintTableRows,        //테이블 row 생성
    }
}());