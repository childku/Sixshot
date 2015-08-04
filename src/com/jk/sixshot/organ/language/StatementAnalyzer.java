package com.jk.sixshot.organ.language;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.jk.sixshot.Instruction;

public class StatementAnalyzer {
	private Map<String, List<String>> abc = new HashMap<String, List<String>>();
	
	private Map<String, String> poems = new HashMap<String, String>();
	
	private Random random = new Random();
	
	public StatementAnalyzer(){
		initPoems();
		
		List<String> statements = null;
		
		statements = new ArrayList<String>();
		statements.add("呜呜，说慢点，我没听懂！");
		statements.add("别说太快啊，太快了我跟不上！");
		statements.add("慢点说，别急！");
		statements.add("你这话是什么意思？");
		statements.add("这...这...");
		abc.put("不知道怎么说", statements);
		
		statements = new ArrayList<String>();
		statements.add("你好！");
		statements.add("诶，在！");
		statements.add("在呢！");
		statements.add("啥事儿？");
		abc.put("豆豆", statements);
		
		statements = new ArrayList<String>();
		statements.add("你好！");
		statements.add("你好呀！");
		statements.add("你好，我是豆豆！");
		statements.add("你好，有什么需要帮助的么？虽然我帮不了你太多。");
		abc.put("问好", statements);
		
		statements = new ArrayList<String>();
		statements.add("我叫豆豆！");
		statements.add("我叫豆豆，黄豆的豆儿！");
		statements.add("我叫豆豆，你呢！？");
		statements.add("恩，我叫豆豆。");
		abc.put("名字", statements);
		
		statements = new ArrayList<String>();
		statements.add("这首诗我还没学会哦，等我学会了在背给你听吧！");
		statements.add("这是谁的诗啊，我怎么没学过！");
		statements.add("真的有这首诗么？我怎么没学过呢？");
		statements.add("恩--恩，那个不好意思啊，你看要不我给你背首《登鹳雀楼》怎么样？");
		abc.put("没有这首诗", statements);
	}
	
	private void initPoems(){
		poems.put("登鹳雀楼", "《登鹳雀楼》，作者：盛唐诗人，王之涣。白日依山尽，黄河入海流。欲穷千里目，更上一层楼。");
		
		poems.put("黄鹤楼", "《黄鹤楼》，作者：唐代诗人，崔颢。昔人已乘黄鹤去，此地空余黄鹤楼。黄鹤一去不复返，白云千载空悠悠。晴川历历汉阳树，芳草萋萋鹦鹉洲。日暮乡关何处是，烟波江上使人愁。");
		
		poems.put("枫桥夜泊", "《枫桥夜泊》，作者：唐代诗人，张继。月落乌啼霜满天，江枫渔火对愁眠。姑苏城外寒山寺，夜半钟声到客船。");
	}
	
	public List<Instruction> analyze(String statement){
		List<Instruction> instructions = new ArrayList<Instruction>();
		Instruction instruction = null;
		if(statement.contains("豆豆")){
			instruction = new Instruction();
			instruction.setInstruction(getStatement("豆豆"));
		}else if(statement.contains("你好")){
			instruction = new Instruction();
			instruction.setInstruction(getStatement("问好"));
		}else if(statement.contains("名字")){
			instruction = new Instruction();
			instruction.setInstruction(getStatement("名字"));
		}else if(statement.contains("北首")||
				statement.contains("被首")||
				statement.contains("背首")||
				statement.contains("被手")){
			String action = null;
			if(statement.contains("北首")){
				action = "北首";
			}else if(statement.contains("被首")){
				action = "被首";
			}else if(statement.contains("背首")){
				action = "背首";
			}else if(statement.contains("背首")){
				action = "背首";
			}
			
			String poemName = statement.substring(statement.indexOf(action));
			poemName = poemName.replace(action, "");
			poemName = poemName.replace("吧", "");
			poemName = poemName.replace("！", "");
			poemName = poemName.replace("。", "");
			poemName = poemName.replace("，", "");
			
			instruction = new Instruction();
			instruction.setInstruction(getPoem(poemName));
		}else{
			instruction = new Instruction();
			instruction.setInstruction(getStatement("不知道怎么说"));
		}
		
		instructions.add(instruction);
		return instructions;
	}
	
	private String getStatement(String key){
		List<String> statements = null;
		String statement = null;
		
		statements = abc.get(key);
		if(statements==null||statements.isEmpty()){
			statement = getStatement("不知道怎么说");
		}else{
			statement = statements.get(random.nextInt(statements.size()));
		}
		return statement;
	}
	private String getPoem(String poemName){
		System.out.println("----poem name is : " + poemName);
		String poem = null;
		poem = poems.get(poemName);
		if(poem == null){
			poem = getStatement("没有这首诗");
		}
		return poem;
	}
	
	
}
