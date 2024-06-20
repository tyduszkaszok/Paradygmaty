% tasks.pl
:- dynamic task/2.

% Dodawanie zadania
add_task(Id, Description) :- assertz(task(Id, Description)).

% Usuwanie zadania
remove_task(Id) :- retract(task(Id, _)).

% Pobieranie wszystkich zadañ
get_tasks(Tasks) :- findall(task(Id, Description), task(Id, Description), Tasks).
