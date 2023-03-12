/************************************************************
파일명	:	login.js
설명		:	로그인 JavaScript
수정일		수정자		Version		설명
----------	----------	----------	----------
2022.00.00	설동재		1.0			최초 생성
************************************************************/

const $login = (function () {
    'use strict'

    /**
     * init
     */
    const init = function () {
        saveId();
    }

    /**
     * 쿠키 저장
     * @param {String} cookieName : 쿠키 key
     * @param {String} value : 사용자 ID
     * @param {Number} exdays : 만료일
     */
    function setCookie(cookieName, value, exdays) {
        const exdate = new Date();

        exdate.setDate(exdate.getDate() + exdays);
        const cookieValue = escape(value) + ((exdays == null) ? "" : "; expires=" + exdate.toUTCString());
        document.cookie = cookieName + "=" + cookieValue;
    }

    /**
     * 쿠키 삭제
     * @param {String} cookieName : 쿠키 key
     */
    function deleteCookie(cookieName) {
        const expireDate = new Date();

        expireDate.setDate(expireDate.getDate() - 1);
        document.cookie = cookieName + "= " + "; expires=" + expireDate.toUTCString();
    }

    /**
     * 쿠키 가져오기
     * @param {String} cookieName : 쿠키 key
     */
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

    /**
     * ID 저장
     */
    const saveId = function () {
        const key = getCookie("key");
        const userId = $("#userId");
        const checkId = $("#checkId");

        userId.val(key);

        userId.val() !== "" && checkId.attr("checked", true);

        checkId.change(function () {
            checkId.is(":checked") ? setCookie("key", userId.val(), 7) : deleteCookie("key");
        });

        userId.keyup(function () {
            $("#checkId").is(":checked") && setCookie("key", userId.val(), 7);
        });
    }

    /**
     * 토큰을 로컬 스토리지에 저장
     */
    function saveTokenToLocalStorage(token) {
      localStorage.setItem('jwtToken', token);
    }

    // 로그인 폼 제출 이벤트 리스너
    document.getElementById('loginForm').addEventListener('submit', function(event) {
      event.preventDefault();
      const formData = new FormData(event.target);
      fetch('/login_proc', {
        method: 'POST',
        body: formData
      })
      .then(response => {
        if (response.ok) {
            // 응답 헤더에서 Authorization 토큰 추출
            const token = response.headers.get('Authorization').split(' ')[1];
            // 추출한 토큰을 로컬 스토리지에 저장
            localStorage.setItem('jwtToken', token);
            // response.json()으로 응답 본문을 처리할 수 있습니다.
            hrefWithToken('/payhist');
            return response.json();
        } else {
          // 로그인 실패 시 에러 처리
          console.error('로그인 실패:', response.status);
        }
      })
      .catch(error => {
        console.error('네트워크 에러:', error);
      });
    });

    // 페이지 로드 시 로컬 스토리지에서 토큰을 가져와서 사용하는 함수
    function getTokenFromLocalStorage() {
      return localStorage.getItem('token');
    }

    return {
        init: init,
    }
}());