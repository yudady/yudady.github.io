package com.tommy.clouser
// Closure
def c = {
    k, v ->
        println "hello ${k} : ${v}"

}
// method
def method(Closure closure, para1, para2) {
    closure(para1, para2)
}

method(c, 'tommy', 'TT')

static void main(String[] args) {

}