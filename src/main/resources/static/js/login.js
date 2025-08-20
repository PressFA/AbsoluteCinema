document.getElementById("loginForm").addEventListener("submit", function (event) {
    event.preventDefault(); // Отменяем стандартную отправку формы

    const loginData = {
        username: document.getElementById("username").value,
        password: document.getElementById("password").value
    };

    fetch("/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(loginData)
    })
        .then(response => {
            const data = response.json()

            if (response.ok) {
                console.log("Успешный вход");
            } else if (response.status === 401) {
                alert(data.message);
            } else {
                alert("Ошибка входа: " + (data.message || "неизвестная ошибка"));
            }
        })
        .catch(err => console.error("Ошибка:", err));
});