async function loadTags() {
    try {
        console.log('requesting tags....')
        const response = await fetch('/api/v1/tags');
        const tags = await response.json();
        const tagSelect = document.getElementById('tagSelect');

        tagSelect.innerHTML = tags.map(tag => `<option value="${tag.id}">${tag.name}</option>`).join('');

    } catch (error) {
        console.error('Error loading tags:', error);
    }
}

async function fetchAnecdote() {
    const loadingElement = document.getElementById('loading');
    const anecdoteElement = document.getElementById('anecdote');
    const ratingButtons = document.getElementById('ratingButtons');
    const ratingInfoElement = document.getElementById("ratingInfo");
    const tagsElement = document.getElementById('tags');


    // Показать индикатор загрузки
    loadingElement.style.display = 'block';
    anecdoteElement.style.display = 'none';
    ratingButtons.style.display = 'none';
    ratingInfoElement.style.display = "none";
    tagsElement.style.display = 'none';

    try {
        const url = new URL('/api/v1/anecdote', document.location.origin);

        Array.from(document.getElementById('tagSelect').selectedOptions)
            .forEach(option => {
                url.searchParams.append('tag_id', option.value);
            });

        console.log('url', url);
        const response = await fetch(
            url,
            // `/api/v1/anecdote?tag_ids=${selectedTags.join(',')}`,
            {headers: {'X-Session-Id': getSessionId()}}
        );
        const data = await response.json();
        anecdoteElement.innerText = data.anecdote;
        anecdoteElement.dataset.id = data.id;

        // Отображаем теги
        if (data.tags && data.tags.length > 0) {
            tagsElement.innerHTML = data.tags.map(tag => `<span class="badge badge-primary mr-1">${tag.name}</span>`).join('');
            tagsElement.style.display = 'block';
        }

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
    loadTags();
    fetchAnecdote();

    document.querySelectorAll('#ratingButtons button').forEach((button, index) => {
        button.addEventListener('click', () => rateAnecdote(index + 1));
    });

    // document.getElementById('tagSelect').addEventListener('change', fetchAnecdote);
});