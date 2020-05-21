package com.example.demo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.jdbc.core.JdbcTemplate;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class PonpokoController {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PonpokoController(JdbcTemplate jdbcTemplate){
        // よくわからないけどコンストラクタでjdbcを使えるようにするらしい
        this.jdbcTemplate = jdbcTemplate;
    }
    

    @RequestMapping("/")

    public String home(Model model) {
        // 初期メッセージ表示
        model.addAttribute("msg", "コギケツ or イヌヌワン");

        // 参考サイト
        // https://teachingprogramming.net/archives/537
        
        List<Map<String, Object>> dataList = jdbcTemplate.queryForList("SELECT * FROM hash_result"); // 過去の診断結果をデータベースから取り出す

        List<ResultItem> resultList = new ArrayList<>(); // 取り出したレコードを突っ込む配列
        // 取り出した診断結果を代入
        for (Map<String, Object> data : dataList) {
            ResultItem resultItem = new ResultItem();
            resultItem.hash_value = (int)data.get("hash_value");
            resultItem.result = (String)data.get("result");
            resultItem.date = (Date)data.get("date");
            resultList.add(resultItem);
        }
        model.addAttribute("itemList", resultList); // 診断結果一覧をモデルに渡す

        return "index";
    }


    @RequestMapping(value = "testform", method = RequestMethod.GET)
    public String judge(@RequestParam(name = "input_name") String name, Model model){

        model.addAttribute("name", name);

        // 入力文字列をSHA-1でハッシュ化(数値の配列が返ってくる)
        MessageDigest digest;
        try { // ハッシュ化メソッドはエラーを吐くのでキャッチできるようにしておく
            digest = MessageDigest.getInstance("SHA-1");

            byte[] hashedValue = digest.digest(name.getBytes());

            // ハッシュの一部を使って判定
            String result = "";
            switch(hashedValue[0] % 2){
            case 0:
                result = "コギケツ";
                break;
            case 1:
            case -1:  // byteがsignedらしいのでこの条件もいるっぽい
                result = "イヌヌワン";
                break;
            default: // ここには来ないはずだが一応書いておくと↑の条件分岐で失敗してるときに気づけるよ 
                result = "hoge";
            }

            // 診断結果をデータベースに突っ込む
            Date date = new Date();
            jdbcTemplate.update("INSERT INTO hash_result ( hash_value , result, date ) VALUES (?, ?, ?)", hashedValue[0], result, date);
       
        
            model.addAttribute("result", result);
            return "result";
        } catch (NoSuchAlgorithmException e) { // ここでエラーをキャッチ
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
            return "";
        }

    }


}
