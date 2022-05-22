package com.tommy.clouser

int x = 5
println '====================='

int fab(int number) {
    int result = 1
    1.upto(number, { num -> return result *= num })
    return result
}

println 'fab : ' + fab(x)

def fab2(int number) {
    int result = 1
    number.downto(1, { num -> return result *= num })
    return result
}

println 'fab2 : ' + fab2(x)

