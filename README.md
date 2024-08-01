Задача:

1) Сделать REST Service с использованием JDBC и Servlet;
2) CRUD сервис, который имеет следующие сущности:

-----------------------------------------

- Course


- Идентификатор
- Название
- Список студентов (ManyToMany)

-----------------------------------------

- Student

- Идентификатор
- Имя
- Координатор
- Список курсов (ManyToMany)

-----------------------------------------

- Coordinator


- Идентификатор
- Имя
- Список студентов (OneToMany)

3) Запрещено использовать Spring, Hibernate;
4) Должны быть реализованы не ленивые связи OneToMany, ManyToMany;
5) Сервлет должен принимать и возвращать DTO (не возвращаем Entity);
6) Должны быть unit тесты, использовать Mockito и Junit;
7) Для проверки работы репозитория (DAO) с БД использовать интеграционное тестирование с помощью testcontainers (не
   забывайте убедиться, что подключение в тестах происходит именно к testcontainer, а не к основной СУБД);
8) Покрытие тестами должно быть больше 80%;
9) СУБД Postgres;
10) Рекомендуется использование Lombok и плагина SonarLint;
11) Не забывайте про интерфейсы, Джавадоки, логирование;
12) Для инжекта пароля и логина используйте проперти;

### Course:

GET http://localhost:8080/RestService_war/course/all - получить все курсы

GET http://localhost:8080/RestService_war/course/{courseId} - получить курс с {courseId}

POST http://localhost:8080/RestService_war/course - создать новый курс

{
"name": "New course name"
}

DELETE http://localhost:8080/RestService_war/course/{courseId} - удалить курс с {courseId}

PUT http://localhost:8080/RestService_war/course - изменить курс

{
"id": 1,
"name": "New course name"
}

DELETE http://localhost:8080/RestService_war/course/{courseId}/deleteStudent/{studentId} - удалить студента из курса 

PUT http://localhost:8080/RestService_war/course/{courseId}/addStudent/{studentId} - добавить студента на курс
### Coordinator:

GET http://localhost:8080/RestService_war/coordinator/all - получить всех координаторов

GET http://localhost:8080/RestService_war/coordinator/{coordinatorId} - получить координатора {coordinatorId}

POST http://localhost:8080/RestService_war/coordinator - сохранить в базу нового координатора
{
"name": "new coordinator"
}

DELETE http://localhost:8080/RestService_war/coordinator/{coordinatorId} - удалить координатора {coordinatorId}

PUT http://localhost:8080/RestService_war/coordinator - изменить имя координатора

{
"id": 2,
"name": "new name"
}

### Student:

GET http://localhost:8080/RestService_war/student/all - получить всех студентов

GET http://localhost:8080/RestService_war/student/{studentId} - получить студента {studentId}

DELETE http://localhost:8080/RestService_war/student/{studentId} - удалить студента {studentId}

POST http://localhost:8080/RestService_war/student - добавить в базу нового студента

{
"name": "New name"
}

PUT http://localhost:8080/RestService_war/student - изменить студента

{
"id": 1,
"name": "Иван Edit2333344"
}


PUT http://localhost:8080/RestService_war/student/{studentId}/addCoordinator/{coordinatorId} - добавить студенту координатора

