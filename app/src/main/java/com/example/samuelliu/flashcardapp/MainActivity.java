package com.example.samuelliu.flashcardapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.plattysoft.leonids.ParticleSystem;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // Make database
    FlashcardDatabase flashcardDatabase;

    // List of flashcards
    List<Flashcard> allFlashcards;

    // counter for our current card
    int currentCardDisplayedIndex = 0;

    // for editing card
    Flashcard cardToEdit;

    // countdown timer
    CountDownTimer countDownTimer;

    /**
     * To start timer
     */
    private void startTimer() {
        countDownTimer.cancel();
        countDownTimer.start();
    }

    /**
     * Gets random number
     * @param minNumber lower bound
     * @param maxNumber upper bound
     * @return random number between min and max
     */
    public int getRandomNumber(int minNumber, int maxNumber) {
        Random rand = new Random();
        return rand.nextInt((maxNumber - minNumber) + 1) + minNumber;
    }

    @Override
    /**
     * On create method that runs everytime mainactivity is entered
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize database
        flashcardDatabase = new FlashcardDatabase(getApplicationContext());

        // initialize flashcard list
        allFlashcards = flashcardDatabase.getAllCards();

        // initialize countdown timer
        countDownTimer = new CountDownTimer(16000, 1000) {
            public void onTick(long millisUntilFinished) {
                ((TextView) findViewById(R.id.timer)).setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
            }
        };

        // start the timer
        startTimer();

        // check to see if we have any cards in the database
        if (allFlashcards != null && allFlashcards.size() > 0) {
            ((TextView) findViewById(R.id.Question)).setText(
                    allFlashcards.get(0).getQuestion());
            ((TextView) findViewById(R.id.prompt3)).setText(
                    allFlashcards.get(0).getAnswer());
            ((TextView) findViewById(R.id.prompt1)).setText(
                    allFlashcards.get(0).getWrongAnswer1());
            ((TextView) findViewById(R.id.prompt2)).setText(
                    allFlashcards.get(0).getWrongAnswer2());
        }

        // add gravity effect, it looks cool :)
        findViewById(R.id.Question).setElevation(20);

        // logic for when user clicks on answer option
        findViewById(R.id.prompt1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.prompt1).setBackgroundColor(Color.parseColor("#fc6565"));
                findViewById(R.id.prompt3).setBackgroundColor(Color.parseColor("#40d12a"));
                findViewById(R.id.prompt2).setBackgroundColor(Color.parseColor("#d2cced"));
            }
        });

        // logic for when user clicks on answer option
        findViewById(R.id.prompt3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // confetti
                new ParticleSystem(MainActivity.this, 100, R.drawable.confetti, 3000)
                        .setSpeedRange(0.2f, 0.5f)
                        .oneShot(findViewById(R.id.prompt3), 100);

                findViewById(R.id.prompt3).setBackgroundColor(Color.parseColor("#40d12a"));
                findViewById(R.id.prompt1).setBackgroundColor(Color.parseColor("#d2cced"));
                findViewById(R.id.prompt2).setBackgroundColor(Color.parseColor("#d2cced"));
            }
        });

        // logic for when user clicks on answer option
        findViewById(R.id.prompt2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.prompt2).setBackgroundColor(Color.parseColor("#FC6565"));
                findViewById(R.id.prompt3).setBackgroundColor(Color.parseColor("#40d12a"));
                findViewById(R.id.prompt1).setBackgroundColor(Color.parseColor("#d2cced"));
            }
        });

        // Makes answer options invisible
        findViewById(R.id.closed_eye).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.prompt2).setVisibility(View.INVISIBLE);
                findViewById(R.id.prompt3).setVisibility(View.INVISIBLE);
                findViewById(R.id.prompt1).setVisibility(View.INVISIBLE);
                findViewById(R.id.closed_eye).setVisibility(View.INVISIBLE);
                findViewById(R.id.open_eye).setVisibility(View.VISIBLE);
                findViewById(R.id.prompt2).setBackgroundColor(Color.parseColor("#d2cced"));
                findViewById(R.id.prompt3).setBackgroundColor(Color.parseColor("#d2cced"));
                findViewById(R.id.prompt1).setBackgroundColor(Color.parseColor("#d2cced"));
            }
        });

        // Makes answer options visible
        findViewById(R.id.open_eye).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.prompt2).setVisibility(View.VISIBLE);
                findViewById(R.id.prompt3).setVisibility(View.VISIBLE);
                findViewById(R.id.prompt1).setVisibility(View.VISIBLE);
                findViewById(R.id.closed_eye).setVisibility(View.VISIBLE);
                findViewById(R.id.open_eye).setVisibility(View.INVISIBLE);
            }
        });

        // Logic for when user wants to add card
        findViewById(R.id.addCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addCard = new Intent(MainActivity.this, AddCardActivity.class);
                startActivityForResult(addCard, 100);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        // Logic for when user wants to edit card
        findViewById(R.id.editCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // string of current card question
                String cardToEditString =
                        ((TextView) findViewById(R.id.Question)).getText().toString();

                // loop through all flashcards to find the right flashcard
                for (Flashcard f : allFlashcards) {
                    if (f.getQuestion().equals(cardToEditString)) {
                        cardToEdit = f;
                    }
                }

                Intent editTheCard = new Intent(MainActivity.this, AddCardActivity.class);
                TextView questionView = findViewById(R.id.Question);
                TextView answerView = findViewById(R.id.prompt3);
                TextView wrongAnswer1View = findViewById(R.id.prompt1);
                TextView wrongAnswer2View = findViewById(R.id.prompt2);
                editTheCard.putExtra("Question", questionView.getText());
                editTheCard.putExtra("Answer", answerView.getText());
                editTheCard.putExtra("WrongAnswer1", wrongAnswer1View.getText());
                editTheCard.putExtra("WrongAnswer2", wrongAnswer2View.getText());

                startActivityForResult(editTheCard, 200);
            }
        });

        // Logic for when user wants to see another card
        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // start timer
                startTimer();

                // import animation
                final Animation leftOutAnim = AnimationUtils.loadAnimation(v.getContext(),
                        R.anim.left_out);
                final Animation rightInAnim = AnimationUtils.loadAnimation(v.getContext(),
                        R.anim.right_in);

                // start the left out
                findViewById(R.id.Question).startAnimation(leftOutAnim);
                findViewById(R.id.prompt1).startAnimation(leftOutAnim);
                findViewById(R.id.prompt2).startAnimation(leftOutAnim);
                findViewById(R.id.prompt3).startAnimation(leftOutAnim);

                // anim listener
                leftOutAnim.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        // increment current card displayed
                        int localSize = allFlashcards.size() - 1;
                        if(localSize < 0) localSize = 0;
                        currentCardDisplayedIndex = getRandomNumber(0, localSize);

                        // if we reached last card already, reset current card displayed
                        if (currentCardDisplayedIndex > allFlashcards.size() - 1) {
                            currentCardDisplayedIndex = 0;
                        }

                        // update flashcard
                        if(allFlashcards.size() != 0) {
                            // set the question and answer TextViews with data from the database
                            ((TextView) findViewById(R.id.Question)).setText(
                                    allFlashcards.get(currentCardDisplayedIndex).getQuestion());
                            ((TextView) findViewById(R.id.prompt3)).setText(
                                    allFlashcards.get(currentCardDisplayedIndex).getAnswer());
                            ((TextView) findViewById(R.id.prompt1)).setText(
                                    allFlashcards.get(currentCardDisplayedIndex).getWrongAnswer1());
                            ((TextView) findViewById(R.id.prompt2)).setText(
                                    allFlashcards.get(currentCardDisplayedIndex).getWrongAnswer2());
                        }

                        // reset colors
                        ((TextView) findViewById(R.id.prompt1)).setBackgroundColor(
                                Color.parseColor("#d2cced"));
                        ((TextView) findViewById(R.id.prompt2)).setBackgroundColor(
                                Color.parseColor("#d2cced"));
                        ((TextView) findViewById(R.id.prompt3)).setBackgroundColor(
                                Color.parseColor("#d2cced"));

                        // start the rightIn Animation
                        findViewById(R.id.Question).startAnimation(rightInAnim);
                        findViewById(R.id.prompt1).startAnimation(rightInAnim);
                        findViewById(R.id.prompt2).startAnimation(rightInAnim);
                        findViewById(R.id.prompt3).startAnimation(rightInAnim);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // we don't need to worry about this method
                    }

                });

            }
        });

        // Logic for when user wants to delete a card
        findViewById(R.id.trash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // start timer
                startTimer();

                // delete card
                flashcardDatabase.deleteCard(
                        ((TextView) findViewById(R.id.Question)).getText().toString());

                // update list
                allFlashcards = flashcardDatabase.getAllCards();

                // increment current card displayed
                currentCardDisplayedIndex++;

                // if we reached last card already, reset current card displayed
                if (currentCardDisplayedIndex > allFlashcards.size() - 1) {
                    currentCardDisplayedIndex = 0;
                }

                // check if there are any cards left
                if(allFlashcards.size() == 0) {
                    ((TextView) findViewById(R.id.Question)).setText(R.string.no_cards);
                    ((TextView) findViewById(R.id.prompt3)).setText(R.string.edit_card);
                    ((TextView) findViewById(R.id.prompt1)).setText(R.string.create_more);
                    ((TextView) findViewById(R.id.prompt2)).setText(R.string.or);
                }
                else {
                    // set the question and answer TextViews with data from the database
                    ((TextView) findViewById(R.id.Question)).setText(
                            allFlashcards.get(currentCardDisplayedIndex).getQuestion());
                    ((TextView) findViewById(R.id.prompt3)).setText(
                            allFlashcards.get(currentCardDisplayedIndex).getAnswer());
                    ((TextView) findViewById(R.id.prompt1)).setText(
                            allFlashcards.get(currentCardDisplayedIndex).getWrongAnswer1());
                    ((TextView) findViewById(R.id.prompt2)).setText(
                            allFlashcards.get(currentCardDisplayedIndex).getWrongAnswer2());
                }

            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if ((requestCode == 100 || requestCode == 200) && data != null) {

            // start timer
            startTimer();

            // get new question and answer
            String question = data.getExtras().getString("question");
            String answer = data.getExtras().getString("answer");
            String wrongAnswer1 = data.getExtras().getString("wrongAnswer1");
            String wrongAnswer2 = data.getExtras().getString("wrongAnswer2");

            // set question and answer views
            TextView questionView = (TextView) findViewById(R.id.Question);
            questionView.setText(question);

            TextView answerView = (TextView) findViewById(R.id.prompt3);
            answerView.setText(answer);

            TextView wrongAnswer1View = (TextView) findViewById(R.id.prompt1);
            wrongAnswer1View.setText(wrongAnswer1);

            TextView wrongAnswer2View = (TextView) findViewById(R.id.prompt2);
            wrongAnswer2View.setText(wrongAnswer2);

            // reset colors
            ((TextView) findViewById(R.id.prompt1)).setBackgroundColor(
                    Color.parseColor("#d2cced"));
            ((TextView) findViewById(R.id.prompt2)).setBackgroundColor(
                    Color.parseColor("#d2cced"));
            ((TextView) findViewById(R.id.prompt3)).setBackgroundColor(
                    Color.parseColor("#d2cced"));



            // snackbar message based on the requestCode
            if(requestCode == 100) {

                // insert a card into the database
                flashcardDatabase.insertCard(new Flashcard(question, answer, wrongAnswer1, wrongAnswer2));

                // update list
                allFlashcards = flashcardDatabase.getAllCards();

                Snackbar.make(findViewById(R.id.Question),
                        "Card created successfully!",
                        Snackbar.LENGTH_SHORT)
                        .show();
            }

            else if(requestCode == 200) {
                Snackbar.make(findViewById(R.id.Question),
                        "Card edited successfully!",
                        Snackbar.LENGTH_SHORT)
                        .show();

                    // set questions
                    cardToEdit.setQuestion(question);
                    cardToEdit.setAnswer(answer);
                    cardToEdit.setWrongAnswer1(wrongAnswer1);
                    cardToEdit.setWrongAnswer2(wrongAnswer2);

                    flashcardDatabase.updateCard(cardToEdit);
            }

        }

    }
}
