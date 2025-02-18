async function fetchAnecdote() {
    const loadingElement = document.getElementById('loading');
    const anecdoteElement = document.getElementById('anecdote');
    const ratingButtons = document.getElementById('ratingButtons');

    // Показать индикатор загрузки
    loadingElement.style.display = 'block';
    anecdoteElement.style.display = 'none';
    ratingButtons.style.display = 'none';

    try {
        const response = await fetch(
            '/api/v1/anecdote',
            {headers: {'X-Session-Id': getSessionId()}}
        );
        const data = await response.json();
        anecdoteElement.innerText = data.anecdote;
        anecdoteElement.dataset.id = data.id;
    } catch (error) {
        anecdoteElement.innerText = 'Не удалось загрузить анекдот.';
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
        const generated = crypto.randomUUID();
        localStorage.setItem('sessionId', generated);
        return generated;
    }
    return current;
}

document.addEventListener('DOMContentLoaded', () => {
    fetchAnecdote();

    document.querySelectorAll('#ratingButtons button').forEach((button, index) => {
        button.addEventListener('click', () => rateAnecdote(index + 1));
    });
});