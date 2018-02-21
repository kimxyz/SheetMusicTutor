package vanwingerdenbarrier.sheetmusictutor.Game;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;

import vanwingerdenbarrier.sheetmusictutor.Key.KeyFragment;
import vanwingerdenbarrier.sheetmusictutor.NoteDefense.NoteDefense;
import vanwingerdenbarrier.sheetmusictutor.Quiz.QuizAnswerFragment;
import vanwingerdenbarrier.sheetmusictutor.Quiz.QuizQuestionFragment;
import vanwingerdenbarrier.sheetmusictutor.R;
import vanwingerdenbarrier.sheetmusictutor.StaffStructure.Note;
import vanwingerdenbarrier.sheetmusictutor.StaffStructure.StaffFragment;
import vanwingerdenbarrier.sheetmusictutor.UserInfo.UserList;

/**
 * @author Bronson VanWingerden
 * the game activity screen to display the Staff Fragment and the Key Fragment
 */
public class GameActivity extends FragmentActivity
        implements QuestionDisplay.Display, AnswerDisplay.Display {

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Fragment currentQuestion;
    Fragment currentAnswer;

    /**
     * indicates the current game mode
     * 0 = quiz,
     * 1 = staff,
     * 2 = combo
     */
    int mode;

    /**
     * rounds
     */

    int rounds;

    /**
     * Allows us to pass information between our fragments
     */
    public void questionPressed(Object correct) {

        if(correct == null){
            endQuestion();
        }else if(currentAnswer instanceof QuizAnswerFragment){
            ((QuizAnswerFragment) currentAnswer).setQuestion((int)correct);
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.answer_holder, currentAnswer);
            fragmentTransaction.commit();
        }

    }

    /**
     * Allows us to pass info between fragments
     * @param answer
     * @param event
     */
    public void answerPressed(Object answer, MotionEvent event) {

        if (currentQuestion instanceof StaffFragment && event != null) {
            ((StaffFragment) currentQuestion)
                    .colorNoteOnStaff(((StaffFragment) currentQuestion)
                            .getNoteAtCurrentLocation((Note) answer), event);
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                new UserList(this.getApplicationContext()).addUserAttempt();
            }
        }else if(currentQuestion instanceof QuizQuestionFragment){

            ((QuizQuestionFragment) currentQuestion).checkIfCorrect((String)answer);
            new UserList(this.getApplicationContext()).addUserAttempt();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rounds = 10;

        int gameType = getIntent().getIntExtra("gameType", -1);

        if (gameType == 1) {
            System.out.println("AAA GAMETYPE = STAFF" + gameType);
        } else if (gameType == 2) {
            System.out.println("AAA GAMETYPE = COMBO" + gameType);
        } else if (gameType == 3) {
            System.out.println("AAA GAMETYPE = NOTE DEFENSE");
        } else {
            System.out.println("AAA GAMETYPE = NOTFOUND" + gameType);
        }

        mode = gameType;

        fragmentManager = getSupportFragmentManager();

        Fragment staffFrag = new StaffFragment();
        Fragment keyFrag = new KeyFragment();

        addQuestion(staffFrag);
        addAnswer(keyFrag);
        setContentView(R.layout.activity_game);
    }

    /**
     * adds the passed fragment to the current question holder
     * @param fragment the fragment to add
     */
    public void addQuestion(Fragment fragment) {
        currentQuestion = fragment;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.question_holder, fragment);
        fragmentTransaction.commit();

    }

    /**
     * adds the passed fragment to the current answer holder
     * @param fragment the fragment to add
     */
    public void addAnswer(Fragment fragment) {
        currentAnswer = fragment;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.answer_holder, fragment);
        fragmentTransaction.commit();
    }

    /**
     * ends the current question
     */
    public void endQuestion() {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Good Job!");
        alertDialog.setMessage("you scored xx!");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {

                        if(rounds  <= 1){
                            finish();
                        }else{
                            makeNextQuestion();
                        }

                        dialogInterface.dismiss();
                    }
                });

        alertDialog.show();
    }

    /**
     * replaces the question with a new question fragment
     * @param fragment
     */
    public void replaceQuestion(Fragment fragment){
        currentQuestion = fragment;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.question_holder, fragment);
        fragmentTransaction.commit();
    }

    /**
     * replaces the answer witha new question fragment
     * @param fragment
     */
    public void replaceAnswer(Fragment fragment){
        currentAnswer = fragment;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.answer_holder, fragment);
        fragmentTransaction.commit();
    }

    /**
     * creates the next question
     */
    public void makeNextQuestion(){
        System.out.println(mode);
        if (mode == 1) {

            replaceQuestion(new StaffFragment());
            rounds--;

        }else if (mode == 2){

            if(currentQuestion instanceof QuizQuestionFragment && currentAnswer
                    instanceof QuizAnswerFragment){

                replaceQuestion(new StaffFragment());
                replaceAnswer(new KeyFragment());
            }else if(currentQuestion instanceof StaffFragment && currentAnswer instanceof KeyFragment){

                replaceQuestion(new QuizQuestionFragment());
                replaceAnswer(new QuizAnswerFragment());
            }
            rounds--;

        } else if (mode == 3) {
            replaceQuestion(new NoteDefense());

        } else if (mode == 4) {
            //TODO implement listening mode
        }
    }

}

