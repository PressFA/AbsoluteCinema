document.getElementById("registrationForm").addEventListener("submit", function (event) {
    event.preventDefault();

    const userData = {
        username: document.getElementById("username").value,
        name: document.getElementById("name").value,
        password: document.getElementById("password").value
    };

    fetch("/auth/registration", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(userData)
    })
        .then(response => {
            if (response.status === 201) {
                // Успешная регистрация
                window.location.href = "/login";
            } else if (response.status === 409) {
                // Пользователь с таким логином уже существует
                alert("Пользователь с таким логином уже существует");
            } else if (response.status === 500) {
                // Внутренняя ошибка сервера
                alert("Не удалось зарегистрировать пользователя. Попробуйте позже.");
            } else {
                // Любая другая непредвиденная ошибка
                alert("Неизвестная ошибка регистрации (код " + response.status + ")");
            }
        })
        .catch(err => console.error("Ошибка запроса:", err));
});
