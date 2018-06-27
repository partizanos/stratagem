
TransitionSystem
ADT myAdt

Signature

Sorts
pp, nat, name

Generators
p1: name
p2: name
p3: name

noPlace : pp
Place: name, nat, pp -> pp
suc: nat -> nat
zero: nat
Variables
jeton_p1: nat
jeton_p2: nat
jeton_p3: nat

p : pp
initialState = Place(p1, suc(zero), Place(p2, zero, Place(p3, zero, noPlace)))


Strategies
Try(S) = Choice(S, Identity)
DoForAllPlaces(V) = Union(Try(V), Choice(One(DoForAllPlaces(V),33), Try(V)))

t1 = { 
    Place(p1, suc($jeton_p1), Place(p2, $jeton_p2, $p)) 
    -> 
    Place(p1, $jeton_p1, Place(p2, suc($jeton_p2), $p)) 
}

t2 = { Place(p2, suc($jeton_p2), Place(p1, $jeton_p1, $p)) -> Place(p2, $jeton_p2, Place(p1, suc($jeton_p1), $p)) }
// forced to write all state in all transition because p1 p2 p3 initial state definition, doens combine p1, p3
t3 = { Place(p1, suc($jeton_p1), Place(p2, $jeton_p2, Place(p3, $jeton_p3, $p))) -> Place(p1, $jeton_p1, Place(p2, $jeton_p2, Place(p3, suc($jeton_p3), $p))) }

// infinite Loop
// checkProp = { Place(p1, suc($jeton_p1), $p) -> Place(p1, suc(suc($jeton_p1)), $p) }
checkProp = { Place(p1, suc($jeton_p1), $p) -> Place(p1, $jeton_p1, $p) }

allStrat = Union(Union(Try(t1()), Try(t2())), t3())
StateSpaceStrat = Fixpoint(Union(Identity, allStrat()))
check = Sequence(StateSpaceStrat(), checkProp())
// checkState = { Place(p1, suc($jeton_p1), Place(p2, $jeton_p2, Place(p3, suc(suc(zero)), $p)) 
//     -> Place(p1, suc($jeton_p1), Place(p2, $jeton_p2, Place(p3, suc(suc(zero)), $p)))}
// findCheck (V) = IfThenElse(checkState(), V, One(findCheck(V), 3))

Transitions
// nextStrat = Union(Union(Try(tStrat1), Try(tStrat2)), tStrat3), · · · ),
// tStratn)
//StateSpaceStrat = saturation(stateWanted)
// makeTransitiont0 = DoForAllPlaces(findCheck(t1()))
// makeTransitiont1 = DoForAllPlaces(t1())
// makeTransitiont2 = DoForAllPlaces(t2())
// makeTransitiont3 = DoForAllPlaces(t3())
makeAllTransitions = DoForAllPlaces(check())