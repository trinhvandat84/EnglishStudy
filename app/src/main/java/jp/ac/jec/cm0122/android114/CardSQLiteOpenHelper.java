package jp.ac.jec.cm0122.android114;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;


public class CardSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "CARD_DB";
    private static final int version = 1;
    public static final String TABLE_NAME = "CARD";

    public CardSQLiteOpenHelper(@Nullable Context context) {
        super(context, DB_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //データのIDのカラム名は"_id"としておく
        //主キー指定: PRIMARY KEY (cột được chỉ định làm khoá chính. Chức năng là cột đó là mỗi giá trị của nó là duy nhất và không thể trống (không null). PRIMARY KEY được sử dụng để xác định một cách duy nhất mỗi bản ghi trong bảng.
        //自動インクリメント: KEY AUTOINCREMENT ・・・データが追加されたときに自動で_idが付番される (Cứ mỗi lần thêm dữ liệu thì id nó cứ cộng thêm 1)
        db.execSQL("CREATE TABLE " +
                TABLE_NAME + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "english TEXT, japanese TEXT)");


        // thêm dữ liệu mặc định vào bảng CARD
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (1, 'apple', 'リンゴ')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (2, 'banana', 'バナナ')");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (3, 'lemon', 'レモン')");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<Card> getAllCard() {
        ArrayList<Card> ary = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        if (db == null) {
            return null;
        }

        try {
            //取得したいデータのカラムをString配列で用意する
            //左から順にカラムの要素インデックスは0, 1,2・・・となっている
            //後にCursorオブジェクトから行データを取り出した時のカラムインデックスと紐づいている
            String[] column = new String[]{"japanese", "english", "_id"};

            //queryメソッドで検索実行
            //第１引数: table: テーブル名
            //第2引数: columns: 取得する列名の配列
            //第3引数: where　選択条件 指定例: "japanese like ?"
            //第4引数: where_args: 選択条件の?を変換する文字列の配列  指定例: "banana%"
            //第5引数：groupBy: 集計条件(Group By)
            //第6引数: having: 選択条件(HAVING)
            //第7引数: orderBy: ソート条件(ORDER BY) 指定例: "id DESC" または "id ASC"
            Cursor cursor = db.query(TABLE_NAME, column, null, null, null, null, null, null);
            //取得したデータはすべてcursorオブジェクトに入っているので中身を順次取り出します
            //Cursorの中に次の行データがあればmoveToNext()の戻り値はtrueが返る
            while (cursor.moveToNext()) { //行データがなくなるまでwhile文で回す
                Card tmp = new Card(cursor.getString(0), cursor.getString(1), cursor.getInt(2));
                ary.add(tmp);
            }
            //Cursorオブジェクトは必ずcloseする
            cursor.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
//        } finally {
//            //SQLiteDatabaseオブジェクトをcloseするのも忘れずに
//            db.close();
//        }
        return ary;
    }
}
