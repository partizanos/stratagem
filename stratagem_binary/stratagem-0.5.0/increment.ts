TransitionSystem
ADT myAdt

Signature

Sorts
pp, state, nat, name

Generators
place1: name
place2: name
place3: name
noPlace : pp
Place: name, nat, pp -> pp
suc: nat -> nat
zero: nat

Variables 
n : name
n1 : name
p : pp
s : state
initialState =  Place(place1, suc(zero), Place(place2, zero, noPlace))

Strategies
Try(S) = Choice(S, Identity)
DoForAllPlaces(V) = Union(Try(V), Choice(One(DoForAllPlaces(V), 3), Try(V)))
DoForLastPlace(V) = Choice(One(DoForLastPlace(V), 3), V)

t1 = { Place(place1, suc(zero), Place(place2, zero, $p)) -> Place(place1, zero, Place(place2, suc(zero), $p)) }
t2 = { Place(place2, suc(zero), Place(place1, zero, $p)) -> Place(place2, zero, Place(place1, suc(zero), $p)) }

Transitions
makeTransition = DoForAllPlaces(t1())
