package greenstory.rtg.com.classes;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Elad on 29/12/2017.
 */

public class Question {

    private String question;
    ArrayList<String> answers = new ArrayList<>();
    private int correctAnswer;
    private boolean isAnswered = false;
    private LatLng latLng;




    public Question(
                    String question,
                    ArrayList<String> answers,
                    int correctAnswer,
                    LatLng latLng) {

        this.question = question;
        this.answers = answers;
        this.correctAnswer = correctAnswer;
        this.isAnswered = false;
        this.latLng = latLng;

    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getQuestion() {
        return question;
    }

    public String getCorrectAnswer(int answerNum) {
        return answers.get(answerNum);
    }

    public void setIsAnswered(boolean isAnswered){
        this.isAnswered = isAnswered;
    }

    public boolean isAnswered(){
        return isAnswered;
    }

    @Override
    public String toString() {
        return "Question{" +
                "question='" + question + '\'' +
                ", correctAnswer='" + correctAnswer + '\'' +
                '}';
    }
}
