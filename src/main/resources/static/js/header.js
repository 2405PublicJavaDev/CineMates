let isMyExpandVisible = false;

// 프로필 사진 클릭하면 메뉴 열고 닫기
document.querySelector("#header-my").addEventListener("click", (e) => {
    e.preventDefault();
    const expand = document.querySelector(".header-my-expand");
    if (isMyExpandVisible) {
        expand.style.display = 'none';
    } else {
        expand.style.display = 'block';
    }
    isMyExpandVisible = !isMyExpandVisible;
});

// 페이지 내 다른 곳을 클릭하면 메뉴 닫기
document.addEventListener("click", (e) => {
    const expand = document.querySelector(".header-my-expand");
    const headerMy = document.querySelector("#header-my");

    if (isMyExpandVisible && !expand.contains(e.target) && !headerMy.contains(e.target)) {
        expand.style.display = 'none';
        isMyExpandVisible = false;
    }
});

window.addEventListener('scroll', function() {
    const headerBottom = document.querySelector('.header-bottom');

    if (window.scrollY > 83) { // 스크롤이 100px 이상 내려가면
        headerBottom.style.backgroundColor = '#DCEFEC'; // nav를 최상단으로 이동
    }else {
        headerBottom.style.backgroundColor = ''; // 원래 색으로 돌아감
    }
});

function moveToMain() { location.href="/" }
function moveToLogin() { location.href="/login" }
function goMyPage() { location.href="/my-page/find-reservation"; }
function logout() {
    if (confirm("로그아웃 하시겠습니까?")) {
        location.href = "/logout"
        alert("로그아웃이 완료되었습니다.")
    }
}