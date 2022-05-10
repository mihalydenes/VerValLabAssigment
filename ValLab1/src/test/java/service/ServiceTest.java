package service;

import domain.Grade;
import domain.Homework;
import domain.Pair;
import domain.Student;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import repository.GradeXMLRepository;
import repository.HomeworkXMLRepository;
import repository.StudentXMLRepository;
import validation.*;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTest {
    private static Service service;

    @BeforeAll
    static void init() {
        Validator<Student> studentValidator = new StudentValidator();
        Validator<Homework> homeworkValidator = new HomeworkValidator();
        Validator<Grade> gradeValidator = new GradeValidator();

        StudentXMLRepository fileRepository1 = new StudentXMLRepository(studentValidator, "students.xml");
        HomeworkXMLRepository fileRepository2 = new HomeworkXMLRepository(homeworkValidator, "homework.xml");
        GradeXMLRepository fileRepository3 = new GradeXMLRepository(gradeValidator, "grades.xml");

        service = new Service(fileRepository1, fileRepository2, fileRepository3);

        // insert Student
        Student initialStudent = new Student("33", "Lado Lajos", 534);
        service.saveStudent(initialStudent.getID(), initialStudent.getName(), initialStudent.getGroup());

        // insert Student to Delete
        Student studentToDelete = new Student("55", "Alajos Marton", 534);
        service.saveStudent(studentToDelete.getID(), studentToDelete.getName(), studentToDelete.getGroup());
    }

    @Test
    void testSaveExistingCorrectStudent() {
        Student existingStudent = new Student("33", "Lado Lajos", 534);

        int returnValue = service.saveStudent(existingStudent.getID(), existingStudent.getName(), existingStudent.getGroup());
        assertEquals(0, returnValue);
    }

    @Test
    void testSaveGradeForNonExistingStudent() {
        Grade grade = new Grade(new Pair<>("44", "44"), 2.3, 12, "Nem jo");

        int returnValue = service.saveGrade(
                grade.getID().getObject1(),
                grade.getID().getObject2(),
                grade.getGrade(),
                grade.getDeliveryWeek(),
                grade.getFeedback()
        );

        assertEquals(-1, returnValue);

    }

    @Test
    void testDeleteExistingStudent() {
        Student studentToDelete = new Student("55", "Alajos Marton", 534);

        int returnValue = service.deleteStudent(studentToDelete.getID());

        assertNotEquals(0, returnValue);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 15})
    void testSaveHomeworkWithInvalidDeadline(int deadline) {
        Homework homeworkToInsert = new Homework("33", "New homework", deadline, 6);

        assertThrows(ValidationException.class, () -> {
            service.saveHomework(
                    homeworkToInsert.getID(),
                    homeworkToInsert.getDescription(),
                    homeworkToInsert.getDeadline(),
                    homeworkToInsert.getStartline()
            );
        });
    }

    @Test
    void testSaveHomeworkWithInvertedDeadlineStartLine() {
        Homework homeworkToInsert = new Homework("33", "New homework", 3, 6);

        assertThrows(ValidationException.class, () -> {
            service.saveHomework(
                    homeworkToInsert.getID(),
                    homeworkToInsert.getDescription(),
                    homeworkToInsert.getDeadline(),
                    homeworkToInsert.getStartline()
            );
        });
    }
}