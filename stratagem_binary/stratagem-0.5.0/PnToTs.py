import json
from pprint import pprint
from functools import reduce


def getPlaces(data):
    return [place for place in data["places"]]


def getPlacesNames(data):
    return [place["name"] for place in data["places"]]


def getPetriNetData():
    with open('petriInput.json') as f:
        return json.load(f)


def getJetonNumber(n, jetonString=""):
    """3 => "suc(suc(suc(0)))" """
    if n == 0:
        return "zero"
    else:
        while n + 1 > 0:
            n = n - 1
            return "".join([
                "suc(",
                getJetonNumber(n, jetonString),
                ")"
            ])


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


def setVariables(program, variablesGenerators, places):
    return "\n".join([
        program,
        "Variables",
        variablesGenerators,
        "p : pp",
        setInitialState(places)
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


def getPlaceVariable(placeName): return "jeton_" + placeName


def setTransitionPlace(place, nextPlace="$p"):
    """ {"name": "p1", "jeton": 1} => "Place(place2, $y, $p)" """
    return "".join([
        "Place(",
        place["name"],
        ", ",
        getJetonNumber(place["jeton"]),
        ", ",
        nextPlace,
        ")"
    ])


def setTransitionPart(restPlaces):
    """
    [{"name": "p1", "jeton": 1 }, {"name": "p2", "jeton": 0}]
    => Place(place1, suc($x), Place(place2, $y, $p))
    """

    if(len(restPlaces) == 1):
        return setTransitionPlace(restPlaces[0])
    else:
        current = restPlaces.pop(0)
        return setTransitionPlace(
            current,
            setTransitionPart(restPlaces)
        )


def setTransitionStrategy(transition):
    return "".join([
        transition["name"],
        " = { ",
        setTransitionPart(transition["pre"][:]),
        " -> ",
        setTransitionPart(transition["after"][:]),
        " }"
    ])


def setStrategies(program, transitions, places):
    transitionStrategies = "\n".join(
        [setTransitionStrategy(t) for t in transitions])

    return "\n".join([
        program,
        "\n",
        "Strategies",
        "Try(S) = Choice(S, Identity)",
        "".join([
            "DoForAllPlaces(V) = Union(Try(V), Choice(One(DoForAllPlaces(V),",
            str(len(places)),
            "), Try(V)))"
        ]),
        transitionStrategies
    ])


def setTransitions(program, transitions):
    return program + "\n\nTransitions\n" + "\n".join([
        "makeTransition" + t["name"] + " = DoForAllPlaces(" + t["name"] + "())" for t in transitions
    ])


def save(program):
    text_file = open("generated.ts", "w")
    text_file.write(program)
    text_file.close()


data = getPetriNetData()
placesNames = getPlacesNames(data)
variables = [("jeton_" + p) for p in placesNames]
variablesGenerators = "".join([var + ": nat\n" for var in variables])
places = data["places"][:]
transitions = data["transitions"]

program = ""
program = init(program)
program = setGenerators(program, placesNames)
program = setVariables(program, variablesGenerators, places)
program = setStrategies(program, transitions, data["places"])
program = setTransitions(program, transitions)
save(program)