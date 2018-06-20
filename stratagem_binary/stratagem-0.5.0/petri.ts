TransitionSystem
ADT myAdt

Signature

Sorts
pp, state, Jeton, name

Generators
JetonUsed : state
JetonFree : state
place1: name
place2: name
place3: name
noPlace : pp
Place: name, state, pp -> pp

Variables 
x : Jeton
n : name
n1 : name
p : pp
f : Jeton
s : state
initialState =  Place(place1, JetonUsed, Place(place2, JetonFree, noPlace))

Strategies
Try(S) = Choice(S, Identity)
DoForAllPlaces(V) = Union(Try(V), Choice(One(DoForAllPlaces(V), 3), Try(V)))
DoForLastPlace(V) = Choice(One(DoForLastPlace(V), 3), V)

t1 = { Place(place1, JetonUsed, Place(place2, JetonFree, $p)) -> Place(place1, JetonFree, Place(place2, JetonUsed, $p)) }
t2 = { Place(place2, JetonUsed, Place(place1, JetonFree, $p)) -> Place(place2, JetonFree, Place(place1, JetonUsed, $p)) }

Transitions
makeTransition = DoForAllPlaces(t1())
