// let visitorCount = /*[[${rDTO.reservationVisitor}]]*/ 0;
const countInput = document.getElementById('countInput');
const adult = document.getElementById('adultReserved');
const child = document.getElementById('childReserved');
const senior = document.getElementById('seniorReserved');
const selectedSeatsInput = document.getElementById('selectedSeatsInput');
const maxVisitorCount = memberIds.length !== 0 ? memberIds.length : Infinity;

function count(value, resultClass) {
    const resultElement = document.querySelector('.' + resultClass);
    let currentCount = parseInt(resultElement.innerText);
    let targetInput;

    // 카테고리에 따른 hidden input 선택
    switch (resultClass) {
        case 'result':
            targetInput = adult;
            break;
        case 'result1':
            targetInput = child;
            break;
        case 'result2':
            targetInput = senior;
            break;
    }

    // 더하기/빼기
    if (value === "plus" && visitorCount < maxVisitorCount) {
        currentCount++;
        visitorCount++;
    } else if (value === "minus" && currentCount > 0) {
        currentCount--;
        visitorCount--;
    }


    // 현재 화면에 표시된 값 업데이트
    resultElement.innerText = currentCount;
    countInput.value = visitorCount;

    // hidden input 값 업데이트
    if (targetInput) {
        targetInput.value = currentCount;
    }
}
function updatePlusButtonStates() {
    const plusButtons = document.querySelectorAll('[onclick*="count(\'plus\'"]');
    plusButtons.forEach(button => {
        if (visitorCount >= maxVisitorCount) {
            button.disabled = true;
            button.style.opacity = '0.5';  // 선택적: 비활성화된 버튼의 스타일 변경
        } else {
            button.disabled = false;
            button.style.opacity = '1';
        }
    });
}


// 페이지 로드 시 초기 상태 설정
document.addEventListener('DOMContentLoaded', updatePlusButtonStates);

// const rDTO = /*[[${rDTO}]]*/ {};
// const reservedSeatsMap = /*[[${reservationSeat}]]*/ {};
const reservedSeats = reservedSeatsMap[rDTO.showtimeNo] || [];
console.log(reservedSeats);
const rows = 5;
const seatsPerRow = 10;
const seats = Array(rows * seatsPerRow).fill(false);
let selectedSeats = [];

function initializeSeatMap() {
    const seatMap = document.getElementById('seatMap');
    // 기존 좌석 요소들을 모두 제거
    seatMap.innerHTML = '';

    for (let i = 0; i < seats.length; i++) {
        const seat = document.createElement('div');
        seat.className = 'seat';
        seat.innerText = i + 1;

        if (reservedSeats.includes(i + 1)) {
            seat.classList.add('reserved');
            seats[i] = true; // 예매된 좌석 표시
        } else {
            seat.onclick = () => toggleSeat(i);
        }

        seatMap.appendChild(seat);
        if ((i + 1) % seatsPerRow === 0) {
            seatMap.appendChild(document.createElement('br'));
        }
    }
}

function toggleSeat(index) {
    if (seats[index]) {
        alert("이미 예약된 좌석입니다.");
        return; // 이미 예약된 좌석은 선택 불가
    }

    const maxSeats = parseInt(visitorCount);
    const seatElement = document.getElementsByClassName('seat')[index];

    if (selectedSeats.includes(index)) {
        selectedSeats = selectedSeats.filter(seatIndex => seatIndex !== index);
        seatElement.classList.remove('selected');
    } else if (selectedSeats.length < maxSeats) {
        selectedSeats.push(index);
        seatElement.classList.add('selected');
    } else {
        alert(`최대 ${maxSeats}개의 좌석만 선택할 수 있습니다.`);
        return;
    }
    updateSelectedSeatsInput();
}

function updateSelectedSeatsInput() {
    selectedSeatsInput.value = selectedSeats.map(index => index + 1).join(',');
    setMessage(`선택된 좌석: ${selectedSeatsInput.value}`);
}

// 페이지 로드 시 좌석 맵 초기화
document.addEventListener('DOMContentLoaded', initializeSeatMap);

function setMessage(msg) {
    document.getElementById('message').innerText = msg;
}

initializeSeatMap();