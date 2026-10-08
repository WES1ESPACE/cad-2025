# Лабораторная работа 1 (семестр 2). Spring Boot

**Студентка:** Севрюкова Валерия Сергеевна  
**Группа:** 12002453  

## Цель

Сделать простой REST API для сообщений на Spring Boot.

## Что сделала

Создала Maven-проект (Java 17, `starter-web`) и контроллер `MessageController`.

Основные методы:
- `GET /` — Hello, World!
- `GET /messages` — все сообщения
- `POST /messages` — добавить
- `PUT /messages/{index}` — изменить
- `DELETE /messages/{index}` — удалить

Доп. задания: очистка всего списка, получение по индексу, count, проверка на пустое/дубликат и лимит 200 символов.

## Запуск

```text
cd sem2/lab1
mvn spring-boot:run
```

Проверяла через браузер и Postman: `http://localhost:8080/messages`.  
Для POST тело в JSON, например `"Привет"`.

## Вывод

CRUD работает, доп. проверки тоже. В целом разобралась со Spring Boot и REST-контроллером.
