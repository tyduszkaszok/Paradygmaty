
:- dynamic task/2.

add_task(Id, Description) :- assertz(task(Id, Description)).

remove_task(Id) :- retract(task(Id, _)).

get_tasks(Tasks) :- findall(task(Id, Description), task(Id, Description), Tasks).
