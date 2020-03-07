package com.example.demo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class PonpokoController {
    @RequestMapping("/")

    public String home(Model model) {
	model.addAttribute("msg", "コギケツ or イヌヌワン");
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

        model.addAttribute("result", result);
        return "result";
    } catch (NoSuchAlgorithmException e) { // ここでエラーをキャッチ
        // TODO 自動生成された catch ブロック
        e.printStackTrace();
        return "";
    }

    }


}
