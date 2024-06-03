window.addEventListener('load', async () => {
    const path = window.location.pathname.split('/');
    const boardId = path[path.length - 2];
    const buttonsArea = document.querySelector('#buttons-area')
    const accessToken = localStorage.getItem('access_token');
    const decodedJwt = parseJwt(accessToken);
    const userId = decodedJwt.id;

    // fetch board {id}
    const boardData = await getBoard(boardId);

    if (boardData !== '' || boardData !== undefined) {

        document.querySelector('#board-id').textContent = boardData.boardId;
        document.querySelector('#board-title').value = boardData.title;
        document.querySelector('#board-writer').value = boardData.writer;
        // TODO add boardHits on response entity(on server). then unlock below.
        // document.querySelector('#board-hits').textContent = boardData.hits;
        document.querySelector('#board-date').textContent =
            boardData.registerDate !== boardData.updateDate
                ? formatDate(boardData.registerDate)
                : '수정됨 ' + formatDate(boardData.updateDate);
        document.querySelector('#board-content').value = boardData.content;

        // buttons DOM
        if (userId === boardData.writerId) {
            const modifyButton = createButton('modify-btn', 'btn btn-primary', 'Modify');
            buttonsArea.append(modifyButton);

            const cancelButton = createButton('cancel-btn', 'btn btn-secondary', 'Cancel');
            buttonsArea.append(cancelButton);
        }

        // cancel button click event
        const cancelButton = document.querySelector('#cancel-btn');
        if (cancelButton) {
            cancelButton.addEventListener(
                'click',
                () => {
                    const confirmation = confirm('수정을 취소하시겠습니까?\n 확인을 클릭 시, 수정 내용을 저장하지 않고 목록으로 이동합니다.');
                    if (confirmation) {
                        history.back();
                    }
                }
            )
        }

        // modify board fetch.
        const modifyButton = document.querySelector('#modify-btn');
        if (modifyButton) {
            modifyButton.addEventListener(
                'click',
                async () => {
                    const url = `/api/v1/board/${boardId}`;
                    let options = {
                        headers: {
                            'Content-Type': 'application/json',
                            'Authorization': localStorage.getItem('access_token')
                        },
                        method: 'PUT',
                        body: JSON.stringify(formToBody())
                    };

                    try {

                        const data = await fetchWithToken(url, options);
                        console.log(data);
                        if (data.id !== undefined) {
                            alert(data.message);
                            location.replace(`/board/${data.id}`);
                        } else {
                            alert(data.message);
                        }

                    } catch (error) {
                        console.error('Error: ', error);
                    }

                }
            );
        }
    }
});

function formToBody() {

    const form = document.querySelector('#form');
    const formData = new FormData(form);
    const body = {};
    formData.forEach((value, key) => {
        body[key] = value;
    });

    return body;
}

function parseJwt(token) {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(window.atob(base64)
        .split('')
        .map(function (c) {
            return '%'
                + ('00' + c.charCodeAt(0).toString(16))
                    .slice(-2);
        })
        .join('')
    );

    return JSON.parse(jsonPayload);
}

function formatDate(data) {

    // LocalDateTime 형식의 JSON 값을 Date 객체로 변환
    const date = new Date(data);

    // 원하는 형식(yyyy-MM-dd)으로 변환
    const formattedDate = date.getFullYear() + '-' +
        String(date.getMonth() + 1).padStart(2, '0') + '-' +
        String(date.getDate()).padStart(2, '0');

    return formattedDate;
}
