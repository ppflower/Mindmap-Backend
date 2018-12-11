package fudan.ss.mindmapbackend.controller.json_model;

import fudan.ss.mindmapbackend.model.StudentAnswer;

import java.util.List;

public class AssignmentShort_json {
    private String title;
    private String correct_answer;
    private List<StudentAnswer> studentAnswers;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCorrect_answer() {
        return correct_answer;
    }

    public void setCorrect_answer(String correct_answer) {
        this.correct_answer = correct_answer;
    }

    public List<StudentAnswer> getStudentAnswers() {
        return studentAnswers;
    }

    public void setStudentAnswers(List<StudentAnswer> studentAnswers) {
        this.studentAnswers = studentAnswers;
    }
}
