document.getElementById("loginForm").addEventListener("submit", function (event) {
    event.preventDefault(); // Отменяем стандартную отправку формы

    const loginData = {
        username: document.getElementById("username").value,
        password: document.getElementById("password").value
    };

    fetch("/api/v1/users/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(loginData)
    })
        .then(response => {
            if (response.ok) {
                console.log("Вход");
            } else if (response.status === 401) {
                alert("Неверный логин или пароль");
            } else {
                alert("Ошибка входа");
            }
        })
        .catch(err => console.error("Ошибка:", err));
});