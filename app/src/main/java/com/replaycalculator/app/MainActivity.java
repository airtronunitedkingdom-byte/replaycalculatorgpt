package com.replaycalculator.app;
import android.app.*;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.view.*;
import android.widget.*;
import org.json.*;
import java.util.*;
import java.util.regex.*;
public class MainActivity extends Activity {
 String expr=""; SharedPreferences prefs; JSONArray saved,history; LinearLayout body;
 JSONArray load(String k){try{return new JSONArray(prefs.getString(k,"[]"));}catch(Exception e){return new JSONArray();}}
 void persist(){prefs.edit().putString("saved",saved.toString()).putString("history",history.toString()).apply();}
 @Override public void onCreate(Bundle b){super.onCreate(b);prefs=getSharedPreferences("replay",0);saved=load("saved");history=load("history");show(0);}
 void button(LinearLayout p,String s,Runnable r){Button b=new Button(this);b.setText(s);p.addView(b,new LinearLayout.LayoutParams(0,-2,1));b.setOnClickListener(v->r.run());}
 void text(String s){TextView v=new TextView(this);v.setText(s);v.setTextSize(20);v.setPadding(15,12,15,12);body.addView(v);}
 void msg(String s){new AlertDialog.Builder(this).setMessage(s).setPositiveButton("OK",null).show();}
 void show(int tab){LinearLayout root=new LinearLayout(this);root.setOrientation(1);setContentView(root);TextView title=new TextView(this);title.setText("Replay Calculator");title.setTextSize(26);root.addView(title);LinearLayout nav=new LinearLayout(this);root.addView(nav);button(nav,"Calculator",()->show(0));button(nav,"Recipes",()->show(1));button(nav,"History",()->show(2));ScrollView sc=new ScrollView(this);root.addView(sc);body=new LinearLayout(this);body.setOrientation(1);sc.addView(body);if(tab==0)calc();if(tab==1)recipes();if(tab==2)history();}
 void calc(){text(expr.isEmpty()?"0":expr);String[][] rows={{"C","⌫","%","÷"},{"7","8","9","×"},{"4","5","6","−"},{"1","2","3","+"},{"(","0",".",")"}};for(String[] row:rows){LinearLayout line=new LinearLayout(this);body.addView(line);for(String s:row)button(line,s,()->{if(s.equals("C"))expr="";else if(s.equals("⌫")){if(!expr.isEmpty())expr=expr.substring(0,expr.length()-1);}else expr+=s;show(0);});}LinearLayout actions=new LinearLayout(this);body.addView(actions);button(actions,"Calculate",()->calculate(true));button(actions,"Save formula",this::saveRecipe);}
 void calculate(boolean record){try{String result=CalculatorEngine.calculate(expr);msg(expr+" = "+result);if(record){JSONObject o=new JSONObject();o.put("expression",expr);o.put("result",result);JSONArray n=new JSONArray();n.put(o);for(int i=0;i<history.length()&&i<99;i++)n.put(history.opt(i));history=n;persist();}}catch(Exception e){msg(e.getMessage());}}
 void saveRecipe(){try{CalculatorEngine.calculate(expr);}catch(Exception e){msg("Enter a valid calculation");return;}EditText input=new EditText(this);input.setHint("Formula name");new AlertDialog.Builder(this).setTitle("Save formula").setView(input).setNegativeButton("Cancel",null).setPositiveButton("Save",(d,w)->{try{JSONObject o=new JSONObject();o.put("name",input.getText().toString());o.put("expression",expr);saved.put(o);persist();}catch(Exception e){msg("Unable to save");}}).show();}
 void recipes(){text("Saved formulas");for(int i=saved.length()-1;i>=0;i--){JSONObject o=saved.optJSONObject(i);if(o==null)continue;text(o.optString("name")+" : "+o.optString("expression"));LinearLayout actions=new LinearLayout(this);body.addView(actions);button(actions,"Replay",()->replay(o));final int index=i;button(actions,"Delete",()->{JSONArray n=new JSONArray();for(int j=0;j<saved.length();j++)if(j!=index)n.put(saved.opt(j));saved=n;persist();show(1);});}}
 void replay(JSONObject o){String template=o.optString("expression");Pattern p=Pattern.compile("(?<![\\d.])(?:\\d+(?:\\.\\d+)?|\\.\\d+)");Matcher m=p.matcher(template);ArrayList<EditText> inputs=new ArrayList<>();LinearLayout form=new LinearLayout(this);form.setOrientation(1);while(m.find()){EditText e=new EditText(this);e.setText(m.group());e.setInputType(8194);form.addView(e);inputs.add(e);}new AlertDialog.Builder(this).setTitle("Replay formula").setView(form).setNegativeButton("Cancel",null).setPositiveButton("Apply",(d,w)->{Matcher a=p.matcher(template);StringBuffer b=new StringBuffer();int i=0;while(a.find()){String value=inputs.get(i++).getText().toString();if(!value.matches("-?(?:\\d+(?:\\.\\d*)?|\\.\\d+)")){msg("Invalid number");return;}a.appendReplacement(b,Matcher.quoteReplacement(value));}a.appendTail(b);expr=b.toString();show(0);}).show();}
 void history(){text("Calculation history");LinearLayout actions=new LinearLayout(this);body.addView(actions);button(actions,"Clear",()->{history=new JSONArray();persist();show(2);});for(int i=0;i<history.length();i++){JSONObject o=history.optJSONObject(i);if(o==null)continue;TextView t=new TextView(this);t.setText(o.optString("expression")+" = "+o.optString("result"));t.setTextSize(18);t.setPadding(15,14,15,14);body.addView(t);t.setOnClickListener(v->{expr=o.optString("expression");show(0);});}}
}
