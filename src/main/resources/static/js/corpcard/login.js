/************************************************************
파일명	:	login.js
설명		:	로그인 JavaScript
수정일		수정자		Version		설명
----------	----------	----------	----------
2022.00.00	설동재		1.0			최초 생성
************************************************************/

const $login = (function(){
    'use strict'

    // 쿠키 저장하기
    function setCookie(cookieName, value, exdays) {
        const exdate = new Date();

        exdate.setDate(exdate.getDate() + exdays);
        const cookieValue = escape(value) + ((exdays == null) ? "" : "; expires=" + exdate.toUTCString());
        document.cookie = cookieName + "=" + cookieValue;
    }

    // 쿠키 삭제
    function deleteCookie(cookieName) {
        const expireDate = new Date();

        expireDate.setDate(expireDate.getDate() - 1);
        document.cookie = cookieName + "= " + "; expires=" + expireDate.toUTCString();
    }

    // 쿠키 가져오기
    function getCookie(cookieName) {
        cookieName = cookieName + '=';

        const cookieData = document.cookie;
        let start = Number(cookieData.indexOf(cookieName));
        let cookieValue = '';

        if (start !== -1) {
            start += cookieName.length;
            let end = Number(cookieData.indexOf(';', start));
            if (end === -1) end = cookieData.length;
            cookieValue = cookieData.substring(start, end);
        }

        return unescape(cookieValue);
    }

    //ID 저장
    const saveId = function() {
        const key = getCookie("key");
        const userId = document.querySelector("userId");
        const checkId = document.querySelector("checkId");

        userId.val(key);

        userId.val() !== "" && checkId.attr("checked", true);

        checkId.change(function() {
            checkId.is(":checked") ? setCookie("key", userId.val(), 7) : deleteCookie("key");
        });

        userId.keyup(function(){
            $("#checkId").is(":checked") && setCookie("key", userId.val(), 7);
        });
    }

    const init = function(){
        saveId();
    }
}());