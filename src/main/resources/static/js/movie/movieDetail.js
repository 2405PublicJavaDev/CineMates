document.addEventListener('DOMContentLoaded', function() {
    const reviewsTab = document.querySelector('#reviewsTab');
    const reviewList = document.querySelector('#reviewList');
    const loadMoreBtn = document.querySelector('#loadMoreBtn');
    const loadMoreContainer = document.querySelector('#load-more-container');
    const movieNo = document.querySelector('#movieNo').value;

    let currentPage = 0;
    const pageSize = 10;
    let hasMoreReviews = true;

    // 관람평 작성 버튼 이벤트
    reviewsTab.addEventListener('click', function(e) {
        if (e.target.classList.contains('review-write-button')) {
            checkLoginAndReview();
        }
    });

    function checkLoginAndReview() {
        fetch(`/checkLoginAndReview?movieNo=${movieNo}`, {
            method: 'GET',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // 관람평 작성 가능
                    toggleReviewForm();
                } else {
                    // 이미 관람평를 작성했거나 다른 이유로 작성 불가
                    alert(data.message);
                    if (data.message === "이미 이 영화에 대한 관람평를 작성하셨습니다.") {
                        // document.querySelector('.review-write-button').style.display = 'none';
                    }
                }
            })
            .catch(error => {
                console.error('Error:', error);
                if (error.status === 401) {
                    alert('로그인이 필요합니다.');
                    // window.location.href = '/login';
                } else {
                    alert('오류가 발생했습니다. 다시 시도해주세요.');
                }
            });
    }

    function toggleReviewForm() {
        const reviewForm = document.querySelector('#reviewForm');
        const reviewWriteButton = document.querySelector('.review-write-button');
        reviewForm.style.display = reviewForm.style.display === 'none' ? 'block' : 'none';
        reviewWriteButton.textContent = reviewForm.style.display === 'none' ? '관람평 작성' : '작성 취소';
    }


    // 폼 제출 이벤트 리스너
    document.addEventListener('submit', function(e) {
        if (e.target.id === 'reviewForm') {
            e.preventDefault();

            const formData = new FormData(e.target);
            const reviewData = Object.fromEntries(formData.entries());

            fetch('/addReview', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(reviewData)
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        // 관람평 추가 성공 시 첫 페이지 관람평를 다시 로드
                        currentPage = 0;
                        fetchReviews(currentPage, false);

                        // 폼 리셋 및 숨기기
                        e.target.reset();
                        toggleReviewForm();

                        alert(data.message);
                    } else {
                        if (data.message === "이미 이 영화에 대한 관람평를 작성하셨습니다.") {
                            // 이미 관람평를 작성한 경우
                            alert(data.message);
                            toggleReviewForm();
                            document.querySelector('.review-write-button').style.display = 'none';
                        } else {
                            // 기타 오류
                            alert(data.message || '관람평 등록에 실패했습니다. 다시 시도해주세요.');
                        }
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('관람평 등록 중 오류가 발생했습니다.');
                });
        }
    });

    function fetchReviews(page, append = false) {
        fetch(`/movie-detail/${movieNo}?page=${page}&size=${pageSize}`, {
            method: 'GET',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
            .then(response => response.json())
            .then(data => {
                if (!append) {
                    reviewList.innerHTML = '';
                    myReviewAdded = false;
                }
                if (data.reviews && data.reviews.length > 0) {
                    appendReviews(data.reviews, data.myReview, append);
                    hasMoreReviews = data.reviews.length === pageSize;
                    loadMoreContainer.style.display = hasMoreReviews ? 'flex' : 'none';
                } else {
                    hasMoreReviews = false;
                    loadMoreContainer.style.display = 'none';
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
    }

    function appendReviews(reviews, myReview, append) {
        if (myReview && !myReviewAdded) {
            const myReviewElement = createReviewElement(myReview, true);
            reviewList.insertBefore(myReviewElement, reviewList.firstChild);
            myReviewAdded = true;
        }
        reviews.forEach(review => {
            if (!myReview || review.reviewNo !== myReview.reviewNo) {
                const reviewElement = createReviewElement(review);
                reviewList.appendChild(reviewElement);
            }
        });
    }

    function createReviewElement(review, isMyReview = false) {
        const reviewDiv = document.createElement('div');
        reviewDiv.className = 'review-item' + (isMyReview ? ' my-review' : '');
        reviewDiv.innerHTML = `
    <div id="thumbnail-container">
        <img id="profile-thumbnail" alt="사진 미리보기"
             src="${review.filePath && review.fileRename ? review.filePath + review.fileRename : '/img/default_profile.png'}"
             onerror="this.src='/img/default_profile.png'">
    </div>
    <div class="review-content">
        <div class="review-header">
            <div class="review-info">
                <div class="username-myReview">
                    <input type="hidden" name="reviewNo" value="${review.reviewNo}">
                    <span class="review-username">${review.memberId}</span>
                    ${isMyReview ? '<span class="isMyReview">나의 관람평</span>' : ''}
                </div>
                <span class="review-date">${review.regDate}</span>
                <div class="review-stars">${getStars(review.score)}</div>
            </div>
            <div class="button-menu">
                ${isMyReview ? '<span class="delete-review">삭제</span>' : `<span class="review-report" data-review-no="${review.reviewNo}" data-member-id="${review.memberId}">신고</span>`}
            </div>
        </div>
        <p class="review-text">${review.reviewContent}</p>
    </div>
    `;

        // 삭제 버튼에 이벤트 리스너 추가
        const deleteButton = reviewDiv.querySelector('.delete-review');
        if (deleteButton) {
            deleteButton.addEventListener('click', function() {
                deleteReview(review.reviewNo);
            });
        }

        const reportButton = reviewDiv.querySelector('.review-report');
        if (reportButton) {
            reportButton.addEventListener('click', function() {
                const reviewNo = this.getAttribute('data-review-no');
                const memberId = this.getAttribute('data-member-id');
                report(reviewNo, memberId);
            });
        }

        return reviewDiv;
    }

    function deleteReview(reviewNo) {
        if (confirm('정말로 이 관람평를 삭제하시겠습니까?')) {
            fetch(`/removeReview/${reviewNo}`, { method: 'DELETE' })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        alert('관람평가 삭제되었습니다.');
                        // 관람평 목록 새로고침
                        fetchReviews(currentPage, false);
                    } else {
                        alert('관람평 삭제에 실패했습니다.');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('관람평 삭제 중 오류가 발생했습니다.');
                });
        }
    }

    // 신고 버튼
    function report(a,b){
        var popupW = 500;
        var popupH = 450;
        var left = Math.ceil((window.screen.width - popupW)/2);
        var top = Math.ceil((window.screen.height - popupH)/2);
        window.open("/report/report/"+a+"&"+b+"&리뷰","pop","width=500,height=450,left="+left+",top="+top+"");

    }

    function getStars(score) {
        const fullStar = '★';
        const emptyStar = '☆';
        return fullStar.repeat(score) + emptyStar.repeat(5 - score);
    }

    loadMoreBtn.addEventListener('click', function() {
        if (hasMoreReviews) {
            currentPage++;
            fetchReviews(currentPage, true);
        }
    });
    // 초기 로드
    fetchReviews(currentPage);
});



// 영화정보 관람평 이동 탭
document.addEventListener('DOMContentLoaded', () => {
    const tabButtons = document.querySelectorAll('.tab-button');
    const tabContents = document.querySelectorAll('.tab-content');

    tabButtons.forEach(button => {
        button.addEventListener('click', () => {
            const tabName = button.getAttribute('data-tab');

            tabContents.forEach(content => {
                content.classList.add('hidden');
            });
            document.getElementById(`${tabName}Tab`).classList.remove('hidden');

            tabButtons.forEach(btn => {
                btn.classList.remove('active');
            });
            button.classList.add('active');
        });
    });
});

//트레일러 슬라이드
document.addEventListener('DOMContentLoaded', () => {
    const trailerList = document.querySelector('.trailer-list');
    const trailerItems = document.querySelectorAll('.trailer-item');
    const prevTrailerBtn = document.getElementById('prevTrailer');
    const nextTrailerBtn = document.getElementById('nextTrailer');
    let currentTrailerIndex = 0;

    function updateTrailerSlide() {
        const itemWidth = trailerItems[0].offsetWidth;
        trailerList.style.transform = `translateX(-${currentTrailerIndex * itemWidth}px)`;

        // 버튼 표시 상태 업데이트
        prevTrailerBtn.style.display = currentTrailerIndex > 0 ? 'block' : 'none';
        nextTrailerBtn.style.display = currentTrailerIndex < trailerItems.length - 3 ? 'block' : 'none';
    }

    prevTrailerBtn.addEventListener('click', () => {
        if (currentTrailerIndex > 0) {
            currentTrailerIndex--;
            updateTrailerSlide();
        }
    });

    nextTrailerBtn.addEventListener('click', () => {
        if (currentTrailerIndex < trailerItems.length - 3) {
            currentTrailerIndex++;
            updateTrailerSlide();
        }
    });

    // 초기 상태 설정
    updateTrailerSlide();
});


// 비디오 모달 팝업
document.addEventListener('DOMContentLoaded', function() {
    const modal = document.getElementById('videoModal');
    const modalVideo = document.getElementById('modalVideo');
    const closeBtn = document.getElementsByClassName('video-close')[0];
    const trailerThumbnails = document.querySelectorAll('.trailer-thumbnail');

    trailerThumbnails.forEach(thumbnail => {
        thumbnail.addEventListener('click', function() {
            const trailerUrl = this.getAttribute('data-trailer-url');
            modalVideo.src = trailerUrl;
            modal.style.display = 'block';
            modalVideo.play();
        });
    });

    closeBtn.onclick = function() {
        modal.style.display = 'none';
        modalVideo.pause();
        modalVideo.currentTime = 0;
    }

    window.onclick = function(event) {
        if (event.target == modal) {
            modal.style.display = 'none';
            modalVideo.pause();
            modalVideo.currentTime = 0;
        }
    }
})

// 포스터 모달
document.addEventListener('DOMContentLoaded', function() {
    const imageModal = document.getElementById('imageModal');
    const modalImage = document.getElementById('modalImage');
    const moviePoster = document.getElementById('moviePoster');
    const closeImageBtn = imageModal.querySelector('.close');

    // 포스터 클릭 시 모달 열기
    moviePoster.onclick = function() {
        imageModal.style.display = 'block';
        modalImage.src = this.src;
        modalImage.alt = this.alt;
    }

    // 닫기 버튼 클릭 시 모달 닫기
    closeImageBtn.onclick = function() {
        imageModal.style.display = 'none';
    }

    // 모달 외부 클릭 시 모달 닫기
    window.onclick = function(event) {
        if (event.target == imageModal) {
            imageModal.style.display = 'none';
        }
    }

});


// 스틸컷 모달
document.addEventListener('DOMContentLoaded', function() {
    const stillList = document.getElementById('stillList');
    const modal = document.getElementById('imageModal');
    const modalImg = document.getElementById('modalImage');
    const closeBtn = document.querySelector('.close');

    // 스틸컷 이미지 클릭 이벤트
    if (stillList) {
        stillList.addEventListener('click', function(e) {
            const stillItem = e.target.closest('.still-item');
            if (stillItem) {
                const imageUrl = stillItem.getAttribute('data-stillcut-url');
                if (imageUrl) {
                    modal.style.display = 'block';
                    modalImg.src = imageUrl;
                    // 스크롤 방지
                    document.body.style.overflow = 'hidden';
                }
            }
        });
    }

    // 모달 닫기 버튼 클릭 이벤트
    if (closeBtn) {
        closeBtn.addEventListener('click', function() {
            closeModal();
        });
    }

    // 모달 바깥 영역 클릭 시 닫기
    modal.addEventListener('click', function(e) {
        if (e.target === modal) {
            closeModal();
        }
    });

    // ESC 키 누르면 모달 닫기
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            closeModal();
        }
    });

    // 모달 닫기 함수
    function closeModal() {
        modal.style.display = 'none';
        modalImg.src = '';
        // 스크롤 다시 활성화
        document.body.style.overflow = 'auto';
    }
});

function moveToTicketing() { location.href="/Ticketing" }