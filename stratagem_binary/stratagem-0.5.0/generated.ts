
TransitionSystem
ADT myAdt

Signature

Sorts
pp, nat, name

Generators
p1: name
p2: name

noPlace : pp
Place: name, nat, pp -> pp
suc: nat -> nat
zero: nat
Variables
jeton_p1: nat
jeton_p2: nat

p : pp
initialState = Place(p1, suc(zero), Place(p2, zero, noPlace))


Strategies
Try(S) = Choice(S, Identity)
DoForAllPlaces(V) = Union(Try(V), Choice(One(DoForAllPlaces(V),2), Try(V)))
t1 = { Place(p1, suc(zero), Place(p2, zero, $p)) -> Place(p1, zero, Place(p2, suc(zero), $p)) }
t2 = { Place(p2, suc(zero), Place(p1, zero, $p)) -> Place(p2, zero, Place(p1, suc(zero), $p)) }

Transitions
makeTransitiont1 = DoForAllPlaces(t1())
makeTransitiont2 = DoForAllPlaces(t2())