package com.lc.df.studentapp.studentinfo;

import com.lc.df.studentapp.model.StudentPojo;
import com.lc.df.studentapp.testbase.TestBase;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.core.annotations.Title;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.lc.df.studentapp.utils.TestUtils.getRandomValue;
import static org.hamcrest.Matchers.hasValue;
import static org.junit.Assert.assertThat;


@RunWith(SerenityRunner.class)
public class StudentCURDTest extends TestBase {

    static String firstName = "" + getRandomValue();
    static String lastName = "" + getRandomValue();
    static String programme = "Student App Testing";
    static String email = "xyz" + getRandomValue() + "@gmail.com";


    static int studentId;


    @Title("This test will create a new student")
    @Test
    public void test001() {

        List<String> courses = new ArrayList<>();
        courses.add("Serenity");
        courses.add("ResAssured");
        courses.add("API Automation");

        StudentPojo studentPojo = new StudentPojo();
        studentPojo.setFirstName(firstName);
        studentPojo.setLastName(lastName);
        studentPojo.setEmail(email);
        studentPojo.setProgramme(programme);
        studentPojo.setCourses(courses);


        SerenityRest.rest()
                .given()
                .header("Content-Type", "application/json")
                .log()
                .all()

                .when()
                .body(studentPojo)
                .post()

                .then()
                .log()
                .all()
                .statusCode(201);

    }

    @Title("Verify if the student was added to the application")
    @Test
    public void test002() {
        //declared these as variable to avoid repeating common code while declaring path
        String p1 = "findAll{it.firstName=='";
        String p2 = "'}.get(0)";

        HashMap<String, Object> value = SerenityRest.rest()
                .given()

                .when()
                .get("/list")

                .then()
                .statusCode(200)
                .extract()
                .path(p1 + firstName + p2);

        //verifying that the student was added
        assertThat(value, hasValue(firstName));

        //casting studentId to int as value returns a object to use as endpoint for the other tests in end to end tests
        studentId = (int) value.get("id");
    }

    @Title("Update the user information and verify the update information")
    @Test
    public void test003() {
        String p1 = "findAll{it.firstName=='";
        String p2 = "'}.get(0)";

        //update the existing 1st name as its an end to end test
        firstName = firstName + "_putRequest";

        List<String> courses = new ArrayList<>();
        courses.add("Rename");
        courses.add("Courses");

        StudentPojo studentPojo = new StudentPojo();
        studentPojo.setFirstName(firstName);
        studentPojo.setLastName(lastName);
        studentPojo.setEmail(email);
        studentPojo.setProgramme(programme);
        studentPojo.setCourses(courses);

        SerenityRest.rest()
                .given()
                .header("Content-Type", "application/json")
                .log()
                .all()

                .when()
                .body(studentPojo)
                .put("/" + studentId)

                .then()
                .log()
                .all()
                .statusCode(200);

        HashMap<String, Object> value =
                SerenityRest.rest()
                        .given()
                        .log()
                        .body()

                        .when()
                        //verify that the student record is updated by the above request from this test
                        .get("/list")

                        .then()
                        .statusCode(200)
                        .extract()
                        .path(p1 + firstName + p2);

        assertThat(value, hasValue(firstName));

    }

    @Title("Delete the student and verify if the student is deleted!")
    @Test
    public void test004() {
        SerenityRest.rest()
                .given()

                .when()
                .delete("/" + studentId)

                .then()
                .statusCode(204);

        SerenityRest.rest()
                .given()

                .when()
                //verify student deleted in previous request sent in this test above
                .get("/" + studentId)

                .then()
                .statusCode(404);

    }

}
