# Лабораторная работа 4 (семестр 2). Spring Data JPA

**Студентка:** Севрюкова Валерия Сергеевна  
**Группа:** 12002453  

## Цель

Хранить задачи уже не в памяти, а в базе через JPA.

## Что сделала

1. Подключила `data-jpa` и драйверы.
2. `Task` сделала `@Entity`, репозиторий — `JpaRepository`.
3. Контроллер сохраняет через `taskRepository.save(...)`.
4. Дополнительно: категория, приоритет, время создания + таблица `SubTask` (подзадачи).

В методичке PostgreSQL. У меня локально подняла через H2 (файл `./data/todo_db`), настройки под postgres лежат в `application-postgres.properties`, если понадобится.

Логины те же: user / moderator / admin, пароль `password`.

## Запуск

```text
cd sem2/lab4
mvn spring-boot:run
```

Если есть postgres с базой `todo_db`:

```text
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

## Вывод

После перезапуска задачи не пропадают — всё лежит в БД. Подзадачи тоже сохраняются.
