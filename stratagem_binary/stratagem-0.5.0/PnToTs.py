import json
from pprint import pprint
from functools import reduce


def getPlaces(data):
    return [place for place in data["places"]]


def getPlacesNames(data):
    return [place["name"] for place in data["places"]]


def getPetriNetData():
    with open('petri3.json') as f:
        return json.load(f)


def getJetonNumber(n, jetonString=""):
    if n == 0:
        return "zero"
    else:
        while n + 1 > 0:
            n = n - 1
            return "".join([
                "suc(",
                getJetonNumber(n, jetonString),
                ")"])


def init(program):
    return "\n".join([
        program,
        "TransitionSystem",
        "ADT myAdt\n",
        "Signature\n",
        "Sorts",
        "pp, nat, name\n"])


def initialStateRecursion(rest):
    current = rest.pop(0)
    current["jeton"] = getJetonNumber(current["jeton"])

    if len(rest) == 0:
        nextPlace = "noPlace"
    else:
        nextPlace = initialStateRecursion(rest)

    return "".join(
        [
            "Place(",
            current["name"],
            ", ",
            str(current["jeton"]),
            ", ",
            nextPlace,
            ")"
        ]
    )


def setInitialState(places):
    return "".join([
        "initialState = ", initialStateRecursion(places)
    ])


def setVariables(program, placesNames, places):

    variablesGenerators = reduce(
        (lambda x, y:  x + "jeton_" + y + "\n"),
        placesNames
    )

    return "\n".join([
        program,
        "Variables",
        variablesGenerators,
        "p : pp",
        setInitialState(places),
        "suc: nat -> nat",
        "zero: nat"
    ])


def setGenerators(program, placesNames):
    placesNames.insert(0, "")
    placesGenerators = reduce(
        (lambda x, y:  x + y + ": name\n"),
        placesNames
    )
    return "\n".join([
        program,
        "Generators",
        placesGenerators,
        "noPlace : pp",
        "Place: name, nat, pp -> pp",
        "suc: nat -> nat",
        "zero: nat"
    ])


data = getPetriNetData()
placesNames = getPlacesNames(data)
places = getPlaces(data)

program = ""
program = init(program)
program = setGenerators(program, placesNames)
program = setVariables(program, placesNames, places)
print(program)


# Strategies
# Try(S) = Choice(S, Identity)
# DoForAllPlaces(V) = Union(Try(V), Choice(One(DoForAllPlaces(V), 3), Try(V)))
# DoForLastPlace(V) = Choice(One(DoForLastPlace(V), 3), V)

# t1 = { Place(place1, suc($x), Place(place2, $y, $p)) -> Place(place1, $x, Place(place2, suc($y), $p)) }
# t2 = { Place(place2, suc($x), Place(place1, $y, $p)) -> Place(place2, $x, Place(place1, suc($y), $p)) }

# Transitions
# makeTransition = DoForAllPlaces(t1())
