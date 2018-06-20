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
noPetriPlaces : pp
Place: name, state, pp -> pp

Variables 
x : Jeton
p : pp
f : Jeton
s : state
initialState =  Place(place1, JetonFree, Place(place2, JetonFree, noPetriPlaces))

Strategies
Try(S) = Choice(S, Identity)
DoForAllPlaces(V) = Union(Try(V), Choice(One(DoForAllPlaces(V), 3), Try(V)))
DoForLastPlace(V) = Choice(One(DoForLastPlace(V), 3), V)
p1Top2 = { Place(place1, JetonUsed, noPetriPlaces) -> Place(place1, JetonFree, noPetriPlaces) }
Transitions
makeTransition = DoForAllPlaces(p1Top2())