/*
Stratagem is a model checker for transition systems described using rewriting
rules and strategies.
Copyright (C) 2013 - SMV@Geneva University.
Program written by Edmundo Lopez Bobeda <edmundo [at] lopezbobeda.net>.
This program is free software; you can redistribute it and/or modify
it under the  terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package ch.unige.cui.smv.stratagem.sigmadd

import org.scalatest.FlatSpec
import ch.unige.cui.smv.stratagem.adt.ADT
import ch.unige.cui.smv.stratagem.adt.Signature
import ch.unige.cui.smv.stratagem.ts.SimpleStrategy
import ch.unige.cui.smv.stratagem.adt.ATerm
import scala.collection.immutable.HashMap
import ch.unige.cui.smv.stratagem.ts.DeclaredStrategyInstance
import ch.unige.cui.smv.stratagem.ts.Strategy
import ch.unige.cui.smv.stratagem.ts.VariableStrategy
import ch.unige.cui.smv.stratagem.ts.TransitionSystem
import ch.unige.cui.smv.stratagem.ts.Choice
import ch.unige.cui.smv.stratagem.ts.Identity
import ch.unige.cui.smv.stratagem.ts.One
import ch.unige.cui.smv.stratagem.ts.FixPointStrategy
import ch.unige.cui.smv.stratagem.ts.Sequence

/**
 * This class tests the strategies.
 * @author mundacho
 *
 */
class TestStrategies extends FlatSpec {
  val adt = {
    val sign = (new Signature)
      .withSort("bool")
      .withSort("nat")
      .withGenerator("zero", "nat")
      .withGenerator("suc", "nat", "nat")
      .withOperation("plus", "nat", "nat", "nat")
      .withGenerator("true", "bool")
      .withGenerator("false", "bool")
      .withOperation("and", "bool", "bool", "bool")
      .withOperation("not", "bool", "bool")

    new ADT("myAdt", sign).declareVariable("b", "bool").declareVariable("x", "nat").declareVariable("y", "nat")
  }

  // defs for defining terms
  def zero = adt.term("zero")
  def suc(term: ATerm) = adt.term("suc", term)
  def trueOp = adt.term("true")
  def falseOp = adt.term("false")
  def andOp(term1: ATerm, term2: ATerm) = adt.term("and", term1, term2)
  def not(term1: ATerm) = adt.term("not", term1)
  def plus(term1: ATerm, term2: ATerm) = adt.term("plus", term1, term2)
  def X = adt.term("x")
  def Y = adt.term("y")
  def B = adt.term("b")

  val S1 = VariableStrategy("S1")
  val S2 = VariableStrategy("S2")

  def Try(s: Strategy) = DeclaredStrategyInstance("try", s)
  def Fixpoint(s: Strategy) = DeclaredStrategyInstance("fixpoint", s)
  def Repeat(s: Strategy) = DeclaredStrategyInstance("repeat", s)

  val booleanStrategy = SimpleStrategy(List(not(trueOp) -> falseOp, not(falseOp) -> trueOp, andOp(B, trueOp) -> B, andOp(falseOp, B) -> falseOp))
  val booleanRewriter = new SimpleSigmaDDRewriter(booleanStrategy)

  "OneStrategyRewriter" should "be able to rewrite simple booleans" in {
    val oneRewriter = new OneRewriter(booleanRewriter, 0)

    val sigmaDDToRewrite1 = SigmaDDFactoryImpl.create(not(trueOp)) v SigmaDDFactoryImpl.create(andOp(trueOp, falseOp)) v SigmaDDFactoryImpl.create(andOp(trueOp, not(falseOp)))
    val rewrittenSigmaDD1 = oneRewriter(sigmaDDToRewrite1).get
    assert(rewrittenSigmaDD1 eq SigmaDDFactoryImpl.create(andOp(trueOp, trueOp)))

    val sigmaDDToRewrite2 = SigmaDDFactoryImpl.create(not(trueOp)) v SigmaDDFactoryImpl.create(andOp(trueOp, not(trueOp))) v SigmaDDFactoryImpl.create(andOp(trueOp, not(falseOp)))
    val rewrittenSigmaDD2 = oneRewriter(sigmaDDToRewrite2).get
    assert(rewrittenSigmaDD2 eq (SigmaDDFactoryImpl.create(andOp(trueOp, falseOp))))

    val sigmaDDToRewrite3 = SigmaDDFactoryImpl.create(not(trueOp)) v SigmaDDFactoryImpl.create(andOp(not(trueOp), falseOp)) v SigmaDDFactoryImpl.create(andOp(trueOp, not(trueOp)))
    val rewrittenSigmaDD3 = oneRewriter(sigmaDDToRewrite3).get
    assert(rewrittenSigmaDD3 eq (SigmaDDFactoryImpl.create(andOp(falseOp, falseOp)) v SigmaDDFactoryImpl.create(andOp(trueOp, falseOp))))

    val sigmaDDToRewrite4 = SigmaDDFactoryImpl.create(not(trueOp)) v SigmaDDFactoryImpl.create(andOp(trueOp, falseOp)) v SigmaDDFactoryImpl.create(andOp(falseOp, trueOp))
    val rewrittenSigmaDD4 = oneRewriter(sigmaDDToRewrite4)
    assert(rewrittenSigmaDD4 == None)
  }

  "DeclaredStrategyRewriter" should "be able to handle common strategies" in {
    var ts = (new TransitionSystem(adt, trueOp))
      .declareStrategy("onebooleanStrategy") { One(booleanStrategy) }(false)
      .declareStrategy("booleanStrategy") { booleanStrategy }(false)
      .declareStrategy("try", S1) { Choice(S1, Identity) }(false)
      .declareStrategy("repeat", S1) { Choice(Sequence(S1, Repeat(S1)), Identity) }(false)
      .declareStrategy("fixpoint", S1) { FixPointStrategy(S1) }(false)
    val decOneBool = DeclaredStrategyInstance("onebooleanStrategy")
    val decBool = DeclaredStrategyInstance("booleanStrategy")
    val declaredStrategy = new DeclaredStrategyRewriter(Try(decOneBool), ts)
    val sigmaDDToRewrite1 = SigmaDDFactoryImpl.create(andOp(trueOp, falseOp)) v SigmaDDFactoryImpl.create(andOp(falseOp, trueOp))
    val rewrittenSigmaDD = declaredStrategy(sigmaDDToRewrite1).get
    assert(rewrittenSigmaDD eq sigmaDDToRewrite1)

    val sigmaDDToRewrite2 = SigmaDDFactoryImpl.create(not(trueOp)) v SigmaDDFactoryImpl.create(not(falseOp))
    val repeatRewriter = new DeclaredStrategyRewriter(Fixpoint(Try(decBool)), ts)
    assert(repeatRewriter(sigmaDDToRewrite2).get eq (SigmaDDFactoryImpl.create(falseOp)))

    val sigmaDDToRewrite3 = SigmaDDFactoryImpl.create(not(trueOp)) v SigmaDDFactoryImpl.create(andOp(not(trueOp), falseOp)) v
      SigmaDDFactoryImpl.create(andOp(trueOp, not(falseOp))) v SigmaDDFactoryImpl.create(not(falseOp))
    val repeatRewriter1 = new DeclaredStrategyRewriter(Fixpoint(Try(Choice(decBool, decOneBool))), ts) // tests fixpoint rewriter
    assert(repeatRewriter1(sigmaDDToRewrite3).get eq SigmaDDFactoryImpl.create(falseOp))

    val repeatRewriter2 = new DeclaredStrategyRewriter(Repeat(Choice(decBool, decOneBool)), ts) // tests repeat rewriter
    assert(repeatRewriter2(sigmaDDToRewrite3).get eq (SigmaDDFactoryImpl.create(falseOp)))
  }

}