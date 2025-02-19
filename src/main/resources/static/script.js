async function fetchAnecdote() {
    const loadingElement = document.getElementById('loading');
    const anecdoteElement = document.getElementById('anecdote');
    const ratingButtons = document.getElementById('ratingButtons');
    const ratingInfoElement = document.getElementById("ratingInfo");

    // Показать индикатор загрузки
    loadingElement.style.display = 'block';
    anecdoteElement.style.display = 'none';
    ratingButtons.style.display = 'none';
    ratingInfoElement.style.display = "none"; // скрываем блок рейтинга до загрузки

    try {
        const response = await fetch(
            '/api/v1/anecdote',
            {headers: {'X-Session-Id': getSessionId()}}
        );
        const data = await response.json();
        anecdoteElement.innerText = data.anecdote;
        anecdoteElement.dataset.id = data.id;

        // Обновляем информацию о рейтинге, если поля есть
        if (data.rating !== 0 && data.ratingCount !== 0) {
            // можно добавить иконку звезды, например так:
            ratingInfoElement.innerHTML = `<span class="star">★</span> ${data.rating.toFixed(1)} / 5 (${data.ratingCount} оценок)`;
            ratingInfoElement.style.display = "block";
        }

    } catch (error) {
        anecdoteElement.innerText = 'Не удалось загрузить анекдот. ' + error;
    } finally {
        loadingElement.style.display = 'none';
        anecdoteElement.style.display = 'block';
        ratingButtons.style.display = 'flex';
    }
}

async function rateAnecdote(rate) {
    const anecdoteElement = document.getElementById('anecdote');
    const anecdoteId = anecdoteElement.dataset.id;

    try {
        await fetch(`/api/v1/anecdote/${anecdoteId}/rate`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-Session-Id': getSessionId()
            },
            body: JSON.stringify({rate})
        });
    } catch (error) {
        console.error('Error rating anecdote:', error);
    } finally {
        await fetchAnecdote();
    }
}

function getSessionId() {
    const current = localStorage.getItem('sessionId');
    if (current == null || current === '') {
        const generated = generateRandomString(32);
        localStorage.setItem('sessionId', generated);
        return generated;
    }
    return current;
}

function generateRandomString(length = 16) {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let result = '';
    for (let i = 0; i < length; i++) {
        result += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return result;
}


document.addEventListener('DOMContentLoaded', () => {
    fetchAnecdote();

    document.querySelectorAll('#ratingButtons button').forEach((button, index) => {
        button.addEventListener('click', () => rateAnecdote(index + 1));
    });
});