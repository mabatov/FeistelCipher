В данной работе приводится алгоритм шифрования в сетях Фейстеля для магистерской работы "Исследование методов динамического формирования ключей в сетях Фейстеля". Особенностью программы является модификация алгоритма: ключ формируется в зависимости от текущей Юлианской даты, а точнее ее разновидности. При проходе каждого раунда считывается текущее время, переводится в UNIX-формат (epoch), затем отсекается левая часть, для того, чтобы размерность совпадала с размерностью подблока. Таким образом, на каждом раунде происходит динамическое формирование ключа.