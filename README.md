## Инструкция запуска

### 1. Клонирование репозитория

```bash
git clone https://github.com/1nvokexdd/Bank_REST_test
cd Bank_REST_test
```

### 2. Сборка и запуск проекта

```bash
mvn clean package -DskipTests
docker-compose up -d
```

---

## 🌐 Доступы

- Базовый URL: [http://localhost:8080](http://localhost:8080)
- Swagger UI: [http://localhost:8080/swagger-ui/index.html#/](http://localhost:8080/swagger-ui/index.html#/)

---

## Начальные данные созданные в бд

### Администратор

| Поле         | Значение                                                                                  |
| ------------ | ----------------------------------------------------------------------------------------- |
| username     | `user_ade1a2a6d161` (сгенерирован из номера телефона)                                     |
| first_name   | `Admin`                                                                                   |
| last_name    | `Ya`                                                                                      |
| password     | `Qwe123456` _(хеширован: `$2a$10$JqWhml.nylfbgvH8hx1f4.Ol/bjVgYjnELV.3/w8T1Dbkn6pCcZR2`)_ |
| phone_number | `+79991112233`                                                                            |
| role         | `ROLE_ADMIN`                                                                              |

---

### Пользователь

| Поле         | Значение                                                                                  |
| ------------ | ----------------------------------------------------------------------------------------- |
| username     | `user_ade1a2a6d161` (сгенерирован из номера телефона)                                     |
| first_name   | `User`                                                                                    |
| last_name    | `Ya`                                                                                      |
| password     | `Qwe654321` _(хеширован: `$2a$10$f9zglhY2TG.eGAOo15GDNewqiAvN8CxK.er2BtAeF61yVYYnlg9/C`)_ |
| phone_number | `+79991112239`                                                                            |
| role         | `ROLE_USER`                                                                               |

---

### Карта пользователя

| Поле                  | Значение                                                                                   |
| --------------------- | ------------------------------------------------------------------------------------------ |
| bin                   | `491684`                                                                                   |
| last_four             | `6486`                                                                                     |
| encrypted_card_number | `DvMoZaRkLhme+YyXBsig4uWM5iSSoMLx8CJouDkkBhLmlu9n2r/HAInjnaPbSboqBkTjBM8YAYspf37RVvyXfw==` |
| cvv                   | `589`                                                                                      |
| create_date           | `CURRENT_DATE`                                                                             |
| expiration_date       | `2028-07-11`                                                                               |
| status                | `ACTIVE`                                                                                   |
| ballance              | `0.00`                                                                                     |
| user_id               | `2`                                                                                        |

---

## ➕ Дополнительно

- Можно создать еще одного администратора с номером телефона `+79991112234` (остальные данные — любые).
- Также можно создать пользователя и выдать ему роль администратора через существующего админа.

---

## 📫 Контакты

**Email:** [miskridevelop@gmail.com](mailto:miskridevelop@gmail.com)

🥶
