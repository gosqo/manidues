let isValidEmail = false;
let isValidPassword = false;
let isValidPasswordCheck = false;
let isValidNickname = false;
let isUniqueEmail = false;
let isUniqueNickname = false;
// 윈도우가 로드될 때 수행할 일들.
// 버튼 핸들링 관련, Node 변수 선언 및 할당.
window.addEventListener('load', () => {
    const submitButton = document.querySelector('#submit-form-btn');
    const isPresentEmailButton = document.querySelector('#is-present-email-button');
    const isPresentNicknameButton = document.querySelector('#is-present-nickname-button');

    isPresentEmailButton.addEventListener('click', async (event) => {
        event.preventDefault();
        const targetElement = document.querySelector('input[name=email]');

        removeElementIfPresent(targetElement, 'IsPresentCheckMessage');

        const valueToCheck = targetElement.value;
        const url = '/api/v1/member/isPresentEmail';
        const options = {
            headers: {
                'Content-Type': 'application/json',
            },
            method: 'POST',
            body: JSON.stringify({ "email": valueToCheck })
        };

        const isPresentResultMessage = await fetchAndReturnMessage(url, options);
        const isPassed = isPresentResultMessage === '사용 가능한 이메일 주소입니다.';
        addResultMessageIsPresent(targetElement, isPassed, isPresentResultMessage, isUniqueEmail);
        if (isPassed) isUniqueEmail = true;
        else isUniqueEmail = false;
        activateSubmitIfClear();
    });

    isPresentNicknameButton.addEventListener('click', async (event) => {
        event.preventDefault();
        const targetElement = document.querySelector('input[name=nickname]');

        removeElementIfPresent(targetElement, 'IsPresentCheckMessage');

        const valueToCheck = targetElement.value;
        const url = '/api/v1/member/isPresentNickname';
        const options = {
            headers: {
                'Content-Type': 'application/json',
            },
            method: 'POST',
            body: JSON.stringify({ "nickname": valueToCheck })
        };

        const isPresentResultMessage = await fetchAndReturnMessage(url, options);
        const isPassed = isPresentResultMessage === '사용 가능한 닉네임입니다.';
        addResultMessageIsPresent(targetElement, isPassed, isPresentResultMessage, isUniqueNickname);
        if (isPassed) isUniqueNickname = true;
        else isUniqueNickname = false;
        activateSubmitIfClear();
    });

    submitButton.addEventListener('click', async (event) => {
        event.preventDefault();

        const body = getBodyFromForm();

        const url = '/api/v1/member';
        const options = {
            headers: {
                'Content-Type': 'application/json',
            },
            method: 'POST',
            body: JSON.stringify(body)
        };
        await fetchSubmit(url, options);
    });

    function getBodyFromForm() {
        const form = document.querySelector('#form');
        const formData = new FormData(form);
        const body = {};

        formData.forEach((value, key) => {
            body[key] = value;
        });
        return body;
    }

    async function fetchSubmit(url, options) {
        try {
            const response = await fetch(url, options);

            if (response.status === 200) {
                const result = await response.text();
                alert(result);

                location.replace('/login');
                return;
            }

            const data = await response.json();
            alert(data.message);
        } catch (error) {
            console.error(error);
        }
    }
});

function activateSubmitIfClear() {
    const submitButton = document.querySelector('#submit-form-btn');

    submitButton.disabled = isValidEmail === true
        && isValidPassword === true
        && isValidPasswordCheck === true
        && isValidNickname === true
        && isUniqueEmail === true
        && isUniqueNickname === true ? false : true;
}

function removeElementIfPresent(targetElement, appendingId) {
    const target = document.querySelector(
        `#${targetElement.name}${appendingId}`);
    if (target) target.remove();
}

function addResultMessageIsPresent(targetElement, passingFlag, resultMessage, resultFlag) {
    removeElementIfPresent(targetElement, 'ResultMessageIsPresent');

    const message = document.createElement('small');
    message.id = `${targetElement.name}ResultMessageIsPresent`;
    message.textContent = resultMessage;
    if (passingFlag) {
        message.style.color = 'green';
        resultFlag = true;
    } else {
        message.style.color = 'red';
        resultFlag = false;
    }
    targetElement.closest('div').nextElementSibling.nextElementSibling.append(message);
}

async function fetchAndReturnMessage(url, options) {
    try {
        const response = await fetch(url, options);
        const data = await response.json();
        return data.message;
    } catch (error) {
        console.error('Error: ', error);
    }
}
