APN philo

//////////////////////////////////////////////////////////////////////
// 
// Multiset encoding in StrataGEM - trivial solution
// 
// Get an item from a multiset (works with both ascending and descending order)
// 
//////////////////////////////////////////////////////////////////////
TransitionSystem
ADT Forks

Signature

Sorts

fork, listFork, place

Generators

f0 : fork
f  : fork -> fork
emptyFork : listFork
consFork : fork, listFork -> listFork
endplace : place
forks : listFork, place -> place
forksGet : listFork, place -> place
stackFk : fork, place -> place
getF : listFork -> listFork
getFElt : fork, listFork -> listFork

// the adt variables, you can use this variables in terms by prefixin them with a dollar sign ($)

Variables

FK1 : fork
FK2 : fork
FK3 : fork
FK4 : fork
FK5 : fork
FK6 : fork
FK7 : fork
FK8 : fork
LF1 : listFork
PL : place

// the initial state

//initialState =  forks(consFork(f0, emptyFork), endplace)
//initialState =  forks(consFork(f0, consFork(f(f0), emptyFork)), endplace)
//initialState =  forks(consFork(f0, consFork(f(f0), consFork(f(f(f0)), emptyFork))), endplace)
//initialState =  forks(consFork(f0, consFork(f(f0), consFork(f(f(f0)), consFork(f(f(f(f0))), emptyFork)))), endplace)
//initialState =  forks(consFork(f0, consFork(f(f0), consFork(f(f(f0)), consFork(f(f(f(f0))), consFork(f(f(f(f(f0)))), emptyFork))))), endplace)
//initialState =  forks(consFork(f0, consFork(f(f0), consFork(f(f(f0)), consFork(f(f(f(f0))), consFork(f(f(f(f(f0)))), consFork(f(f(f(f(f(f0))))), emptyFork)))))), endplace)
//initialState =  forks(consFork(f0, consFork(f(f0), consFork(f(f(f0)), consFork(f(f(f(f0))), consFork(f(f(f(f(f0)))), consFork(f(f(f(f(f(f0))))), consFork(f(f(f(f(f(f(f0)))))), emptyFork))))))), endplace)
initialState =  forks(consFork(f0, consFork(f(f0), consFork(f(f(f0)), consFork(f(f(f(f0))), consFork(f(f(f(f(f0)))), consFork(f(f(f(f(f(f0))))), consFork(f(f(f(f(f(f(f0)))))), consFork(f(f(f(f(f(f(f(f0))))))), emptyFork)))))))), endplace)

// Here we define the strategies

Strategies

Try(S) = Choice(S, Identity)

takeForkFromForks = { forks($LF1, $PL) -> forksGet($LF1, $PL) }

actual1 = { forksGet(consFork($FK1, emptyFork), $PL) -> stackFk($FK1, forksGet(emptyFork, $PL)) }


actual2 = { forksGet(consFork($FK1, consFork($FK2, emptyFork)), $PL) -> stackFk($FK1, forksGet(consFork($FK2, emptyFork), $PL)) }

actual3 = { forksGet(consFork($FK1, consFork($FK2, emptyFork)), $PL) -> stackFk($FK2, forksGet(consFork($FK1, emptyFork), $PL)) }


actual4 = { forksGet(consFork($FK1, consFork($FK2, consFork($FK3, emptyFork))), $PL) -> stackFk($FK1, forksGet(consFork($FK2, consFork($FK3, emptyFork)), $PL)) }

actual5 = { forksGet(consFork($FK1, consFork($FK2, consFork($FK3, emptyFork))), $PL) -> stackFk($FK2, forksGet(consFork($FK1, consFork($FK3, emptyFork)), $PL)) }

actual6 = { forksGet(consFork($FK1, consFork($FK2, consFork($FK3, emptyFork))), $PL) -> stackFk($FK3, forksGet(consFork($FK1, consFork($FK2, emptyFork)), $PL)) }


actual7 = { forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, emptyFork)))), $PL) -> stackFk($FK1, forksGet(consFork($FK2, consFork($FK3, consFork($FK4, emptyFork))), $PL)) }

actual8 = { forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, emptyFork)))), $PL) -> stackFk($FK2, forksGet(consFork($FK1, consFork($FK3, consFork($FK4, emptyFork))), $PL)) }

actual9 = { forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, emptyFork)))), $PL) -> stackFk($FK3, forksGet(consFork($FK1, consFork($FK2, consFork($FK4, emptyFork))), $PL)) }

actual10 = { forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, emptyFork)))), $PL) -> stackFk($FK4, forksGet(consFork($FK1, consFork($FK2, consFork($FK3, emptyFork))), $PL)) }


actual11 = { forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, emptyFork))))), $PL) -> stackFk($FK1, forksGet(consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, emptyFork)))), $PL)) }

actual12 = { forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, emptyFork))))), $PL) -> stackFk($FK2, forksGet(consFork($FK1, consFork($FK3, consFork($FK4, consFork($FK5, emptyFork)))), $PL)) }

actual13 = { forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, emptyFork))))), $PL) -> stackFk($FK3, forksGet(consFork($FK1, consFork($FK2, consFork($FK4, consFork($FK5, emptyFork)))), $PL)) }

actual14 = { forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, emptyFork))))), $PL) -> stackFk($FK4, forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK5, emptyFork)))), $PL)) }

actual15 = { forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, emptyFork))))), $PL) -> stackFk($FK5, forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, emptyFork)))), $PL)) }


actual16 = { forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, consFork($FK6, emptyFork)))))), $PL) -> stackFk($FK1, forksGet(consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, consFork($FK6, emptyFork))))), $PL)) }

actual17 = { forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, consFork($FK6, emptyFork)))))), $PL) -> stackFk($FK2, forksGet(consFork($FK1, consFork($FK3, consFork($FK4, consFork($FK5, consFork($FK6, emptyFork))))), $PL)) }

actual18 = { forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, consFork($FK6, emptyFork)))))), $PL) -> stackFk($FK3, forksGet(consFork($FK1, consFork($FK2, consFork($FK4, consFork($FK5, consFork($FK6, emptyFork))))), $PL)) }

actual19 = { forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, consFork($FK6, emptyFork)))))), $PL) -> stackFk($FK4, forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK5, consFork($FK6, emptyFork))))), $PL)) }

actual20 = { forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, consFork($FK6, emptyFork)))))), $PL) -> stackFk($FK5, forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK6, emptyFork))))), $PL)) }

actual21 = { forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, consFork($FK6, emptyFork)))))), $PL) -> stackFk($FK6, forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, emptyFork))))), $PL)) }


actual22 = { forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, consFork($FK6, consFork($FK7, emptyFork))))))), $PL) -> stackFk($FK1, forksGet(consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, consFork($FK6, consFork($FK7, emptyFork)))))), $PL)) }

actual23 = { forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, consFork($FK6, consFork($FK7, emptyFork))))))), $PL) -> stackFk($FK2, forksGet(consFork($FK1, consFork($FK3, consFork($FK4, consFork($FK5, consFork($FK6, consFork($FK7, emptyFork)))))), $PL)) }

actual24 = { forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, consFork($FK6, consFork($FK7, emptyFork))))))), $PL) -> stackFk($FK3, forksGet(consFork($FK1, consFork($FK2, consFork($FK4, consFork($FK5, consFork($FK6, consFork($FK7, emptyFork)))))), $PL)) }

actual25 = { forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, consFork($FK6, consFork($FK7, emptyFork))))))), $PL) -> stackFk($FK4, forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK5, consFork($FK6, consFork($FK7, emptyFork)))))), $PL)) }

actual26 = { forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, consFork($FK6, consFork($FK7, emptyFork))))))), $PL) -> stackFk($FK5, forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK6, consFork($FK7, emptyFork)))))), $PL)) }

actual27 = { forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, consFork($FK6, consFork($FK7, emptyFork))))))), $PL) -> stackFk($FK6, forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, consFork($FK7, emptyFork)))))), $PL)) }

actual28 = { forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, consFork($FK6, consFork($FK7, emptyFork))))))), $PL) -> stackFk($FK7, forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, consFork($FK6, emptyFork)))))), $PL)) }


actual29 = { forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, consFork($FK6, consFork($FK7, consFork($FK8, emptyFork)))))))), $PL) -> stackFk($FK1, forksGet(consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, consFork($FK6, consFork($FK7, consFork($FK8, emptyFork))))))), $PL)) }

actual30 = { forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, consFork($FK6, consFork($FK7, consFork($FK8, emptyFork)))))))), $PL) -> stackFk($FK2, forksGet(consFork($FK1, consFork($FK3, consFork($FK4, consFork($FK5, consFork($FK6, consFork($FK7, consFork($FK8, emptyFork))))))), $PL)) }

actual31 = { forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, consFork($FK6, consFork($FK7, consFork($FK8, emptyFork)))))))), $PL) -> stackFk($FK3, forksGet(consFork($FK1, consFork($FK2, consFork($FK4, consFork($FK5, consFork($FK6, consFork($FK7, consFork($FK8, emptyFork))))))), $PL)) }

actual32 = { forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, consFork($FK6, consFork($FK7, consFork($FK8, emptyFork)))))))), $PL) -> stackFk($FK4, forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK5, consFork($FK6, consFork($FK7, consFork($FK8, emptyFork))))))), $PL)) }

actual33 = { forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, consFork($FK6, consFork($FK7, consFork($FK8, emptyFork)))))))), $PL) -> stackFk($FK5, forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK6, consFork($FK7, consFork($FK8, emptyFork))))))), $PL)) }

actual34 = { forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, consFork($FK6, consFork($FK7, consFork($FK8, emptyFork)))))))), $PL) -> stackFk($FK6, forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, consFork($FK7, consFork($FK8, emptyFork))))))), $PL)) }

actual35 = { forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, consFork($FK6, consFork($FK7, consFork($FK8, emptyFork)))))))), $PL) -> stackFk($FK7, forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, consFork($FK6, consFork($FK8, emptyFork))))))), $PL)) }

actual36 = { forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, consFork($FK6, consFork($FK7, consFork($FK8, emptyFork)))))))), $PL) -> stackFk($FK8, forksGet(consFork($FK1, consFork($FK2, consFork($FK3, consFork($FK4, consFork($FK5, consFork($FK6, consFork($FK7, emptyFork))))))), $PL)) }
// These are the strategies that are interpreted as transitions, there must be at least one of these and also,
// they are not allowed to have parameters, in the body you can use any strategy defined either in the strategies
// section or in this section.

Transitions

//do1 = Sequence(takeForkFromForks(), Union( Try(actual1()), Union(Try(actual2()), Union(Try(actual3()), Union(Try(actual4()), Union(Try(actual5()), Union(Try(actual6()), Union(Try(actual7()), Union(Try(actual8()), Union(Try(actual9()), Union(Try(actual10()), Union(Try(actual11()), Union(Try(actual12()), Union(Try(actual13()), Union(Try(actual14()), Union(Try(actual15()), Union(Try(actual16()), Union(Try(actual17()), Union(Try(actual18()), Union(Try(actual19()), Union(Try(actual20()), Union(Try(actual21()), Union(Try(actual22()), Union(Try(actual23()), Union(Try(actual24()), Union(Try(actual25()), Union(Try(actual26()), Union(Try(actual27()),  Try(actual28())  ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) )

do1 = Sequence(takeForkFromForks(), Union( Try(actual1()), Union(Try(actual2()), Union(Try(actual3()), Union(Try(actual4()), Union(Try(actual5()), Union(Try(actual6()), Union(Try(actual7()), Union(Try(actual8()), Union(Try(actual9()), Union(Try(actual10()), Union(Try(actual11()), Union(Try(actual12()), Union(Try(actual13()), Union(Try(actual14()), Union(Try(actual15()), Union(Try(actual16()), Union(Try(actual17()), Union(Try(actual18()), Union(Try(actual19()), Union(Try(actual20()), Union(Try(actual21()), Union(Try(actual22()), Union(Try(actual23()), Union(Try(actual24()), Union(Try(actual25()), Union(Try(actual26()), Union(Try(actual27()), Union(Try(actual28()), Union(Try(actual29()), Union(Try(actual30()), Union(Try(actual31()), Union(Try(actual32()), Union(Try(actual33()), Union(Try(actual34()), Union(Try(actual35()),  Try(actual36())  ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) ) )