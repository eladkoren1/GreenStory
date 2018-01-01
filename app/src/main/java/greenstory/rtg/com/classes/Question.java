package greenstory.rtg.com.classes;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Elad on 29/12/2017.
 */

public class Question {

    private int questionID;
    private LatLng latLng;
    private String question;
    private String answerA;
    private String answerB;
    private String answerC;
    private String answerD;
    private String correctAnswer;
    private boolean isAnswered = false;

    public Question(int questionID,
                    LatLng latLng,
                    String question,
                    String answerA,
                    String answerB,
                    String answerC,
                    String answerD,
                    String correctAnswer,boolean isAnswered) {

        this.questionID = questionID;
        this.latLng = latLng;
        this.question = question;
        this.answerA = answerA;
        this.answerB = answerB;
        this.answerC = answerC;
        this.answerD = answerD;
        this.correctAnswer = correctAnswer;
        this.isAnswered = isAnswered;

    }

    public int getQuestionID() {
        return questionID;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswerA() {
        return answerA;
    }

    public String getAnswerB() {
        return answerB;
    }

    public String getAnswerC() {
        return answerC;
    }

    public String getAnswerD() {
        return answerD;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    @Override
    public String toString() {
        return "Question{" +
                "question='" + question + '\'' +
                ", answerA='" + answerA + '\'' +
                ", answerB='" + answerB + '\'' +
                ", answerC='" + answerC + '\'' +
                ", answerD='" + answerD + '\'' +
                ", correctAnswer='" + correctAnswer + '\'' +
                '}';
    }
}
