package service;

import domain.Grade;
import domain.Pair;
import domain.Student;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import repository.GradeXMLRepository;
import repository.HomeworkXMLRepository;
import repository.StudentXMLRepository;

import java.nio.charset.Charset;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
public class ServiceMockTest {
    @Mock
    private static HomeworkXMLRepository homeworkXMLRepository;
    @Mock
    private static GradeXMLRepository gradeXMLRepository;
    @Mock
    private static StudentXMLRepository studentXMLRepository;

    private static Service service;

    @BeforeAll
    static void init() {

        studentXMLRepository = Mockito.mock(StudentXMLRepository.class);
        homeworkXMLRepository = Mockito.mock(HomeworkXMLRepository.class);
        gradeXMLRepository = Mockito.mock(GradeXMLRepository.class);

        service = new Service(studentXMLRepository, homeworkXMLRepository, gradeXMLRepository);
    }


    @Test
    void testSaveExistingCorrectStudent() {
        Student existingStudent = new Student("33", "Lado Lajos", 534);
        Mockito.when(studentXMLRepository.save(existingStudent)).thenReturn(new Student(existingStudent.getID(), "Mihaly Denes", 576));

        int returnValue = service.saveStudent(existingStudent.getID(), existingStudent.getName(), existingStudent.getGroup());
        assertEquals(0, returnValue);

        Mockito.verify(studentXMLRepository, Mockito.times(1)).save(existingStudent);
    }

    @Test
    void testSaveGradeForNonExistingStudent() {
        Grade grade = new Grade(new Pair<>("44", "44"), 2.3, 12, "Nem jo");

        Mockito.when(studentXMLRepository.findOne(grade.getID().getObject1())).thenReturn(null);

        int returnValue = service.saveGrade(
                grade.getID().getObject1(),
                grade.getID().getObject2(),
                grade.getGrade(),
                grade.getDeliveryWeek(),
                grade.getFeedback()
        );

        assertEquals(-1, returnValue);

        Mockito.verify(gradeXMLRepository, Mockito.times(0)).save(grade);

    }

    @Test
    void testDeleteExistingStudent() {
        Mockito.when(studentXMLRepository.delete(Mockito.anyString())).thenReturn(new Student("12", "Mihaly Denes", 576));

        byte[] array = new byte[7]; // length
        new Random().nextBytes(array);
        String generatedString = new String(array, Charset.forName("UTF-8"));

        int returnValue = service.deleteStudent(generatedString);

        assertNotEquals(0, returnValue);
    }

}