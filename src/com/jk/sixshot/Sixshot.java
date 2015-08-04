package com.jk.sixshot;

import java.util.ArrayList;
import java.util.List;

import com.jk.sixshot.organ.auditory.Listener;
import com.jk.sixshot.organ.language.Speaker;
import com.jk.sixshot.organ.language.StatementAnalyzer;

public class Sixshot {

	private Listener  listener = new Listener(this);
	private StatementAnalyzer analyzer = new StatementAnalyzer(); 
	private Speaker speaker = new Speaker();
	private boolean listening = false;
	private boolean speaking = false;
	/**
	 * 说话
	 * @param statement 语句
	 */
	private void speak(String statement){
		speaker.speak(statement);
	}
	public void analyze(String statement){
		List<Instruction> instructions = analyzer.analyze(statement);
		execute(instructions);
	}
	
	private void execute(List<Instruction> instructions){
		speaking = true;
		for(Instruction instruction:instructions){
			if(instruction.getType().equals(Instruction.INSTRUCTION_TYPE_SPEAK)){
				speak(instruction.getInstruction());
			}else{
				
			}
		}
		weakup();
	}
	
	public void weakup(){
		listening = true;
		listener.listen();
	}
	public static void main(String[] args) {
		Sixshot sixshot = new Sixshot();
		List<String> statements = new ArrayList<String>();
//		statements.add("《登鹳雀楼》，作者：盛唐诗人，王之涣。白日依山尽，黄河入海流。欲穷千里目，更上一层楼。");
//		statements.add("《黄鹤楼》，作者：唐代诗人，崔颢。昔人已乘黄鹤去，此地空余黄鹤楼。黄鹤一去不复返，白云千载空悠悠。晴川历历汉阳树，芳草萋萋鹦鹉洲。日暮乡关何处是，烟波江上使人愁。");
//		statements.add("你好！");
//		statements.add("我四岁了！");
//		statements.add("我叫豆豆！");
//		statements.add("属兔的！");
//		statements.add("你慢点说，太快了我听不懂！");
//		statements.add("不要逗我玩哦！？");
//		for(String statement: statements){
//			sixshot.speak(statement);
//		}
//		sixshot.analyze("你好！");
//		sixshot.analyze("豆豆！");
//		sixshot.analyze("你叫什么名字！");
//		sixshot.analyze("背首黄鹤楼！");
//		sixshot.analyze("背首枫桥夜泊吧！");
		sixshot.weakup();
	}
	
// 背诗
// 算数
// 唱歌
}
