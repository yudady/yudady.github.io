package com.tommy.clouser
// Closure
def c = {
    println "hello tommy"
}
// method
def method(Closure closure) {
    closure()
}

method(c)

static void main(String[] args) {

}
