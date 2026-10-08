# Лабораторная работа 6 (семестр 2). Android-клиент

**Студентка:** Севрюкова Валерия Сергеевна  
**Группа:** 12002453  

## Цель

Клиент под Android к тому же `/api`, что в lab5.

## Что сделала

Проект `TodoAndroid`:
- Retrofit + Gson
- `LoginActivity` — логин, Base64
- `TaskManagerActivity` + `RecyclerView`
- `network_security_config.xml` для `10.0.2.2`

Сервер при этом должен быть запущен из `sem2/lab5/server`.

## Как открыть

В Android Studio: Open → `sem2/lab6/TodoAndroid`, эмулятор, Run.  
Логин как на сервере, например `user` / `password`.

## Вывод

С эмулятора список задач подтягивается. Если сервер не запущен — приложение пишет, что нет связи.
