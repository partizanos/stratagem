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
x : nat
y : nat
n : name
n1 : name
p : pp
s : state
initialState =  Place(place1, suc(zero), Place(place2, zero, noPlace))

Strategies
Try(S) = Choice(S, Identity)
DoForAllPlaces(V) = Union(Try(V), Choice(One(DoForAllPlaces(V), 3), Try(V)))
DoForLastPlace(V) = Choice(One(DoForLastPlace(V), 3), V)

t1 = { Place(place1, suc($x), Place(place2, $y, $p)) -> Place(place1, $x, Place(place2, suc($y), $p)) }
t2 = { Place(place2, suc($x), Place(place1, $y, $p)) -> Place(place2, $x, Place(place1, suc($y), $p)) }

Transitions
makeTransition = DoForAllPlaces(t1())
