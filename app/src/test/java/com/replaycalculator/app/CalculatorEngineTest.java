package com.replaycalculator.app;
import org.junit.Test;
import static org.junit.Assert.*;
public class CalculatorEngineTest {
 private void eq(String expression,String expected){assertEquals(expected,CalculatorEngine.calculate(expression));}
 private void invalid(String expression){try{CalculatorEngine.calculate(expression);fail("Expected failure for: "+expression);}catch(IllegalArgumentException expected){}}
 @Test public void arithmetic(){eq("2+3","5");eq("8−3","5");eq("4×5","20");eq("18÷3","6");}
 @Test public void precedence(){eq("2+3×4","14");eq("(2+3)×4","20");eq("20÷2+3","13");}
 @Test public void decimals(){eq("1.5+2.5","4");eq(".5×4","2");}
 @Test public void percentages(){eq("50%","0.5");eq("200×10%","20");}
 @Test public void negatives(){eq("-5+2","-3");eq("10−(−2)","12");eq("(-5)×3","-15");}
 @Test public void badInput(){invalid("");invalid("2+");invalid("(2+3");invalid("2÷0");invalid("1..2");invalid("abc");}
}
