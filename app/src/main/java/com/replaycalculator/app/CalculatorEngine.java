package com.replaycalculator.app;
public final class CalculatorEngine {
 private final String expression;
 private int pos;
 private CalculatorEngine(String value) { expression = value.replace("×","*").replace("÷","/").replace("−","-"); }
 public static String calculate(String value) {
  CalculatorEngine p = new CalculatorEngine(value);
  double result = p.sum();
  p.skip();
  if(p.pos != p.expression.length() || !Double.isFinite(result)) throw new IllegalArgumentException("Invalid expression");
  return java.math.BigDecimal.valueOf(result).stripTrailingZeros().toPlainString();
 }
 private void skip() { while(pos < expression.length() && Character.isWhitespace(expression.charAt(pos))) pos++; }
 private boolean take(char ch) { skip(); if(pos < expression.length() && expression.charAt(pos) == ch) { pos++; return true; } return false; }
 private double sum() { double v = product(); while(true) { if(take('+')) v += product(); else if(take('-')) v -= product(); else return v; } }
 private double product() { double v = factor(); while(true) { if(take('*')) v *= factor(); else if(take('/')) { double d = factor(); if(d == 0) throw new IllegalArgumentException("Division by zero"); v /= d; } else return v; } }
 private double factor() {
  if(take('+')) return factor();
  if(take('-')) return -factor();
  double v;
  if(take('(')) { v = sum(); if(!take(')')) throw new IllegalArgumentException("Missing bracket"); }
  else {
   skip(); int start = pos;
   while(pos < expression.length() && (Character.isDigit(expression.charAt(pos)) || expression.charAt(pos) == '.')) pos++;
   if(start == pos) throw new IllegalArgumentException("Expected number");
   try { v = Double.parseDouble(expression.substring(start,pos)); }
   catch(NumberFormatException ex) { throw new IllegalArgumentException("Invalid number"); }
  }
  while(take('%')) v /= 100;
  return v;
 }
}
