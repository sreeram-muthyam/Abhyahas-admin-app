package model;

public class ItemModel {
    private String Creater_ID,Course_Name,Course_Description,Course_Price,Course_image,Intro_Url,Course_PDF,Intro_Name,Enrolments;

    public ItemModel() {
    }

    public ItemModel(String creater_ID, String course_Name, String course_Description, String course_Price, String course_image, String intro_Url, String course_PDF, String intro_Name, String enrolments) {
        Creater_ID = creater_ID;
        Course_Name = course_Name;
        Course_Description = course_Description;
        Course_Price = course_Price;
        Course_image = course_image;
        Intro_Url = intro_Url;
        Course_PDF = course_PDF;
        Intro_Name = intro_Name;
        Enrolments = enrolments;
    }

    public String getEnrolments() {
        return Enrolments;
    }

    public void setEnrolments(String enrolments) {
        Enrolments = enrolments;
    }

    public String getIntro_Name() {
        return Intro_Name;
    }

    public void setIntro_Name(String intro_Name) {
        Intro_Name = intro_Name;
    }

    public String getCourse_PDF() {
        return Course_PDF;
    }

    public void setCourse_PDF(String course_PDF) {
        Course_PDF = course_PDF;
    }

    public String getCreater_ID() {
        return Creater_ID;
    }

    public void setCreater_ID(String creater_ID) {
        Creater_ID = creater_ID;
    }

    public String getCourse_Name() {
        return Course_Name;
    }

    public void setCourse_Name(String course_Name) {
        Course_Name = course_Name;
    }

    public String getCourse_Description() {
        return Course_Description;
    }

    public void setCourse_Description(String course_Description) {
        Course_Description = course_Description;
    }

    public String getCourse_Price() {
        return Course_Price;
    }

    public void setCourse_Price(String course_Price) {
        Course_Price = course_Price;
    }

    public String getCourse_image() {
        return Course_image;
    }

    public void setCourse_image(String course_image) {
        Course_image = course_image;
    }

    public String getIntro_Url() {
        return Intro_Url;
    }

    public void setIntro_Url(String intro_Url) {
        Intro_Url = intro_Url;
    }
}
