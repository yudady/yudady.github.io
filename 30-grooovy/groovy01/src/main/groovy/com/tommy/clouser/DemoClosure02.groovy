package com.tommy.clouser
// Closure
def c = {
    v ->
        println "hello ${v}"
}
method('call method ', c)
methodB('call methodB ', c)
// ---------------------------
method('tommy') {
    v ->
        println "inner method hello ${v}"
}
methodB('tommy') {
    v ->
        println "inner methodB hello ${v}"
}
// ---------------------------
// method
def method(para, Closure closure) {
    closure(para)
}
// method
def methodB(para, Closure closure) {
    closure(para)
}

static void main(String[] args) {

}