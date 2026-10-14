package com.replaycalculator.app;
import android.app.*;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.widget.*;
import android.text.*;
import org.json.*;
import java.util.*;
import java.util.regex.*;
public class MainActivity extends Activity {
 String expr=""; SharedPreferences prefs; JSONArray saved,history; LinearLayout body; TextView expressionView,resultView; int activeTab=0;
 JSONArray load(String k){try{return new JSONArray(prefs.getString(k,"[]"));}catch(Exception e){return new JSONArray();}}
 void persist(){prefs.edit().putString("saved",saved.toString()).putString("history",history.toString()).apply();}
 @Override public void onCreate(Bundle b){super.onCreate(b);prefs=getSharedPreferences("replay",0);saved=load("saved");history=load("history");expr=prefs.getString("draft_expression","");show(0);}
 int bg=0xff0B1020,card=0xff1B2440,purple=0xff8068EF,muted=0xffB4C1DC;
 GradientDrawable shape(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(radius);return d;}
 void button(LinearLayout p,String s,Runnable r){
  TextView b=new TextView(this);b.setText(s);b.setGravity(Gravity.CENTER);b.setTextSize(19);b.setTypeface(null,Typeface.BOLD);
  boolean action=s.equals("Calculate")||s.equals("Replay")||s.equals("Save formula");
  boolean operator=s.equals("+")||s.equals("−")||s.equals("×")||s.equals("÷")||s.equals("%");
  b.setTextColor(Color.WHITE);b.setBackground(shape(action?purple:operator?0xff384274:card,22));b.setElevation(3*getResources().getDisplayMetrics().density);
  LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-2,1);
  lp.setMargins(4,5,4,5);p.addView(b,lp);b.setMinHeight((int)(56*getResources().getDisplayMetrics().density));
  b.setPadding(7,12,7,12);b.setOnClickListener(v->r.run());b.setContentDescription(s.equals("⌫")?"Backspace, hold to clear":s);if(s.equals("⌫"))b.setOnLongClickListener(v->{expr="";updateDisplay();return true;});
 }
 void copyText(String value){ClipboardManager clipboard=(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);clipboard.setPrimaryClip(ClipData.newPlainText("Replay Calculator",value));Toast.makeText(this,"Copied to clipboard",Toast.LENGTH_SHORT).show();}
 void navButton(LinearLayout nav,String label,int tab){TextView v=new TextView(this);v.setText(label);v.setGravity(Gravity.CENTER);v.setTextColor(activeTab==tab?Color.WHITE:muted);v.setTypeface(null,Typeface.BOLD);v.setTextSize(15);v.setBackground(shape(activeTab==tab?purple:card,18));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-2,1);lp.setMargins(4,4,4,4);nav.addView(v,lp);v.setPadding(4,16,4,16);v.setOnClickListener(w->{if(activeTab!=tab)show(tab);});}
 void text(String s){TextView v=new TextView(this);v.setText(s);v.setTextColor(Color.WHITE);v.setTextSize(22);v.setPadding(20,22,20,22);v.setBackground(shape(card,24));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.setMargins(6,8,6,16);body.addView(v,lp);}
 void msg(String s){new AlertDialog.Builder(this).setMessage(s).setPositiveButton("OK",null).show();}
 void show(int tab){activeTab=tab;LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setBackgroundColor(bg);root.setPadding(12,20,12,8);getWindow().setStatusBarColor(bg);getWindow().setNavigationBarColor(bg);setContentView(root);TextView title=new TextView(this);title.setText("Replay Calculator");title.setLetterSpacing(0.02f);title.setTextSize(26);title.setTextColor(Color.WHITE);title.setTypeface(null,Typeface.BOLD);title.setPadding(10,12,10,18);root.addView(title);LinearLayout nav=new LinearLayout(this);root.addView(nav);navButton(nav,"Calculator",0);navButton(nav,"Recipes",1);navButton(nav,"History",2);ScrollView sc=new ScrollView(this);root.addView(sc);body=new LinearLayout(this);body.setOrientation(1);sc.setFillViewport(true);sc.addView(body);if(tab==0)calc();if(tab==1)recipes();if(tab==2)history();}
 void calc(){LinearLayout display=new LinearLayout(this);display.setOrientation(1);display.setPadding(20,18,20,18);display.setBackground(shape(card,24));LinearLayout.LayoutParams dlp=new LinearLayout.LayoutParams(-1,-2);dlp.setMargins(6,10,6,18);body.addView(display,dlp);expressionView=new TextView(this);expressionView.setTextColor(Color.WHITE);expressionView.setTextSize(30);expressionView.setMaxLines(3);expressionView.setGravity(Gravity.RIGHT);display.addView(expressionView);resultView=new TextView(this);resultView.setTextColor(muted);resultView.setTextSize(22);resultView.setTypeface(null,Typeface.BOLD);resultView.setGravity(Gravity.RIGHT);display.addView(resultView);resultView.setPadding(0,12,0,4);resultView.setOnClickListener(v->{try{copyText(CalculatorEngine.calculate(expr));}catch(Exception e){Toast.makeText(this,"No valid result to copy",Toast.LENGTH_SHORT).show();}});updateDisplay();String[][] rows={{"C","⌫","%","÷"},{"7","8","9","×"},{"4","5","6","−"},{"1","2","3","+"},{"(","0",".",")"}};for(String[] row:rows){LinearLayout line=new LinearLayout(this);body.addView(line);for(String s:row)button(line,s,()->{if(s.equals("C"))expr="";else if(s.equals("⌫")){if(!expr.isEmpty())expr=expr.substring(0,expr.length()-1);}else expr+=s;updateDisplay();});}LinearLayout actions=new LinearLayout(this);body.addView(actions);button(actions,"Calculate",()->calculate(true));button(actions,"Save formula",this::saveRecipe);TextView tip=new TextView(this);tip.setText("Tap result to copy  •  Hold ⌫ to clear all");tip.setTextColor(muted);tip.setTextSize(13);tip.setGravity(Gravity.CENTER);tip.setPadding(8,12,8,8);body.addView(tip);}
 void updateDisplay(){prefs.edit().putString("draft_expression",expr).apply();if(expressionView==null||resultView==null)return;expressionView.setText(expr.isEmpty()?"0":expr);try{resultView.setText(expr.isEmpty()?"Ready":"= "+CalculatorEngine.calculate(expr));}catch(Exception e){resultView.setText("Enter expression");}}
 void calculate(boolean record){try{String result=CalculatorEngine.calculate(expr);if(activeTab==0&&resultView!=null)resultView.setText("= "+result);if(record){JSONObject o=new JSONObject();o.put("expression",expr);o.put("result",result);JSONArray n=new JSONArray();n.put(o);for(int i=0;i<history.length()&&i<99;i++)n.put(history.opt(i));history=n;persist();Toast.makeText(this,"Added to history",Toast.LENGTH_SHORT).show();}}catch(Exception e){msg(e.getMessage());}}
 void saveRecipe(){final String formula=expr;try{CalculatorEngine.calculate(formula);}catch(Exception e){msg("Enter a valid calculation");return;}EditText input=new EditText(this);input.setHint("Formula name");input.setSingleLine(true);AlertDialog dialog=new AlertDialog.Builder(this).setTitle("Save formula").setView(input).setNegativeButton("Cancel",null).setPositiveButton("Save",null).create();dialog.show();dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v->{try{JSONObject o=new JSONObject();String name=input.getText().toString().trim();if(name.isEmpty()){msg("Please enter a formula name");return;}o.put("name",name);o.put("expression",formula);saved.put(o);persist();Toast.makeText(this,"Formula saved",Toast.LENGTH_SHORT).show();dialog.dismiss();show(1);}catch(Exception e){msg("Unable to save");}});}
 void recipes(){text("Saved formulas");if(saved.length()==0){text("No saved formulas yet. Create one from Calculator.");return;}EditText search=new EditText(this);search.setSingleLine(true);search.setHint("Search saved formulas");search.setTextColor(Color.WHITE);search.setHintTextColor(muted);body.addView(search);LinearLayout results=new LinearLayout(this);results.setOrientation(1);body.addView(results);renderRecipes(results,"");search.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int start,int count,int after){}public void onTextChanged(CharSequence s,int start,int before,int count){renderRecipes(results,s.toString());}public void afterTextChanged(Editable e){}});}
 void renderRecipes(LinearLayout container,String query){container.removeAllViews();ArrayList<Integer> matches=new ArrayList<>();String q=query.trim().toLowerCase(java.util.Locale.ROOT);for(int i=0;i<saved.length();i++){JSONObject o=saved.optJSONObject(i);if(o!=null&&(o.optString("name")+" "+o.optString("expression")).toLowerCase(java.util.Locale.ROOT).contains(q))matches.add(i);}Collections.sort(matches,(a,b)->{boolean fa=saved.optJSONObject(a).optBoolean("favorite"),fb=saved.optJSONObject(b).optBoolean("favorite");return fa==fb?Integer.compare(b,a):(fa?-1:1);});if(matches.isEmpty()){TextView empty=new TextView(this);empty.setText("No matching formulas");empty.setTextColor(muted);empty.setPadding(20,24,20,24);container.addView(empty);return;}for(int index:matches){JSONObject o=saved.optJSONObject(index);if(o==null)continue;TextView label=new TextView(this);label.setText((o.optBoolean("favorite")?"★  ":"")+o.optString("name")+"\n"+o.optString("expression"));label.setTextColor(Color.WHITE);label.setTextSize(18);label.setPadding(18,16,18,16);label.setBackground(shape(card,20));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.setMargins(4,8,4,4);container.addView(label,lp);LinearLayout actions=new LinearLayout(this);container.addView(actions);button(actions,o.optBoolean("favorite")?"★":"☆",()->{try{o.put("favorite",!o.optBoolean("favorite"));persist();renderRecipes(container,query);}catch(JSONException ex){msg("Unable to update favourite");}});button(actions,"Replay",()->replay(o));button(actions,"Edit",()->editRecipe(o));button(actions,"Delete",()->new AlertDialog.Builder(this).setTitle("Delete formula?").setMessage("Delete "+o.optString("name")+"?").setNegativeButton("Cancel",null).setPositiveButton("Delete",(d,w)->{JSONArray n=new JSONArray();for(int j=0;j<saved.length();j++)if(j!=index)n.put(saved.opt(j));saved=n;persist();renderRecipes(container,query);}).show());}}
 void editRecipe(JSONObject o){LinearLayout form=new LinearLayout(this);form.setOrientation(1);EditText name=new EditText(this);name.setSingleLine(true);name.setHint("Formula name");name.setText(o.optString("name"));EditText expression=new EditText(this);expression.setSingleLine(true);expression.setHint("Expression");expression.setText(o.optString("expression"));form.setPadding(18,0,18,0);form.addView(name);form.addView(expression);AlertDialog dialog=new AlertDialog.Builder(this).setTitle("Edit saved formula").setView(form).setNegativeButton("Cancel",null).setPositiveButton("Save",null).create();dialog.show();dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v->{String n=name.getText().toString().trim(),e=expression.getText().toString().trim();if(n.isEmpty()){msg("Formula name cannot be empty");return;}try{CalculatorEngine.calculate(e);o.put("name",n);o.put("expression",e);persist();dialog.dismiss();show(1);}catch(Exception ex){msg("Invalid formula expression");}});}
 void replay(JSONObject o){
  String template=o.optString("expression");
  Pattern p=Pattern.compile("(?<![\\d.])(?:\\d+(?:\\.\\d+)?|\\.\\d+)");
  Matcher m=p.matcher(template);
  ArrayList<EditText> inputs=new ArrayList<>();
  LinearLayout form=new LinearLayout(this);form.setOrientation(1);form.setPadding(24,4,24,8);
  TextView original=new TextView(this);original.setText("Original: "+template);original.setTextColor(muted);original.setTextSize(16);original.setPadding(0,0,0,14);form.addView(original);
  while(m.find()){
   TextView label=new TextView(this);label.setText("Value "+(inputs.size()+1));label.setTextColor(muted);label.setTextSize(14);form.addView(label);
   EditText e=new EditText(this);e.setText(m.group());e.setSelectAllOnFocus(true);
   e.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
   e.setSingleLine(true);form.addView(e);inputs.add(e);
  }
  if(inputs.isEmpty()){msg("No numbers to replay");return;}
  TextView preview=new TextView(this);preview.setTextColor(Color.WHITE);preview.setTextSize(17);preview.setPadding(0,16,0,8);form.addView(preview);
  Runnable refresh=()->{try{String candidate=replayExpression(template,p,inputs);preview.setText("Preview: "+candidate+"\n= "+CalculatorEngine.calculate(candidate));preview.setTextColor(Color.WHITE);}catch(Exception ex){preview.setText("Enter valid values to preview");preview.setTextColor(0xffffa6a6);}};
  for(EditText input:inputs)input.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int st,int count,int after){}public void onTextChanged(CharSequence s,int st,int before,int count){refresh.run();}public void afterTextChanged(Editable e){}});
  refresh.run();
  ScrollView scroll=new ScrollView(this);scroll.addView(form);
  AlertDialog dialog=new AlertDialog.Builder(this).setTitle("Replay • "+o.optString("name")).setView(scroll).setNegativeButton("Cancel",null).setPositiveButton("Use result",null).create();
  dialog.show();
  dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(w->{try{String candidate=replayExpression(template,p,inputs);CalculatorEngine.calculate(candidate);expr=candidate;dialog.dismiss();show(0);}catch(Exception ex){preview.setText("Invalid values — please correct them");preview.setTextColor(0xffffa6a6);}});
 }
 String replayExpression(String template,Pattern pattern,ArrayList<EditText> inputs){
  Matcher matcher=pattern.matcher(template);StringBuffer out=new StringBuffer();int index=0;
  while(matcher.find()){
   String value=inputs.get(index++).getText().toString().trim();
   if(!value.matches("-?(?:\\d+(?:\\.\\d*)?|\\.\\d+)"))throw new IllegalArgumentException("Invalid number");
   matcher.appendReplacement(out,Matcher.quoteReplacement(value.startsWith("-")?"("+value+")":value));
  }
  matcher.appendTail(out);return out.toString();
 }
 void history(){text("Calculation history");if(history.length()==0){text("No calculations yet. Press Calculate to save one.");return;}TextView hint=new TextView(this);hint.setText("Tap an entry to reuse • Hold to copy result");hint.setTextColor(muted);hint.setPadding(16,8,16,12);body.addView(hint);LinearLayout actions=new LinearLayout(this);body.addView(actions);button(actions,"Clear",()->new AlertDialog.Builder(this).setTitle("Clear history?").setMessage("This will delete all saved calculation history.").setNegativeButton("Cancel",null).setPositiveButton("Clear",(d,w)->{history=new JSONArray();persist();show(2);}).show());for(int i=0;i<history.length();i++){JSONObject o=history.optJSONObject(i);if(o==null)continue;TextView t=new TextView(this);t.setText(o.optString("expression")+" = "+o.optString("result"));t.setTextSize(18);t.setTextColor(Color.WHITE);t.setBackground(shape(card,18));t.setPadding(18,18,18,18);LinearLayout.LayoutParams hlp=new LinearLayout.LayoutParams(-1,-2);hlp.setMargins(4,5,4,5);body.addView(t,hlp);t.setOnClickListener(v->{expr=o.optString("expression");show(0);});t.setOnLongClickListener(v->{copyText(o.optString("result"));return true;});}}
}
