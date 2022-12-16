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

    return{
        isNullorEmpty: isNullorEmpty,
    }
}());