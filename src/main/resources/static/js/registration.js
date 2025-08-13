// Обработчик для формы регистрации
document.getElementById("registrationForm").addEventListener("submit", function (event) {
    event.preventDefault();

    const userData = {
        username: document.getElementById("username").value,
        name: document.getElementById("name").value,
        password: document.getElementById("password").value
    };

    fetch("/api/v1/users/registration", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(userData)
    })
        .then(response => {
            if (response.status === 201) {
                window.location.href = "/login";
            } else if (response.status === 409) {
                alert("Пользователь с таким логином уже существует");
            } else {
                alert("Ошибка регистрации");
            }
        })
        .catch(err => console.error("Ошибка:", err));
});
