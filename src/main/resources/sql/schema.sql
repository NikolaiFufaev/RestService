DROP TABLE IF EXISTS student CASCADE;
DROP TABLE IF EXISTS coordinator CASCADE;
DROP TABLE IF EXISTS course CASCADE;
DROP TABLE IF EXISTS student_course CASCADE;

CREATE TABLE IF NOT EXISTS coordinator
(
    coordinator_id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    coordinator_name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS course
(
    course_id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    course_name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS student
(
    student_id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    student_name   VARCHAR(255) NOT NULL,
    coordinator_id BIGINT REFERENCES coordinator (coordinator_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS student_course
(
    student_course_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    student_id BIGINT REFERENCES student (student_id) ON DELETE CASCADE,
    course_id  BIGINT REFERENCES course (course_id) ON DELETE CASCADE,
    CONSTRAINT unique_link UNIQUE (student_id, course_id)
);

INSERT INTO course(course_name)
VALUES ('Физика'),
       ('Математика'),
       ('Информатика');

INSERT INTO coordinator(coordinator_name)
VALUES ('Степан'),
       ('Игорь'),
       ('Сергей');

INSERT INTO student(student_name, coordinator_id)
VALUES ('Николай', 1),
       ('Александр', 1),
       ('Иван', 2),
       ('Ольга', 3);

INSERT INTO student_course(student_id, course_id)
VALUES (1, 1),
       (1, 2),
       (2, 1),
       (3, 3),
       (4, 1),
       (3, 2),
       (4, 2);




