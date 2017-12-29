package greenstory.rtg.com.data;

import android.provider.BaseColumns;

import static greenstory.rtg.com.data.GreenStoryDbHelper.OP_CLOSE_PARENTHESIS;
import static greenstory.rtg.com.data.GreenStoryDbHelper.OP_CREATE_TABLE;
import static greenstory.rtg.com.data.GreenStoryDbHelper.OP_OPEN_PARENTHESIS;
import static greenstory.rtg.com.data.GreenStoryDbHelper.TYPE_INTEGER;
import static greenstory.rtg.com.data.GreenStoryDbHelper.TYPE_INTEGER_PRIMARY_KEY_AUTOINCREMENT;
import static greenstory.rtg.com.data.GreenStoryDbHelper.TYPE_TEXT;
import static greenstory.rtg.com.data.GreenStoryDbHelper.TYPE_TEXT_NOT_NULL;


/**
 * Created by Elad on 20/12/2017.
 */
public class QuestionsContract {


    public static final class QuestionEntry implements BaseColumns {

        public static final String TABLE_NAME = "questions";
        public static final String COLUMN_QUESTION = "question";
        public static final String COLUMN_ANS1 = "answer_a";
        public static final String COLUMN_ANS2 = "answer_b";
        public static final String COLUMN_ANS3 = "answer_c";
        public static final String COLUMN_ANS4 = "answer_d";
        public static final String COLUMN_CORRENT_ANS = "correct_answer";


        public static final String SQL_CREATE_USERS_TABLE = OP_CREATE_TABLE + TABLE_NAME + OP_OPEN_PARENTHESIS +
                BaseColumns._ID + TYPE_INTEGER_PRIMARY_KEY_AUTOINCREMENT +
                COLUMN_QUESTION + TYPE_TEXT +
                COLUMN_ANS1 + TYPE_TEXT +
                COLUMN_ANS2 + TYPE_TEXT +
                COLUMN_ANS3 + TYPE_TEXT +
                COLUMN_ANS4 + TYPE_TEXT +
                COLUMN_CORRENT_ANS + " TEXT " +
                OP_CLOSE_PARENTHESIS;





    }


}







